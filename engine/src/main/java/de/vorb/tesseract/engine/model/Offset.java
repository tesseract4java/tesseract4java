package de.vorb.tesseract.engine.model;

import com.google.common.base.Objects;

public class Offset {

    private final int left;
    private final int top;

    private Offset(int left, int top) {
        this.left = left;
        this.top = top;
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Offset that = (Offset) o;

        return left == that.left && top == that.top;
    }

    @Override
    public int hashCode() {
        int result = left;
        result = 31 * result + top;
        return result;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("left", left)
                .add("top", top)
                .toString();
    }

    public static Offset of(int left, int top) {
        return new Offset(left, top);
    }
}
