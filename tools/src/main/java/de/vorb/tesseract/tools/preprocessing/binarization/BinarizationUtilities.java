package de.vorb.tesseract.tools.preprocessing.binarization;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

public final class BinarizationUtilities {

    private BinarizationUtilities() {}

    private static final ColorConvertOp RGB_TO_GRAYSCALE = new ColorConvertOp(
            ColorSpace.getInstance(ColorSpace.CS_sRGB),
            ColorSpace.getInstance(ColorSpace.CS_GRAY), null);

    public static BufferedImage imageToGrayscale(BufferedImage image) {
        final BufferedImage grayscale;

        // return the buffered image as-is, if it is binary already
        if (image.getType() == BufferedImage.TYPE_BYTE_BINARY) {
            return image;
        } else if (image.getType() == BufferedImage.TYPE_BYTE_GRAY) {
            grayscale = image;
        } else if (image.getType() == BufferedImage.TYPE_INT_RGB
                || image.getType() == BufferedImage.TYPE_BYTE_INDEXED) {
            grayscale = new BufferedImage(image.getWidth(), image.getHeight(),
                    BufferedImage.TYPE_BYTE_GRAY);

            // convert rgb image to grayscale
            RGB_TO_GRAYSCALE.filter(image, grayscale);
        } else {
            throw new IllegalArgumentException(String.format(
                    "illegal color space: %s",
                    image.getColorModel().getColorSpace().getType()));
        }

        return grayscale;
    }
}
