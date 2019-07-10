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
import eu.digitisation.log.Messages;
import eu.digitisation.math.BiCounter;
import eu.digitisation.text.Text;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An arbitrary length sequence of basic edit operations (keep, insert,
 * substitute, delete).
 *
 * @author R.C.C.
 */
public class EditSequence {

    List<EdOp> ops;  // the list of edit operations
    int numOps; // the number of non-trivial (KEEP) operations 
    int length1; // the length of the first string (number of non-INSERT operations in the list)
    int length2; // the lenth of the second string (number of non-DELETE operations in the list)

    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    public EditSequence() {
        ops = new ArrayList<EdOp>();
    }

    /**
     * Create an EditPAth with the specified initial capacity
     *
     * @param initialCapacity
     */
    public EditSequence(int initialCapacity) {
        ops = new ArrayList<EdOp>(initialCapacity);
    }

    /**
     *
     * @param pos a position in the sequence
     * @return the basic edit operation in the sequence which is at the
     * specified position
     */
    public EdOp get(int pos) {
        return ops.get(pos);
    }

    /**
     * Add an operation to the sequence
     *
     * @param op an edit operation
     */
    public final void add(EdOp op) {
        ops.add(op);
        switch (op) {
            case KEEP:
                ++length1;
                ++length2;
                break;
            case INSERT:
                ++length2;
                ++numOps;
                break;
            case SUBSTITUTE:
                ++length1;
                ++length2;
                ++numOps;
                break;
            case DELETE:
                ++length1;
                ++numOps;
                break;
        }
    }

    /**
     * Add an operation to the sequence
     *
     * @param other another sequence of edit operations
     */
    public final void append(EditSequence other) {
        for (EdOp op : other.ops) {
            this.add(op);
        }
    }

    /**
     * Build a new path containing only a prefix of the sequence
     *
     * @param len the length of the new sequence
     * @return the path truncated to the required length
     */
    public EditSequence head(int len) {
        EditSequence path = new EditSequence();
        for (int n = 0; n < len; ++n) {
            path.add(ops.get(n));
        }
        return path;
    }

    /**
     * The size of the list
     *
     * @return the number of basic edit operations in the sequence
     */
    public int size() {
        return ops.size();
    }

    /**
     *
     * @return the number of non-trivial (KEEP) edit operations in this sequence
     */
    public int length() {
        return numOps;
    }

    /**
     * The length of the first string
     *
     * @return the number of edit operations in the sequence involving the first
     * string (all but DELETE)
     */
    public final int shift1() {
        return length1;
    }

    /**
     * The length of the second string
     *
     * @return the number of edit operations in the sequence involving the
     * second string (all but INSERT)
     */
    public final int shift2() {
        return length2;
    }

    /**
     * String representation
     *
     * @return a string representing the EditSequence
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (EdOp op : ops) {
            builder.append(op);
        }
        return builder.toString();
    }

    /**
     * Build the EditSequence for a pair of strings
     *
     * @param first the first string
     * @param second the second string
     * @param w the weights applied to basic edit operations
     */
    public EditSequence(String first, String second, EdOpWeight w) {
        int l1;      // length of first 
        int l2;      // length of second
        int[][] A;   // distance table
        EditTable B; // edit operations

        // intialize 
        l1 = first.length();
        l2 = second.length();
        A = new int[2][second.length() + 1];
        B = new EditTable(first.length() + 1, second.length() + 1);
        // Compute first row
        A[0][0] = 0;
        B.set(0, 0, EdOp.KEEP);
        for (int j = 1; j <= second.length(); ++j) {
            char c2 = second.charAt(j - 1);
            A[0][j] = A[0][j - 1] + w.ins(c2);
            B.set(0, j, EdOp.INSERT);
        }

        // Compute other rows
        for (int i = 1; i <= first.length(); ++i) {
            char c1 = first.charAt(i - 1);
            A[i % 2][0] = A[(i - 1) % 2][0] + w.del(c1);
            B.set(i, 0, EdOp.DELETE);
            for (int j = 1; j <= second.length(); ++j) {
                char c2 = second.charAt(j - 1);

                if (c1 == c2) {
                    A[i % 2][j] = A[(i - 1) % 2][j - 1];
                    B.set(i, j, EdOp.KEEP);
                } else {
                    A[i % 2][j] = Math.min(A[(i - 1) % 2][j - 1] + w.sub(c1, c2),
                            Math.min(A[i % 2][j - 1] + w.ins(c2),
                                    A[(i - 1) % 2][j] + w.del(c1)));

                    if (A[i % 2][j] == A[i % 2][j - 1] + w.ins(c2)) {
                        B.set(i, j, EdOp.INSERT);
                    } else if (A[i % 2][j] == A[(i - 1) % 2][j] + w.del(c1)) {
                        B.set(i, j, EdOp.DELETE);
                    } else {
                        B.set(i, j, EdOp.SUBSTITUTE);
                    }
                }
            }
        }

        // extract sequence of edit operations
        int i = B.width - 1;
        int j = B.height - 1;

        ops = new ArrayList<EdOp>();

        while (i > 0 || j > 0) {
            EdOp e = null;
            try {
                e = B.get(i, j);
            } catch (Exception ex) {
                Messages.severe(i + "," + j);
                Messages.severe(B.toString());
            }
            switch (e) {
                case INSERT:
                    --j;
                    break;
                case DELETE:
                    --i;
                    break;
                default:
                    --i;
                    --j;
                    break;
            }
            add(e);
        }
        if (i != 0
                || j != 0
                || length1 != first.length()
                || length2 != second.length()) {
            throw new java.lang.IllegalArgumentException("Unvalid EditTable");
        } else {
            Collections.reverse(ops);
        }
    }

    /**
     * Linear-time approximation to the construction of the EditSequence for a
     * pair of strings. The complexity gets reduced by splitting the strings
     * into smaller, overlapping, chunks.
     *
     * @param s1 the first string
     * @param s2 the second string
     * @param w the weights applied to basic edit operations
     * @param chunkLen the length of the chunks in which the strings are split
     *
     */
    public EditSequence(String s1, String s2, EdOpWeight w, int chunkLen) {
        int len1 = s1.length();
        int len2 = s2.length();

        if (chunkLen < 2) {
            throw new IllegalArgumentException("chunkLen mut be greater than 1");
        }

        ops = new ArrayList<EdOp>();
        while (shift1() < len1 || shift2() < len2) {
            int high1 = Math.min(shift1() + chunkLen, len1);
            int high2 = Math.min(shift2() + chunkLen, len2);
            String sub1 = s1.substring(shift1(), high1);
            String sub2 = s2.substring(shift2(), high2);
            EditSequence subseq = new EditSequence(sub1, sub2, w);
            EditSequence head = (high1 < len1 || high2 < len2)
                    ? subseq.head(subseq.size() / 2)
                    : subseq;

            append(head);
            if (len1 > 10 * chunkLen) {
                int frac =  (100 * length1) / len1;
                Messages.info(frac + "% of file processed");
            }
        }
    }

    /**
     * Build the EditSequence for a pair of TokenArrays
     *
     * @param first the first TokenArray
     * @param second the second TokenArray
     */
    public EditSequence(TokenArray first, TokenArray second) {
        int l1;      // length of first 
        int l2;      // length of second
        int[][] A;   // distance table
        EditTable B; // edit operations

        // intialize 
        l1 = first.length();
        l2 = second.length();
        A = new int[2][second.length() + 1];
        B = new EditTable(first.length() + 1, second.length() + 1);
        // Compute first row
        A[0][0] = 0;
        B.set(0, 0, EdOp.KEEP);
        for (int j = 1; j <= second.length(); ++j) {
            A[0][j] = A[0][j - 1] + 1;
            B.set(0, j, EdOp.INSERT);
        }

        // Compute other rows
        for (int i = 1; i <= first.length(); ++i) {
            int n1 = first.tokenAt(i - 1);
            A[i % 2][0] = A[(i - 1) % 2][0] + 1;
            B.set(i, 0, EdOp.DELETE);
            for (int j = 1; j <= second.length(); ++j) {
                int n2 = second.tokenAt(j - 1);
                if (n1 == n2) {
                    A[i % 2][j] = A[(i - 1) % 2][j - 1];
                    B.set(i, j, EdOp.KEEP);
                } else {
                    A[i % 2][j] = Math.min(A[(i - 1) % 2][j] + 1,
                            Math.min(A[i % 2][j - 1] + 1,
                                    A[(i - 1) % 2][j - 1] + 1));
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

        // extract sequence of edit operations
        int i = B.width - 1;
        int j = B.height - 1;

        ops = new ArrayList<EdOp>();

        while (i > 0 || j > 0) {
            EdOp e = B.get(i, j);
            switch (e) {
                case INSERT:
                    --j;
                    break;
                case DELETE:
                    --i;
                    break;
                default:
                    --i;
                    --j;
                    break;
            }
            add(e);
        }
        if (i != 0 || j != 0) {
            throw new java.lang.IllegalArgumentException("Unvalid EditTable");
        } else {
            Collections.reverse(ops);
        }
    }

    /**
     * Linear-time approximation to the construction of the EditSequence for a
     * pair of TokenArrays. The complexity gets reduced by splitting the arrays
     * into smaller, overlapping, chunks.
     *
     * @param a1 the first TokenArray
     * @param a2 the second TokenArray
     * @param chunkLen the length of the chunks in which the arrays are split
     *
     */
    public EditSequence(TokenArray a1, TokenArray a2, int chunkLen) {
        int len1 = a1.length();
        int len2 = a2.length();

        if (chunkLen < 2) {
            throw new IllegalArgumentException("chunkLen mut be greater than 1");
        }

        ops = new ArrayList<EdOp>();
        while (shift1() < len1 || shift2() < len2) {
            int high1 = Math.min(shift1() + chunkLen, len1);
            int high2 = Math.min(shift2() + chunkLen, len2);
            TokenArray sub1 = a1.subArray(shift1(), high1);
            TokenArray sub2 = a2.subArray(shift2(), high2);
            EditSequence subseq = new EditSequence(sub1, sub2);

            EditSequence head = (high1 < len1 || high2 < len2)
                    ? subseq.head(subseq.size() / 2)
                    : subseq;

            append(head);
        }
    }

    /**
     * Extract alignment statistics
     *
     * @param s1 the source string
     * @param s2 the target string
     * @return the statistics on the number of edit operations (per character
     * and type of operation)
     */
    public BiCounter<Character, EdOp> stats(String s1, String s2) {
        BiCounter<Character, EdOp> stats = new BiCounter<Character, EdOp>();
        int n1 = 0;
        int n2 = 0;
        for (EdOp op : ops) {
            if (op != EdOp.INSERT) {
                stats.inc(s1.charAt(n1), op);
                ++n1;
            } else {
                stats.inc(s2.charAt(n2), EdOp.INSERT);
            }
            if (op != EdOp.DELETE) {
                ++n2;
            }
        }
        return stats;
    }

    /**
     * Extract alignment statistics
     *
     * @param s1 the source string
     * @param s2 the target string
     * @param w weights of basic edit operations
     * @return the statistics on the number of edit operations (per character
     * and type of operation)
     */
    public BiCounter<Character, EdOp> stats(String s1, String s2, EdOpWeight w) {
        BiCounter<Character, EdOp> stats = new BiCounter<Character, EdOp>();
        int n1 = 0;
        int n2 = 0;
        for (EdOp op : ops) {
            switch (op) {
                case INSERT:
                    if (w.ins(s2.charAt(n2)) > 0) {
                        stats.inc(s2.charAt(n2), op);
                    } // costless insertion is equivalent to neglegible character
                    ++n2;
                    break;
                case SUBSTITUTE:
                    if (w.sub(s1.charAt(n1), s2.charAt(n2)) > 0) {
                        stats.inc(s1.charAt(n1), op);
                    } else {  // costless SUBSTITUTE is equivalent to KEEP
                        stats.inc(s1.charAt(n1), EdOp.KEEP);
                    }
                    ++n1;
                    ++n2;
                    break;
                case DELETE:
                    if (w.del(s1.charAt(n1)) > 0) {
                        stats.inc(s1.charAt(n1), op);
                    } // costless deletion is equivalent to neglegible character
                    ++n1;
                    break;
                case KEEP:
                    stats.inc(s1.charAt(n1), op);
                    ++n1;
                    ++n2;
                    break;
            }
        }
        return stats;
    }

    /**
     * Compute cost of the transformation
     *
     * @param s1 the source string
     * @param s2 the target string
     * @param w the cost of basic edit operations
     * @return the added cost of the edit operations (not identical to length
     * because some operation may be free)
     */
    public int cost(String s1, String s2, EdOpWeight w) {
        int added = 0;
        int n1 = 0;
        int n2 = 0;
        for (EdOp op : ops) {
            switch (op) {
                case INSERT:
                    added += w.ins(s2.charAt(n2));
                    ++n2;
                    break;
                case SUBSTITUTE:
                    added += w.sub(s1.charAt(n1), s2.charAt(n2));
                    ++n1;
                    ++n2;
                    break;
                case DELETE:
                    added += w.del(s1.charAt(n1));
                    ++n1;
                    break;
                case KEEP:
                    ++n1;
                    ++n2;
                    break;
            }
        }
        return added;
    }

    public static void main(String[] args)
            throws Exception {
        File gtfile = new File(args[0]);
        File ocrfile = new File(args[1]);
        String gts = new Text(gtfile).toString();
        String ocrs = new Text(ocrfile).toString();
        EdOpWeight w = new OcrOpWeight();
        EditSequence eds = new EditSequence(gts, ocrs, w, 2000);
        System.out.println(eds);
    }
}
