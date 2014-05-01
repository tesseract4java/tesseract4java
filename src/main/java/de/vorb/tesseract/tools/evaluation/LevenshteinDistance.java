package de.vorb.tesseract.tools.evaluation;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Implementation of the Levenshtein distance with the Hirshberg algorithm.
 * 
 * @author Paul Vorbach
 */
public class LevenshteinDistance {
  private static int min(int a, int b, int c) {
    return Math.min(Math.min(a, b), c);
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
        newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
      }

      // swap cost/newcost arrays
      int[] swap = cost;
      cost = newcost;
      newcost = swap;
    }

    // the distance is the cost for transforming all letters in both strings
    return cost[len0 - 1];
  }

  private static String readFile(Path path) throws IOException {
    byte[] encoded = Files.readAllBytes(path);
    return new String(encoded, Charset.forName("UTF-8"));
  }

  public static void main(String[] args) throws IOException {
    long start = System.currentTimeMillis();
    Path tess = Paths.get("E:\\Masterarbeit\\Ressourcen\\DE-20__32_AM_49000_L869_G927-1\\deu-frak\\complete-tesseract.txt");
    Path fine = Paths.get("E:\\Masterarbeit\\Ressourcen\\DE-20__32_AM_49000_L869_G927-1\\korrigiert\\gesamt.txt");

    System.out.println(distance(readFile(tess), readFile(fine)));
    System.out.println(System.currentTimeMillis() - start + "ms");
  }
}
