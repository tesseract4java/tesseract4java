package de.vorb.tesseract.tools.preprocessing;

import ij.IJ;
import ij.ImagePlus;
import ij.blob.Blob;
import ij.blob.ManyBlobs;
import ij.process.ImageProcessor;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class BlobSizeFilter implements ImageFilter {
    private final int minArea;
    private final int maxArea;

    public BlobSizeFilter(int minArea, int maxArea) {
        this.minArea = minArea;
        this.maxArea = maxArea;
    }

    @Override
    public BufferedImage filter(BufferedImage image) {
        final int w = image.getWidth(), h = image.getHeight();

        // if the image is not single channel, create a new one
        if (image.getType() != BufferedImage.TYPE_BYTE_GRAY) {
            final BufferedImage singleChannel = new BufferedImage(w, h,
                    BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D g2d = singleChannel.createGraphics();
            g2d.drawImage(image, 0, 0, null);
            g2d.dispose();

            image.flush();
            image = singleChannel;
        }

        final ImagePlus source = new ImagePlus("noisy", image);
        final ImagePlus dest = IJ.createImage("result", "white", w, h, 1);

        final ManyBlobs blobs = new ManyBlobs(source);
        blobs.findConnectedComponents();

        final ManyBlobs filtered = blobs.filterBlobs(minArea, maxArea,
                Blob.GETENCLOSEDAREA);

        final ImageProcessor context = dest.getProcessor();
        for (final Blob blob : filtered) {
            blob.draw(context);
        }

        final BufferedImage result = new BufferedImage(w, h,
                BufferedImage.TYPE_BYTE_BINARY);
        final Graphics2D g2d = result.createGraphics();
        g2d.drawImage(dest.getBufferedImage(), 0, 0, w, h, null);
        g2d.dispose();

        return result;
    }
}
