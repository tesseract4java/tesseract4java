package de.vorb.tesseract.gui.view;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

public class Canvas extends JComponent {
    private static final long serialVersionUID = 1L;

    private static final Dimension DIM_EMPTY = new Dimension(0, 0);

    @Nullable
    private Image image = null;

    Canvas() {
    }

    public void setImage(@Nullable Image image) {
        this.image = image;
    }

    @Override
    public void paintComponent(Graphics g) {
        final Rectangle rect = getVisibleRect();

        if (image != null) {
            g.setClip(rect.x, rect.y, rect.width, rect.height);
            g.drawImage(image, 0, 0, null);
        } else {
            g.setColor(Color.WHITE);
            g.fillRect((int) rect.getX(), (int) rect.getY(),
                    (int) rect.getWidth(), (int) rect.getHeight());
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if (image == null) {
            return DIM_EMPTY;
        }

        return new Dimension(image.getWidth(null), image.getHeight(null));
    }
}
