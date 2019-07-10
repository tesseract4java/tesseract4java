/*
 * Copyright (C) 2013 Universidad de Alicante
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

import eu.digitisation.math.BiCounter;
import eu.digitisation.output.ErrorMeasure;
import eu.digitisation.log.Messages;

/**
 * Provides basic implementations of some popular edit distance methods
 * operating on strings (currently, Levenshtein and indel)
 *
 * @version 2011.03.10
 */
public class StringEditDistance {

    /**
     * @return 3-wise minimum.
     */
    private static int min(int x, int y, int z) {
        return Math.min(x, Math.min(y, z));
    }

    /**
     * @param first the first string.
     * @param second the second string.
     * @return the indel distance between first and second.
     */
    public static int indel(String first, String second) {
        int i, j;
        int[][] A = new int[2][second.length() + 1];

        // Compute first row
        A[0][0] = 0;
        for (j = 1; j <= second.length(); ++j) {
            A[0][j] = A[0][j - 1] + 1;
        }

        // Compute other rows
        for (i = 1; i <= first.length(); ++i) {
            A[i % 2][0] = A[(i - 1) % 2][0] + 1;
            for (j = 1; j <= second.length(); ++j) {
                if (first.charAt(i - 1) == second.charAt(j - 1)) {
                    A[i % 2][j] = A[(i - 1) % 2][j - 1];
                } else {
                    A[i % 2][j] = Math.min(A[(i - 1) % 2][j] + 1,
                            A[i % 2][j - 1] + 1);
                }
            }
        }
        return A[first.length() % 2][second.length()];
    }

    /**
     * @param first the first string.
     * @param second the second string.
     * @return the Levenshtein distance between first and second.
     */
    public static int levenshtein(String first, String second) {
        int i, j;
        int[][] A;

        // intialize
        A = new int[2][second.length() + 1];

        // Compute first row
        A[0][0] = 0;
        for (j = 1; j <= second.length(); ++j) {
            A[0][j] = A[0][j - 1] + 1;
        }

        // Compute other rows
        for (i = 1; i <= first.length(); ++i) {
            A[i % 2][0] = A[(i - 1) % 2][0] + 1;
            for (j = 1; j <= second.length(); ++j) {
                if (first.charAt(i - 1) == second.charAt(j - 1)) {
                    A[i % 2][j] = A[(i - 1) % 2][j - 1];
                } else {
                    A[i % 2][j] = min(A[(i - 1) % 2][j] + 1,
                            A[i % 2][j - 1] + 1,
                            A[(i - 1) % 2][j - 1] + 1);
                }
            }
        }
        return A[first.length() % 2][second.length()];
    }

    /**
     * @param first the first string.
     * @param second the second string.
     * @return the Damerau-Levenshtein distance between first and second.
     */
    public static int DL(String first, String second) {
        int i, j;
        int[][] A;

        // intialize
        A = new int[3][second.length() + 1];

        // Compute first row
        A[0][0] = 0;
        for (j = 1; j <= second.length(); ++j) {
            A[0][j] = A[0][j - 1] + 1;
        }

        // Compute other rows
        for (i = 1; i <= first.length(); ++i) {
            A[i % 3][0] = A[(i - 1) % 3][0] + 1;
            for (j = 1; j <= second.length(); ++j) {
                if (first.charAt(i - 1) == second.charAt(j - 1)) {
                    A[i % 3][j] = A[(i - 1) % 3][j - 1];
                } else {
                    if (i > 1 && j > 1
                            && first.charAt(i - 1) == second.charAt(j - 2)
                            && first.charAt(i - 2) == second.charAt(j - 1)) {
                        A[i % 3][j] = min(A[(i - 1) % 3][j] + 1,
                                A[i % 3][j - 1] + 1,
                                A[(i - 2) % 3][j - 2] + 1);
                    } else {
                        A[i % 3][j] = min(A[(i - 1) % 3][j] + 1,
                                A[i % 3][j - 1] + 1,
                                A[(i - 1) % 3][j - 1] + 1);
                    }
                }
            }
        }
        return A[first.length() % 3][second.length()];
    }

    /**
     *
     * @param first the first string.
     * @param second the second string.
     * @param type the type of distance to be computed
     * @return the distance between first and second (defaults to Levenshtein)
     */
    public static int distance(String first, String second, EditDistanceType type) {
        switch (type) {
            case INDEL:
                return indel(first, second);
            case LEVENSHTEIN:
                return levenshtein(first, second);
            case DAMERAU_LEVENSHTEIN:
                return DL(first, second);
            default:
                return levenshtein(first, second);
        }
    }

    /**
     * Computes the number of edit operations per character
     *
     * @param first the reference text
     * @param second the fuzzy text
     * @return a counter with the number of insertions, substitutions and
     * deletions for every character
     */
    public static BiCounter<Character, EdOp> operations(String first, String second) {
        int i, j;
        int[][] A;
        EditTable B;
        BiCounter<Character, EdOp> stats = new BiCounter<Character, EdOp>();

        // intialize
        A = new int[2][second.length() + 1];
        B = new EditTable(first.length() + 1, second.length() + 1);
        // Compute first row
        A[0][0] = 0;
        B.set(0, 0, EdOp.KEEP);
        for (j = 1; j <= second.length(); ++j) {
            A[0][j] = A[0][j - 1] + 1;
            B.set(0, j, EdOp.INSERT);
        }

        // Compute other rows
        for (i = 1; i <= first.length(); ++i) {
            A[i % 2][0] = A[(i - 1) % 2][0] + 1;
            B.set(i, 0, EdOp.DELETE);
            for (j = 1; j <= second.length(); ++j) {
                if (first.charAt(i - 1) == second.charAt(j - 1)) {
                    A[i % 2][j] = A[(i - 1) % 2][j - 1];
                    B.set(i, j, EdOp.KEEP);
                } else {
                    A[i % 2][j] = min(A[(i - 1) % 2][j] + 1,
                            A[i % 2][j - 1] + 1,
                            A[(i - 1) % 2][j - 1] + 1);
                    if (A[i % 2][j] == A[(i - 1) % 2][j] + 1) {
                        B.set(i, j, EdOp.DELETE);
                    } else if (A[i % 2][j] == A[i % 2][j - 1] + 1) {
                        B.set(i, j, EdOp.INSERT);
                    } else {
                        B.set(i, j, EdOp.SUBSTITUTE);
                    }
                }
            }
        }

        i = first.length();
        j = second.length();
        while (i > 0 && j > 0) {
            switch (B.get(i, j)) {
                case KEEP:
                    stats.inc(first.charAt(i - 1), EdOp.KEEP);
                    --i;
                    --j;
                    break;
                case DELETE:
                    stats.inc(first.charAt(i - 1), EdOp.DELETE);
                    --i;
                    break;
                case INSERT:
                    stats.inc(second.charAt(j - 1), EdOp.INSERT);
                    --j;
                    break;
                case SUBSTITUTE:
                    stats.inc(first.charAt(i - 1), EdOp.SUBSTITUTE);

                    --i;
                    --j;
                    break;
            }
        }
        while (i > 0) {
            stats.inc(first.charAt(i - 1), EdOp.DELETE);
            --i;
        }
        while (j > 0) {
            stats.inc(second.charAt(j - 1), EdOp.INSERT);
            --j;

        }

        return stats;
    }

    /**
     * Aligns two strings (one to one alignments with substitutions).
     *
     * @param first the first string.
     * @param second the second string.
     * @return the mapping between positions.
     * @deprecated use Aligner class
     */
    public static int[] alignment(String first, String second) {
        int i, j;
        int[][] A;

        // intialize
        A = new int[first.length() + 1][second.length() + 1];

        // Compute first row
        A[0][0] = 0;
        for (j = 1; j <= second.length(); ++j) {
            A[0][j] = A[0][j - 1] + 1;
        }

        // Compute other rows
        for (i = 1; i <= first.length(); ++i) {
            A[i][0] = A[i - 1][0] + 1;
            for (j = 1; j <= second.length(); ++j) {
                if (first.charAt(i - 1) == second.charAt(j - 1)) {
                    A[i][j] = A[i - 1][j - 1];
                } else {
                    A[i][j] = min(A[i - 1][j] + 1, A[i][j - 1] + 1,
                            A[i - 1][j - 1] + 1);
                }
            }
        }

        int[] alignments = new int[first.length()];
        java.util.Arrays.fill(alignments, -1);

        i = first.length();
        j = second.length();
        while (i > 0 && j > 0) {
            if (first.charAt(i - 1) == second.charAt(j - 1)
                    || A[i][j] == A[i - 1][j - 1] + 1) {
                alignments[--i] = --j;
            } else if (A[i][j] == A[i - 1][j] + 1) {
                --i;
            } else if (A[i][j] == A[i][j - 1] + 1) {
                --j;
            } else { // remove after debugging
                Messages.info(ErrorMeasure.class.getName() + ": Wrong code");
            }
        }

        return alignments;
    }
}
