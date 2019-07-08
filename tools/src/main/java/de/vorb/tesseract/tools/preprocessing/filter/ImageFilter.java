package de.vorb.tesseract.tools.preprocessing.filter;

import java.awt.image.BufferedImage;

/**
 * Image filter.
 *
 * @author Paul Vorbach
 */
public interface ImageFilter {
    /**
     * Filters the image in-situ.
     * <p>
     * Make a copy of the source image if you want to
     *
     * @param image source image
     */
    void filter(BufferedImage image);
}
