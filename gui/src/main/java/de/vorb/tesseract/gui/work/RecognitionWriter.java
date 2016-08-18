package de.vorb.tesseract.gui.work;

import de.vorb.tesseract.util.Page;

import java.io.IOException;
import java.io.Writer;

public interface RecognitionWriter {
    void write(Page page, Writer writer) throws IOException;
}
