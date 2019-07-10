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
package eu.digitisation.math;

import java.util.Map;
import java.util.Set;

/**
 * Keeps a counter for pairs of objects (joint frequencies) and marginal counts
 *
 * @author R.C.C.
 * @param <T1> the type of first object
 * @param <T2> the type of the second object
 */
public class BiCounter<T1 extends Comparable<T1>, T2 extends Comparable<T2>>
        extends Counter<Pair<T1, T2>> {

    private static final long serialVersionUID = 1L;
    Counter<T1> subtotal1;
    Counter<T2> subtotal2;

    /**
     * Default constructor
     */
    public BiCounter() {
        super();
        subtotal1 = new Counter<T1>();
        subtotal2 = new Counter<T2>();
    }

    /**
     *
     * @param o1 first component in pair to be incremented
     * @param o2 second component in pair to be incremented
     * @param value the value to be added for the pair count
     * @return this BiCounter
     */
    public BiCounter<T1, T2> add(T1 o1, T2 o2, int value) {
        Pair<T1, T2> pair = new Pair<T1, T2>(o1, o2);
        super.add(pair, value);
        subtotal1.add(o1, value);
        subtotal2.add(o2, value);
        return this;
    }

    /**
     * Set the value for a count and reset accordingly the total and marginal
     * counts
     *
     * @param o1 first component in pair
     * @param o2 second component in pair
     * @param value the value for the pair count
     * @return this BiCounter
     */
    public BiCounter<T1, T2> set(T1 o1, T2 o2, int value) {
        Pair<T1, T2> pair = new Pair<T1, T2>(o1, o2);
        super.set(pair, value);
        subtotal1.set(o1, value);
        subtotal2.set(o2, value);
        return this;
    }

    /**
     * Add one to the count for a pair
     *
     * @param o1 first component in pair to be incremented
     * @param o2 second component in pair to be incremented
     * @return this BiCounter
     */
    public BiCounter<T1, T2> inc(T1 o1, T2 o2) {
        return add(o1, o2, 1);
    }

    /**
     * Subtract one to the count for a pair
     *
     * @param o1 first component in the pair to be decremented
     * @param o2 second component in the pair be decremented
     * @return this BiCounter
     */
    public BiCounter<T1, T2> dec(T1 o1, T2 o2) {
        return add(o1, o2, -1);
    }

    /**
     * Increment the count for an pair with the value stored in another
     * BiCounter.
     *
     * @param counter the counter whose values will be added to this one.
     * @return this BiCounter
     */
    public BiCounter<T1, T2> add(BiCounter<T1, T2> counter) {
        for (Map.Entry<Pair<T1, T2>, Integer> entry : counter.entrySet()) {
            Pair<T1, T2> key = entry.getKey();
            Integer value = entry.getValue();
            add(key.first, key.second, value);
        }
        return this;
    }

    /**
     *
     * @param o1 first component in pair
     * @param o2 second component in pair
     * @return the value of the counter for that pair, or 0 if not stored. If
     * one the components is null the marginal count is returned.
     * @throws NullPointerException if both objects are null
     */
    public int value(T1 o1, T2 o2) {
        if (o1 == null) {
            return subtotal2.value(o2);
        } else if (o2 == null) {
            return subtotal1.value(o1);
        } else {
            Pair<T1, T2> pair = new Pair<T1, T2>(o1, o2);
            return super.value(pair);
        }
    }

    /**
     *
     * @return the set of left components in pairs of the key set
     */
    public Set<T1> leftKeySet() {
        return subtotal1.keySet();
    }

    /**
     * 
     * @return the marginal counts for the left ley
     */
    public Counter<T1> leftSubtotal() {
        return subtotal1;
    }

    /**
     *
     * @return the set of right components in pairs of the key set
     */
    public Set<T2> rightKeySet() {
        return subtotal2.keySet();
    }

    /**
     * 
     * @return the marginal counts for the right ley
     */
    public Counter<T2> rightSubtotal() {
        return subtotal2;
    }
    
    /**
     * Clear the BiCounter
     */
    @Override
    public void clear() {
        super.clear();
        subtotal1.clear();
        subtotal2.clear();
    }
}
