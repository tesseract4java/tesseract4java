package de.vorb.tesseract.gui.work;

import java.io.IOException;
import java.io.Writer;

import de.vorb.tesseract.util.Line;
import de.vorb.tesseract.util.Page;
import de.vorb.tesseract.util.Word;

public class PlainTextWriter implements RecognitionWriter {
    @Override
    public void write(Page page, Writer writer) throws IOException {
        for (final Line line : page.getLines()) {
            final int wordsInLine = line.getWords().size();
            int wordIndex = 0;
            for (final Word word : line.getWords()) {
                writer.write(word.getText());

                if (++wordIndex < wordsInLine) {
                    writer.write(' ');
                }
            }

            writer.write("\n");
        }
    }
}
