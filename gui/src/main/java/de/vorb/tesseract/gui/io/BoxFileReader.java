package de.vorb.tesseract.gui.io;

import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.Symbol;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public final class BoxFileReader {

    private BoxFileReader() {}

    public static List<Symbol> readBoxFile(Path boxFile, int pageHeight)
            throws IOException {

        final List<Symbol> boxes = new LinkedList<>();
        try (final BufferedReader boxReader = Files.newBufferedReader(boxFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = boxReader.readLine()) != null) {
                final String[] components = line.split("\\s+");
                if (components.length < 5) {
                    continue;
                }

                final String text = components[0];
                final int x = Integer.parseInt(components[1]);
                final int y = Integer.parseInt(components[2]);
                final int w = Integer.parseInt(components[3]) - x;
                final int h = Integer.parseInt(components[4]) - y;

                boxes.add(new Symbol(text,
                        new Box(x, pageHeight - y - h, w, h), 0f));
            }
        } catch (NumberFormatException e) {
            throw new IOException();
        }

        return boxes;
    }
}
