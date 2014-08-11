package de.vorb.tesseract.gui.util;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

import com.google.common.base.Optional;

import de.vorb.tesseract.gui.controller.TesseractController;
import de.vorb.tesseract.gui.model.ImageModel;

public class PreprocessingWorker extends SwingWorker<ImageModel, Void> {
    private final TesseractController controller;
    private final Path sourceFile;
    private final Path destinationDir;

    public PreprocessingWorker(TesseractController controller,
            Path sourceFile, Path destinationDir) {
        this.controller = controller;
        this.sourceFile = sourceFile;
        this.destinationDir = destinationDir;
    }

    @Override
    protected ImageModel doInBackground() throws Exception {
        final BufferedImage sourceImg = ImageIO.read(sourceFile.toFile());

        Files.createDirectories(destinationDir);

        final Path destFile =
                destinationDir.resolve(sourceFile.getFileName());

        final BufferedImage binaryImg;
        if (!controller.hasPreprocessorChanged(sourceFile)
                && Files.exists(destFile)) {
            binaryImg = ImageIO.read(destFile.toFile());
        } else if (sourceImg.getType() == BufferedImage.TYPE_BYTE_BINARY) {
            binaryImg = sourceImg;
        } else {
            binaryImg = controller.getPreprocessor(sourceFile).process(
                    sourceImg);
        }

        return new ImageModel(sourceFile, sourceImg, destFile, binaryImg);
    }

    @Override
    protected void done() {
        try {
            controller.setImageModel(Optional.of(get()));
        } catch (InterruptedException | ExecutionException e) {
        }
    }
}
