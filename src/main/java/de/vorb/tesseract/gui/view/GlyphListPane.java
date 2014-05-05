package de.vorb.tesseract.gui.view;

import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import javax.swing.JSplitPane;
import javax.swing.JLabel;
import java.awt.FlowLayout;

public class GlyphListPane extends JPanel {
    private static final long serialVersionUID = 1L;

    /**
     * Create the panel.
     */
    public GlyphListPane() {
        super();
        setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        FlowLayout fl_panel = (FlowLayout) panel.getLayout();
        fl_panel.setAlignment(FlowLayout.LEADING);
        add(panel, BorderLayout.NORTH);

        JLabel lblVariants = new JLabel("Variants");
        panel.add(lblVariants);

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, BorderLayout.CENTER);

        JList list_1 = new JList();
        scrollPane.setViewportView(list_1);
    }

}
