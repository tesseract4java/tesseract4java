/*
 * Copyright (C) 2014 Universidad de Alicante
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package eu.digitisation.distance;

import eu.digitisation.document.TokenArray;
import eu.digitisation.math.MinimalPerfectHash;
import eu.digitisation.text.Text;
import eu.digitisation.text.WordSet;
import java.io.File;

/**
 * Provides linear time implementations of some popular edit distance methods
 * operating on strings
 *
 * @version 2014.01.25
 */
public class EditDistance {

    /**
     * @param s1 the first string.
     * @param s2 the second string.
     * @param w weights for basic edit operations
     * @param chunkLen the length of the chunks analyzed at every step (must be
     * strictly greater than 1)
     * @return the approximate (linear time) Levenshtein distance between first
     * and second.
     */
    public static int charDistance(String s1, String s2, EdOpWeight w, int chunkLen) {
        EditSequence seq = new EditSequence(s1, s2, w, chunkLen);
        return seq.cost(s1, s2, w);
    }

    /**
     * @param s1 the first string.
     * @param s2 the second string.
     * @param chunkLen the length of the chunks analyzed at every step (must be
     * strictly greater than 1)
     * @return the length (number of words) of first, the length (number of
     * words) of second, and the approximate (linear time) word-based
     * Levenshtein distance between first and second.
     */
    public static int[] wordDistance(String s1, String s2, int chunkLen) {
        MinimalPerfectHash mph = new MinimalPerfectHash(true); // case sensitive
        TokenArray a1 = new TokenArray(mph, s1);
        TokenArray a2 = new TokenArray(mph, s2);
        EditSequence seq = new EditSequence(a1, a2, chunkLen);
        return new int[]{a1.length(), a2.length(), seq.length()};
    }

    /**
     * @param s1 the first string.
     * @param s2 the second string.
     * @param stopwords a set of stop-words
     * @param chunkLen the length of the chunks analyzed at every step (must be
     * strictly greater than 1)
     * @return the length (number of words) of first, the length (number of
     * words) of second, and the approximate (linear time) word-based
     * Levenshtein distance between first and second. Whenever a stop-word in
     * the first string is aligned (substituted) with a different target word in
     * the second string or with no string at all (deleted), this difference
     * does not contribute to the distance
     */
    public static int[] wordDistance(String s1, String s2,
            WordSet stopwords, int chunkLen) {
        MinimalPerfectHash mph = new MinimalPerfectHash(true); // case sensitive
        TokenArray a1 = new TokenArray(mph, s1);
        TokenArray a2 = new TokenArray(mph, s2);
        EditSequence seq = new EditSequence(a1, a2, chunkLen);
        int d = 0;
        int n1 = 0;
        int n2 = 0;
        for (EdOp op : seq.ops) {
            String word = a1.wordAt(n1);
            if (op != EdOp.KEEP && !stopwords.contains(word)) {
                ++d;
            }
            if (op != EdOp.INSERT) {
                ++n1;
            }
            if (op != EdOp.DELETE) {
                ++n2;
            }
        }
        return new int[]{a1.length(), a2.length(), d};
    }

    /**
     *
     * @param first the first string.
     * @param second the second string.
     * @param chunkLen the length of the chunks analyzed at every step
     * @param type the type of distance to be computed
     * @return the distance between first and second (defaults to Levenshtein)
     * @throws java.lang.NoSuchMethodException
     */
    public static int distance(String first, String second,
            int chunkLen, EditDistanceType type)
            throws NoSuchMethodException {
        switch (type) {
            case OCR_CHAR:
                EdOpWeight w = new OcrOpWeight();
                return charDistance(first, second, w, chunkLen);
            case OCR_WORD:
                return wordDistance(first, second, chunkLen)[2];
            default:
                throw new java.lang.NoSuchMethodException(type
                        + " distance still to be implemented");

        }
    }

    public static void main(String[] args) throws Exception {
        File f1 = new File(args[0]);
        File f2 = new File(args[1]);
        int len = Integer.parseInt(args[2]);
        String s1 = new Text(f1).toString();
        String s2 = new Text(f2).toString();
        int d = EditDistance.distance(s1, s2, len, EditDistanceType.OCR_CHAR);
        System.out.println(d);
        d = EditDistance.distance(s1, s2, len, EditDistanceType.OCR_WORD);
        System.out.println(d);
    }
}
