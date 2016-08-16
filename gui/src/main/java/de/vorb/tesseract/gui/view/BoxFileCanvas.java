package de.vorb.tesseract.gui.view;

import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.Symbol;

import com.google.common.base.Optional;

import javax.swing.*;
import java.awt.*;

import static de.vorb.tesseract.gui.model.Scale.scaled;

public class BoxFileCanvas extends Canvas {
    private static final long serialVersionUID = 1L;

    private float scale;
    private Optional<ListModel<Symbol>> symbols =
            Optional.<ListModel<Symbol>> absent();
    private int selectedIndex = -1;

    public BoxFileCanvas() {
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setSymbols(Optional<ListModel<Symbol>> symbols,
            int selectedIndex) {
        this.symbols = symbols;
        this.selectedIndex = -1;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!this.symbols.isPresent())
            return;

        final ListModel<Symbol> symbols = this.symbols.get();
        final int size = symbols.getSize();

        for (int i = 0; i < size; i++) {
            final Symbol symbol = symbols.getElementAt(i);
            drawSymbolBox((Graphics2D) g, symbol, scale, i == selectedIndex);
        }
    }

    private void drawSymbolBox(final Graphics2D g, final Symbol symbol,
            final float scale, final boolean isSelected) {
        final Box bbox = symbol.getBoundingBox();

        // set selected colors
        if (isSelected) {
            g.setColor(Colors.SELECTION);
            g.setStroke(Strokes.SELECTION);
        }

        // draw the box
        g.drawRect(scaled(bbox.getX(), scale), scaled(bbox.getY(), scale),
                scaled(bbox.getWidth(), scale), scaled(bbox.getHeight(), scale));

        // unset selected colors
        if (isSelected) {
            g.setColor(Colors.NORMAL);
            g.setStroke(Strokes.NORMAL);
        }
    }
}
