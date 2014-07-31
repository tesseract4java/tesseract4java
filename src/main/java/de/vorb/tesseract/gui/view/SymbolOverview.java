package de.vorb.tesseract.gui.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.common.base.Optional;

import de.vorb.tesseract.gui.event.SymbolLinkListener;
import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.gui.view.renderer.GlyphListCellRenderer;
import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.Line;
import de.vorb.tesseract.util.Page;
import de.vorb.tesseract.util.Symbol;
import de.vorb.tesseract.util.Word;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class SymbolOverview extends JPanel implements MainComponent {
    private static final long serialVersionUID = 1L;

    private final SymbolGroupList glyphSelectionPane;
    private final SymbolVariantList glyphListPane;

    private Optional<PageModel> model = Optional.absent();

    private final LinkedList<SymbolLinkListener> listeners =
            new LinkedList<SymbolLinkListener>();

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

    public SymbolGroupList getGlyphSelectionPane() {
        return glyphSelectionPane;
    }

    public SymbolVariantList getGlyphListPane() {
        return glyphListPane;
    }

    @Override
    public void setPageModel(Optional<PageModel> model) {
        this.model = model;

        if (!model.isPresent())
            return;

        final JList<Entry<String, Set<Symbol>>> glyphList =
                getGlyphSelectionPane().getList();

        final HashMap<String, Set<Symbol>> glyphs = new HashMap<>();

        final Page page = model.get().getPage();

        // set a new renderer that has a reference to the thresholded image
        getGlyphListPane().getList().setCellRenderer(
                new GlyphListCellRenderer(model.get().getImage()));

        // // TODO remove
        // BufferedImage img = model.get().getImage();
        // try {
        // final Path dir = Paths.get("symbols");
        // Files.createDirectories(dir);

        // insert all symbols into the map
        for (final Line line : page.getLines()) {
            for (final Word word : line.getWords()) {
                for (final Symbol symbol : word.getSymbols()) {
                    final String sym = symbol.getText();

                    // String dirname = sym.replaceAll("\\p{Lu}", "+$0");
                    // dirname = dirname.replaceAll("ß", "scharfs");
                    // dirname = dirname.replaceAll("ſ", "langs");
                    // dirname = dirname.replaceAll(",", "komma");
                    // dirname = dirname.replaceAll("\\.", "punkt");
                    // dirname = dirname.replaceAll("-", "bindestrich");
                    // dirname = dirname.replaceAll("„", "anfz_unten");
                    // dirname = dirname.replaceAll("“", "anfz_oben");
                    // dirname = dirname.replaceAll("\\?", "fragezeichen");
                    // dirname = dirname.replaceAll("!", "ausrufezeichen");
                    // dirname = dirname.replaceAll("ä", "ae");
                    // dirname = dirname.replaceAll("ö", "oe");
                    // dirname = dirname.replaceAll("ü", "ue");
                    // dirname = dirname.replaceAll("\\(", "klammer_auf");
                    // dirname = dirname.replaceAll("\\)", "klammer_zu");
                    // dirname = dirname.replaceAll("\\*", "stern");
                    // Path dir2 = dir.resolve(dirname.replaceAll(
                    // "[^a-zA-Z0-9\\.\\-+]", "_"));
                    // Files.createDirectories(dir2);

                    if (!glyphs.containsKey(sym)) {
                        glyphs.put(sym, new TreeSet<Symbol>(SYMBOL_COMP));
                    }

                    glyphs.get(sym).add(symbol);

                    // Box bbox = symbol.getBoundingBox();
                    //
                    // BufferedImage sub = img.getSubimage(bbox.getX(),
                    // bbox.getY(),
                    // bbox.getWidth(), bbox.getHeight());
                    // int i = 1;
                    // for (Path p : Files.newDirectoryStream(dir2)) {
                    // i++;
                    // }
                    // ImageIO.write(sub, "PNG",
                    // dir2.resolve(i + ".png").toFile());
                }
            }
        }
        // } catch (IOException e) {
        // e.printStackTrace();
        // }

        final ArrayList<Entry<String, Set<Symbol>>> entries = new ArrayList<>(
                glyphs.entrySet());

        Collections.sort(entries, GLYPH_COMP);

        final DefaultListModel<Entry<String, Set<Symbol>>> listModel =
                new DefaultListModel<>();

        for (final Entry<String, Set<Symbol>> entry : entries) {
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
