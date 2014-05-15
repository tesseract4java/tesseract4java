package de.vorb.tesseract.gui.view.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.Line;
import de.vorb.tesseract.util.Page;
import de.vorb.tesseract.util.Symbol;
import de.vorb.tesseract.util.Word;

public class BoxFileRenderer implements PageRenderer {
    private final ImageIcon canvas;

    public BoxFileRenderer(ImageIcon canvas) {
        this.canvas = canvas;
    }

    @Override
    public void render(Page page, BufferedImage pageBackground, float scale) {
        final BufferedImage img = pageBackground;
        canvas.setImage(img);
        final Graphics2D g2d = img.createGraphics();

        g2d.setColor(Color.BLUE);

        for (Line line : page.getLines()) {
            for (Word word : line.getWords()) {
                for (Symbol symbol : word.getSymbols()) {
                    drawSymbolBox(g2d, symbol, scale);
                }
            }
        }
    }

    private void drawSymbolBox(Graphics2D g2d, Symbol symbol, float scale) {
        final Box bbox = symbol.getBoundingBox();

        g2d.drawRect(scaled(bbox.getX(), scale), scaled(bbox.getY(), scale),
                scaled(bbox.getWidth(), scale), scaled(bbox.getHeight(), scale));
    }

    private static int scaled(int coord, float scale) {
        return coord;
    }
}
