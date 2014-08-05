package de.vorb.tesseract.tools.preprocessing;

import java.awt.image.BufferedImage;

/**
 * Image filter.
 * 
 * @author Paul Vorbach
 */
public interface ImageFilter {
    /**
     * Filters the image.
     * 
     * @param image
     *            incoming image
     * @return resulting image
     */
    BufferedImage filter(BufferedImage image);
}
