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
package eu.digitisation.document;

import eu.digitisation.math.Counter;
import eu.digitisation.math.MinimalPerfectHash;

/**
 * A term frequency vector stores counts for every word in text.
 *
 * @author R.C.C.
 */
public class TermFrequencyVector {

    static MinimalPerfectHash mph = new MinimalPerfectHash();
    Counter<Integer> tf;

    /**
     * Create a TermFrequencyVector from a String
     *
     * @param s
     */
    public TermFrequencyVector(String s) {
        TokenArray array = new TokenArray(mph, s);

        tf = new Counter<Integer>();
        for (Integer n : array) {
            tf.inc(n);
        }
    }

    /**
     * Compute the distance between two bag of words (order independent
     * distance)
     *
     * @param other another bag of words
     * @return the number of differences between this and the other bag of words
     */
    public int distance(TermFrequencyVector other) {
        int dplus = 0;    // excess
        int dminus = 0;   // fault
        for (Integer word : this.tf.keySet()) {
            int delta = this.tf.value(word) - other.tf.value(word);
            if (delta > 0) {
                dplus += delta;
            } else {
                dminus -= delta;
            }
        }
        for (Integer word : other.tf.keySet()) {
            if (!this.tf.containsKey(word)) {
                int delta = this.tf.value(word) - other.tf.value(word);
                if (delta > 0) {
                    dplus += delta;
                } else {
                    dminus -= delta;
                }
            }
        }
     
        return Math.max(dplus, dminus);
    }

    /**
     * The total number of words
     *
     * @return the total number of words
     */
    public int total() {
        return tf.total();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Integer code : tf.keyList(Counter.Order.ASCENDING)) {
            String s = mph.decode(code);
            builder.append(s).append('[').append(tf.get(code)).append("] ");
        }
        return builder.toString().trim();
    }
}
