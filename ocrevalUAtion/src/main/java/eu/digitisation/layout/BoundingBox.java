/*
 * Copyright (C) 2014 Universidad de Alicante
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package eu.digitisation.layout;

import java.awt.Polygon;
import java.awt.Rectangle;

/**
 * Bounding box = coordinates of the rectangular border that fully encloses the
 * digital image
 *
 * @author R.C.C.
 */
public class BoundingBox extends Rectangle {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an empty BoundingBox, that is a rectangle with coordinates
     * (x0, y0, x1, y1) = (0, 0, 0, 0)
     */
    public BoundingBox() {
        super();
    }

    /**
     * Create a bounding box with the specified corners
     *
     * @param x0 upper left corner x-coordinate
     * @param y0 upper-left corner y-coordinate
     * @param x1 lower-right corner x-coordinate
     * @param y1 lower-right corner y-coordinate
     */
    public BoundingBox(int x0, int y0, int x1, int y1) {
        super(x0, y0, x1 - x0, y1 - y0);
    }

    /**
     * Build a bounding box for a polygon
     *
     * @param polygon
     */
    public BoundingBox(Polygon polygon) {
        super(polygon.getBounds());
    }

    /**
     * The bounding box a s a polygon
     *
     * @return
     */
    public Polygon asPolygon() {
        Polygon polygon = new Polygon();
        polygon.addPoint(x, y);
        polygon.addPoint(x, y + height);
        polygon.addPoint(x + width, y + height);
        polygon.addPoint(x + width, y);
        return polygon;
    }
}
