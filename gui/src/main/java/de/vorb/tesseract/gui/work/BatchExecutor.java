package de.vorb.tesseract.gui.work;

import de.vorb.tesseract.gui.controller.TesseractController;
import de.vorb.tesseract.gui.model.BatchExportModel;
import de.vorb.tesseract.gui.model.ProjectModel;
import de.vorb.tesseract.gui.util.DocumentWriter;
import de.vorb.tesseract.gui.view.dialogs.Dialogs;
import de.vorb.tesseract.tools.preprocessing.Preprocessor;
import de.vorb.tesseract.util.TraineddataFiles;

import eu.digitisation.input.Batch;
import eu.digitisation.input.Parameters;
import eu.digitisation.input.WarningException;
import eu.digitisation.output.Report;

import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.xml.transform.TransformerException;
import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class BatchExecutor {
    private final TesseractController controller;
    private final ProjectModel project;
    private final BatchExportModel export;

    public BatchExecutor(TesseractController controller, ProjectModel project,
            BatchExportModel export) {

        this.controller = controller;
        this.project = project;
        this.export = export;
    }

    public void start(final ProgressMonitor progressMonitor,
            final Writer errorLog) throws IOException, InterruptedException {
        final int numThreads = export.getNumThreads();

        final ExecutorService threadPool =
                Executors.newFixedThreadPool(numThreads);

        final LinkedBlockingQueue<PageRecognitionProducer> recognizers =
                new LinkedBlockingQueue<>(numThreads);

        final String trainingFile = controller.getTrainingFile().get();
        for (int i = 0; i < numThreads; i++) {
            final PageRecognitionProducer recognizer =
                    new PageRecognitionProducer(controller,
                            TraineddataFiles.getTessdataDir(),
                            trainingFile);
            recognizer.init();
            recognizers.put(recognizer);
        }

        final List<Future<?>> futures = new ArrayList<>();

        // ensure the destination directory and others exist
        Files.createDirectories(project.getPreprocessedDir());
        Files.createDirectories(project.getEvaluationDir());
        Files.createDirectories(project.getTranscriptionDir());
        Files.createDirectories(project.getOCRDir());
        Files.createDirectories(export.getDestinationDir());

        // holds progress count
        final AtomicInteger progress = new AtomicInteger(0);

        // holds error count
        final AtomicInteger errors = new AtomicInteger(0);

        // prepare reports
        final Path equivalencesFile = controller.prepareReports();

        // create tasks and submit them
        for (final Path sourceFile : project.getImageFiles()) {
            final Preprocessor preprocessor =
                    controller.getPreprocessor(sourceFile);

            final boolean hasPreprocessorChanged =
                    controller.hasPreprocessorChanged(sourceFile);

            final OCRTask task = new OCRTask(sourceFile,
                    project, export, preprocessor, recognizers,
                    hasPreprocessorChanged, equivalencesFile, progressMonitor,
                    progress, errorLog, errors);

            futures.add(threadPool.submit(task));
        }

        final Future<Void> all = new Future<Void>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                int cancelled = 0;

                for (Future<?> future : futures) {
                    if (future.cancel(mayInterruptIfRunning))
                        cancelled++;
                }

                return cancelled > 0;
            }

            @Override
            public Void get() throws InterruptedException, ExecutionException {
                for (Future<?> future : futures) {
                    future.get();
                }

                return null;
            }

            @Override
            public Void get(long timeout, TimeUnit unit)
                    throws InterruptedException, ExecutionException,
                    TimeoutException {

                // end time
                final long end = System.currentTimeMillis()
                        + unit.toMillis(timeout);

                // while it has not timed out
                while (System.currentTimeMillis() < end) {
                    // when all tasks are completed, return
                    if (isDone()) {
                        return null;
                    }

                    // wait 100ms before rechecking
                    Thread.currentThread().wait(100);
                }

                throw new TimeoutException();
            }

            @Override
            public boolean isCancelled() {
                int cancelled = 0;

                for (Future<?> future : futures) {
                    if (future.isCancelled()) {
                        cancelled++;
                    }
                }

                return cancelled == futures.size();
            }

            @Override
            public boolean isDone() {
                int done = 0;

                for (Future<?> future : futures) {
                    if (future.isDone()) {
                        done++;
                    }
                }

                return done == futures.size();
            }
        };

        threadPool.submit(new CompletionCallback(all, progressMonitor,
                progress, errorLog, errors));

        // prevent new tasks, the threads will be garbage collected once all
        // tasks have completed
        threadPool.shutdown();
    }

    private class CompletionCallback implements Callable<Void> {
        Future<Void> all;
        final ProgressMonitor progressMonitor;
        final AtomicInteger progress;
        final Writer errorLog;
        final AtomicInteger errors;

        CompletionCallback(Future<Void> all, ProgressMonitor progressMonitor,
                AtomicInteger progress, Writer errorLog, AtomicInteger errors) {
            this.all = all;
            this.progressMonitor = progressMonitor;
            this.progress = progress;
            this.errorLog = errorLog;
            this.errors = errors;
        }

        @Override
        public Void call() throws IOException {
            // wait for all other tasks to complete
            try {
                all.get();
            } catch (InterruptedException | ExecutionException e) {
            } finally {
                all = null;
                if (export.exportReports()) {
                    progressMonitor.setNote("Final report");
                    // create a single report for all transcriptions
                    DirectoryStream<Path> transcriptions;
                    try {
                        transcriptions = Files.newDirectoryStream(
                                project.getTranscriptionDir());

                        final Batch batch = new Batch(transcriptions,
                                project.getOCRDir());

                        final Parameters params = new Parameters();
                        final Path eqFile = controller.prepareReports();
                        params.eqfile.setValue(eqFile.toFile());

                        final Report report = new Report(batch, params);

                        final Path projectReport = export.getDestinationDir()
                                .resolve("project.report.html");

                        // write report html
                        DocumentWriter.writeToFile(report.document(), projectReport);

                        // write report csv
                        final BufferedWriter csv = Files.newBufferedWriter(
                                export.getDestinationDir().resolve(
                                        "project.report.csv"),
                                StandardCharsets.UTF_8);
                        csv.write(report.getStats().asCSV("\n", ",").toString());
                        csv.close();
                    } catch (IOException | WarningException
                            | TransformerException e) {
                        errors.incrementAndGet();

                        errorLog.write(e.getMessage());
                        errorLog.write(System.lineSeparator());
                    }
                }

                if (export.openDestination()) {
                    try {
                        Desktop.getDesktop().browse(
                                export.getDestinationDir().toUri());
                    } catch (IOException e) {
                        errors.incrementAndGet();

                        errorLog.write(e.getMessage());
                        errorLog.write(System.lineSeparator());
                    }
                }

                progressMonitor.setProgress(progress.incrementAndGet());
                errorLog.close();

                // show errors or success dialog
                SwingUtilities.invokeLater(() -> {
                    if (errors.get() == 0) {
                        Dialogs.showInfo(controller.getView(),
                                "Export completed",
                                "The batch export finished without any errors.");
                    } else {
                        final boolean investigate = Dialogs.ask(
                                controller.getView(),
                                "Errors during export",
                                String.format(
                                        "The batch export finished, but there have been %d errors. Do you want to"
                                                + " investigate the error log?",
                                        errors.get()));

                        if (investigate) {
                            try {
                                Desktop.getDesktop().open(
                                        export.getDestinationDir().resolve(
                                                "errors.log").toFile());
                            } catch (IOException e) {
                                Dialogs.showError(controller.getView(),
                                        "Error",
                                        "Could not open the error log.");
                            }
                        }
                    }
                });
            }

            return null;
        }
    }

}
