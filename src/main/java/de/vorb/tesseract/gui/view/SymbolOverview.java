package de.vorb.tesseract.gui.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.google.common.base.Optional;

import de.vorb.tesseract.gui.event.SymbolLinkListener;
import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.gui.view.renderer.GlyphListCellRenderer;
import de.vorb.tesseract.util.Line;
import de.vorb.tesseract.util.Page;
import de.vorb.tesseract.util.Symbol;
import de.vorb.tesseract.util.Word;

public class SymbolOverview extends JPanel implements MainComponent {
    private static final long serialVersionUID = 1L;

    private final SymbolGroupList glyphSelectionPane;
    private final SymbolVariantList glyphListPane;

    private Optional<PageModel> model = Optional.absent();

    private final LinkedList<SymbolLinkListener> listeners =
            new LinkedList<SymbolLinkListener>();

    public static final Comparator<Entry<String, List<Symbol>>> SYMBOL_GROUP_COMP =
            new Comparator<Entry<String, List<Symbol>>>() {
                @Override
                public int compare(Entry<String, List<Symbol>> o1,
                        Entry<String, List<Symbol>> o2) {
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
    public SymbolOverview() {
        super();
        setLayout(new BorderLayout(0, 0));

        JSplitPane splitPane = new JSplitPane();
        add(splitPane, BorderLayout.CENTER);

        glyphSelectionPane = new SymbolGroupList();
        glyphListPane = new SymbolVariantList();

        splitPane.setLeftComponent(glyphSelectionPane);
        splitPane.setRightComponent(glyphListPane);
    }

    public SymbolGroupList getSymbolGroupList() {
        return glyphSelectionPane;
    }

    public SymbolVariantList getSymbolVariantList() {
        return glyphListPane;
    }

    @Override
    public void setPageModel(Optional<PageModel> model) {
        this.model = model;

        if (!model.isPresent())
            return;

        final JList<Entry<String, List<Symbol>>> glyphList =
                getSymbolGroupList().getList();

        final HashMap<String, List<Symbol>> glyphs = new HashMap<>();

        final Page page = model.get().getPage();

        // set a new renderer that has a reference to the thresholded image
        getSymbolVariantList().getList().setCellRenderer(
                new GlyphListCellRenderer(model.get().getImage()));

        // insert all symbols into the map
        for (final Line line : page.getLines()) {
            for (final Word word : line.getWords()) {
                for (final Symbol symbol : word.getSymbols()) {
                    final String sym = symbol.getText();

                    if (!glyphs.containsKey(sym)) {
                        glyphs.put(sym, new ArrayList<Symbol>());
                    }

                    glyphs.get(sym).add(symbol);
                }
            }
        }

        final ArrayList<Entry<String, List<Symbol>>> entries = new ArrayList<>(
                glyphs.entrySet());

        Collections.sort(entries, SYMBOL_GROUP_COMP);

        final DefaultListModel<Entry<String, List<Symbol>>> listModel =
                new DefaultListModel<>();

        for (final Entry<String, List<Symbol>> entry : entries) {
            listModel.addElement(entry);
        }

        glyphList.setModel(listModel);
    }

    public void addSymbolLinkListener(SymbolLinkListener listener) {
        listeners.add(listener);
    }

    @Override
    public Optional<PageModel> getPageModel() {
        return model;
    }

    @Override
    public Component asComponent() {
        return this;
    }
}
