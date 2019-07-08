package de.vorb.tesseract.tools.preprocessing.conncomp;

import ij.process.ColorProcessor;

import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConnectedComponentLabeler {
    private static final ConnectedComponentFilter ACCEPT_ALL = connComp -> true;

    private static final int MARK = -2;
    private static final int NON_LABEL = -1;

    private final BufferedImage image;
    private final int width;
    private final int height;

    private final ColorProcessor labels;

    private final int foreground;

    public ConnectedComponentLabeler(BufferedImage image, boolean blackOnWhite) {
        if (image.getType() != BufferedImage.TYPE_BYTE_BINARY) {
            throw new IllegalArgumentException("not a binary image");
        }

        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();

        this.labels = new ColorProcessor(width + 2, height + 2);
        this.labels.setColor(NON_LABEL);
        this.labels.fill();

        if (blackOnWhite) {
            foreground = 0xFF000000;
        } else {
            foreground = 0xFFFFFFFF;
        }
    }

    public List<ConnectedComponent> apply(ConnectedComponentFilter filter) {
        final List<ConnectedComponent> connectedComponents = apply();

        return connectedComponents.stream()
                .filter(filter::filter)
                .collect(Collectors.toList());
    }

    /**
     * For more info, see Chang, Chen et al. 2004.
     */
    public List<ConnectedComponent> apply() {
        final ArrayList<ConnectedComponent> connectedComponents = new ArrayList<>();
        int label = NON_LABEL + 1; // current label counter (C in the paper)
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                if (isForeground(x, y)) {
                    // starting point of a new outer contour
                    if (!isForeground(x, y - 1) && !hasLabel(x, y)) {
                        labels.set(x, y, label);

                        final Polygon outer = trace(x, y, label, true);

                        // trace external
                        final ConnectedComponent connComp =
                                new ConnectedComponent(label, outer);
                        connectedComponents.add(connComp);

                        // increase C
                        label++;
                    }

                    // starting point of a new inner contour
                    if (!isForeground(x, y + 1) && !hasMark(x, y + 1)) {
                        if (!hasLabel(x, y)) {
                            // if this pixel doesn't have a label, assign it the
                            // same label like the pixel to the left
                            setLabel(x, y, getLabel(x - 1, y));
                        }

                        final int local = getLabel(x, y);
                        if (local >= 0) {
                            final Polygon contour = trace(x, y, local, false);

                            connectedComponents.get(local).addInnerContour(contour);
                        }
                    }

                    // non-labeled pixel
                    else if (!hasLabel(x, y)) {
                        // label it with previous pixel's label
                        setLabel(x, y, getLabel(x - 1, y));
                    }
                }
            }
        }

        return connectedComponents;
    }

    private Polygon trace(int startX, int startY, int label, boolean isOuter) {
        final Polygon contour = new Polygon();
        contour.addPoint(startX, startY);

        final int[] point = new int[]{startX, startY};

        int pos;
        if (isOuter) {
            pos = -7;
        } else {
            pos = -3;
        }

        pos = traceNext(point, pos);

        // single/isolated point
        if (pos == -1) {
            return contour;
        }

        final int nextX = point[0], nextY = point[1];

        boolean equalsStartPoint;
        do {
            contour.addPoint(point[0], point[1]);
            setLabel(point[0], point[1], label);
            equalsStartPoint =
                    point[0] == startX && point[1] == startY;
            pos = traceNext(point, pos);

        } while (!equalsStartPoint || point[0] != nextX || point[1] != nextY);

        return contour;
    }

    private int traceNext(int[] point, int position) {
        if (position < 0) {
            position = -position;
        } else {
            position = (position + 6) % 8;
        }

        final int start = position;

        int[] nextPoint = new int[2];
        do {
            nextPosition(point, nextPoint, position);

            if (isForeground(nextPoint[0], nextPoint[1])) {
                point[0] = nextPoint[0];
                point[1] = nextPoint[1];
                return position;
            } else {
                setMark(nextPoint[0], nextPoint[1]);
            }

            position = (position + 1) % 8;
        } while (position != start);

        return -1;
    }

    private void nextPosition(int[] point, int[] nextPoint, int position) {
        // indexes of the neighboring points of P = (x, y)
        // as defined by Chang, Chen et al. 2004:
        //
        // | 5 6 7 |
        // | 4 P 0 |
        // | 3 2 1 |
        //

        switch (position) {
            case 0: // right
                nextPoint[0] = point[0] + 1;
                nextPoint[1] = point[1];
                break;
            case 1: // bottom right
                nextPoint[0] = point[0] + 1;
                nextPoint[1] = point[1] + 1;
                break;
            case 2: // bottom
                nextPoint[0] = point[0];
                nextPoint[1] = point[1] + 1;
                break;
            case 3:
                nextPoint[0] = point[0] - 1;
                nextPoint[1] = point[1] + 1;
                break;
            case 4:
                nextPoint[0] = point[0] - 1;
                nextPoint[1] = point[1];
                break;
            case 5:
                nextPoint[0] = point[0] - 1;
                nextPoint[1] = point[1] - 1;
                break;
            case 6:
                nextPoint[0] = point[0];
                nextPoint[1] = point[1] - 1;
                break;
            case 7:
                nextPoint[0] = point[0] + 1;
                nextPoint[1] = point[1] - 1;
                break;
            default:
                throw new IllegalArgumentException("invalid position "
                        + position);
        }
    }

    private int getPixel(int x, int y) {
        return image.getRGB(x, y);
    }

    private boolean isForeground(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }

        return getPixel(x, y) == foreground;
    }

    private void setMark(int x, int y) {
        setLabel(x, y, MARK);
    }

    private boolean hasMark(int x, int y) {
        return getLabel(x, y) == MARK;
    }

    private void setLabel(int x, int y, int value) {
        labels.set(x + 1, y + 1, value);
    }

    private int getLabel(int x, int y) {
        return labels.get(x + 1, y + 1);
    }

    private boolean hasLabel(int x, int y) {
        return getLabel(x, y) != NON_LABEL;
    }
}
