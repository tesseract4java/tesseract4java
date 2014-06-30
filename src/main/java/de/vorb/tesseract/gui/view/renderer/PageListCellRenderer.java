package de.vorb.tesseract.gui.view.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.google.common.base.Optional;

import de.vorb.tesseract.gui.model.PageThumbnail;

public class PageListCellRenderer extends JLabel implements
        ListCellRenderer<PageThumbnail> {
    private static final long serialVersionUID = 1L;

    private static final ImageIcon ICON_PLACEHOLDER = new ImageIcon(
            PageListCellRenderer.class.getResource("/page_loading.png"));

    public static final Color COLOR_SELECT = new Color(0x4477FF);

    public PageListCellRenderer() {
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

        if (isSelected) {
            setBackground(COLOR_SELECT);
            setForeground(Color.WHITE);
        } else {
            setBackground(Color.WHITE);
            setForeground(Color.BLACK);
        }

        final Optional<BufferedImage> opt = value.getThumbnail();

        if (opt.isPresent()) {
            final BufferedImage thumbnail = opt.get();
            final Graphics2D g2d = (Graphics2D) thumbnail.getGraphics();
            if (isSelected) {
                g2d.setColor(Color.BLACK);
            } else {
                g2d.setColor(Color.GRAY);
            }
            g2d.drawRect(0, 0, thumbnail.getWidth() - 1,
                    thumbnail.getHeight() - 1);
            g2d.dispose();

            setIcon(new ImageIcon(thumbnail));
        } else {
            setIcon(ICON_PLACEHOLDER);
        }

        return this;
    }

    @Override
    public int getHorizontalAlignment() {
        return CENTER;
    }
}
