package de.vorb.tesseract.gui.view.renderer;

import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.Symbol;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SymbolVariantListCellRenderer extends JLabel implements
        ListCellRenderer<Symbol> {
    private static final long serialVersionUID = 1L;

    private final BufferedImage source;

    public SymbolVariantListCellRenderer(BufferedImage source) {
        super();
        this.source = source;
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Symbol> list,
            Symbol value, int index, boolean isSelected, boolean cellHasFocus) {
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        final Box bbox = value.getBoundingBox();
        final BufferedImage subImage = source.getSubimage(bbox.getX(),
                bbox.getY(), bbox.getWidth(), bbox.getHeight());

        setIcon(new ImageIcon(subImage));
        setToolTipText(String.format("confidence = %.2f%%",
                value.getConfidence()));

        return this;
    }
}
