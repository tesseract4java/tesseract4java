package de.vorb.tesseract.gui.util;

public interface Filter<T> {
    boolean accept(T item);
}
