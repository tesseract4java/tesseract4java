package de.vorb.tesseract.gui.view.renderer;

import static de.vorb.tesseract.gui.view.Coordinates.scaled;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

import de.vorb.tesseract.gui.model.SingleSelectionModel;
import de.vorb.tesseract.gui.view.Colors;
import de.vorb.tesseract.gui.view.Strokes;
import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.Iterators;
import de.vorb.tesseract.util.Page;
import de.vorb.tesseract.util.Symbol;

public class BoxFileRenderer implements PageRenderer {
    private final JLabel canvas;
    private final SingleSelectionModel selectionModel;

    private SwingWorker<BufferedImage, Void> renderWorker;

    public BoxFileRenderer(JLabel canvas, SingleSelectionModel selectionModel) {
        this.canvas = canvas;
        this.selectionModel = selectionModel;
    }

    @Override
    public void render(final Page page, final BufferedImage pageBackground,
            final float scale) {
        final int w = pageBackground.getWidth();
        final int h = pageBackground.getHeight();

        final int scaledW = scaled(w, scale);
        final int scaledH = scaled(h, scale);

        final int selectedIndex = selectionModel.getSelectedIndex();

        renderWorker = new SwingWorker<BufferedImage, Void>() {
            @Override
            protected BufferedImage doInBackground() throws Exception {
                final BufferedImage rendered = new BufferedImage(scaledW,
                        scaledH, BufferedImage.TYPE_INT_RGB);

                final Graphics2D g2d = rendered.createGraphics();

                // initial color & stroke
                g2d.setColor(Colors.NORMAL);
                g2d.setStroke(Strokes.NORMAL);

                g2d.drawImage(pageBackground, 0, 0, scaledW - 1, scaledH - 1,
                        0, 0, w - 1, h - 1, null);

                final Iterator<Symbol> it = Iterators.symbolIterator(page);
                for (int symbolIndex = 0; it.hasNext(); symbolIndex++) {
                    // determine if box is selected
                    final boolean isSelected = selectedIndex == symbolIndex;

                    // draw box on canvas
                    drawSymbolBox(g2d, it.next(), scale, isSelected);
                }

                return rendered;
            }

            @Override
            public void done() {
                try {
                    canvas.setIcon(new ImageIcon(get()));
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };

        renderWorker.execute();
    }

    private void drawSymbolBox(final Graphics2D g2d, final Symbol symbol,
            final float scale, final boolean isSelected) {
        final Box bbox = symbol.getBoundingBox();

        if (isSelected) {
            g2d.setColor(Colors.SELECTION);
            g2d.setStroke(Strokes.SELECTION);
        }

        g2d.drawRect(scaled(bbox.getX(), scale), scaled(bbox.getY(), scale),
                scaled(bbox.getWidth(), scale), scaled(bbox.getHeight(), scale));

        if (isSelected) {
            g2d.setColor(Colors.NORMAL);
            g2d.setStroke(Strokes.NORMAL);
        }
    }
}
