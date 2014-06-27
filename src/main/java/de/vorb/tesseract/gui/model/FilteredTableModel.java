package de.vorb.tesseract.gui.model;

import javax.swing.table.AbstractTableModel;

public abstract class FilteredTableModel<T> extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    private final FilteredListModel<T> source;

    public FilteredTableModel(FilteredListModel<T> source) {
        this.source = source;
    }

    public FilteredListModel<T> getSource() {
        return source;
    }

    @Override
    public int getRowCount() {
        return source.getSize();
    }
}
