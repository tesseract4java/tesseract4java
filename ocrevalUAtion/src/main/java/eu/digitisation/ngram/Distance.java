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
package eu.digitisation.ngram;

/**
 * Several types of distances between n-gram models
 *
 * @author R.C.C.
 */
public class Distance {

    /**
     *
     * @param first an NgramModel
     * @param second a second NgramModel (its order must be identical to
     * first's)
     * @return
     */
    public static double[] delta(NgramModel first, NgramModel second) {
        double[] result = new double[first.order];
        int[] deltas = new int[first.order];
        int[] tot = new int[first.order];

        if (first.order != second.order) {
            throw new IllegalArgumentException("Illegal comparison "
                    + "of n-gram models with different n");
        }
        for (String s : first.occur.keySet()) {
            if (s.length() > 0) {
                int val1 = first.occur.get(s).getValue();
                int val2 = second.occur.containsKey(s)
                        ? second.occur.get(s).getValue() : 0;
                deltas[s.length() - 1] += Math.abs(val1 - val2);
                tot[s.length() - 1] += (val1 + val2);
            }
        }
        for (String s : second.occur.keySet()) {
            if (s.length() > 0 && !first.occur.containsKey(s)) {
                int val2 = second.occur.get(s).getValue();
                deltas[s.length() - 1] += val2;
                tot[s.length() - 1] += val2;
            }
        }
        for (int n = 0; n < first.order; ++n) {
            //result += Math.log(deltas[n] / (double) tot[n]);
            result[n] = deltas[n] / (double) tot[n];
        }
//            return Math.exp(result / first.order);
        return result;
    }

    /**
     *
     * @param first an NgramModel
     * @param second a second NgramModel (its order must be identical to
     * first's)
     * @return
     */
    public static double cosine(NgramModel first, NgramModel second) {
        int sum = 0;
        int norm1 = 0;
        int norm2 = 0;

        if (first.order != second.order) {
            throw new IllegalArgumentException("Illegal comparison "
                    + "of n-gram models with different n");
        }
        for (String s : first.occur.keySet()) {
            if (s.length() > 0) {
                int val1 = first.occur.get(s).getValue();
                int val2 = second.occur.containsKey(s)
                        ? second.occur.get(s).getValue() : 0;
                sum += val1 * val2;
                norm1 += val1 * val1;
                norm2 += val2 * val2;
            }
        }
        for (String s : second.occur.keySet()) {
            if (s.length() > 0 && !first.occur.containsKey(s)) {
                int val2 = second.occur.get(s).getValue();
                norm2 += val2 * val2;
            }
        }
        if (norm1 > 0 && norm2 > 0) {
            return sum / (Math.sqrt(norm1) * Math.sqrt(norm2));
        } else {
            return 0;
        }
    }
}
