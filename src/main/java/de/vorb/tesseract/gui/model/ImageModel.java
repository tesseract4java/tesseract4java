package de.vorb.tesseract.gui.model;

import java.awt.image.BufferedImage;
import java.nio.file.Path;

public class ImageModel {
    private final Path sourceFile;
    private final BufferedImage sourceImage;

    private final Path preprocessedFile;
    private final BufferedImage preprocessedImage;

    public ImageModel(Path sourceFile, BufferedImage sourceImage,
            Path preprocessedFile, BufferedImage preprocessedImage) {
        this.sourceFile = sourceFile;
        this.sourceImage = sourceImage;
        this.preprocessedFile = preprocessedFile;
        this.preprocessedImage = preprocessedImage;
    }

    public Path getSourceFile() {
        return sourceFile;
    }

    public BufferedImage getSourceImage() {
        return sourceImage;
    }

    public Path getPreprocessedFile() {
        return preprocessedFile;
    }

    public BufferedImage getPreprocessedImage() {
        return preprocessedImage;
    }
}
