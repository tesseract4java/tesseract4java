package de.vorb.tesseract.gui.view;

import de.vorb.tesseract.gui.model.FilteredTableModel;
import de.vorb.tesseract.gui.util.FilterProvider;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import java.awt.BorderLayout;
import java.awt.SystemColor;

public class FilteredTable<T> extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JTable table;
    private final SearchField filterField;

    /**
     * Create the panel.
     */
    public FilteredTable(final FilteredTableModel<T> model,
            final FilterProvider<T> filterProvider) {
        setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(BorderFactory.createEtchedBorder());
        scrollPane.setBackground(SystemColor.window);

        add(scrollPane, BorderLayout.CENTER);

        table = new JTable(model);
        table.setFillsViewportHeight(true);
        scrollPane.setViewportView(table);

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

                    // ignore unchecked behavior and make it fail at runtime
                    @SuppressWarnings("unchecked")
                    private void filter() {
                        final TableModel model = table.getModel();
                        if (!(model instanceof FilteredTableModel)) {
                            return;
                        }

                        final FilteredTableModel<T> filteredModel =
                                (FilteredTableModel<T>) model;

                        final String query =
                                filterField.getTextField().getText();

                        filteredModel.getSource().setFilter(
                                filterProvider.getFilterFor(query));
                    }
                });
    }

    public JTable getTable() {
        return table;
    }

    public JTextField getTextField() {
        return filterField.getTextField();
    }

    @SuppressWarnings("unchecked")
    public ListModel<T> getListModel() {
        return ((FilteredTableModel<T>) table.getModel()).getSource();
    }
}
