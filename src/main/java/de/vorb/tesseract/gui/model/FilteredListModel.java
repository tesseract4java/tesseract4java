package de.vorb.tesseract.gui.model;

import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.google.common.base.Optional;

import de.vorb.tesseract.gui.util.Filter;

public class FilteredListModel<T> extends AbstractListModel<T> {
    private static final long serialVersionUID = 1L;

    private final ListModel<T> source;
    private final ArrayList<T> filtered = new ArrayList<>();

    // if filter is absent, all items are shown
    private Optional<Filter<T>> filter = Optional.absent();

    public FilteredListModel(ListModel<T> source) {
        if (source == null) {
            throw new IllegalArgumentException("model was null");
        }

        this.source = source;

        // apply the filter on every change
        source.addListDataListener(new ListDataListener() {
            @Override
            public void intervalRemoved(ListDataEvent evt) {
                applyFilter();
            }

            @Override
            public void intervalAdded(ListDataEvent evt) {
                applyFilter();
            }

            @Override
            public void contentsChanged(ListDataEvent evt) {
                applyFilter();
            }
        });
    }

    private void applyFilter() {
        if (filter.isPresent()) {
            final Filter<T> f = filter.get();
            filtered.clear();

            // apply filter to every item in source model
            final int sourceSize = source.getSize();
            for (int i = 0; i < sourceSize; ++i) {
                final T item = source.getElementAt(i);
                if (f.accept(item)) {
                    filtered.add(item);
                }
            }
        }

        // propagate changes
        fireContentsChanged(this, 0, getSize() - 1);
    }

    public void setFilter(Optional<Filter<T>> filter) {
        this.filter = filter;
        applyFilter();
    }

    @Override
    public T getElementAt(int index) {
        if (!filter.isPresent()) {
            return source.getElementAt(index);
        }

        return filtered.get(index);
    }

    @Override
    public int getSize() {
        if (!filter.isPresent()) {
            return source.getSize();
        }

        return filtered.size();
    }

    public ListModel<T> getSource() {
        return source;
    }
}
