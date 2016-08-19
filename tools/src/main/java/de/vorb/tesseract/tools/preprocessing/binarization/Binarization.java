package de.vorb.tesseract.tools.preprocessing.binarization;

import java.awt.image.BufferedImage;

/**
 * Binarization algorithm.
 *
 * @author Paul Vorbach
 */
public interface Binarization {
    /**
     * Binarize an image.
     *
     * @param image input image
     * @return binary image
     */
    BufferedImage binarize(BufferedImage image);
}
