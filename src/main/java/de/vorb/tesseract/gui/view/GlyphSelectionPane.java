package de.vorb.tesseract.gui.view;

import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JLabel;

import de.vorb.tesseract.gui.view.renderer.GlyphSelectionRenderer;
import de.vorb.tesseract.util.Symbol;

import java.awt.FlowLayout;
import java.util.List;
import java.util.Map.Entry;

public class GlyphSelectionPane extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JList<Entry<String, List<Symbol>>> selectionList;

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

        selectionList = new JList<>();
        selectionList.setCellRenderer(new GlyphSelectionRenderer());
        scrollPane.setViewportView(selectionList);
    }

    public JList<Entry<String, List<Symbol>>> getList() {
        return selectionList;
    }
}
