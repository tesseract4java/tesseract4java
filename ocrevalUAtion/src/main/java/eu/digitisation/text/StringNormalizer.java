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

/**
 * Normalizes strings: collapse whitespace and use composed form (see
 * java.text.Normalizer.Form)
 *
 * @author R.C.C.
 */
public class StringNormalizer {

    final static java.text.Normalizer.Form decomposed = java.text.Normalizer.Form.NFD;
    final static java.text.Normalizer.Form composed = java.text.Normalizer.Form.NFC;
    static final java.text.Normalizer.Form compatible = java.text.Normalizer.Form.NFKC;

    /**
     * Reduce whitespace (including line and paragraph separators)
     *
     * @param s
     *            a string.
     * @return The string with simple spaces between words.
     */
    public static String reduceWS(String s) {
        return s.replaceAll("-\n", "").replaceAll(
                "(\\p{Space}|\u2028|\u2029)+", " ").trim();
    }

    /**
     * @param s
     *            a string
     * @return the canonical representation of the string.
     */
    public static String composed(String s) {
        return java.text.Normalizer.normalize(s, composed);
    }

    /**
     * @param s
     *            a string
     * @return the canonical representation of the string with normalized
     *         compatible characters.
     */
    public static String compatible(String s) {
        return java.text.Normalizer.normalize(s, compatible);
    }

    /**
     * @param s
     *            a string
     * @return the string with all diacritics removed.
     */
    public static String removeDiacritics(String s) {
        return java.text.Normalizer.normalize(s, decomposed)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    /**
     * @param s
     *            a string
     * @return the string with all punctuation symbols removed.
     */
    public static String removePunctuation(String s) {
        return s.replaceAll("\\p{P}+", "");
    }

    /**
     * @param s
     *            a string
     * @return the string with leading and trailing whitespace and punctuation
     *         symbols removed.
     */
    public static String trim(String s) {
        return s.replaceAll("^(\\p{P}|\\p{Space})+", "")
                .replaceAll("(\\p{P}|\\p{Space})+$", "");
    }

    /**
     *
     * @param s
     *            the input string
     * @param ignoreCase
     *            true if case is irrelevant
     * @param ignoreDiacritics
     *            true if diacritics are irrelevant
     * @param ignorePunctuation
     *            true if punctuation is irrelevant
     * @return the canonical representation for comparison
     */
    public static String canonical(String s,
            boolean ignoreCase,
            boolean ignoreDiacritics,
            boolean ignorePunctuation) {

        String res = (ignorePunctuation) ? removePunctuation(s) : s;
        if (ignoreCase) {
            if (ignoreDiacritics) {
                return StringNormalizer.removeDiacritics(res).toLowerCase();
            } else {
                return res.toLowerCase();
            }
        } else if (ignoreDiacritics) {
            return StringNormalizer.removeDiacritics(res);
        } else {
            return res;
        }
    }

    /**
     * Remove everything except for letters (with diacritics), numbers and
     * spaces
     *
     * @param s
     *            a string
     * @return the string with only letters, numbers, spaces and diacritics.
     */
    public static String strip(String s) {
        return s.replaceAll("[^\\p{L}\\p{M}\\p{N}\\p{Space}]", "");
    }

    /**
     * @param s
     *            a string
     * @return the string with characters <, >, &, " escaped
     */
    public static String encode(String s) {
        StringBuilder result = new StringBuilder();
        for (Character c : s.toCharArray()) {
            if (c.equals('<')) {
                result.append("&lt;");
            } else if (c.equals('>')) {
                result.append("&gt;");
            } else if (c.equals('"')) {
                result.append("&quot;");
            } else if (c.equals('&')) {
                result.append("&amp;");
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
