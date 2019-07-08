package de.vorb.tesseract.gui.model;

import de.vorb.tesseract.img.BinaryImage;
import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.Symbol;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Vector;

public class SymbolListModel implements ListModel<Symbol> {
    private final Vector<Symbol> data = new Vector<>();
    private final LinkedList<ListDataListener> listeners = new LinkedList<>();
    private final BufferedImage page;

    private Comparator<Symbol> confidenceComp = (s1, s2) -> {
        final float conf1 = s1.getConfidence();
        final float conf2 = s2.getConfidence();

        if (conf1 > conf2)
            return -1;
        if (conf1 < conf2)
            return 1;

        return 0;
    };

    private Comparator<Symbol> sizeComp = (s1, s2) -> s2.getBoundingBox().getArea()
            - s1.getBoundingBox().getArea();

    private Comparator<Symbol> weightComp = new Comparator<Symbol>() {
        // TODO implement cache

        @Override
        public int compare(Symbol s1, Symbol s2) {
            final Box bb1 = s1.getBoundingBox();
            final Box bb2 = s2.getBoundingBox();

            final BufferedImage img1 = page.getSubimage(bb1.getX(), bb1.getY(),
                    bb1.getWidth(), bb1.getHeight());
            final BufferedImage img2 = page.getSubimage(bb2.getX(), bb2.getY(),
                    bb2.getWidth(), bb2.getHeight());

            return BinaryImage.weight(img2) - BinaryImage.weight(img1);
        }
    };

    public SymbolListModel(BufferedImage page) {
        this.page = page;
    }

    public void sortBy(SymbolOrder order) {
        final Comparator<Symbol> comparator;

        switch (order) {
            case CONFIDENCE:
                comparator = confidenceComp;
                break;
            case SIZE:
                comparator = sizeComp;
                break;
            default:
                comparator = weightComp;
        }

        Collections.sort(data, comparator);

        final int size = data.size();
        for (ListDataListener l : listeners) {
            l.contentsChanged(new ListDataEvent(this,
                    ListDataEvent.CONTENTS_CHANGED, 0, size - 1));
        }
    }

    @Override
    public void addListDataListener(ListDataListener listener) {
        listeners.add(listener);
    }

    public void addElement(Symbol symbol) {
        data.add(symbol);

        final int size = data.size();
        for (ListDataListener l : listeners) {
            l.contentsChanged(new ListDataEvent(this,
                    ListDataEvent.INTERVAL_ADDED, size - 2, size - 1));
        }
    }

    @Override
    public Symbol getElementAt(int index) {
        return data.get(index);
    }

    @Override
    public int getSize() {
        return data.size();
    }

    @Override
    public void removeListDataListener(ListDataListener listener) {
        listeners.remove(listener);
    }
}
