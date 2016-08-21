package de.vorb.tesseract.tools.preprocessing.binarization;

import java.awt.image.BufferedImage;

public class Otsu implements Binarization {
    public Otsu() { // Otsu doesn't take parameters
    }

    @Override
    public BufferedImage binarize(BufferedImage image) {
        final int width = image.getWidth();
        final int height = image.getHeight();

        final BufferedImage grayscale =
                BinarizationUtilities.imageToGrayscale(image);

        final int[] histogram = getHistogram(grayscale);
        final int threshold = getOtsuThreshold(histogram, width, height);

        final BufferedImage result = new BufferedImage(width, height,
                BufferedImage.TYPE_BYTE_BINARY);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final int value = grayscale.getRGB(x, y) & 0xFF;

                if (value > threshold) {
                    result.setRGB(x, y, 0xFFFFFFFF);
                } else {
                    result.setRGB(x, y, 0xFF000000);
                }
            }
        }

        return result;
    }

    private static int[] getHistogram(BufferedImage grayscale) {
        final int width = grayscale.getWidth();
        final int height = grayscale.getHeight();

        final int[] histogram = new int[256];

        for (int i = 0; i < histogram.length; i++)
            histogram[i] = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final int value = grayscale.getRGB(x, y) & 0xFF;
                histogram[value]++;
            }
        }

        return histogram;
    }

    private static int getOtsuThreshold(int[] histogram, int width, int height) {
        final int total = width * height;

        float sum = 0;
        for (int i = 0; i < 256; i++) {
            sum += i * histogram[i];
        }

        float sumB = 0;
        int wB = 0;
        int wF;

        float varMax = 0;
        int threshold = 0;

        for (int i = 0; i < 256; i++) {
            wB += histogram[i];
            if (wB == 0) {
                continue;
            }

            wF = total - wB;

            if (wF == 0)
                break;

            sumB += (float) (i * histogram[i]);
            float mB = sumB / wB;
            float mF = (sum - sumB) / wF;

            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

            if (varBetween > varMax) {
                varMax = varBetween;
                threshold = i;
            }
        }

        return threshold;
    }
}
