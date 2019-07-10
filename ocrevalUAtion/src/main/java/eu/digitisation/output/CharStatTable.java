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
package eu.digitisation.output;

import eu.digitisation.distance.EdOp;
import eu.digitisation.distance.StringEditDistance;
import eu.digitisation.math.BiCounter;
import eu.digitisation.xml.DocumentBuilder;
import org.w3c.dom.Element;

/**
 * Provide statistics of the differences between two texts
 *
 * @author R.C.C.
 */
public class CharStatTable extends BiCounter<Character, EdOp> {
    private static final long serialVersionUID = 1L;

    /**
     * Create empty CharStatTable
     */
    public CharStatTable() {
        super();
    }

    /**
     * Separate statistics of errors for every character
     *
     * @param s1
     *            the reference text
     * @param s2
     *            the fuzzy text
     */
    public CharStatTable(String s1, String s2) {
        super();
        add(StringEditDistance.operations(s1, s2));
    }

    /**
     * Separate statistics of errors for every character form a collection of
     * texts
     *
     * @param array1
     *            an array of reference texts
     * @param array2
     *            an array of fuzzy texts
     */
    public CharStatTable(String[] array1, String[] array2) {
        if (array1.length == array2.length) {
            for (int n = 0; n < array1.length; ++n) {
                add(StringEditDistance.operations(array1[n], array2[n]));
            }
        } else {
            throw new java.lang.IllegalArgumentException(
                    "Arrays of different length");
        }
    }

    /**
     * Add statistic for a pair of strings
     * 
     * @param s1
     *            the reference text
     * @param s2
     *            the fuzzy text
     */
    public void add(String s1, String s2) {
        add(StringEditDistance.operations(s1, s2));
    }

    /**
     * Separate statistics of errors for every character
     *
     * @return an element containing table with the statistics: one character
     *         per row and one edit operation per column.
     */
    public Element asTable() {
        DocumentBuilder builder = new DocumentBuilder("table");
        Element table = builder.root();
        Element row = builder.addElement("tr");

        // features
        table.setAttribute("border", "1");
        // header
        builder.addTextElement(row, "th", "Character");
        builder.addTextElement(row, "th", "Character name");
        builder.addTextElement(row, "th", "Hex code");
        builder.addTextElement(row, "th", "Total");
        builder.addTextElement(row, "th", "Keep");
        builder.addTextElement(row, "th", "Insert");
        builder.addTextElement(row, "th", "Substitute");
        builder.addTextElement(row, "th", "Delete");
        builder.addTextElement(row, "th", "Error rate (%)");
        builder.addTextElement(row, "th", "Accuracy (%)");

        // content
        for (Character c : leftKeySet()) {
            int spu = value(c, EdOp.INSERT);
            int sub = value(c, EdOp.SUBSTITUTE);
            int add = value(c, EdOp.DELETE);
            int kep = value(c, EdOp.KEEP);
            int tot = kep + sub + add;
            double rate = (spu + sub + add) / (double) tot * 100;
            double accuracy = (tot - (spu + sub + add)) / (double) tot * 100;
            row = builder.addElement("tr");
            builder.addTextElement(row, "td", c.toString());
            builder.addTextElement(row, "td", Character.getName((int) c));
            builder.addTextElement(row, "td", Integer.toHexString(c));
            builder.addTextElement(row, "td", String.valueOf(tot));
            builder.addTextElement(row, "td", String.valueOf(kep));
            builder.addTextElement(row, "td", String.valueOf(spu));
            builder.addTextElement(row, "td", String.valueOf(sub));
            builder.addTextElement(row, "td", String.valueOf(add));
            builder.addTextElement(row, "td", String.format("%.2f", rate));
            builder.addTextElement(row, "td", String.format("%.2f", accuracy));
        }
        return builder.document().getDocumentElement();
    }

    /**
     * Prints separate statistics of errors for every character
     *
     * @param recordSeparator
     *            text between data records
     * @param fieldSeparator
     *            text between data fields
     * @return text with the statistics: every character separated by a record
     *         separator and every type of edit operation separated by field
     *         separator.
     *
     */
    public StringBuilder asCSV(String recordSeparator, String fieldSeparator) {
        StringBuilder builder = new StringBuilder();

        builder.append("Character")
                .append(fieldSeparator).append("Hex code")
                .append(fieldSeparator).append("Total")
                .append(fieldSeparator).append("Keep")
                .append(fieldSeparator).append("Insert")
                .append(fieldSeparator).append("Substitute")
                .append(fieldSeparator).append("Delete")
                .append(fieldSeparator).append("Error rate (%)")
                .append(fieldSeparator).append("Accuracy (%)");

        for (Character c : leftKeySet()) {
            int spu = value(c, EdOp.INSERT);
            int sub = value(c, EdOp.SUBSTITUTE);
            int add = value(c, EdOp.DELETE);
            int kep = value(c, EdOp.KEEP);
            int tot = kep + sub + add;
            double rate = (spu + sub + add) / (double) tot * 100;
            double accuracy = (tot - (spu + sub + add)) / (double) tot * 100;
            builder.append(recordSeparator);
            builder.append(c)
                    .append(fieldSeparator).append(Integer.toHexString(c))
                    .append(fieldSeparator).append(tot)
                    .append(fieldSeparator).append(kep)
                    .append(fieldSeparator).append(spu)
                    .append(fieldSeparator).append(sub)
                    .append(fieldSeparator).append(add)
                    .append(fieldSeparator).append(String.format("%.2f", rate))
                    .append(fieldSeparator).append(
                            String.format("%.2f", accuracy));
        }
        return builder;
    }

    /**
     * Extract CER from character statistics
     * 
     * @return the global CER
     */
    public double cer() {
        int spu = 0;
        int sub = 0;
        int add = 0;
        int tot = 0;

        for (Character c : leftKeySet()) {
            spu += value(c, EdOp.INSERT);
            sub += value(c, EdOp.SUBSTITUTE);
            add += value(c, EdOp.DELETE);
            tot += value(c, EdOp.KEEP)
                    + value(c, EdOp.SUBSTITUTE)
                    + value(c, EdOp.DELETE);
        }

        return (spu + sub + add) / (double) tot;
    }

    public double accuracy() {
        int spu = 0;
        int sub = 0;
        int add = 0;
        int tot = 0;

        for (Character c : leftKeySet()) {
            spu += value(c, EdOp.INSERT);
            sub += value(c, EdOp.SUBSTITUTE);
            add += value(c, EdOp.DELETE);
            tot += value(c, EdOp.KEEP)
                    + value(c, EdOp.SUBSTITUTE)
                    + value(c, EdOp.DELETE);
        }

        return (tot - (spu + sub + add)) / (double) tot;
    }
}
