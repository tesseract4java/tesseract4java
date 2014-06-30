package de.vorb.tesseract.gui.util;

import java.util.ListIterator;
import java.util.NoSuchElementException;

public class ScaleState implements ListIterator<Float> {
    private static final float[] VALUES =
            new float[] { 0.01f, 0.02f, 0.05f, 0.1f, 0.2f, 0.5f, 1f, 2f };
    private int cursor = 6;

    @Override
    public boolean hasNext() {
        return cursor < VALUES.length - 1;
    }

    @Override
    public boolean hasPrevious() {
        return cursor != 0;
    }

    @Override
    public Float next() {
        if (!hasNext())
            throw new NoSuchElementException();

        return VALUES[++cursor];
    }

    @Override
    public int nextIndex() {
        return hasNext() ? cursor + 1 : -1;
    }

    @Override
    public Float previous() {
        if (!hasPrevious())
            throw new NoSuchElementException();

        return VALUES[--cursor];
    }

    @Override
    public int previousIndex() {
        return hasPrevious() ? cursor - 1 : -1;
    }

    @Override
    public void add(Float v) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(Float v) {
        throw new UnsupportedOperationException();
    }

}
