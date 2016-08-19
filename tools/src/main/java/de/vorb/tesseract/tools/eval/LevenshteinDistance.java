package de.vorb.tesseract.tools.eval;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Implementation of the Levenshtein distance with the Hirshberg algorithm.
 *
 * @author Paul Vorbach
 */
public class LevenshteinDistance extends EditDistance {
    public int distance(String a, String b) {
        final int aLen = a.length() + 1;
        final int bLen = b.length() + 1;

        // the array of distances
        int[] cost = new int[aLen];
        int[] newcost = new int[aLen];

        // initial cost of skipping prefix in String s0
        for (int i = 0; i < aLen; i++)
            cost[i] = i;

        // dynamicaly computing the array of distances

        // transformation cost for each letter in s1
        for (int j = 1; j < bLen; j++) {

            // initial cost of skipping prefix in String s1
            newcost[0] = j - 1;

            // transformation cost for each letter in s0
            for (int i = 1; i < aLen; i++) {

                // matching current letters in both strings
                final int match = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;

                // computing cost for each transformation
                final int cost_replace = cost[i - 1] + match;
                final int cost_insert = cost[i] + 1;
                final int cost_delete = newcost[i - 1] + 1;

                // keep minimum cost
                newcost[i] = Math.min(Math.min(cost_insert, cost_delete),
                        cost_replace);
            }

            // swap cost/newcost arrays
            final int[] swap = cost;
            cost = newcost;
            newcost = swap;
        }

        // the distance is the cost for transforming all letters in both strings
        return cost[aLen - 1];
    }

    public static void main(String[] args) throws IOException {
        final LevenshteinDistance levenshtein = new LevenshteinDistance();

        long start = System.currentTimeMillis();
        Path tess = Paths.get(
                "E:\\Masterarbeit\\Ressourcen\\DE-20__32_AM_49000_L869_G927-1\\reviewed\\tess_sauvola\\DE"
                        + "-20__32_AM_49000_L869_G927-1__0068__0041.txt");
        Path fine = Paths.get(
                "E:\\Masterarbeit\\Ressourcen\\DE-20__32_AM_49000_L869_G927-1\\reviewed\\hand\\DE"
                        + "-20__32_AM_49000_L869_G927-1__0068__0041.txt");

        System.out.println(levenshtein.distance(
                Files.newBufferedReader(tess, StandardCharsets.UTF_8),
                Files.newBufferedReader(fine, StandardCharsets.UTF_8)));
        System.out.println(System.currentTimeMillis() - start + "ms");
    }
}
