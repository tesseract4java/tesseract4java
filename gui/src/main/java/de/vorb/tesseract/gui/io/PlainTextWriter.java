package de.vorb.tesseract.gui.io;

import de.vorb.tesseract.gui.work.RecognitionWriter;
import de.vorb.tesseract.util.Block;
import de.vorb.tesseract.util.Line;
import de.vorb.tesseract.util.Page;
import de.vorb.tesseract.util.Paragraph;
import de.vorb.tesseract.util.Word;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

public class PlainTextWriter implements RecognitionWriter {
    public PlainTextWriter(boolean replaceHighLetterSpacing) {
    }

    @Override
    public void write(Page page, Writer writer) throws IOException {
        final Iterator<Block> blockIt = page.blockIterator();
        while (blockIt.hasNext()) {
            final Block block = blockIt.next();

            for (final Paragraph para : block.getParagraphs()) {
                for (final Line line : para.getLines()) {
                    final int wordsInLine = line.getWords().size();
                    int wordIndex = 0;

                    StringBuilder wordBuilder = new StringBuilder();

                    for (final Word word : line.getWords()) {
                        final String wordText = word.getText();

                        if (wordText.length() > 1) {
                            if (wordBuilder.length() > 0) {
                                writer.write(wordBuilder.toString());

                                wordBuilder = new StringBuilder();

                                writer.write(' ');
                            }

                            writer.write(wordText);
                        } else {
                            wordBuilder.append(word.getText());
                        }

                        // prevent space after last word in the line
                        if (++wordIndex < wordsInLine
                                && wordBuilder.length() == 0) {
                            writer.write(' ');
                        } else if (wordBuilder.length() > 0) {
                            writer.write(wordBuilder.toString());

                            wordBuilder = new StringBuilder();
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
