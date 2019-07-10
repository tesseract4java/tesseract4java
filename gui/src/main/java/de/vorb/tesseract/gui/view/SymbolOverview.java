package de.vorb.tesseract.gui.view;

import de.vorb.tesseract.gui.model.BoxFile;
import de.vorb.tesseract.gui.model.Page;
import de.vorb.tesseract.gui.view.renderer.SymbolVariantListCellRenderer;
import de.vorb.tesseract.util.Symbol;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

public class SymbolOverview extends JPanel implements BoxFileComponent {

    private static final long serialVersionUID = 1L;

    private final SymbolGroupList glyphSelectionPane;
    private final SymbolVariantList glyphListPane;

    @Nullable
    private BoxFile boxFile = null;
    private Page page = null;

    private static final Comparator<Entry<String, List<Symbol>>> SYMBOL_GROUP_COMP =
            Comparator.comparingInt(symbolGroup -> symbolGroup.getValue().size());

    /**
     * Create the panel.
     */
    SymbolOverview() {
        super();
        setLayout(new BorderLayout(0, 0));

        JSplitPane splitPane = new JSplitPane();
        add(splitPane, BorderLayout.CENTER);

        glyphSelectionPane = new SymbolGroupList();
        glyphListPane = new SymbolVariantList();

        splitPane.setLeftComponent(glyphSelectionPane);
        splitPane.setRightComponent(glyphListPane);
    }

    @Override
    public Component asComponent() {
        return this;
    }

    @Override
    public @Nullable BoxFile getBoxFile() {
        return boxFile;
    }

    public SymbolGroupList getSymbolGroupList() {
        return glyphSelectionPane;
    }

    public SymbolVariantList getSymbolVariantList() {
        return glyphListPane;
    }

    @Override
    public void setBoxFile(@Nullable BoxFile boxFile) {
        this.boxFile = boxFile;

        if (boxFile == null) {
            glyphSelectionPane.getList().setModel(new DefaultListModel<>());
            glyphListPane.getList().setModel(new DefaultListModel<>());
            return;
        }

        final JList<Entry<String, List<Symbol>>> glyphList =
                getSymbolGroupList().getList();

        final HashMap<String, List<Symbol>> glyphs = new HashMap<>();

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

        entries.sort(SYMBOL_GROUP_COMP);

        final DefaultListModel<Entry<String, List<Symbol>>> listModel =
                new DefaultListModel<>();

        entries.forEach(listModel::addElement);

        glyphList.setModel(listModel);
    }

    @Override
    public void setPage(Page page) {
        this.page = page;
        setBoxFile(Optional.ofNullable(page).map(Page::toBoxFileModel).orElse(null));
    }

    @Override
    public @Nullable Page getPage() {
        return page;
    }

    @Override
    public void freeResources() {
        getSymbolGroupList().getList().setModel(new DefaultListModel<>());
        getSymbolVariantList().getList().setModel(new DefaultListModel<>());
    }

}
