package de.vorb.tesseract.gui.work;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.google.common.base.Optional;

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
    private final OCRTaskCallback callback;

    public OCRTask(Path sourceFile, ProjectModel project,
            BatchExportModel export, Preprocessor preprocessor,
            LinkedBlockingQueue<PageRecognitionProducer> recognizers,
            boolean hasPreprocessorChanged, OCRTaskCallback callback) {
        this.sourceFile = sourceFile;
        this.project = project;
        this.export = export;
        this.preprocessor = preprocessor;
        this.recognizers = recognizers;
        this.hasPreprocessorChanged = hasPreprocessorChanged;
        this.callback = callback;
    }

    @Override
    public Void call() throws IOException, InterruptedException, JAXBException {
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
                    return false;
                }
            });

            final Page page = new Page(sourceFile, width, height, 300, lines);

            if (export.isExportXML()) {
                final Path xmlFile = export.getDestinationDir().resolve(
                        FileNames.replaceExtension(sourceFile.getFileName(),
                                "xml"));

                final JAXBContext context = JAXBContext.newInstance(Page.class);
                final Marshaller marshaller = context.createMarshaller();

                final BufferedWriter writer = Files.newBufferedWriter(xmlFile,
                        StandardCharsets.UTF_8);

                marshaller.marshal(page, writer);

                writer.close();

                callback.taskComplete(Optional.<Exception> absent(),
                        Optional.of(sourceFile));
            }
        } catch (IOException | JAXBException e) {
            callback.taskComplete(Optional.of(e), Optional.<Path> absent());

            // rethrow exception
            throw e;
        } finally {
            recognizers.put(recognizer);
        }

        return null;
    }
}
