package de.vorb.tesseract.gui.work;

import de.vorb.tesseract.gui.controller.TesseractController;
import de.vorb.tesseract.gui.model.ImageModel;
import de.vorb.tesseract.tools.preprocessing.Preprocessor;
import de.vorb.util.FileNames;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public class PreprocessingWorker extends SwingWorker<ImageModel, Void> {
    private final TesseractController controller;
    private final Preprocessor preprocessor;
    private final Path sourceFile;
    private final Path destinationDir;

    public PreprocessingWorker(TesseractController controller,
            Preprocessor preprocessor, Path sourceFile, Path destinationDir) {
        this.controller = controller;
        this.preprocessor = preprocessor;
        this.sourceFile = sourceFile;
        this.destinationDir = destinationDir;
    }

    @Override
    protected ImageModel doInBackground() throws Exception {
        Files.createDirectories(destinationDir);

        final Path destFile = destinationDir.resolve(FileNames.replaceExtension(
                sourceFile, "png").getFileName());

        final BufferedImage sourceImg = ImageIO.read(sourceFile.toFile());

        final BufferedImage preprocessedImg = preprocessor.process(sourceImg);
        ImageIO.write(preprocessedImg, "PNG", destFile.toFile());

        return new ImageModel(sourceFile, sourceImg, destFile, preprocessedImg);
    }

    @Override
    protected void done() {
        try {
            controller.setImageModel(Optional.of(get()));
        } catch (InterruptedException | ExecutionException
                | CancellationException e) {
        } finally {
            controller.getView().getProgressBar().setIndeterminate(false);
        }
    }
}
