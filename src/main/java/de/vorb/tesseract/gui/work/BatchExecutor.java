package de.vorb.tesseract.gui.work;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.vorb.tesseract.gui.controller.TesseractController;
import de.vorb.tesseract.gui.model.BatchExportModel;
import de.vorb.tesseract.gui.model.ProjectModel;
import de.vorb.tesseract.tools.preprocessing.Preprocessor;
import de.vorb.tesseract.util.TrainingFiles;

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

    public Future<Void> start(OCRTaskCallback callback) throws IOException,
            InterruptedException {
        final int numThreads = export.getNumThreads();

        final ExecutorService threadPool =
                Executors.newFixedThreadPool(numThreads);

        final LinkedBlockingQueue<PageRecognitionProducer> recognizers =
                new LinkedBlockingQueue<>();

        final String trainingFile = controller.getTrainingFile().get();
        for (int i = 0; i < numThreads; i++) {
            recognizers.put(new PageRecognitionProducer(
                    TrainingFiles.getTessdataDir(), trainingFile));
        }

        final List<Future<?>> futures = new ArrayList<>();

        // ensure the destination directory exists
        Files.createDirectories(project.getPreprocessedDir());

        // create tasks and submit them
        for (final Path sourceFile : project.getImageFiles()) {
            final Preprocessor preprocessor =
                    controller.getPreprocessor(sourceFile);

            final boolean hasPreprocessorChanged =
                    controller.hasPreprocessorChanged(sourceFile);

            final OCRTask task = new OCRTask(sourceFile,
                    project, export, preprocessor, recognizers,
                    hasPreprocessorChanged, callback);

            futures.add(threadPool.submit(task));
        }

        // prevent new tasks, the threads will be garbage collected once all
        // tasks have completed
        threadPool.shutdown();

        return new Future<Void>() {
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
    }
}
