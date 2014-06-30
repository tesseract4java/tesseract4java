package de.vorb.tesseract.gui.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.JPanel;

import com.google.common.base.Optional;

public class Canvas extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Dimension DIM_EMPTY = new Dimension(0, 0);

    private Optional<Image> image = Optional.<Image> absent();

    public void setImage(Optional<Image> image) {
        this.image = image;

        final Dimension size;
        if (image.isPresent()) {
            size = new Dimension(image.get().getWidth(null),
                    image.get().getHeight(null));
        } else {
            size = DIM_EMPTY;
        }

        setSize(size);
        setPreferredSize(size);
    }

    @Override
    public void paintComponent(Graphics g) {
        final Rectangle rect = getVisibleRect();

        if (image.isPresent()) {
            g.drawImage(image.get(), 0, 0, null);
        } else {
            g.setColor(Color.WHITE);
            g.fillRect((int) rect.getX(), (int) rect.getY(),
                    (int) rect.getWidth(), (int) rect.getHeight());
        }
    }
}
