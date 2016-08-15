package de.vorb.tesseract.gui.model;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;

public abstract class FilteredTableModel<T> extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    private final FilteredListModel<T> source;

    protected FilteredTableModel(FilteredListModel<T> source) {
        this.source = source;

        source.addListDataListener(new ListDataListener() {
            @Override
            public void intervalRemoved(ListDataEvent evt) {
                fireTableStructureChanged();
            }

            @Override
            public void intervalAdded(ListDataEvent evt) {
                fireTableStructureChanged();
            }

            @Override
            public void contentsChanged(ListDataEvent evt) {
                fireTableStructureChanged();
            }
        });
    }

    public FilteredListModel<T> getSource() {
        return source;
    }

    @Override
    public int getRowCount() {
        return source.getSize();
    }
}
