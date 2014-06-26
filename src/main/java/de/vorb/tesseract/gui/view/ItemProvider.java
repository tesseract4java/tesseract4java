package de.vorb.tesseract.gui.view;

import org.eclipse.swt.widgets.Composite;

public interface ItemProvider<T> {
    Composite itemFor(T source, Composite parent);
}
