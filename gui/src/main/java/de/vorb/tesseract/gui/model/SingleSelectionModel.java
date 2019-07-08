package de.vorb.tesseract.gui.model;

import de.vorb.tesseract.gui.event.SelectionListener;

import java.util.LinkedList;
import java.util.List;

public class SingleSelectionModel {
    private int index = -1;
    private List<SelectionListener> selectionListeners = new LinkedList<>();

    /**
     * Creates default selection model without a selection.
     */
    public SingleSelectionModel() {
    }

    /**
     * Creates a selection model with the given initial selection index.
     *
     * @param index initial selection index.
     */
    public SingleSelectionModel(int index) {
        this.index = index;
    }

    /**
     * Determines if nothing is selected.
     *
     * @return true on empty selection.
     */
    public boolean isSelectionEmpty() {
        return index < 0;
    }

    /**
     * @return selected index.
     */
    public int getSelectedIndex() {
        return index;
    }

    /**
     * Sets the selected index.
     *
     * @param index Selected index. Negative values are considered as no
     *              selection.
     */
    public void setSelectedIndex(int index) {
        this.index = index;

        for (SelectionListener sl : selectionListeners) {
            sl.selectionChanged(index);
        }
    }

    public void addSelectionListener(SelectionListener listener) {
        selectionListeners.add(listener);
    }

    public void removeSelectionListener(SelectionListener listener) {
        selectionListeners.remove(listener);
    }
}
