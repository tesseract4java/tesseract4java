package de.vorb.tesseract.gui.view;

import de.vorb.tesseract.gui.event.SymbolLinkListener;
import de.vorb.tesseract.gui.model.BoxFileModel;
import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.gui.view.renderer.SymbolVariantListCellRenderer;
import de.vorb.tesseract.util.Symbol;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

public class SymbolOverview extends JPanel implements BoxFileModelComponent {
    private static final long serialVersionUID = 1L;

    private final SymbolGroupList glyphSelectionPane;
    private final SymbolVariantList glyphListPane;

    private Optional<BoxFileModel> model = Optional.empty();
    private Optional<PageModel> pageModel = Optional.empty();

    private final LinkedList<SymbolLinkListener> listeners = new LinkedList<>();

    public static final Comparator<Entry<String, List<Symbol>>> SYMBOL_GROUP_COMP =
            (o1, o2) -> o2.getValue().size() - o1.getValue().size();

    public static final Comparator<Symbol> SYMBOL_COMP = (o1, o2) -> {
        if (o2.getConfidence() >= o1.getConfidence()) {
            return 1;
        }

        return -1;
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

    public void addSymbolLinkListener(SymbolLinkListener listener) {
        listeners.add(listener);
    }

    @Override
    public Component asComponent() {
        return this;
    }

    @Override
    public Optional<BoxFileModel> getBoxFileModel() {
        return model;
    }

    public SymbolGroupList getSymbolGroupList() {
        return glyphSelectionPane;
    }

    public SymbolVariantList getSymbolVariantList() {
        return glyphListPane;
    }

    @Override
    public void setBoxFileModel(Optional<BoxFileModel> model) {
        this.model = model;

        if (!model.isPresent()) {
            glyphSelectionPane.getList().setModel(
                    new DefaultListModel<>());
            glyphListPane.getList().setModel(new DefaultListModel<>());
            return;
        }

        final JList<Entry<String, List<Symbol>>> glyphList =
                getSymbolGroupList().getList();

        final HashMap<String, List<Symbol>> glyphs = new HashMap<>();

        final BoxFileModel boxFile = model.get();

        getSymbolVariantList().getList().setModel(
                new DefaultListModel<>());

        // set a new renderer that has a reference to the thresholded image
        getSymbolVariantList().getList().setCellRenderer(
                new SymbolVariantListCellRenderer(boxFile.getImage()));

        // insert all symbols into the map
        for (final Symbol symbol : boxFile.getBoxes()) {
            final String text = symbol.getText();

            if (!glyphs.containsKey(text)) {
                glyphs.put(text, new ArrayList<>());
            }

            glyphs.get(text).add(symbol);
        }

        final ArrayList<Entry<String, List<Symbol>>> entries = new ArrayList<>(
                glyphs.entrySet());

        Collections.sort(entries, SYMBOL_GROUP_COMP);

        final DefaultListModel<Entry<String, List<Symbol>>> listModel =
                new DefaultListModel<>();

        entries.forEach(listModel::addElement);

        glyphList.setModel(listModel);
    }

    @Override
    public void setPageModel(Optional<PageModel> model) {
        if (model.isPresent()) {
            setBoxFileModel(Optional.of(model.get().toBoxFileModel()));
            pageModel = model;
        } else {
            setBoxFileModel(Optional.empty());
            pageModel = model;
        }
    }

    @Override
    public Optional<PageModel> getPageModel() {
        return pageModel;
    }

    @Override
    public void freeResources() {
        getSymbolGroupList().getList().setModel(
                new DefaultListModel<>());
        getSymbolVariantList().getList().setModel(
                new DefaultListModel<>());
    }
}
