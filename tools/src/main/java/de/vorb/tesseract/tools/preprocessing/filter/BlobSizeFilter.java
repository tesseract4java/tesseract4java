package de.vorb.tesseract.tools.preprocessing.filter;

import de.vorb.tesseract.tools.preprocessing.conncomp.ConnectedComponent;
import de.vorb.tesseract.tools.preprocessing.conncomp.ConnectedComponentLabeler;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

public class BlobSizeFilter implements ImageFilter {
    private final int minArea;
    private final int maxArea;

    public BlobSizeFilter(int minArea, int maxArea) {
        this.minArea = minArea;
        if (maxArea == 0) {
            this.maxArea = Integer.MAX_VALUE;
        } else {
            this.maxArea = maxArea;
        }
    }

    public int getMinArea() {
        return minArea;
    }

    public int getMaxArea() {
        return maxArea == Integer.MAX_VALUE ? 0 : maxArea;
    }

    @Override
    public void filter(BufferedImage image) {
        if (image.getType() != BufferedImage.TYPE_BYTE_BINARY) {
            throw new IllegalArgumentException("not a binary image");
        }

        final ConnectedComponentLabeler labeler =
                new ConnectedComponentLabeler(image, true);
        final List<ConnectedComponent> connectedComponents =
                labeler.apply();

        // clear the input image
        final Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

        g2d.setColor(Color.BLACK);
        connectedComponents.stream()
                .filter(connComp -> connComp.getArea() <= maxArea && connComp.getArea() >= minArea)
                .forEach(connComp -> connComp.drawOn(g2d));

        g2d.dispose();
    }
}
