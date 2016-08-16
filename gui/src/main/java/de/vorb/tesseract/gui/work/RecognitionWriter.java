package de.vorb.tesseract.gui.work;

import java.io.IOException;
import java.io.Writer;

import de.vorb.tesseract.util.Page;

public interface RecognitionWriter {
    void write(Page page, Writer writer) throws IOException;
}
