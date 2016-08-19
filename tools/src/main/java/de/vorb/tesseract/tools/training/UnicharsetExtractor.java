package de.vorb.tesseract.tools.training;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UnicharsetExtractor {
    public static Set<String> extractCharacterSet(Iterable<Path> files)
            throws IOException {
        final Set<String> charset = new HashSet<>();

        for (final Path file : files) {
            final List<String> lines = Files.readAllLines(file,
                    Charset.forName("UTF-8"));

            for (final String line : lines) {
                if (line.length() == 0) {
                    continue;
                }

                final String[] tokens = line.split("\\s+");
                if (!"".equals(tokens[0])) {
                    charset.add(tokens[0]);
                }
            }
        }

        return charset;
    }

    public static void main(String[] args) throws IOException {
        final DirectoryStream<Path> dir =
                Files.newDirectoryStream(
                        Paths.get("E:/Masterarbeit/Ressourcen/tessdata/experiment04-complete-training"),
                        new DirectoryStream.Filter<Path>() {
                            @Override
                            public boolean accept(Path entry)
                                    throws IOException {
                                return entry.getFileName().toString().endsWith(
                                        ".box");
                            }
                        });

        final Set<String> charset = extractCharacterSet(dir);

        final BufferedWriter out = Files.newBufferedWriter(
                Paths.get(
                        "E:/Masterarbeit/Ressourcen/tessdata/experiment04-complete-training/deu-frak-fries.unicharset"),
                Charset.forName("UTF-8"));

        int id = 0;
        for (final String c : charset) {
            final CharacterProperties props = CharacterProperties.forString(c);
            out.append(c + " " + props.toHexString() + " " + "NULL" + " "
                    + (id++) + "\n");
        }

        out.close();
    }
}
