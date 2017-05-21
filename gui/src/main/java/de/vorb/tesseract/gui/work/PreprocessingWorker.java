package de.vorb.tesseract.gui.work;

import de.vorb.tesseract.gui.controller.TesseractController;
import de.vorb.tesseract.gui.model.ImageModel;
import de.vorb.tesseract.tools.preprocessing.Preprocessor;
import de.vorb.util.FileNames;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
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

        final BufferedImage sourceImg = readRgbImageFromFile(sourceFile);

        final BufferedImage preprocessedImg = preprocessor.process(sourceImg);

        ImageIO.write(preprocessedImg, "PNG", destFile.toFile());

        return new ImageModel(sourceFile, sourceImg, destFile, preprocessedImg);
    }

    private BufferedImage readRgbImageFromFile(Path imageFile) throws IOException {

        final BufferedImage originalImage = ImageIO.read(imageFile.toFile());

        final BufferedImage rgbImage;
        if (originalImage.getType() != BufferedImage.TYPE_INT_RGB) {
            rgbImage = getImageAsRgb(originalImage);
        } else {
            rgbImage = originalImage;
        }

        return rgbImage;
    }

    private BufferedImage getImageAsRgb(BufferedImage originalImage) {

        final BufferedImage rgbImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        final Graphics2D g2d = rgbImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, null);
        g2d.dispose();

        return rgbImage;
    }

    @Override
    protected void done() {
        try {
            controller.setImageModel(Optional.of(get()));
        } catch (InterruptedException | ExecutionException | CancellationException e) {
        } finally {
            controller.getView().getProgressBar().setIndeterminate(false);
        }
    }
}
