package de.vorb.tesseract.tools.training;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.Line;
import de.vorb.tesseract.util.Page;
import de.vorb.tesseract.util.Symbol;
import de.vorb.tesseract.util.Word;

public class BoxFiles {
    private BoxFiles() {
    }

    /**
     * Writes the page
     * 
     * @param out
     * @param page
     * @param pageIndex
     * @throws IOException
     */
    public static void writePageTo(Writer out, Page page, int pageIndex)
            throws IOException {
        for (final Line line : page.getLines()) {
            for (final Word word : line.getWords()) {
                for (final Symbol symbol : word.getSymbols()) {
                    final Box box = symbol.getBoundingBox();

                    // line format: text x y width height index
                    out.append(symbol.getText()).append(' ').append(
                            String.valueOf(box.getX())).append(' ').append(
                            String.valueOf(box.getY())).append(' ').append(
                            String.valueOf(box.getWidth())).append(' ').append(
                            String.valueOf(box.getHeight())).append(' ').append(
                            String.valueOf(pageIndex)).append('\n');
                }
            }
        }
    }

    public static void writeTo(Writer out, List<Page> pages)
            throws IOException {
        int i = 0;
        for (final Page page : pages) {
            writePageTo(out, page, i++);
        }
    }
}
