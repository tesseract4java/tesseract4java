package de.vorb.tesseract.img;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.math.BigInteger;

public class BinaryImage {
    public static void requireBinary(BufferedImage img) {
        if (img.getType() != BufferedImage.TYPE_BYTE_BINARY)
            throw new IllegalArgumentException("binary image required");
    }

    public static int weight(BufferedImage img) {
        requireBinary(img);

        final byte[] bytes =
                ((DataBufferByte) img.getData().getDataBuffer()).getData();

        return new BigInteger(bytes).bitCount();
    }
}
