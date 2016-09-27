package de.vorb.tesseract.engine.model;

import com.google.common.base.Objects;

public class Dimension {

    private final int width;
    private final int height;

    private Dimension(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Dimension that = (Dimension) o;

        return width == that.width && height == that.height;
    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        return result;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("width", width)
                .add("height", height)
                .toString();
    }

    public static Dimension of(int width, int height) {
        return new Dimension(width, height);
    }
}
