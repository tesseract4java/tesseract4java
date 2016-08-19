package de.vorb.tesseract.tools.eval;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LevenshteinDiff {
    public static void main(String[] args) throws IOException {
        final Path f = Paths.get(
                "E:/Masterarbeit/Ressourcen/DE-20__32_AM_49000_L869_G927-1/deu-frak/plain/DE"
                        + "-20__32_AM_49000_L869_G927-1__0094__0067.txt");

        final BufferedReader r = Files.newBufferedReader(f,
                Charset.forName("UTF-8"));

        int c0 = -1;
        int c1 = -1;

        final List<String> words = new ArrayList<>();
        StringBuilder word = new StringBuilder();
        while ((c1 = r.read()) != -1) {
            if (c0 == '-' && c1 == '\n') {
                // delete last character
                word.deleteCharAt(word.length() - 1);
            } else if (Character.isWhitespace(c1)) {
                if (word.length() > 0) {
                    words.add(word.toString());
                    word = new StringBuilder();
                }
            } else {
                word.append((char) c1);
            }

            c0 = c1;
        }

        for (final String w : words) {
            System.out.println(w);
        }
    }
}
