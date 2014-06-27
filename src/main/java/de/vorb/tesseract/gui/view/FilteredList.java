package de.vorb.tesseract.gui.view;

import java.awt.BorderLayout;
import java.awt.SystemColor;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.google.common.base.Optional;

import de.vorb.tesseract.gui.model.FilteredListModel;
import de.vorb.tesseract.gui.util.Filter;

public class FilteredList<T> extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JList<T> list;
    private final SearchField filterField;

    public static interface FilterProvider<T> {
        Optional<Filter<T>> getFilter(String filterText);
    }

    /**
     * Create the panel.
     */
    public FilteredList(final FilterProvider<T> filterProvider) {
        setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(BorderFactory.createEtchedBorder());
        scrollPane.setBackground(SystemColor.window);

        add(scrollPane, BorderLayout.CENTER);

        list = new JList<T>();
        scrollPane.setViewportView(list);

        filterField = new SearchField();
        add(filterField, BorderLayout.SOUTH);

        filterField.getTextField().getDocument().addDocumentListener(
                new DocumentListener() {
                    @Override
                    public void removeUpdate(DocumentEvent evt) {
                        filter();
                    }

                    @Override
                    public void insertUpdate(DocumentEvent evt) {
                        filter();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent evt) {
                        filter();
                    }

                    private void filter() {
                        final ListModel<T> model = list.getModel();
                        if (!(model instanceof FilteredListModel)) {
                            return;
                        }

                        final FilteredListModel<T> filteredModel =
                                (FilteredListModel<T>) model;

                        final String query =
                                filterField.getTextField().getText();

                        filteredModel.setFilter(
                                filterProvider.getFilter(query));
                    }
                });
    }

    public JList<T> getList() {
        return list;
    }

    public JTextField getTextField() {
        return filterField.getTextField();
    }
}
