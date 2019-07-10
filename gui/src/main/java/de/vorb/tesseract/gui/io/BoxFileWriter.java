package de.vorb.tesseract.gui.io;

import de.vorb.tesseract.gui.model.BoxFile;
import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.Symbol;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

public final class BoxFileWriter {

    private BoxFileWriter() {
    }

    public static void writeBoxFile(BoxFile boxFile) throws IOException {
        try (final BufferedWriter boxFileWriter = Files.newBufferedWriter(boxFile.getFilePath(),
                StandardCharsets.UTF_8, CREATE, TRUNCATE_EXISTING, WRITE)) {

            final int pageHeight = boxFile.getImage().getHeight();

            for (Symbol symbol : boxFile.getBoxes()) {

                final Box boundingBox = symbol.getBoundingBox();
                final int x0 = boundingBox.getX();
                final int y0 = pageHeight - boundingBox.getY() - boundingBox.getHeight();
                final int x1 = x0 + boundingBox.getWidth();
                final int y1 = y0 + boundingBox.getHeight();

                boxFileWriter.write(String.format("%s %d %d %d %d 0\n", symbol.getText(), x0, y0, x1, y1));
            }
        }
    }
}
