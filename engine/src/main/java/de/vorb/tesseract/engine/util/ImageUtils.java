package de.vorb.tesseract.engine.util;

import de.vorb.tesseract.engine.model.Box;

import java.awt.image.BufferedImage;

public final class ImageHelper {

    private ImageHelper() {}

    public static BufferedImage getSubImageForBoundingBox(BufferedImage source, Box boundingBox) {
        return source.getSubimage(
                boundingBox.getOffset().getLeft(),
                boundingBox.getOffset().getTop(),
                boundingBox.getDimension().getWidth(),
                boundingBox.getDimension().getHeight());
    }

}
