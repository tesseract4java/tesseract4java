package de.vorb.tesseract.tools.training;

import de.vorb.tesseract.util.Block;
import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.Line;
import de.vorb.tesseract.util.Page;
import de.vorb.tesseract.util.Paragraph;
import de.vorb.tesseract.util.Symbol;
import de.vorb.tesseract.util.Word;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;

/**
 * Methods for creating box files.
 *
 * @author Paul Vorbach
 */
public final class BoxFiles {

    private BoxFiles() {}

    /**
     * Writes a single page to the given Writer in the box file format.
     *
     * @param out
     * @param page
     * @param pageIndex
     * @throws IOException
     */
    public static void writePageTo(Writer out, Page page, int pageIndex)
            throws IOException {
        for (final Block block : page.getBlocks()) {
            for (final Paragraph para : block.getParagraphs()) {
                for (final Line line : para.getLines()) {
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
        }
    }

    /**
     * Writes multiple pages to the given Writer in the box file format.
     *
     * @param out
     * @param pages
     * @throws IOException
     */
    public static void writeTo(Writer out, List<Page> pages)
            throws IOException {
        int i = 0;
        for (final Page page : pages) {
            writePageTo(out, page, i++);
        }
    }

    /**
     * Creates or overwrites the given file with multiple pages in the box file
     * format.
     *
     * @param file
     * @param pages
     * @throws IOException
     */
    public static void writeTo(Path file, List<Page> pages) throws IOException {
        Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file.toFile()), Charset.forName("UTF-8")));

        writeTo(out, pages);

        out.close();
    }
}
