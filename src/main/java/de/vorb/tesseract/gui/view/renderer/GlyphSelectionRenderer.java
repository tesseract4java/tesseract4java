package de.vorb.tesseract.gui.view.renderer;

import java.awt.Component;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import de.vorb.tesseract.util.Symbol;

public class GlyphSelectionRenderer extends JLabel implements
        ListCellRenderer<Entry<String, Set<Symbol>>> {
    private static final long serialVersionUID = 1L;

    public GlyphSelectionRenderer() {
        super();
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends Entry<String, Set<Symbol>>> list,
            Entry<String, Set<Symbol>> value, int index, boolean isSelected,
            boolean cellHasFocus) {
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        final String symbol = value.getKey();

        // list all char codes in a string
        final StringBuilder charCodes = new StringBuilder();
        charCodes.append("charCodes = [ ");
        for (int i = 0; i < symbol.length(); i++) {
            charCodes.append((int) symbol.charAt(i));
            charCodes.append(' ');
        }
        charCodes.append(']');

        // Set the icon and text. If icon was null, say so.
        setText(symbol + " (" + value.getValue().size()
                + ")");
        setToolTipText(charCodes.toString());

        return this;
    }
}
