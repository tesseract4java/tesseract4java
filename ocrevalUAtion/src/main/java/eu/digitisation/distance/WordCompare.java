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

import eu.digitisation.document.TokenArray;
import eu.digitisation.math.MinimalPerfectHash;

/**
 * Word alignments between 2 texts (output in text format)
 *
 * @author R.C.C
 */
public class WordCompare {

    /**
     * @return 3-wise minimum.
     */
    private static int min(int x, int y, int z) {
        return Math.min(x, Math.min(y, z));
    }

    /**
     * Compute the table of basic edit operations needed to transform first into
     * second
     *
     * @param first source string
     * @param second target string
     * @return the table of minimal basic edit operations needed to transform
     * first into second
     */
    private static EditTable align(TokenArray a1, TokenArray a2) {

        int l1 = a1.length();      // length of first 
        int l2 = a2.length();      // length of second
        int[][] A;   // distance table
        EditTable B; // edit operations

        // intialize 
        A = new int[2][l2 + 1];
        B = new EditTable(l1 + 1, l2 + 1);
        // Compute first row
        A[0][0] = 0;
        B.set(0, 0, EdOp.KEEP);
        for (int j = 1; j <= l2; ++j) {
            A[0][j] = A[0][j - 1] + 1;
            B.set(0, j, EdOp.INSERT);
        }

        // Compute other rows
        for (int i = 1; i <= l1; ++i) {
            int n1 = a1.tokenAt(l1 - i);
            A[i % 2][0] = A[(i - 1) % 2][0] + 1;
            B.set(i, 0, EdOp.DELETE);
            for (int j = 1; j <= l2; ++j) {
                int n2 = a2.tokenAt(l2 - j);

                if (n1 == n2) {
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
        return B;
    }

    /**
     * Show word-level differences based on a Levenshtein distance
     *
     * @param first the first text
     * @param second the second text
     * @return a report on the differences between files
     */
    public static String wdiff(String first, String second) {
        MinimalPerfectHash mph = new MinimalPerfectHash(false); // case unsensitive  
        TokenArray a1 = new TokenArray(mph, first);
        TokenArray a2 = new TokenArray(mph, second);
        EditTable B = align(a1, a2);
        StringBuilder builder = new StringBuilder();

        int l1 = a1.length();
        int l2 = a2.length();
        int i = l1;
        int j = l2;

        while (i > 0 && j > 0) {
            switch (B.get(i, j)) {
                case KEEP:
                    builder.append(a1.wordAt(l1 - i)).append(" = ")
                            .append(a2.wordAt(l2 - j)).append('\n');
                    --i;
                    --j;
                    break;
                case DELETE:
                    builder.append(a1.wordAt(l1 - i)).append(" # []\n");
                    --i;
                    break;
                case INSERT:
                    builder.append("[] # ")
                            .append(a2.wordAt(l2 - j)).append('\n');
                    --j;
                    break;
                case SUBSTITUTE:
                    builder.append(a1.wordAt(l1 - i))
                            .append(" # ").append(a2.wordAt(l2 - j)).append('\n');
                    --i;
                    --j;
                    break;
            }

        }
        if (i > 0) {
            builder.append(a1.wordAt(l1 - i)).append(" # []\n");
            --i;
        }
        if (j > 0) {
            builder.append("[] # ")
                    .append(a2.wordAt(l2 - j)).append('\n');
            --j;
        }

        return builder.toString();
    }
}
