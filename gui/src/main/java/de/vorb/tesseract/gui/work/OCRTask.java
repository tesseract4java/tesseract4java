package de.vorb.tesseract.gui.work;

import de.vorb.tesseract.gui.io.PlainTextWriter;
import de.vorb.tesseract.gui.model.BatchExportModel;
import de.vorb.tesseract.gui.model.ProjectModel;
import de.vorb.tesseract.gui.util.DocumentWriter;
import de.vorb.tesseract.tools.preprocessing.Preprocessor;
import de.vorb.tesseract.tools.recognition.PageRecognitionConsumer;
import de.vorb.tesseract.util.Block;
import de.vorb.tesseract.util.Page;
import de.vorb.util.FileNames;

import eu.digitisation.input.Batch;
import eu.digitisation.input.Parameters;
import eu.digitisation.output.Report;

import javax.imageio.ImageIO;
import javax.swing.ProgressMonitor;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class OCRTask implements Callable<Void> {
    private final Path sourceFile;
    private final ProjectModel project;
    private final BatchExportModel export;
    private final Preprocessor preprocessor;
    private final LinkedBlockingQueue<PageRecognitionProducer> recognizers;
    private final boolean hasPreprocessorChanged;
    private final Path equivalencesFile;

    private final ProgressMonitor progressMonitor;
    private final AtomicInteger progress;

    private final Writer errorLog;
    private final AtomicInteger errors;

    public OCRTask(Path sourceFile, ProjectModel project,
            BatchExportModel export, Preprocessor preprocessor,
            LinkedBlockingQueue<PageRecognitionProducer> recognizers,
            boolean hasPreprocessorChanged, Path equivalencesFile,
            ProgressMonitor progressMonitor, AtomicInteger progress,
            Writer errorLog, AtomicInteger errors) {
        this.sourceFile = sourceFile;
        this.project = project;
        this.export = export;
        this.preprocessor = preprocessor;
        this.recognizers = recognizers;
        this.hasPreprocessorChanged = hasPreprocessorChanged;
        this.equivalencesFile = equivalencesFile;

        this.progressMonitor = progressMonitor;
        this.progress = progress;

        this.errorLog = errorLog;
        this.errors = errors;
    }

    @Override
    public Void call() throws IOException {
        if (progressMonitor.isCanceled()) {
            return null;
        }

        progressMonitor.setNote(sourceFile.getFileName().toString());

        final Path imgDestFile = project.getPreprocessedDir().resolve(
                FileNames.replaceExtension(sourceFile, "png").getFileName());

        try {

            final int width;
            final int height;
            {
                final BufferedImage sourceImg =
                        ImageIO.read(sourceFile.toFile());

                width = sourceImg.getWidth();
                height = sourceImg.getHeight();

                final BufferedImage binaryImg;
                if (!hasPreprocessorChanged && Files.exists(imgDestFile)) {
                    // read existing preprocessed image
                    binaryImg = ImageIO.read(imgDestFile.toFile());
                } else {
                    // pre-process source image
                    binaryImg = preprocessor.process(sourceImg);

                    ImageIO.write(binaryImg, "PNG", imgDestFile.toFile());
                }

                // optionally copy preprocessed images to the destination
                // directory
                if (export.exportImages()) {
                    Files.copy(
                            imgDestFile,
                            export.getDestinationDir().resolve(
                                    imgDestFile.getFileName()),
                            StandardCopyOption.REPLACE_EXISTING);
                }
            }

            if (Thread.currentThread().isInterrupted()) {
                return null;
            }

            final PageRecognitionProducer recognizer = recognizers.take();
            if (recognizer == null) {
                throw new IllegalStateException(
                        "No more PageRecognitionProducers");
            }

            try {
                recognizer.reset();
                recognizer.loadImage(imgDestFile);

                final List<Block> blocks = new ArrayList<>();

                recognizer.recognize(new PageRecognitionConsumer(blocks) {
                    @Override
                    public boolean isCancelled() {
                        return Thread.currentThread().isInterrupted();
                    }
                });

                final Page page = new Page(sourceFile, width, height, 300,
                        blocks);

                if (export.exportXML()) {
                    final Path xmlFile = export.getDestinationDir().resolve(
                            FileNames.replaceExtension(sourceFile, "xml").getFileName());

                    try (BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(xmlFile))) {
                        page.writeTo(out);
                    }
                }

                if (export.exportHTML()) {
                    // TODO
                }

                // write the ocr result as text
                final Path txtFile = project.getOCRDir().resolve(
                        FileNames.replaceExtension(sourceFile, "txt").getFileName());
                {
                    final Writer out = Files.newBufferedWriter(txtFile,
                            StandardCharsets.UTF_8);

                    new PlainTextWriter(true).write(page, out);

                    out.close();

                    // if text files are exported, copy it over
                    if (export.exportTXT()) {
                        Files.copy(
                                txtFile,
                                export.getDestinationDir().resolve(
                                        txtFile.getFileName()),
                                StandardCopyOption.REPLACE_EXISTING);
                    }
                }

                if (export.exportReports()) {
                    final Path transcriptionFile =
                            project.getTranscriptionDir().resolve(
                                    txtFile.getFileName());

                    if (Files.isRegularFile(transcriptionFile)) {
                        // generate report
                        final Batch reportBatch = new Batch(
                                transcriptionFile.toFile(), txtFile.toFile());
                        final Parameters pars = new Parameters();
                        pars.eqfile.setValue(equivalencesFile.toFile());
                        Report rep;
                        rep = new Report(reportBatch, pars);

                        // write to file
                        DocumentWriter.writeToFile(
                                rep.document(),
                                export.getDestinationDir().resolve(
                                        FileNames.replaceExtension(
                                                sourceFile, "report.html").getFileName()));

                        // write csv file
                        final BufferedWriter csv = Files.newBufferedWriter(
                                export.getDestinationDir().resolve(
                                        FileNames.replaceExtension(
                                                sourceFile, "report.csv").getFileName()),
                                StandardCharsets.UTF_8);

                        csv.write(rep.getStats().asCSV("\n", ",").toString());
                        csv.close();
                    }
                }
            } finally {
                recognizers.put(recognizer);

                // update progress
                progressMonitor.setProgress(progress.incrementAndGet());
            }
        } catch (Throwable e) {
            errorLog.write(e.getMessage());
            errorLog.write(System.lineSeparator());

            errors.incrementAndGet();
        }

        return null;
    }
}
