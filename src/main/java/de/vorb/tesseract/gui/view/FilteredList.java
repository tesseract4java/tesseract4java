package de.vorb.tesseract.gui.view;

import java.awt.BorderLayout;
import java.awt.SystemColor;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.google.common.base.Optional;

import de.vorb.tesseract.gui.model.FilteredListModel;
import de.vorb.tesseract.gui.model.FilteredListModel.Filter;

public class FilteredList<T> extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JList<T> list;
    private final SearchField filterField;

    /**
     * Create the panel.
     */
    public FilteredList() {
        setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(BorderFactory.createEtchedBorder());
        scrollPane.setBackground(SystemColor.window);

        add(scrollPane, BorderLayout.CENTER);

        list = new JList<T>();
        list.setModel(new FilteredListModel<T>(new AbstractListModel() {
            String[] values = new String[] { "a", "b", "c", "d", "e", "f", "g",
                    "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
                    "t" };

            public int getSize() {
                return values.length;
            }

            public Object getElementAt(int index) {
                return values[index];
            }
        }));
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
                        final String query =
                                filterField.getTextField().getText();

                        final ListModel<T> model = list.getModel();
                        if (!(model instanceof FilteredListModel)) {
                            return;
                        }

                        final FilteredListModel<T> filteredModel =
                                (FilteredListModel<T>) model;

                        final Filter<T> filter;
                        if (filterField.getTextField().getText().isEmpty()) {
                            filter = null;
                        } else {
                            filter = new FilteredListModel.Filter<T>() {
                                @Override
                                public boolean accept(T item) {
                                    return item.equals(query);
                                }
                            };
                        }
                        filteredModel.setFilter(Optional.fromNullable(filter));
                    }
                });
    }

    public JList<T> getList() {
        return list;
    }

    public JTextField getTextField() {
        return filterField.getTextField();
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.add(new FilteredList<String>());
        f.pack();
        f.setVisible(true);
    }
}
