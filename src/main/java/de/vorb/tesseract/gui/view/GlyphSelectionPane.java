package de.vorb.tesseract.gui.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import de.vorb.tesseract.gui.view.renderer.GlyphSelectionRenderer;
import de.vorb.tesseract.util.Symbol;

public class GlyphSelectionPane extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JList<Entry<String, Set<Symbol>>> selectionList;

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
        selectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        selectionList.setCellRenderer(new GlyphSelectionRenderer());
        scrollPane.setViewportView(selectionList);
    }

    public JList<Entry<String, Set<Symbol>>> getList() {
        return selectionList;
    }
}
