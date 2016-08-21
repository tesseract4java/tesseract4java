package de.vorb.tesseract.tools.preprocessing.conncomp;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ConnectedComponent {
    private final int label;
    private final Polygon outerContour;
    private final List<Polygon> innerContours;

    private int area = -1;

    public ConnectedComponent(int label, Polygon outerContour) {
        this.label = label;
        this.outerContour = outerContour;
        this.innerContours = new LinkedList<>();
    }

    public void addInnerContour(Polygon innerContour) {
        this.innerContours.add(innerContour);
    }

    public int getLabel() {
        return label;
    }

    public Polygon getOuterContour() {
        return outerContour;
    }

    public List<Polygon> getInnerContours() {
        return Collections.unmodifiableList(innerContours);
    }

    public int getArea() {
        if (area == -1) {
            area = areaOfContour(outerContour);
            for (final Polygon contour : innerContours) {
                area -= areaOfContour(contour);
                area += contour.npoints;
            }
        }

        return area;
    }

    /**
     * Count the number of pixels that are part of a contour
     *
     * @param contour
     * @return area of contour
     */
    private static int areaOfContour(Polygon contour) {
        final int minY = (int) contour.getBounds().getMinY();
        final int height = (int) contour.getBounds().getHeight() + 1;

        final int[] left = new int[height];
        final int[] right = new int[height];

        Arrays.fill(left, Integer.MAX_VALUE);

        for (int i = 0; i < contour.npoints; i++) {
            final int y = contour.ypoints[i] - minY;
            final int x = contour.xpoints[i];
            left[y] = Math.min(left[y], x);
            right[y] = Math.max(right[y], x);
        }

        int sum = 0;
        for (int y = 0; y < height; y++) {
            sum += right[y] - left[y] + 1;
        }

        return sum;
    }

    public void drawOn(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.drawPolygon(outerContour);
        g2d.fillPolygon(outerContour);
        for (int i = 0; i < outerContour.npoints; i++) {
            g2d.drawLine(outerContour.xpoints[i], outerContour.ypoints[i],
                    outerContour.xpoints[i], outerContour.ypoints[i]);
        }

        for (final Polygon inner : innerContours) {
            g2d.setColor(Color.WHITE);
            g2d.fillPolygon(inner);
            g2d.setColor(Color.BLACK);
            g2d.drawPolygon(inner);
        }
    }
}
