package de.vorb.tesseract.gui.view.renderer;

import de.vorb.tesseract.gui.model.BoxFileModel;
import de.vorb.tesseract.gui.view.BoxEditor;
import de.vorb.tesseract.gui.view.Colors;
import de.vorb.tesseract.gui.view.Strokes;
import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.Symbol;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.ListModel;
import javax.swing.SwingWorker;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import static de.vorb.tesseract.gui.model.Scale.scaled;

public class BoxFileRenderer {
    private final BoxEditor boxEditor;

    private SwingWorker<BufferedImage, Void> renderWorker;

    public BoxFileRenderer(BoxEditor boxEditor) {
        this.boxEditor = boxEditor;
    }

    public void render(final Optional<BoxFileModel> model, final float scale) {
        if (!model.isPresent()) {
            // remove image, if no page model is given and free resources
            final Icon icon = boxEditor.getCanvas().getIcon();
            if (icon != null && icon instanceof ImageIcon) {
                ((ImageIcon) icon).getImage().flush();
            }

            boxEditor.getCanvas().setIcon(null);

            renderWorker = null;

            return;
        }

        // TODO add a version of render() that takes two rectangles and a new
        // box and updates the necessary area only
        final BufferedImage image = model.get().getImage();

        // calculate image dimensions
        final int w = image.getWidth();
        final int h = image.getHeight();

        final int scaledW = scaled(w, scale);
        final int scaledH = scaled(h, scale);

        // try to cancel the last rendering task
        if (renderWorker != null && !renderWorker.isCancelled()
                && !renderWorker.isDone()) {
            renderWorker.cancel(true);
        }

        // create an array of all currently visible symbols
        final ListModel<Symbol> listModel =
                boxEditor.getSymbols().getListModel();
        final Symbol[] symbols = new Symbol[listModel.getSize()];

        // remember index of selected symbol in the table
        final int selectedIndex =
                boxEditor.getSymbols().getTable().getSelectedRow();

        // fill array and save ref to selected symbol
        Symbol selSym = null;
        for (int i = 0; i < symbols.length; i++) {
            final Symbol symbol = listModel.getElementAt(i);
            if (selectedIndex == i) {
                selSym = symbol;
            }
            symbols[i] = symbol;
        }
        final Symbol selectedSymbol = selSym;

        // worker that renders the boxes to a new buffered image
        renderWorker = new SwingWorker<BufferedImage, Void>() {
            @Override
            protected BufferedImage doInBackground() throws Exception {
                final BufferedImage rendered = new BufferedImage(scaledW,
                        scaledH, BufferedImage.TYPE_INT_RGB);

                final Graphics2D g2d = rendered.createGraphics();

                // initial color & stroke
                g2d.setColor(Colors.NORMAL);
                g2d.setStroke(Strokes.NORMAL);

                // draw the scaled image
                g2d.drawImage(image, 0, 0, scaledW, scaledH, 0, 0,
                        w - 1, h - 1, null);

                for (final Symbol symbol : symbols) {
                    final boolean isSelected = symbol == selectedSymbol;
                    // draw box on canvas
                    drawSymbolBox(g2d, symbol, scale, isSelected);
                }

                // dispose context
                g2d.dispose();

                return rendered;
            }

            @Override
            public void done() {
                try {
                    // draw the rendered image
                    boxEditor.getCanvas().setIcon(new ImageIcon(get()));
                } catch (InterruptedException | ExecutionException
                        | CancellationException e) {
                    // ignore interrupts of any kind, those are intended
                } finally {
                    System.gc();
                }
            }
        };

        renderWorker.execute();
    }

    private void drawSymbolBox(final Graphics2D g2d, final Symbol symbol,
            final float scale, final boolean isSelected) {
        final Box boundingBox = symbol.getBoundingBox();

        // set selected colors
        if (isSelected) {
            g2d.setPaint(Colors.SELECTION_BG);
            g2d.fillRect(scaled(boundingBox.getX(), scale),
                    scaled(boundingBox.getY(), scale),
                    scaled(boundingBox.getWidth(), scale),
                    scaled(boundingBox.getHeight(), scale));

            g2d.setPaint(Colors.SELECTION);
            g2d.setStroke(Strokes.SELECTION);
        }

        // draw the box
        g2d.drawRect(scaled(boundingBox.getX(), scale), scaled(boundingBox.getY(), scale),
                scaled(boundingBox.getWidth(), scale), scaled(boundingBox.getHeight(), scale));

        // unset selected colors
        if (isSelected) {
            g2d.setColor(Colors.NORMAL);
            g2d.setStroke(Strokes.NORMAL);
        }
    }
}
