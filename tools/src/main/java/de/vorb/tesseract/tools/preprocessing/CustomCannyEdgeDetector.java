package de.vorb.tesseract.tools.preprocessing;

import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Canny edge detector.
 * <p>
 * <em>This class is based on the implementation by Tom Gibara, which has been
 * released into the public domain. <strong>Please read the notes in this source
 * file for additional information.</strong></em>
 * <p>
 * This class provides a configurable implementation of the Canny edge detection
 * algorithm. <em>This class is designed for single threaded use only.</em>
 * <p>
 * <p>
 * Sample usage:
 * </p>
 * <p>
 * <pre>
 * <code>
 * //create the detector
 * CannyEdgeDetector detector = new CannyEdgeDetector();
 * //adjust its parameters as desired
 * detector.setLowThreshold(0.5f);
 * detector.setHighThreshold(1f);
 * //apply it to an image
 * detector.setSourceImage(frame);
 * detector.process();
 * BufferedImage edges = detector.getEdgesImage();
 * </code>
 * </pre>
 *
 * @author Tom Gibara
 * @author Paul Vorbach
 */
public class CustomCannyEdgeDetector {
    // constants
    public final static float GAUSSIAN_CUTOFF = 0.005f;
    public final static float MAGNITUDE_SCALE = 100f;
    public final static float MAGNITUDE_LIMIT = 1000f;
    public final static int MAGNITUDE_MAX = (int) (MAGNITUDE_SCALE * MAGNITUDE_LIMIT);

    // configuration fields
    private final float lowThreshold;
    private final float highThreshold;
    private final float kernelRadius;
    private final int kernelWidth;
    private final boolean contrastNormalized;

    /**
     * Constructor using the default values.
     */
    public CustomCannyEdgeDetector() {
        lowThreshold = 2.5f;
        highThreshold = 7.5f;
        kernelRadius = 2f;
        kernelWidth = 16;
        contrastNormalized = false;
    }

    /**
     * Use custom settings.
     *
     * @param lowThreshold         must not be negative
     * @param highThreshold        must not be negative
     * @param gaussianKernelRadius must be >= 0.1
     * @param gaussianKernelWidth  must be >= 2
     * @param contrastNormalized
     */
    public CustomCannyEdgeDetector(
            float lowThreshold,
            float highThreshold,
            float gaussianKernelRadius,
            int gaussianKernelWidth,
            boolean contrastNormalized) {
        if (lowThreshold < 0)
            throw new IllegalArgumentException("low threshold < 0");
        else
            this.lowThreshold = lowThreshold;

        if (highThreshold < 0)
            throw new IllegalArgumentException("high threshold < 0");
        else
            this.highThreshold = highThreshold;

        if (gaussianKernelRadius < 0.1f)
            throw new IllegalArgumentException("kernel radius < 0.1");
        else
            this.kernelRadius = gaussianKernelRadius;

        if (gaussianKernelWidth < 2)
            throw new IllegalArgumentException("kernel width < 2");
        else
            this.kernelWidth = gaussianKernelWidth;

        this.contrastNormalized = contrastNormalized;
    }

    /**
     * Detects the edges using Canny edge detection.
     *
     * @param src input image - either an (A)RGB or grayscale image
     * @return binary image with black edges on white
     */
    public BufferedImage detectEdges(BufferedImage src) {
        /*
         * Preparations.
         */
        final int width = src.getWidth();
        final int height = src.getHeight();

        // number of pixels in the image
        final int size = width * height;

        // prepare resulting image
        final BufferedImage out = new BufferedImage(width, height,
                BufferedImage.TYPE_BYTE_BINARY);

        // initialize the temporary arrays
        final int[] magnitude = new int[size];

        final float[] xConv = new float[size];
        final float[] yConv = new float[size];

        final float[] xGradient = new float[size];
        final float[] yGradient = new float[size];

        /*
         * Get the image data as a byte array.
         */
        byte[] data;
        final int imageType = src.getType();
        switch (imageType) {

            case BufferedImage.TYPE_INT_RGB:
            case BufferedImage.TYPE_INT_ARGB:
                // retrieve the image data as an int[]
                final int[] rgb = (int[]) src.getData().getDataElements(0, 0,
                        width, height, null);

                data = new byte[size];
                // get the luminance for every pixel
                for (int i = 0; i < size; i++) {
                    data[i] = rgbToLuminance(rgb[i]);
                }
                break;

            case BufferedImage.TYPE_BYTE_GRAY:
                data = (byte[]) src.getData().getDataElements(0, 0, width,
                        height, null);
                break;

            default:
                throw new IllegalArgumentException("unsupported image type");
        }

        // TODO
        // if (contrastNormalized) {
        // normalizeContrast();
        // }

        /*
         * compute gradients
         */

        // generate the gaussian convolution masks
        final float[] kernel = new float[kernelWidth];
        final float[] diffKernel = new float[kernelWidth];
        int k = 0;
        for (; k < kernelWidth; k++) {
            final float g1 = gaussian(k, kernelRadius);

            if (g1 <= GAUSSIAN_CUTOFF && k >= 2)
                break;

            final float g2 = gaussian(k - 0.5f, kernelRadius);
            final float g3 = gaussian(k + 0.5f, kernelRadius);

            kernel[k] = (g1 + g2 + g3) / 3f
                    / (2f * (float) Math.PI * kernelRadius * kernelRadius);
            diffKernel[k] = g3 - g2;
        }

        int initX = k - 1;
        int maxX = width - (k - 1);
        int initY = width * (k - 1);
        int maxY = width * (height - (k - 1));

        // perform convolution in x and y directions
        for (int x = initX; x < maxX; x++) {
            for (int y = initY; y < maxY; y += width) {
                int index = x + y;
                float sumX = data[index] * kernel[0];
                float sumY = sumX;
                int xOffset = 1;
                int yOffset = width;

                for (; xOffset < k; ) {
                    sumY += kernel[xOffset]
                            * (data[index - yOffset] + data[index + yOffset]);
                    sumX += kernel[xOffset]
                            * (data[index - xOffset] + data[index + xOffset]);
                    yOffset += width;
                    xOffset++;
                }

                yConv[index] = sumY;
                xConv[index] = sumX;
            }
        }

        for (int x = initX; x < maxX; x++) {
            for (int y = initY; y < maxY; y += width) {
                float sum = 0f;
                int index = x + y;
                for (int i = 1; i < k; i++)
                    sum += diffKernel[i]
                            * (yConv[index - i] - yConv[index + i]);

                xGradient[index] = sum;
            }

        }

        for (int x = k; x < width - k; x++) {
            for (int y = initY; y < maxY; y += width) {
                float sum = 0.0f;
                int index = x + y;
                int yOffset = width;
                for (int i = 1; i < k; i++) {
                    sum += diffKernel[i]
                            * (xConv[index - yOffset] - xConv[index + yOffset]);
                    yOffset += width;
                }

                yGradient[index] = sum;
            }

        }

        initX = k;
        maxX = width - k;
        initY = width * k;
        maxY = width * (height - k);
        for (int x = initX; x < maxX; x++) {
            for (int y = initY; y < maxY; y += width) {
                int index = x + y;
                int indexN = index - width;
                int indexS = index + width;
                int indexW = index - 1;
                int indexE = index + 1;
                int indexNW = indexN - 1;
                int indexNE = indexN + 1;
                int indexSW = indexS - 1;
                int indexSE = indexS + 1;

                float xGrad = xGradient[index];
                float yGrad = yGradient[index];
                float gradMag = hypot(xGrad, yGrad);

                // perform non-maximal supression
                float nMag = hypot(xGradient[indexN], yGradient[indexN]);
                float sMag = hypot(xGradient[indexS], yGradient[indexS]);
                float wMag = hypot(xGradient[indexW], yGradient[indexW]);
                float eMag = hypot(xGradient[indexE], yGradient[indexE]);
                float neMag = hypot(xGradient[indexNE], yGradient[indexNE]);
                float seMag = hypot(xGradient[indexSE], yGradient[indexSE]);
                float swMag = hypot(xGradient[indexSW], yGradient[indexSW]);
                float nwMag = hypot(xGradient[indexNW], yGradient[indexNW]);
                float tmp;
                /*
                 * An explanation of what's happening here, for those who want
                 * to understand the source: This performs the "non-maximal
                 * supression" phase of the Canny edge detection in which we
                 * need to compare the gradient magnitude to that in the
                 * direction of the gradient; only if the value is a local
                 * maximum do we consider the point as an edge candidate.
                 * 
                 * We need to break the comparison into a number of different
                 * cases depending on the gradient direction so that the
                 * appropriate values can be used. To avoid computing the
                 * gradient direction, we use two simple comparisons: first we
                 * check that the partial derivatives have the same sign (1) and
                 * then we check which is larger (2). As a consequence, we have
                 * reduced the problem to one of four identical cases that each
                 * test the central gradient magnitude against the values at two
                 * points with 'identical support'; what this means is that the
                 * geometry required to accurately interpolate the magnitude of
                 * gradient function at those points has an identical geometry
                 * (upto right-angled-rotation/reflection).
                 * 
                 * When comparing the central gradient to the two interpolated
                 * values, we avoid performing any divisions by multiplying both
                 * sides of each inequality by the greater of the two partial
                 * derivatives. The common comparand is stored in a temporary
                 * variable (3) and reused in the mirror case (4).
                 */
                if (xGrad * yGrad <= (float) 0 /* (1) */
                        ? Math.abs(xGrad) >= Math.abs(yGrad) /* (2) */
                        ? (tmp = Math.abs(xGrad * gradMag)) >= Math.abs(yGrad
                        * neMag - (xGrad + yGrad) * eMag) /* (3) */
                        && tmp > Math.abs(yGrad * swMag
                        - (xGrad + yGrad) * wMag) /* (4) */
                        : (tmp = Math.abs(yGrad * gradMag)) >= Math.abs(xGrad
                        * neMag - (yGrad + xGrad) * nMag) /* (3) */
                        && tmp > Math.abs(xGrad * swMag
                        - (yGrad + xGrad) * sMag) /* (4) */
                        : Math.abs(xGrad) >= Math.abs(yGrad) /* (2) */
                        ? (tmp = Math.abs(xGrad * gradMag)) >= Math.abs(yGrad
                        * seMag + (xGrad - yGrad) * eMag) /* (3) */
                        && tmp > Math.abs(yGrad * nwMag
                        + (xGrad - yGrad) * wMag) /* (4) */
                        : (tmp = Math.abs(yGrad * gradMag)) >= Math.abs(xGrad
                        * seMag + (yGrad - xGrad) * sMag) /* (3) */
                        && tmp > Math.abs(xGrad * nwMag
                        + (yGrad - xGrad) * nMag) /* (4) */
                        ) {
                    magnitude[index] = gradMag >= MAGNITUDE_LIMIT ? MAGNITUDE_MAX
                            : (int) (MAGNITUDE_SCALE * gradMag);
                    // NOTE: The orientation of the edge is not employed by this
                    // implementation. It is a simple matter to compute it at
                    // this point as: Math.atan2(yGrad, xGrad);
                } else {
                    magnitude[index] = 0;
                }
            }
        }

        final int low = Math.round(lowThreshold * MAGNITUDE_SCALE);
        final int high = Math.round(lowThreshold * MAGNITUDE_SCALE);

        Arrays.fill(data, (byte) 0);

        int offset = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (data[offset] == 0 && magnitude[offset] >= high) {
                    follow(x, y, offset, low);
                }
                offset++;
            }
        }

        return out;
    }

    private void follow(int x1, int y1, int i1, int threshold) {
        // int x0 = x1 == 0 ? x1 : x1 - 1;
        // int x2 = x1 == width - 1 ? x1 : x1 + 1;
        // int y0 = y1 == 0 ? y1 : y1 - 1;
        // int y2 = y1 == height - 1 ? y1 : y1 + 1;
        //
        // data[i1] = magnitude[i1];
        // for (int x = x0; x <= x2; x++) {
        // for (int y = y0; y <= y2; y++) {
        // int i2 = x + y * width;
        // if ((y != y1 || x != x1)
        // && data[i2] == 0
        // && magnitude[i2] >= threshold) {
        // follow(x, y, i2, threshold);
        // return;
        // }
        // }
        // }
    }

    private byte rgbToLuminance(int rgb) {
        final short r = (short) ((rgb & 0xFF0000) >> 16);
        final short g = (short) ((rgb & 0x00FF00) >> 8);
        final short b = (short) ((rgb & 0x0000FF) >> 0);
        return (byte) (Math.round(0.299f * r + 0.587f * g + 0.114f * b) & 0xFF);
    }

    private float gaussian(float x, float sigma) {
        return (float) Math.exp(-(x * x) / (2f * sigma * sigma));
    }

    private float hypot(float x, float y) {
        return (float) Math.hypot(x, y);
    }

    /**
     * Byte comparison that takes bytes as if they were unsigned.
     *
     * @param a
     * @param b
     * @return
     */
    private boolean unsignedByteLT(byte a, byte b) {
        if (a >= 0 && b >= 0) {
            return a < b;
        } else if (a < 0 && b >= 0) {
            return true;
        } else if (a >= 0 && b < 0) {
            return false;
        } else {
            return b < a;
        }
    }
}
