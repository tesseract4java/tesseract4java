package de.vorb.tesseract.engine.model;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class Box {

    private final Offset offset;
    private final Dimension dimension;

    private Box(Offset offset, Dimension dimension) {

        Preconditions.checkNotNull(offset);
        Preconditions.checkNotNull(dimension);

        this.offset = offset;
        this.dimension = dimension;
    }

    public Offset getOffset() {
        return offset;
    }

    public Dimension getDimension() {
        return dimension;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Box box = (Box) o;

        return offset.equals(box.offset) && dimension.equals(box.dimension);
    }

    @Override
    public int hashCode() {
        int result = offset.hashCode();
        result = 31 * result + dimension.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("offset", offset)
                .add("dimension", dimension)
                .toString();
    }

    public static Box of(Offset offset, Dimension dimension) {
        return new Box(offset, dimension);
    }
}
