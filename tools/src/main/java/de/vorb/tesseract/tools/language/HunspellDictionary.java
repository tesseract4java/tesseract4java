package de.vorb.tesseract.tools.language;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HunspellDictionary {

    public static void main(String[] args) throws IOException {
        final BufferedReader in = Files.newBufferedReader(
                Paths.get("E:/Masterarbeit/Ressourcen/Wörterbücher/de_DE_frami.dic"),
                Charset.forName("windows-1252"));

        final BufferedWriter out = Files.newBufferedWriter(
                Paths.get("E:/Masterarbeit/Ressourcen/Wörterbücher/de_DE_frami.words-list"),
                StandardCharsets.UTF_8);

        int wordCount = -1;
        String line = null;
        while ((line = in.readLine()) != null) {
            if (line.isEmpty() || line.startsWith("#") || line.startsWith("\t")
                    || wordCount == -1) {
                wordCount = Math.max(0, wordCount);
                continue;
            }

            final String[] split = line.split("/");

            out.write(split[0]);
            out.write('\n');

            wordCount++;
        }

        System.out.println(String.format("%d words in the dictionary.",
                wordCount));

        in.close();
        out.close();
    }

}
