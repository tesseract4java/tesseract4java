package de.vorb.tesseract.img;

import java.awt.image.BufferedImage;

public final class BinaryImage {

    private BinaryImage() {}

    /**
     * Require <code>img</code> to be a binary (monochrome) image.
     *
     * @param img
     */
    public static void requireBinary(BufferedImage img) {
        if (img.getType() != BufferedImage.TYPE_BYTE_BINARY)
            throw new IllegalArgumentException("binary image required");
    }

    /**
     * Counts all black pixels in <code>img</code>.
     *
     * @param img
     * @return number of black pixels in <code>img</code>.
     */
    public static int weight(BufferedImage img) {
        // requireBinary(img);

        final int w = img.getWidth(), h = img.getHeight();

        int blackPixels = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (img.getRGB(x, y) == 0xFF_00_00_00) // check for black
                    blackPixels++;
            }
        }

        return blackPixels;
    }
}
