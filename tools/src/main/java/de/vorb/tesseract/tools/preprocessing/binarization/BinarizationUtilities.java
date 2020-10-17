package de.vorb.tesseract.tools.preprocessing.binarization;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

public final class BinarizationUtilities {

    private BinarizationUtilities() {
    }

    private static final ColorConvertOp RGB_TO_GRAYSCALE = new ColorConvertOp(
            ColorSpace.getInstance(ColorSpace.CS_sRGB),
            ColorSpace.getInstance(ColorSpace.CS_GRAY), null);

    static BufferedImage imageToGrayscale(BufferedImage image) {
        final BufferedImage grayscale;

        switch (image.getType()) {
            case BufferedImage.TYPE_BYTE_BINARY:
                return image;
            case BufferedImage.TYPE_BYTE_GRAY:
                grayscale = image;
                break;
            case BufferedImage.TYPE_INT_RGB:
            case BufferedImage.TYPE_BYTE_INDEXED:
            case BufferedImage.TYPE_3BYTE_BGR:
            case BufferedImage.TYPE_4BYTE_ABGR:
                grayscale = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
                RGB_TO_GRAYSCALE.filter(image, grayscale);
                break;
            default:
                throw new IllegalArgumentException(
                        "illegal color space: " + image.getColorModel().getColorSpace().getType());
        }

        return grayscale;
    }
}
