package de.vorb.tesseract.tools.eval;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class LevenshteinWordDistance {
    public static List<String> readWordList(Path file) throws IOException {
        final byte[] bytes = Files.readAllBytes(file);
        final String text = new String(bytes, Charset.forName("UTF-8"));
        final String[] words = text.split("\\s+");

        if (words[0].length() == 0)
            return Arrays.asList(words).subList(0, words.length - 1);
        else
            return Arrays.asList(words);
    }

    public static int distanceWords(List<String> a, List<String> b) {
        final int lenA = a.size() + 1;
        final int lenB = b.size() + 1;

        // the array of distances
        int[] cost = new int[lenA];
        int[] newcost = new int[lenA];

        // initial cost of skipping prefix in String s0
        for (int i = 0; i < lenA; i++)
            cost[i] = i;

        // dynamicaly computing the array of distances

        // transformation cost for each letter in s1
        for (int j = 1; j < lenB; j++) {

            // initial cost of skipping prefix in String s1
            newcost[0] = j - 1;

            // transformation cost for each letter in s0
            for (int i = 1; i < lenA; i++) {

                // matching current words in both strings
                int match = (a.get(i - 1).equals(b.get(j - 1))) ? 0 : 1;

                // computing cost for each transformation
                int cost_replace = cost[i - 1] + match;
                int cost_insert = cost[i] + 1;
                int cost_delete = newcost[i - 1] + 1;

                // keep minimum cost
                newcost[i] = Math.min(Math.min(cost_insert, cost_delete),
                        cost_replace);
            }

            // swap cost/newcost arrays
            int[] swap = cost;
            cost = newcost;
            newcost = swap;
        }

        // the distance is the cost for transforming all letters in both strings
        return cost[lenA - 1];
    }

    public static int distance(String str0, String str1) {
        int len0 = str0.length() + 1;
        int len1 = str1.length() + 1;

        // the array of distances
        int[] cost = new int[len0];
        int[] newcost = new int[len0];

        // initial cost of skipping prefix in String s0
        for (int i = 0; i < len0; i++)
            cost[i] = i;

        // dynamicaly computing the array of distances

        // transformation cost for each letter in s1
        for (int j = 1; j < len1; j++) {

            // initial cost of skipping prefix in String s1
            newcost[0] = j - 1;

            // transformation cost for each letter in s0
            for (int i = 1; i < len0; i++) {

                // matching current letters in both strings
                int match = (str0.charAt(i - 1) == str1.charAt(j - 1)) ? 0 : 1;

                // computing cost for each transformation
                int cost_replace = cost[i - 1] + match;
                int cost_insert = cost[i] + 1;
                int cost_delete = newcost[i - 1] + 1;

                // keep minimum cost
                newcost[i] = Math.min(Math.min(cost_insert, cost_delete),
                        cost_replace);
            }

            // swap cost/newcost arrays
            int[] swap = cost;
            cost = newcost;
            newcost = swap;
        }

        // the distance is the cost for transforming all letters in both strings
        return cost[len0 - 1];
    }

    private static String join(List<String> words) {
        final StringBuilder result = new StringBuilder();
        for (String word : words) {
            result.append(word);
        }
        return result.toString();
    }

    public static void main(String[] args) throws IOException {
        final Path tess = Paths.get("E:/Masterarbeit/Ressourcen/DE-20__32_AM_49000_L869_G927-1/reviewed/tess_otsu_nodawg");
        final Path hand = Paths.get("E:/Masterarbeit/Ressourcen/DE-20__32_AM_49000_L869_G927-1/reviewed/hand");

        final DirectoryStream<Path> handFiles = Files.newDirectoryStream(hand);

        Path tessFile;
        int wordDist;
        int charDist;

        int sumWordDists = 0;
        int sumWords = 0;
        int sumCharDists = 0;
        int sumChars = 0;

        final BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(
                                "E:/Masterarbeit/Ressourcen/DE-20__32_AM_49000_L869_G927-1/reviewed/word_comparison_otsu_nodawg.txt"),
                        Charset.forName("UTF-8").newEncoder()));

        for (Path handFile : handFiles) {
            tessFile = tess.resolve(handFile.getFileName());

            final List<String> tessWords = readWordList(tessFile);
            final List<String> handWords = readWordList(handFile);

            final String handJoined = join(handWords);
            wordDist = distanceWords(tessWords, handWords);
            charDist = distance(join(tessWords), handJoined);

            sumWordDists += wordDist;
            sumCharDists += charDist;
            sumWords += handWords.size();
            sumChars += handJoined.length();

            writer.write("word-dist = " + wordDist + "/" + handWords.size()
                    + ", char-dist = " + charDist + "/" + handJoined.length()
                    + " (" + handFile.getFileName() + ")\n");
        }

        writer.write("total: word-dist = " + sumWordDists + "/" + sumWords
                + ", char-dist = " + sumCharDists + "/" + sumChars);

        writer.close();
    }
}
