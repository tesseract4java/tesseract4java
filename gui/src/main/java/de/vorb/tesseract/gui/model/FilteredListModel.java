package de.vorb.tesseract.gui.model;

import de.vorb.tesseract.gui.util.Filter;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;

public class FilteredListModel<T> extends AbstractListModel<T> {

    private static final long serialVersionUID = 1L;

    private final ListModel<T> source;
    private final ArrayList<T> filtered = new ArrayList<>();

    @NonNull
    private Filter<T> filter = createMatchAllFilter();

    public FilteredListModel(@NonNull ListModel<T> source) {

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
        filtered.clear();

        // apply filter to every item in source model
        final int sourceSize = source.getSize();
        for (int i = 0; i < sourceSize; ++i) {
            final T item = source.getElementAt(i);
            if (filter.accept(item)) {
                filtered.add(item);
            }
        }

        // propagate changes
        fireContentsChanged(this, 0, getSize() - 1);
    }

    public void setFilter(@NonNull Filter<T> filter) {
        this.filter = filter;
        applyFilter();
    }

    @Nullable
    @Override
    public T getElementAt(int index) {
        if (index < 0) {
            return null;
        }

        return filtered.get(index);
    }

    @Override
    public int getSize() {
        return filtered.size();
    }

    public ListModel<T> getSource() {
        return source;
    }

    public static <T> Filter<T> createMatchAllFilter() {
        return item -> true;
    }

}
