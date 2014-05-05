package de.vorb.tesseract.gui.view;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JLabel;
import java.awt.FlowLayout;

public class GlyphSelectionPane extends JPanel {
    private static final long serialVersionUID = 1L;

    /**
     * Create the panel.
     */
    public GlyphSelectionPane() {
        super();
        setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel.getLayout();
        flowLayout.setAlignment(FlowLayout.LEADING);
        add(panel, BorderLayout.NORTH);

        JLabel lblGlyphSelection = new JLabel("Glyph selection");
        panel.add(lblGlyphSelection);

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, BorderLayout.CENTER);

        JList list = new JList();
        scrollPane.setViewportView(list);
    }

}
