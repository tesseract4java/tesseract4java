package de.vorb.tesseract.gui.view;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.vorb.tesseract.gui.model.LanguageSelectionModel;

public class LanguageSelectionPane extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JList<String> listLangs;

    private LanguageSelectionModel model;

    public LanguageSelectionPane() {
        setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane);

        listLangs = new JList<String>();
        listLangs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listLangs.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                getModel().setSelectedIndex(listLangs.getSelectedIndex());
            }
        });

        scrollPane.setViewportView(listLangs);
    }

    public void setModel(LanguageSelectionModel model) {
        final DefaultListModel<String> listModel = new DefaultListModel<>();

        for (String lang : model.getLanguages()) {
            listModel.addElement(lang);
        }

        listLangs.setModel(listModel);

        this.model = model;
    }

    public LanguageSelectionModel getModel() {
        return model;
    }
}
