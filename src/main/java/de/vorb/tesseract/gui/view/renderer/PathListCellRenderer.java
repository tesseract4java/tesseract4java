package de.vorb.tesseract.gui.view.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.nio.file.Path;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import de.vorb.tesseract.gui.model.PageThumbnail;

public class PathListCellRenderer extends JLabel implements
        ListCellRenderer<PageThumbnail> {
    private static final long serialVersionUID = 1L;

    private static final ImageIcon DUMMY_ICON = new ImageIcon(
            new BufferedImage(70, 100, BufferedImage.TYPE_BYTE_BINARY));

    public static final Color COLOR_SELECT = new Color(0x4477FF);

    public PathListCellRenderer() {
        setOpaque(true);

        setVerticalTextPosition(BOTTOM);
        setHorizontalTextPosition(CENTER);

        setIconTextGap(5);

        // 10px empty border
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends PageThumbnail> list, PageThumbnail value,
            int index, boolean isSelected, boolean cellHasFocus) {
        String fname = value.getFile().getFileName().toString();
        if (fname.length() > 32) {
            fname = fname.substring(0, 10) + "..."
                    + fname.substring(fname.length() - 16);
        }

        setText(fname);
        setIcon(value.getThumbnail());

        if (isSelected) {
            setBackground(COLOR_SELECT);
            setForeground(Color.WHITE);
        } else {
            setBackground(Color.WHITE);
            setForeground(Color.BLACK);
        }

        return this;
    }

    @Override
    public int getHorizontalAlignment() {
        return CENTER;
    }
}
