package de.vorb.tesseract.tools;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class CannyExample {
    public static enum ColorComponent {
        RED, GREEN, BLUE;
    }

    public static void main(String[] args) throws IOException {
        // final BufferedImage src = ImageIO.read(new File("input2.png"));
        //
        // final CannyEdgeDetector canny = new CannyEdgeDetector();
        // canny.setGaussianKernelRadius(1.5f);
        // canny.setGaussianKernelWidth(4);
        // canny.setSourceImage(src);
        // canny.process();
        //
        // final BufferedImage edges = canny.getEdgesImage();

        final BufferedImage edges = ImageIO.read(new File("edges.png"));

        twoPassConnectedComponentLabeling(edges);
    }

    public static void twoPassConnectedComponentLabeling(BufferedImage src) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        final int size = width * height;

        final int[][] labels = new int[height][width];

        int label = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (isForeground(src, x, y)) {
                    // Do both pixels to the North and West of the current pixel
                    // have the same value as the current pixel but not the same
                    // label?
                    if (isForeground(src, x - 1, y)
                            && isForeground(src, x, y - 1)
                            && labels[y][x - 1] != labels[y - 1][x]) {
                        labels[y][x] = Math.min(labels[y][x - 1],
                                labels[y - 1][x]);
                    }
                    // Does the pixel to the West have the same value as the
                    // current pixel?
                    else if (isForeground(src, x - 1, y)) {
                        labels[y][x] = labels[y][x - 1];
                    }
                    // Does the pixel to the West have a different value and the
                    // one to the North the same value as the current pixel?
                    else if (!isForeground(src, x - 1, y)
                            && isForeground(src, x, y - 1)) {
                        labels[y][x] = labels[y - 1][x];
                    }
                    // Do the pixel's North and West neighbors have different
                    // pixel values than current pixel?
                    else if (!isForeground(src, x - 1, y)
                            && !isForeground(src, x, y - 1)) {
                        labels[y][x] = ++label;
                    }

                    System.out.println("x=" + x + ", y=" + y + ", label="
                            + labels[y][x]);
                }
            }
        }
    }

    private static boolean isForeground(BufferedImage src, int x, int y) {
        if (x < 0 || y < 0)
            return false;

        return src.getRGB(x, y) == 0xFFFFFFFF;
    }

    public static BufferedImage getColorComponent(BufferedImage src,
            ColorComponent comp) {
        final int shift;
        switch (comp) {
            case RED:
                shift = 16;
                break;
            case GREEN:
                shift = 8;
                break;
            default:
                shift = 0;
        }

        final int width = src.getWidth();
        final int height = src.getHeight();

        final BufferedImage dest = new BufferedImage(width, height,
                BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                final int c = (src.getRGB(x, y) >> shift) & 0xFF;
                dest.setRGB(x, y, (c << 16) | (c << 8) | c);
            }
        }

        return dest;
    }
}
