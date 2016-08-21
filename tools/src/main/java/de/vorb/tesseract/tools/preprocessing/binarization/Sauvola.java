package de.vorb.tesseract.tools.preprocessing.binarization;

import ij.plugin.filter.RankFilters;
import ij.process.FloatProcessor;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Sauvola's method.
 *
 * @author Paul Vorbach
 */
public class Sauvola implements Binarization {
    private static final int FOREGROUND = 0xFFFFFFFF;
    private static final int BACKGROUND = 0xFF000000;

    private final int radius;
    private final float k;
    private final float R;

    /**
     * Creates a new binarization configuration for binarizing image's with
     * Sauvola's method.
     *
     * @param radius
     * @param k
     * @param R
     * @see <a
     * href="http://www.ee.oulu.fi/research/mvmp/mvg/files/pdf/pdf_24.pdf">Sauvola
     * et al. 2000 - Adaptive document image binarization</a>
     */
    public Sauvola(int radius, float k, float R) {
        this.radius = radius;
        this.k = k;
        this.R = R;
    }

    public Sauvola(int radius) {
        this(radius, 0.5F, 128F);
    }

    public Sauvola() {
        this(15);
    }

    public int getRadius() {
        return radius;
    }

    @Override
    public BufferedImage binarize(BufferedImage image) {
        final int width = image.getWidth();
        final int height = image.getHeight();

        final BufferedImage grayscale =
                BinarizationUtilities.imageToGrayscale(image);

        final BufferedImage result = new BufferedImage(width, height,
                BufferedImage.TYPE_BYTE_BINARY);

        final FloatProcessor mean = new FloatProcessor(width, height);
        final FloatProcessor var = (FloatProcessor) mean.duplicate();

        final byte[] pxs = ((DataBufferByte) grayscale.getRaster()
                .getDataBuffer()).getData();
        final float[] meanPxs = (float[]) mean.getPixels();
        final float[] varPxs = (float[]) var.getPixels();

        // fill
        for (int i = 0; i < pxs.length; i++) {
            meanPxs[i] = pxs[i] & 0xFF;
            varPxs[i] = pxs[i] & 0xFF;
        }

        // pre-calculate mean and variance/std deviation
        final RankFilters rankFilters = new RankFilters();
        rankFilters.rank(mean, radius, RankFilters.MEAN);
        rankFilters.rank(var, radius, RankFilters.VARIANCE);

        // binarization
        for (int y = 0, offset = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final int i = offset + x;
                if ((pxs[i] & 0xFF) > meanPxs[i]
                        * (1.0 + k * ((Math.sqrt(varPxs[i]) / R) - 1.0))) {
                    result.setRGB(x, y, FOREGROUND);
                } else {
                    result.setRGB(x, y, BACKGROUND);
                }
            }
            offset += width;
        }

        return result;
    }
}
