package de.vorb.tesseract.gui.view.renderer;

import java.awt.Component;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.Symbol;

public class GlyphListCellRenderer extends JCheckBox implements
        ListCellRenderer<Symbol> {
    private static final long serialVersionUID = 1L;

    private final BufferedImage source;

    public GlyphListCellRenderer(BufferedImage source) {
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
        setIcon(new ImageIcon(source.getSubimage(bbox.getX(), bbox.getY(),
                bbox.getWidth(), bbox.getHeight())));

        setToolTipText("confidence = " + value.getConfidence());

        return this;
    }
}
