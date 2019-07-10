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

import eu.digitisation.text.CharMap;
import eu.digitisation.text.Text;
import eu.digitisation.xml.DocumentBuilder;
import java.io.File;
import org.w3c.dom.Element;

/**
 * Alignments between 2 texts (output in XHTML format)
 *
 * @author R.C.C
 */
public class Aligner {

    // style for unaligned segments
    final static String uStyle = "background-color:aquamarine";
    // style for highlight replacement in parallel text
    final static String twinStyle = "";

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
     * @param first
     *            source string
     * @param second
     *            target string
     * @return the table of minimal basic edit operations needed to transform
     *         first into second
     */
    private static EditTable alignTab(String first, String second) {
        int l1; // length of first
        int l2; // length of second
        int[][] A; // distance table
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
            char c1 = first.charAt(l1 - i);
            A[i % 2][0] = A[(i - 1) % 2][0] + 1;
            B.set(i, 0, EdOp.DELETE);
            for (int j = 1; j <= second.length(); ++j) {
                char c2 = second.charAt(l2 - j);

                if (c1 == c2) {
                    A[i % 2][j] = A[(i - 1) % 2][j - 1];
                    B.set(i, j, EdOp.KEEP);
                } else if (Character.isSpaceChar(c1)
                        ^ Character.isSpaceChar(c2)) {
                    // No alignment between blank and not-blank
                    if (A[(i - 1) % 2][j] < A[i % 2][j - 1]) {
                        A[i % 2][j] = A[(i - 1) % 2][j] + 1;
                        B.set(i, j, EdOp.DELETE);
                    } else {
                        A[i % 2][j] = A[i % 2][j - 1] + 1;
                        B.set(i, j, EdOp.INSERT);
                    }
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
     * A minimal sequence of edit operations transforming the first string into
     * the second
     *
     * @param first
     *            the first string
     * @param second
     *            the second string
     * @return a minimal sequence of edit operations transforming the first
     *         string into the second
     */
    public static EditSequence path(String first, String second) {
        return alignTab(first, second).path();
    }

    /**
     * Shows text alignment based on a pseudo-Levenshtein distance where
     * white-spaces are not allowed to be replaced with text or vice-versa
     *
     * @param header1
     *            first text title for table head
     * @param header2
     *            second text title for table head
     * @param first
     *            the first text
     * @param second
     *            the second text
     * @param map
     *            a CharMap for character equivalences
     * @return a table in XHTML format showing the alignments
     */
    public static Element alignmentMap(String header1, String header2,
            String first, String second, CharMap map) {
        EditTable B = (map == null)
                ? alignTab(first, second)
                : alignTab(map.normalForm(first), map.normalForm(second));
        DocumentBuilder builder = new DocumentBuilder("table");
        Element table = builder.root();
        Element row;
        Element cell1;
        Element cell2;
        int l1;
        int l2;
        int len;
        int i;
        int j;
        String s1;
        String s2;

        // features
        table.setAttribute("border", "1");
        // content
        row = builder.addElement("tr");
        cell1 = builder.addElement(row, "td");
        cell2 = builder.addElement(row, "td");
        cell1.setAttribute("width", "50%");
        cell2.setAttribute("width", "50%");
        cell1.setAttribute("align", "center");
        cell2.setAttribute("align", "center");
        builder.addTextElement(cell1, "h3", header1);
        builder.addTextElement(cell2, "h3", header2);
        row = builder.addElement("tr");
        cell1 = builder.addElement(row, "td");
        cell2 = builder.addElement(row, "td");

        l1 = first.length();
        l2 = second.length();
        i = l1;
        j = l2;
        while (i > 0 && j > 0) {
            switch (B.get(i, j)) {
            case KEEP:
                len = 1;
                while (len < i && len < j
                        && B.get(i - len, j - len) == EdOp.KEEP) {
                    ++len;
                }
                s1 = first.substring(l1 - i, l1 - i + len);
                s2 = second.substring(l2 - j, l2 - j + len);
                builder.addText(cell1, s1);
                builder.addText(cell2, s2);
                i -= len;
                j -= len;
                break;
            case DELETE:
                len = 1;
                while (len < i && B.get(i - len, j) == EdOp.DELETE) {
                    ++len;
                }
                s1 = first.substring(l1 - i, l1 - i + len);
                builder.addTextElement(cell1, "font", s1)
                        .setAttribute("style", uStyle);
                i -= len;
                break;
            case INSERT:
                len = 1;

                while (len < j && B.get(i, j - len) == EdOp.INSERT) {
                    ++len;
                }
                s2 = second.substring(l2 - j, l2 - j + len);
                builder.addTextElement(cell2, "font", s2)
                        .setAttribute("style", uStyle);
                j -= len;
                break;
            case SUBSTITUTE:
                len = 1;
                while (len < i && len < j
                        && B.get(i - len, j - len) == EdOp.SUBSTITUTE) {
                    ++len;
                }
                s1 = first.substring(l1 - i, l1 - i + len);
                s2 = second.substring(l2 - j, l2 - j + len);
                Element span1 = builder.addElement(cell1, "span");
                Element span2 = builder.addElement(cell2, "span");
                String id1 = "l" + i + "." + j;
                String id2 = "r" + i + "." + j;
                span1.setAttribute("title", s2);
                span2.setAttribute("title", s1);
                span1.setAttribute("id", id1);
                span2.setAttribute("id", id2);
                span1.setAttribute("onmouseover",
                        "document.getElementById('"
                                + id2 + "').style.background='greenyellow'");
                span2.setAttribute("onmouseover",
                        "document.getElementById('"
                                + id1 + "').style.background='greenyellow'");
                span1.setAttribute("onmouseout",
                        "document.getElementById('"
                                + id2 + "').style.background='none'");
                span2.setAttribute("onmouseout",
                        "document.getElementById('"
                                + id1 + "').style.background='none'");
                builder.addTextElement(span1, "font", s1)
                        .setAttribute("color", "red");
                builder.addTextElement(span2, "font", s2)
                        .setAttribute("color", "red");
                i -= len;
                j -= len;
                break;
            }
        }
        if (i > 0) {
            s1 = first.substring(l1 - i, l1);
            builder.addTextElement(cell1, "font", s1)
                    .setAttribute("style", uStyle);

        }
        if (j > 0) {
            s2 = second.substring(l2 - j, l2);
            builder.addTextElement(cell2, "font", s2)
                    .setAttribute("style", uStyle);
        }
        return builder.document().getDocumentElement();
    }

    /**
     * Shows text alignment based on a pseudo-Levenshtein distance where
     * white-spaces are not allowed to be replaced with text or vice-versa
     *
     * @param header1
     *            first text title for table head
     * @param header2
     *            second text title for table head
     * @param first
     *            the first text
     * @param second
     *            the second text
     * @return a table in XHTML format showing the alignments
     */
    public static Element alignmentMap(String header1, String header2,
            String first, String second) {
        return alignmentMap(header1, header2, first, second, null);
    }

    /**
     * Shows text alignment based on a pseudo-Levenshtein distance where
     * white-spaces are not allowed to be replaced with text or vice-versa
     *
     * @param header1
     *            first text title for table head
     * @param header2
     *            second text title for table head
     * @param first
     *            the first text
     * @param second
     *            the second text
     * @param w
     *            the weighs associated to basic edit operations
     * @return a table in XHTML format showing the alignments
     */
    public static Element bitext(String header1, String header2,
            String first, String second, EdOpWeight w, EditSequence edition) {
        DocumentBuilder builder = new DocumentBuilder("table");
        Element table = builder.root();
        Element row;
        Element cell1;
        Element cell2;
        int l1;
        int l2;
        int len;
        int i;
        int j;
        String s1;
        String s2;

        // features
        table.setAttribute("border", "1");
        // content
        row = builder.addElement("tr");
        cell1 = builder.addElement(row, "td");
        cell2 = builder.addElement(row, "td");
        cell1.setAttribute("width", "50%");
        cell2.setAttribute("width", "50%");
        cell1.setAttribute("align", "center");
        cell2.setAttribute("align", "center");
        builder.addTextElement(cell1, "h3", header1 + " (Transcription)");
        builder.addTextElement(cell2, "h3", header2 + " (OCR Result)");
        row = builder.addElement("tr");
        cell1 = builder.addElement(row, "td");
        cell2 = builder.addElement(row, "td");

        l1 = first.length();
        l2 = second.length();
        i = 0;
        j = 0;
        len = 0;

        for (int n = 0; n < edition.size(); n += len) {
            EdOp op = edition.get(n);

            // free rides first
            if (op == EdOp.DELETE && w.del(first.charAt(i)) == 0) {
                builder.addText(cell1, first.substring(i, i + 1));
                ++i;
                len = 1;
            } else if (op == EdOp.INSERT && w.ins(second.charAt(j)) == 0) {
                builder.addText(cell2, second.substring(j, j + 1));
                ++j;
                len = 1;
            } else if (op == EdOp.SUBSTITUTE
                    && w.sub(first.charAt(i), second.charAt(j)) == 0) {
                builder.addText(cell1, first.substring(i, i + 1));
                builder.addText(cell2, second.substring(j, j + 1));
                ++i;
                ++j;
                len = 1;
            } else {
                switch (op) {
                case KEEP:
                    len = 1;
                    while (i + len < l1 && j + len < l2
                            && edition.get(n + len) == EdOp.KEEP) {
                        ++len;
                    }
                    s1 = first.substring(i, i + len);
                    s2 = second.substring(j, j + len);
                    builder.addText(cell1, s1);
                    builder.addText(cell2, s2);
                    i += len;
                    j += len;
                    break;
                case DELETE:
                    len = 1;
                    while (i + len < l1
                            && edition.get(n + len) == EdOp.DELETE
                            && w.del(first.charAt(i + len)) > 0) {
                        ++len;
                    }
                    s1 = first.substring(i, i + len);
                    builder.addTextElement(cell1, "font", s1)
                            .setAttribute("style", uStyle);
                    i += len;
                    break;

                case INSERT:
                    len = 1;
                    while (j + len < l2
                            && edition.get(n + len) == EdOp.INSERT
                            && w.ins(second.charAt(j + len)) > 0) {
                        ++len;
                    }
                    s2 = second.substring(j, j + len);
                    builder.addTextElement(cell2, "font", s2)
                            .setAttribute("style", uStyle);
                    j += len;
                    break;
                case SUBSTITUTE:
                    len = 1;
                    while (i + len < l1
                            && j + len < l2
                            && edition.get(n + len) == EdOp.SUBSTITUTE
                            && w.sub(first.charAt(i + len),
                                    second.charAt(j + len)) > 0) {
                        ++len;
                    }
                    s1 = first.substring(i, i + len);
                    s2 = second.substring(j, j + len);
                    Element span1 = builder.addElement(cell1, "span");
                    Element span2 = builder.addElement(cell2, "span");
                    String id1 = "l" + i + "." + j;
                    String id2 = "r" + i + "." + j;
                    span1.setAttribute("title", s2);
                    span2.setAttribute("title", s1);
                    span1.setAttribute("id", id1);
                    span2.setAttribute("id", id2);
                    span1.setAttribute("onmouseover",
                            "document.getElementById('"
                                    + id2 + "').style.background='greenyellow'");
                    span2.setAttribute("onmouseover",
                            "document.getElementById('"
                                    + id1 + "').style.background='greenyellow'");
                    span1.setAttribute("onmouseout",
                            "document.getElementById('"
                                    + id2 + "').style.background='none'");
                    span2.setAttribute("onmouseout",
                            "document.getElementById('"
                                    + id1 + "').style.background='none'");
                    builder.addTextElement(span1, "font", s1)
                            .setAttribute("color", "red");
                    builder.addTextElement(span2, "font", s2)
                            .setAttribute("color", "red");
                    i += len;
                    j += len;
                    break;
                }
            }
        }
        return builder.document().getDocumentElement();
    }

    public static void main(String[] args)
            throws Exception {
        File f1 = new File(args[0]);
        File f2 = new File(args[1]);
        File ofile = new File("/tmp/out.html");

        String s1 = new Text(f1).toString();
        String s2 = new Text(f2).toString();
        EdOpWeight w = new OcrOpWeight();
        EditSequence eds = new EditSequence(s1, s2, w);
        DocumentBuilder builder = new DocumentBuilder("html");
        Element body = builder.addElement("body");
        Element alitab = Aligner.bitext("s1", "s2", s1, s2, w, eds);
        builder.addElement(body, alitab);
        builder.write(ofile);
    }
}
