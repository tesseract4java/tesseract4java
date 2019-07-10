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
package eu.digitisation.text;

import eu.digitisation.log.Messages;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * Transformations between Unicode strings and codepoints
 *
 * @version 2012.06.20
 */
public class UnicodeReader {

    /**
     * Transform a sequence of Unicode values (blocks of four hexadecimal
     * digits) into the string they represent. For example, "00410042" (or "0041
     * 0042") represents "AB"
     *
     * @param codes the sequence of one or more Unicode hex values
     * @return the string represented by these codes
     * @throws java.io.IOException
     */
    protected static String codepointsToString(String codes) throws IOException {
        StringBuilder builder = new StringBuilder();
        String[] tokens = codes.trim().split("\\p{Space}+");

        for (String token : tokens) {
            if (token.length() % 4 != 0) {
                throw new IOException(token
                        + " is not a valid Unicode hex sequence");
            }
            for (int pos = 0; pos + 3 < token.length(); pos += 4) {
                String sub = token.substring(pos, pos + 4);
                int val = Integer.parseInt(sub, 16);
                builder.append((char) val);
            }
        }
        return builder.toString();
    }

    /**
     * Build a string from the codepoints (Unicode values) defining its content
     *
     * @param codes
     * @return the string represented by those codepoints
     */
    public static String codepointsToString(int[] codes) {
        StringBuilder buff = new StringBuilder();
        for (int code : codes) {
            buff.append((char) code);
        }
        return buff.toString();
    }

    /**
     * Convert a string into a sequence of Unicode values
     *
     * @param s a Java String
     * @return The array of Unicode values of the characters in s
     */
    public static int[] toCodepoints(String s) {
        int[] codes = new int[s.length()];
        for (int n = 0; n < s.length(); ++n) {
            codes[n] = (int) s.charAt(n);
        }
        return codes;
    }

    /**
     * Transform an array of integers into their hexadecimal representation
     *
     * @param values an integer array
     * @return the hexadecimal strings representing their value.
     */
    private static String[] toHexString(int[] values) {
        String[] hex = new String[values.length];
        for (int n = 0; n < values.length; ++n) {
            hex[n] = Integer.toHexString(values[n]);
        }
        return hex;
    }

    /**
     * Convert a string into a sequence of Unicode hexadecimal values
     *
     * @param s a Java String
     * @return The array of Unicode values (hexadecimal representation) of the
     * characters in s
     */
    public static String[] toHexCodepoints(String s) {
        return toHexString(toCodepoints(s));
    }

    /**
     * Read a text file and print the content as codepoints (Unicode values) in
     * it
     *
     * @param file the input file
     * @throws Exception
     */
    public static void printHexCodepoints(File file)
            throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while (reader.ready()) {
            String line = reader.readLine();
            String[] hexcodes = toHexCodepoints(line);
            System.out.println(java.util.Arrays.toString(hexcodes));
        }
        reader.close();
    }

    /**
     * Search for a Unicode sequence and highlight them in browser
     *
     * @param files files where the sequence is searched
     * @param outFile the output file
     * @param codepoints the Unicode sequence
     */
    public static void find(File[] files, String codepoints, File outFile) {
        try {
            String pattern = codepointsToString(codepoints);
            for (File file : files) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                PrintWriter writer = new PrintWriter(outFile);
                writer.print("<p><font color='blue'>"
                        + file + "</font></p>");
                while (reader.ready()) {
                    String line = reader.readLine();
                    if (line.contains(pattern)) {
                        writer.print("<p><font color='red'>"
                                + StringNormalizer.encode(line) + "</font></p>");
                    } else {
                        writer.print("<p>" + StringNormalizer.encode(line) + "</p>");
                    }
                }
                reader.close();
                writer.close();
            }
        } catch (IOException ex) {
            Messages.info(CharFilter.class.getName() + ": " + ex);
        }
    }
}
