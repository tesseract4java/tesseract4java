package de.vorb.tesseract.gui.work;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;
import javax.swing.ProgressMonitor;
import javax.xml.bind.JAXBException;

import de.vorb.tesseract.gui.model.BatchExportModel;
import de.vorb.tesseract.gui.model.ProjectModel;
import de.vorb.tesseract.tools.preprocessing.Preprocessor;
import de.vorb.tesseract.tools.recognition.PageRecognitionConsumer;
import de.vorb.tesseract.util.Line;
import de.vorb.tesseract.util.Page;
import de.vorb.util.FileNames;

public class OCRTask implements Callable<Void> {
    private final Path sourceFile;
    private final ProjectModel project;
    private final BatchExportModel export;
    private final Preprocessor preprocessor;
    private final LinkedBlockingQueue<PageRecognitionProducer> recognizers;
    private final boolean hasPreprocessorChanged;
    private final ProgressMonitor progressMonitor;
    private final AtomicInteger progress;

    public OCRTask(Path sourceFile, ProjectModel project,
            BatchExportModel export, Preprocessor preprocessor,
            LinkedBlockingQueue<PageRecognitionProducer> recognizers,
            boolean hasPreprocessorChanged, ProgressMonitor progressMonitor,
            AtomicInteger progress) {
        this.sourceFile = sourceFile;
        this.project = project;
        this.export = export;
        this.preprocessor = preprocessor;
        this.recognizers = recognizers;
        this.hasPreprocessorChanged = hasPreprocessorChanged;
        this.progressMonitor = progressMonitor;
        this.progress = progress;
    }

    @Override
    public Void call() throws IOException, InterruptedException, JAXBException {
        if (progressMonitor.isCanceled()) {
            return null;
        }

        progressMonitor.setNote(sourceFile.getFileName().toString());

        final Path imgDestFile = project.getPreprocessedDir().resolve(
                FileNames.replaceExtension(sourceFile.getFileName(), "png"));

        final int width;
        final int height;
        {
            final BufferedImage sourceImg =
                    ImageIO.read(sourceFile.toFile());

            width = sourceImg.getWidth();
            height = sourceImg.getHeight();

            final BufferedImage binaryImg;
            if (!hasPreprocessorChanged
                    && Files.exists(imgDestFile)) {
                // read existing preprocessed image
                binaryImg = ImageIO.read(imgDestFile.toFile());
            } else {
                // preprocess source image
                binaryImg = preprocessor.process(sourceImg);

                ImageIO.write(binaryImg, "PNG", imgDestFile.toFile());
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

            final Vector<Line> lines = new Vector<>();

            recognizer.recognize(new PageRecognitionConsumer(lines) {
                @Override
                public boolean isCancelled() {
                    return Thread.currentThread().isInterrupted();
                }
            });

            final Page page = new Page(sourceFile, width, height, 300, lines);

            if (export.isExportXML()) {
                final Path xmlFile = export.getDestinationDir().resolve(
                        FileNames.replaceExtension(sourceFile.getFileName(),
                                "xml"));

                final BufferedOutputStream out = new BufferedOutputStream(
                        Files.newOutputStream(xmlFile));

                try {
                    page.writeTo(out);
                } catch (JAXBException jbe) {
                    throw jbe;
                } finally {
                    out.close();
                }
            }
        } catch (IOException | JAXBException e) {
            // rethrow exception
            throw e;
        } finally {
            recognizers.put(recognizer);

            // update progress
            progressMonitor.setProgress(progress.incrementAndGet());
        }

        return null;
    }
}
