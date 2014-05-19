package de.vorb.tesseract.gui.view;

import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JSplitPane;
import javax.swing.JButton;

import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.gui.view.renderer.GlyphListCellRenderer;
import de.vorb.tesseract.util.Line;
import de.vorb.tesseract.util.Page;
import de.vorb.tesseract.util.Symbol;
import de.vorb.tesseract.util.Word;

import java.awt.FlowLayout;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

public class GlyphExportPane extends JPanel implements MainComponent {
    private static final long serialVersionUID = 1L;

    private final GlyphSelectionPane glyphSelectionPane;
    private final GlyphListPane glyphListPane;

    private PageModel model = null;

    public static final Comparator<Entry<String, Set<Symbol>>> GLYPH_COMP =
            new Comparator<Entry<String, Set<Symbol>>>() {
                @Override
                public int compare(Entry<String, Set<Symbol>> o1,
                        Entry<String, Set<Symbol>> o2) {
                    return o2.getValue().size() - o1.getValue().size();
                }
            };

    public static final Comparator<Symbol> SYMBOL_COMP =
            new Comparator<Symbol>() {
                @Override
                public int compare(Symbol o1, Symbol o2) {
                    if (o2.getConfidence() >= o1.getConfidence())
                        return 1;

                    return -1;
                }
            };

    /**
     * Create the panel.
     */
    public GlyphExportPane() {
        super();
        setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel.getLayout();
        flowLayout.setAlignment(FlowLayout.TRAILING);
        add(panel, BorderLayout.SOUTH);

        JButton btnExport = new JButton("Export ...");
        panel.add(btnExport);

        JSplitPane splitPane = new JSplitPane();
        add(splitPane, BorderLayout.CENTER);

        glyphSelectionPane = new GlyphSelectionPane();
        glyphListPane = new GlyphListPane();

        splitPane.setLeftComponent(glyphSelectionPane);
        splitPane.setRightComponent(glyphListPane);
    }

    public GlyphSelectionPane getGlyphSelectionPane() {
        return glyphSelectionPane;
    }

    public GlyphListPane getGlyphListPane() {
        return glyphListPane;
    }

    @Override
    public void setModel(PageModel model) {
        final JList<Entry<String, Set<Symbol>>> glyphList =
                getGlyphSelectionPane().getList();

        final HashMap<String, Set<Symbol>> glyphs = new HashMap<>();

        final Page page = model.getPage();

        // set a new renderer that has a reference to the thresholded image
        getGlyphListPane().getList().setCellRenderer(
                new GlyphListCellRenderer(model.getBlackAndWhiteImage()));

        // insert all symbols into the map
        for (final Line line : page.getLines()) {
            for (final Word word : line.getWords()) {
                for (final Symbol symbol : word.getSymbols()) {
                    final String sym = symbol.getText();

                    if (!glyphs.containsKey(sym)) {
                        glyphs.put(sym, new TreeSet<Symbol>(SYMBOL_COMP));
                    }

                    glyphs.get(sym).add(symbol);
                }
            }
        }

        final LinkedList<Entry<String, Set<Symbol>>> entries = new LinkedList<>(
                glyphs.entrySet());

        Collections.sort(entries, GLYPH_COMP);

        final DefaultListModel<Entry<String, Set<Symbol>>> listModel =
                new DefaultListModel<>();

        for (final Entry<String, Set<Symbol>> entry : entries) {
            listModel.addElement(entry);
        }

        glyphList.setModel(listModel);
    }

    @Override
    public PageModel getModel() {
        return model;
    }

    @Override
    public Component asComponent() {
        return this;
    }
}
