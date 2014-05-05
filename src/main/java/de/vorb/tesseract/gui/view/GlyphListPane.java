package de.vorb.tesseract.gui.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.vorb.tesseract.util.Symbol;

public class GlyphListPane extends JPanel {
    private static final long serialVersionUID = 1L;
    private final JList<Symbol> glyphList;

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

        glyphList = new JList<Symbol>();
        scrollPane.setViewportView(glyphList);
    }

    public JList<Symbol> getList() {
        return glyphList;
    }

}
