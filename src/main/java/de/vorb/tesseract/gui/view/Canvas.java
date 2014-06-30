package de.vorb.tesseract.gui.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.JComponent;

import com.google.common.base.Optional;

public class Canvas extends JComponent {
    private static final long serialVersionUID = 1L;

    private static final Dimension DIM_EMPTY = new Dimension(0, 0);

    private Optional<Image> image = Optional.<Image> absent();

    public Canvas() {
    }

    public void setImage(Optional<Image> image) {
        this.image = image;
    }

    @Override
    public void paintComponent(Graphics g) {
        final Rectangle rect = getVisibleRect();

        if (image.isPresent()) {
            g.setClip(rect.x, rect.y, rect.width, rect.height);
            g.drawImage(image.get(), 0, 0, null);
        } else {
            g.setColor(Color.WHITE);
            g.fillRect((int) rect.getX(), (int) rect.getY(),
                    (int) rect.getWidth(), (int) rect.getHeight());
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if (!image.isPresent()) {
            return DIM_EMPTY;
        }

        return new Dimension(image.get().getWidth(null), image.get().getHeight(
                null));
    }
}
