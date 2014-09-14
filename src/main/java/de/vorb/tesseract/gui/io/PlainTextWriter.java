package de.vorb.tesseract.gui.io;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import de.vorb.tesseract.gui.work.RecognitionWriter;
import de.vorb.tesseract.util.Block;
import de.vorb.tesseract.util.Line;
import de.vorb.tesseract.util.Page;
import de.vorb.tesseract.util.Paragraph;
import de.vorb.tesseract.util.Word;

public class PlainTextWriter implements RecognitionWriter {
    @Override
    public void write(Page page, Writer writer) throws IOException {
        final Iterator<Block> blockIt = page.blockIterator();
        while (blockIt.hasNext()) {
            final Block block = blockIt.next();

            for (final Paragraph para : block.getParagraphs()) {
                for (final Line line : para.getLines()) {
                    final int wordsInLine = line.getWords().size();
                    int wordIndex = 0;

                    for (final Word word : line.getWords()) {
                        writer.write(word.getText());

                        // prevent space after last word in the line
                        if (++wordIndex < wordsInLine) {
                            writer.write(' ');
                        }
                    }

                    writer.write("\n");
                }

                writer.write('\n');
            }

            writer.write('\n');
        }
    }
}
