package de.vorb.tesseract.gui.io;

import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.Symbol;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class BoxFileReader {

    private BoxFileReader() {
    }

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    public static List<Symbol> readBoxFile(Path boxFile, int pageHeight)
            throws IOException {

        return Files.lines(boxFile, StandardCharsets.UTF_8)
                .map(WHITESPACE_PATTERN::split)
                .filter(components -> components.length >= 5)
                .map(symbolParser(pageHeight))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static Function<String[], Symbol> symbolParser(int pageHeight) {
        return (String[] components) -> {
            try {
                final String text = components[0];
                final int x = Integer.parseInt(components[1]);
                final int y = Integer.parseInt(components[2]);
                final int w = Integer.parseInt(components[3]) - x;
                final int h = Integer.parseInt(components[4]) - y;

                final Box boundingBox = new Box(x, pageHeight - y - h, w, h);
                return new Symbol(text, boundingBox, 0f);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Could not parse line \"" + String.join(" ", components) + '"', e);
            }
        };
    }
}
