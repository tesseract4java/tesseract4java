package de.vorb.tesseract.util;

import java.awt.Rectangle;

public class Box {
    private int x;
    private int y;
    private int width;
    private int height;

    public Box(int x, int y, int width, int height) {
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        if (x < 0) {
            throw new IllegalArgumentException("negative coordinate x");
        }

        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        if (y < 0) {
            throw new IllegalArgumentException("negative coordinate y");
        }

        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        if (width < 0) {
            throw new IllegalArgumentException("negative width");
        }

        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        if (height < 0) {
            throw new IllegalArgumentException("negative height");
        }

        this.height = height;
    }

    public int getArea() {
        return width * height;
    }

    public Rectangle toRectangle() {
        return new Rectangle(x, y, width, height);
    }

    public boolean contains(Point point) {
        final int px = point.getX(), py = point.getY();
        return !(px < x || px > x + width || py < y || py > y + height);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + height;
        result = prime * result + width;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof Box)) {
            return false;
        }
        Box other = (Box) obj;
        if (height != other.height) {
            return false;
        } else if (width != other.width) {
            return false;
        } else if (x != other.x) {
            return false;
        }
        return y == other.y;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("Box(x = %d, y = %d, width = %d, height = %d)", x, y, width, height);
    }
}
