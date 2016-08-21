package de.vorb.tesseract.gui.view.renderer;

import de.vorb.tesseract.util.Symbol;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Component;
import java.util.List;
import java.util.Map.Entry;

public class SymbolGroupListCellRenderer extends JLabel implements
        ListCellRenderer<Entry<String, List<Symbol>>> {
    private static final long serialVersionUID = 1L;

    public SymbolGroupListCellRenderer() {
        super();
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends Entry<String, List<Symbol>>> list,
            Entry<String, List<Symbol>> value, int index, boolean isSelected,
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
        setText(symbol + " (" + value.getValue().size() + ")");
        setToolTipText(charCodes.toString());

        return this;
    }
}
