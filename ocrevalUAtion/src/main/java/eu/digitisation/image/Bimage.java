/*
 * Copyright (C) 2013 Universidad de Alicante
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
package eu.digitisation.image;

import eu.digitisation.math.Counter;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.IOException;
import java.util.List;
import javax.media.jai.JAI;

/**
 * Extends BufferedImage with some useful operations
 *
 * @author R.C.C.
 */
public class Bimage extends BufferedImage {

    static int defaultImageType = BufferedImage.TYPE_INT_RGB;

    /**
     * Basic constructor
     *
     * @param width
     * @param height
     * @param imageType
     */
    public Bimage(int width, int height, int imageType) {
        super(width, height, imageType);
    }

    /**
     * Basic constructor
     *
     * @param width
     * @param height
     */
    public Bimage(int width, int height) {
        super(width, height, defaultImageType);
    }

    /**
     * Create a BufferedImage from another BufferedImage. Type set to default in
     * case of TYPE_CUSTOM (not handled by BufferedImage) .
     *
     * @param image the source image
     */
    public Bimage(BufferedImage image) {
        super(image.getWidth(null), image.getHeight(null),
                image.getType() == BufferedImage.TYPE_CUSTOM
                ? defaultImageType
                : image.getType());
        Graphics2D g = createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
    }

    /**
     * Create a BufferedImage of the given type from another BufferedImage.
     *
     * @param image the source image
     * @param type the type of BufferedImage
     */
    public Bimage(BufferedImage image, int type) {
        super(image.getWidth(null), image.getHeight(null), type);
        Graphics2D g = createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
    }

    /**
     * Create image from file.
     *
     * @param file the file storing the image
     * @throws IOException
     * @throws NullPointerException if the file format is unsupported
     */
    public Bimage(java.io.File file) throws IOException {
        //            this(javax.imageio.ImageIO.read(file));
        this(JAI.create("FileLoad",
                file.getCanonicalPath()).getAsBufferedImage());
    }

    /**
     * Create a scaled image
     *
     * @param img the source image
     * @param scale the scale factor
     */
    public Bimage(BufferedImage img, double scale) {
        super((int) (scale * img.getWidth()),
                (int) (scale * img.getHeight()),
                img.getType());
        int hints = java.awt.Image.SCALE_SMOOTH; //scaling algorithm
        Image scaled = img.getScaledInstance(this.getWidth(),
                this.getHeight(),
                hints);
        Graphics2D g = createGraphics();
        g.drawImage(scaled, 0, 0, null);
        g.dispose();
    }

    /**
     * Finds the background (statistical mode of the rgb value for pixels in the
     * image)
     *
     * @return the mode of the color for pixels in this image
     */
    private Color background() {
        Counter<Integer> colors = new Counter<Integer>();

        for (int x = 0; x < getWidth(); ++x) {
            for (int y = 0; y < getHeight(); ++y) {
                int rgb = getRGB(x, y);
                colors.inc(rgb);
            }
        }

        Integer mu = colors.maxValue();
        for (Integer n : colors.keySet()) {
            if (colors.get(n).equals(mu)) {
                return new Color(n);
            }
        }
        return null;
    }

    /**
     * Create a scaled image
     *
     * @param scale the scale factor
     * @return a scaled image
     */
    public Bimage scale(double scale) {
        int w = (int) (scale * getWidth());
        int h = (int) (scale * getHeight());
        Bimage scaled = new Bimage(w, h, getType());
        //int hints = java.awt.Image.SCALE_SMOOTH; //scaling algorithm
        //Image img = getScaledInstance(w, h, hints);
        Graphics2D g = scaled.createGraphics();
        AffineTransform at = new AffineTransform();
        at.scale(scale, scale);
        g.drawImage(this, at, null);
        g.dispose();
        return scaled;
    }

    /**
     * Create a rotated image
     *
     * @param alpha the rotation angle (anticlockwise)
     * @return the rotated image
     */
    public Bimage rotate(double alpha) {
        double cos = Math.cos(alpha);
        double sin = Math.abs(Math.sin(alpha));
        int w = (int) Math.floor(getWidth() * cos + getHeight() * sin);
        int h = (int) Math.floor(getHeight() * cos + getWidth() * sin);
        Bimage rotated = new Bimage(w, h, getType());
        Graphics2D g = (Graphics2D) rotated.getGraphics();
        g.setBackground(background());
        g.clearRect(0, 0, w, h);
        if (alpha < 0) {
            g.translate(getHeight() * sin, 0);
        } else {
            g.translate(0, getWidth() * sin);
        }
        g.rotate(-alpha);
        g.drawImage(this, 0, 0, null);
        g.dispose();
        return rotated;

    }

    /**
     * Create a new image from two layers (with the type of first)
     *
     * @param first the first source image
     * @param second the second source image
     */
    public Bimage(BufferedImage first, BufferedImage second) {
        super(Math.max(first.getWidth(), second.getWidth()),
                Math.max(first.getHeight(), second.getHeight()),
                first.getType());
        BufferedImage combined = new BufferedImage(this.getWidth(),
                this.getHeight(),
                this.getType());
        Graphics2D g = combined.createGraphics();
        g.drawImage(first, 0, 0, null);
        g.drawImage(second, 0, 0, null);
        g.dispose();
    }

    /**
     * Transform image to gray-scale
     *
     * @return this image as gray-scale image
     */
    public Bimage toGrayScale() {
        ColorSpace space = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorConvertOp operation = new ColorConvertOp(space, null);
        return new Bimage(operation.filter(this, null));
    }

    /**
     * Transform image to RGB
     *
     * @return this image as RGB image
     */
    public Bimage toRGB() {
        Bimage bim = new Bimage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bim.createGraphics();
        g.drawImage(this, 0, 0, null);
        g.dispose();
        return bim;
    }

    /**
     * Clear the image to white
     */
    public void clear() {
        Graphics2D g = createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.dispose();
    }

    /**
     * Add a polygonal frontier to the image
     *
     * @param p a polygon
     * @param color the color of the polygon
     * @param stroke the line width in pixels
     */
    public void add(Polygon p, Color color, float stroke) {
        Graphics2D g = createGraphics();
        g.setColor(color);
        g.setStroke(new BasicStroke(stroke));
        g.drawPolygon(p);
        g.dispose();
    }

    /**
     * Add a dashed polygonal frontier to the image
     *
     * @param p a polygon
     * @param color the color of the polygon
     * @param stroke the line width in pixels
     * @param pattern the dash pattern, for example, {4f,2f} draws dashes with
     * length 4-units and separated 2 units
     */
    public void add(Polygon p, Color color, float stroke, float[] pattern) {
        Graphics2D g = createGraphics();
        BasicStroke bs = new BasicStroke(stroke, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 1, pattern, 0.0f);
        g.setColor(color);
        g.setStroke(bs);
        g.drawPolygon(p);
        g.dispose();
    }

    /**
     * Add polygonal frontiers to the image
     *
     * @param polygons list of polygonal regions
     * @param color he color of the polygons
     * @param stroke the line width in pixels
     */
    public void add(List<Polygon> polygons, Color color, float stroke) {
        for (Polygon p : polygons) {
            add(p, color, stroke);
        }
    }

    /**
     * Add polygonal frontiers to the image
     *
     * @param polygons an array of polygonal regions
     * @param color he color of the polygons
     * @param stroke the line width in pixels
     * @param pattern the dash pattern, for example, {4f,2f} draws dashes
     * 4-pixels long separated by 2 pixels
     */
    public void add(Polygon[] polygons, Color color, float stroke, float[] pattern) {
        for (Polygon p : polygons) {
            add(p, color, stroke, pattern);
        }
    }

    /**
     * Add polygonal frontiers to the image
     *
     * @param polygons an array of polygonal regions
     * @param color he color of the polygons
     * @param stroke the line width in pixels
     * @param pattern the dash pattern, for example, {4f,2f} draws dashes
     * 4-pixels long separated by 2 pixels
     */
    public void add(List<Polygon> polygons, Color color, float stroke, float[] pattern) {
        for (Polygon p : polygons) {
            add(p, color, stroke, pattern);
        }
    }

    /**
     * Write the image to a file
     *
     * @param file the output file
     * @throws java.io.IOException
     */
    public void write(java.io.File file)
            throws IOException {
        Format format = Format.valueOf(file);
        JAI.create("filestore", this,
                file.getCanonicalPath(), format.toString());
        //javax.imageio.ImageIO.write(this, format, file);
    }
}
