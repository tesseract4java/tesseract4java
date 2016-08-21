package de.vorb.tesseract.gui.view;

import de.vorb.tesseract.gui.view.renderer.SymbolGroupListCellRenderer;
import de.vorb.tesseract.util.Symbol;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import java.util.Map.Entry;

public class SymbolGroupList extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JList<Entry<String, List<Symbol>>> selectionList;

    /**
     * Create the panel.
     */
    public SymbolGroupList() {
        super();
        setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(3, 0, 3, 0));
        FlowLayout flowLayout = (FlowLayout) panel.getLayout();
        flowLayout.setAlignment(FlowLayout.LEADING);
        add(panel, BorderLayout.NORTH);

        JLabel lblGlyphSelection = new JLabel("Glyph selection");
        panel.add(lblGlyphSelection);

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, BorderLayout.CENTER);

        selectionList = new JList<>();
        selectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        selectionList.setCellRenderer(new SymbolGroupListCellRenderer());
        scrollPane.setViewportView(selectionList);
    }

    public JList<Entry<String, List<Symbol>>> getList() {
        return selectionList;
    }
}
