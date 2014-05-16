package de.vorb.tesseract.gui.model;

public class SingleSelectionModel {
    private int index = -1;

    /**
     * Creates default selection model without a selection.
     */
    public SingleSelectionModel() {
    }

    /**
     * Creates a selection model with the given initial selection index.
     * 
     * @param index
     *            initial selection index.
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
     * @param index
     *            Selected index. Negative values are considered as no
     *            selection.
     */
    public void setSelectedIndex(int index) {
        this.index = index;
    }
}
