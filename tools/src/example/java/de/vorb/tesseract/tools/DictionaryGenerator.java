package de.vorb.tesseract.tools;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

import de.vorb.tesseract.tools.eval.LevenshteinWordDistance;

public class DictionaryGenerator {

    public static void main(String[] args) throws IOException {
        final Path total = Paths.get("E:/Masterarbeit/Ressourcen/DE-20__32_AM_49000_L869_G927-1/reviewed/combined.txt");

        // hash map that contains all words with the absolute number of
        // occurrences
        final HashMap<String, Integer> dict = new HashMap<>();
        for (String word : LevenshteinWordDistance.readWordList(total)) {
            word = sanitizeWord(word);

            // no empty words
            if (word.length() == 0)
                continue;

            if (!dict.containsKey(word)) {
                dict.put(word, 1);
            } else {
                dict.put(word, dict.get(word) + 1);
            }
        }

        final ArrayList<Entry<String, Integer>> sortedDict = new ArrayList<>(
                dict.entrySet());
        Collections.sort(sortedDict, new Comparator<Entry<String, Integer>>() {
            @Override
            public int compare(Entry<String, Integer> o1,
                    Entry<String, Integer> o2) {
                final int v1 = o1.getValue();
                final int v2 = o2.getValue();
                return v1 < v2 ? 1 : v1 > v2 ? -1 : o1.getKey().compareTo(
                        o2.getKey());
            }
        });

        final BufferedWriter dictFile = Files.newBufferedWriter(
                Paths.get("E:/Masterarbeit/Ressourcen/DE-20__32_AM_49000_L869_G927-1/reviewed/dict.txt"),
                Charset.forName("UTF-8"));
        final BufferedWriter wordList = Files.newBufferedWriter(
                Paths.get("E:/Masterarbeit/Ressourcen/DE-20__32_AM_49000_L869_G927-1/reviewed/deu-frak-dict-adapt.word-list"),
                Charset.forName("UTF-8"));
        final BufferedWriter freqWordList = Files.newBufferedWriter(
                Paths.get("E:/Masterarbeit/Ressourcen/DE-20__32_AM_49000_L869_G927-1/reviewed/deu-frak-dict-adapt.freq-list"),
                Charset.forName("UTF-8"));
        final BufferedWriter userWordList = Files.newBufferedWriter(
                Paths.get("E:/Masterarbeit/Ressourcen/DE-20__32_AM_49000_L869_G927-1/reviewed/deu-frak-dict-adapt.user-words"),
                Charset.forName("UTF-8"));

        for (final Entry<String, Integer> entry : sortedDict) {
            dictFile.write(entry.getKey() + " " + entry.getValue() + "\n");
        }

        Collections.sort(sortedDict, new Comparator<Entry<String, Integer>>() {
            @Override
            public int compare(Entry<String, Integer> o1,
                    Entry<String, Integer> o2) {
                return o1.getKey().toLowerCase().compareTo(
                        o2.getKey().toLowerCase());
            }
        });

        for (final Entry<String, Integer> entry : sortedDict) {
            userWordList.write(entry.getKey() + "\n");

            if (entry.getValue() > 5) {
                freqWordList.write(entry.getKey() + " " + entry.getValue()
                        + "\n");
            }

            if (entry.getValue() > 0) {
                wordList.write(entry.getKey() + " " + entry.getValue() + "\n");
            }
        }

        dictFile.close();
        wordList.close();
        freqWordList.close();
    }

    private static String sanitizeWord(String word) {
        return word.replaceFirst("^[ \t-\\.;:,_<>\"„“!?)('*\ufeff]+", "").replaceFirst(
                "[ \t-\\.;:,_<>\"„“!?)('*\ufeff]+$", "");
    }
}
