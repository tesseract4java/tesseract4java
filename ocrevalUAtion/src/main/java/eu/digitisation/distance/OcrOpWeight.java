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

import eu.digitisation.input.Parameters;

/**
 * Integer weights for basic edit operations.
 *
 * @author R.C.C.
 */
public class OcrOpWeight implements EdOpWeight {

    //    boolean ignoreCase;
    //    boolean ignoreDiacritics;
    boolean ignorePunctuation;

    /**
     *
     * @param ignoreCase true if case must be ignored
     * @param ignoreDiacritics true if diacritics must be ignored
     * @param ignorePunctuation true if punctuation must be ignored
     */
    public OcrOpWeight(boolean ignorePunctuation) {
        //      this.ignoreCase = ignoreCase;
        //      this.ignoreDiacritics = ignoreDiacritics;
        this.ignorePunctuation = ignorePunctuation;
    }

    public OcrOpWeight(Parameters pars) {
        this(pars.ignorePunctuation.getValue());
    }

    /**
     * Default constructor creates weights which are case-sensitive,
     * diacritics-aware and punctuation-aware.
     */
    public OcrOpWeight() {
        this(false);
    }

    /**
     *
     * @param c1 the character found in text
     * @param c2 the replacing character
     * @return the cost of substituting character c1 with c2. Note: whitespace
     * must not substitute character which are rather deleted; therefore, such
     * cases return a value greater than 2 (standard insertion+deletion).
     * Diacritics and case cannot be compared here due to efficiency reasons
     * (too slow).
     */
    @Override
    public int sub(char c1, char c2) {
        return (Character.isSpaceChar(c1) ^ Character.isSpaceChar(c2)) ? 4 : 1;
        /*
         if (Character.isSpaceChar(c1) ^ Character.isSpaceChar(c2)) {
         return 4;  // replacing whitespace with character is not recommended
         } else if (ignoreCase) {
         if (ignoreDiacritics) {
         String s1 = StringNormalizer.removeDiacritics(String.valueOf(c1));
         String s2 = StringNormalizer.removeDiacritics(String.valueOf(c2));
         return (s1.toLowerCase().equals(s2.toLowerCase())) ? 0 : 1;
         } else {
         return (Character.toLowerCase(c1) == Character.toLowerCase(c2))
         ? 0 : 1;
         }

         } else if (ignoreDiacritics) {  // case matters
         String s1 = StringNormalizer.removeDiacritics(String.valueOf(c1));
         String s2 = StringNormalizer.removeDiacritics(String.valueOf(c2));
         return (s1.equals(s2)) ? 0 : 1;
         } else {
         return c1 == c2 ? 0 : 1;
         }
         */
    }

    /**
     *
     * @param c a character
     * @return the cost of inserting a character c
     */
    @Override
    public int ins(char c) {
        if (ignorePunctuation) {
            return (Character.isSpaceChar(c) || Character.isLetterOrDigit(c))
                    ? 1 : 0;
        } else {
            return 1;
        }
    }

    /**
     *
     * @param c a character
     * @return the cost of removing a character c
     */
    @Override
    public int del(char c) {
        if (ignorePunctuation) {
            return (Character.isSpaceChar(c) || Character.isLetterOrDigit(c))
                    ? 1 : 0;
        } else {
            return 1;
        }
    }
}
