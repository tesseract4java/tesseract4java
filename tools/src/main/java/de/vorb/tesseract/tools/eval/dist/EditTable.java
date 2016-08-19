package de.vorb.tesseract.tools.eval.dist;

import java.util.BitSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class EditTable implements Iterable<EditOperation> {
    private final int width;
    private final int height;
    private final BitSet data;

    public EditTable(int width, int height) {
        if (width < 0)
            throw new IllegalArgumentException("width < 0");
        if (height < 0)
            throw new IllegalArgumentException("height < 0");

        this.width = width;
        this.height = height;
        data = new BitSet(2 * width * height);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public EditOperation get(int x, int y) {
        if (x >= width || y >= height) {
            throw new NoSuchElementException();
        }

        final int addr = 2 * (x + y * width);
        final boolean high = data.get(addr);
        final boolean low = data.get(addr + 1);

        if (!high && !low) {
            return EditOperation.KEEP;
        } else if (!high && low) {
            return EditOperation.SUBSTITUTE;
        } else if (high && !low) {
            return EditOperation.INSERT;
        } else {
            return EditOperation.DELETE;
        }
    }

    public void set(int x, int y, EditOperation op) {
        final boolean high;
        final boolean low;

        switch (op) {
            case KEEP:
                high = false;
                low = false;
                break;
            case SUBSTITUTE:
                high = false;
                low = true;
                break;
            case INSERT:
                high = true;
                low = false;
                break;
            default:
                high = true;
                low = true;
        }

        final int addr = 2 * (x + y * width);

        if (high) {
            data.set(addr);
        } else {
            data.clear(addr);
        }

        if (low) {
            data.set(addr + 1);
        } else {
            data.clear(addr + 1);
        }
    }

    @Override
    public Iterator<EditOperation> iterator() {
        return new EditTableIterator(this);
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();

        for (final EditOperation op : this) {
            switch (op) {
                case KEEP:
                    result.append('K');
                    break;
                case SUBSTITUTE:
                    result.append('S');
                    break;
                case INSERT:
                    result.append('I');
                    break;
                case DELETE:
                    result.append('D');
            }
        }

        return result.toString();
    }

    private static class EditTableIterator implements Iterator<EditOperation> {
        int x = 0;
        int y = 0;

        final EditTable table;
        final int w;
        final int h;

        EditTableIterator(EditTable table) {
            this.table = table;
            w = table.getWidth() - 1;
            h = table.getHeight() - 1;
        }

        @Override
        public boolean hasNext() {
            return x < w || y < h;
        }

        @Override
        public EditOperation next() {
            if (++x == w) {
                x = 0;
                y++;
            }

            return table.get(x, y);
        }

        @Override
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }
    }

    ;
}
