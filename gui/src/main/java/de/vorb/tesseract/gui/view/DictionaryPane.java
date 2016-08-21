package de.vorb.tesseract.gui.view;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class DictionaryPane extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JList<String> list;

    /**
     * Create the panel.
     */
    public DictionaryPane() {
        setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        add(panel, BorderLayout.SOUTH);
        panel.setLayout(new BorderLayout(0, 0));

        JPanel panel_1 = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
        flowLayout.setAlignment(FlowLayout.LEADING);
        panel.add(panel_1, BorderLayout.CENTER);

        JButton btnNewButton = new JButton("Add word");
        panel_1.add(btnNewButton);

        JButton btnRemoveWord = new JButton("Remove word");
        panel_1.add(btnRemoveWord);

        JPanel panel_2 = new JPanel();
        FlowLayout flowLayout_1 = (FlowLayout) panel_2.getLayout();
        flowLayout_1.setAlignment(FlowLayout.TRAILING);
        panel.add(panel_2, BorderLayout.EAST);

        JButton btnNewButton_1 = new JButton("Save");
        panel_2.add(btnNewButton_1);

        JPanel panel_3 = new JPanel();
        panel_3.setBorder(new EmptyBorder(5, 5, 0, 5));
        add(panel_3, BorderLayout.CENTER);
        panel_3.setLayout(new BorderLayout(0, 0));

        list = new JList<>();
        panel_3.add(list);
    }

    public JList<String> getWordList() {
        return list;
    }
}
