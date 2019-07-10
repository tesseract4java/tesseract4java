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

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Counts number of different objects, a map between objects and integers which
 * can be incremented and decremented.
 *
 * @version 2012.06.07
 * @param <Type> the class of objects being counted
 */
public class Counter<Type extends Comparable<Type>>
        extends java.util.TreeMap<Type, Integer> {

    private static final long serialVersionUID = 1L;

    int total = 0;  // stores aggregated counts

    /**
     * Increment the count for an object with the given value
     *
     * @param object the object whose count will be incremented
     * @param value the delta value
     * @return this Counter
     */
    public Counter<Type> add(Type object, int value) {
        int storedValue;
        if (containsKey(object)) {
            storedValue = get(object);
        } else {
            storedValue = 0;
        }
        put(object, storedValue + value);
        total += value;
        return this;
    }

    /**
     * Set the count for an object (and the global one) with the given value
     *
     * @param object the object whose count will be incremented
     * @param value the value for this count
     * @return this Counter
     */
    public Counter<Type> set(Type object, int value) {
        int storedValue;
        if (containsKey(object)) {
            storedValue = get(object);
        } else {
            storedValue = 0;
        }
        put(object, value);
        total += value - storedValue;
        return this;
    }

    /**
     * Add one to the count for an object
     *
     * @param object the object whose count will be incremented
     * @return this Counter
     */
    public Counter<Type> inc(Type object) {
        return add(object, 1);
    }

    /**
     * Subtract one to the count for an object
     *
     * @param object the object whose count will be decremented
     * @return this Counter
     */
    public Counter<Type> dec(Type object) {
        return add(object, -1);
    }

    /**
     * Increment the count for an object with the value stored in another
     * counter.
     *
     * @param counter the counter whose values will be added to this one.
     * @return this Counter
     */
    public Counter<Type> add(Counter<Type> counter) {
        for (Map.Entry<Type, Integer> entry : counter.entrySet()) {
            add(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * Increment counts for a an array of objects
     *
     * @param objects the array of objects
     * @return this Counter
     */
    public Counter<Type> add(Type[] objects) {
        for (Type object : objects) {
            inc(object);
        }
        return this;
    }

    /**
     *
     * @param object
     * @return the value of the counter for that object, or 0 if not stored
     */
    public int value(Type object) {
        Integer val = super.get(object);
        return (val == null) ? 0 : val;
    }

    /**
     * Maximum value stored
     *
     * @return max stored value or 0 if no value is stored
     */
    public int maxValue() {
        if (size() > 0) {
            return java.util.Collections.max(values());
        } else {
            return 0;
        }
    }

    /**
     *
     * @return the aggregated count for all objects
     */
    public int total() {
        return total;
    }

    /**
     * Clear the counter
     */
    @Override
    public void clear() {
        super.clear();
        total = 0;
    }

    /**
     * Specifies several orders for keys
     */
    public enum Order {

        ASCENDING, DESCENDING, ASCENDING_VALUE, DESCENDING_VALUE, LEXICOGRAPHIC;
    }

    private class KeyComparator implements Comparator<Type> {

        @Override
        public int compare(Type first, Type second) {
            int r = get(first).compareTo(get(second));
            return r;
        }
    }

    /**
     *
     * @param order determines ascending or descending order
     * @return the sorted list of keys stored in the counter
     */
    public List<Type> keyList(Order order) {
        List<Type> list = new ArrayList<Type>(keySet());
        if (order == Order.ASCENDING) {
            Collections.sort(list);
        } else if (order == Order.DESCENDING) {
            Collections.sort(list, Collections.reverseOrder());
        } else if (order == Order.ASCENDING_VALUE) {
            Collections.sort(list, new KeyComparator());
        } else if (order == Order.DESCENDING_VALUE) {
            Collections.sort(list, Collections.reverseOrder(new KeyComparator()));
        } else if (order == Order.LEXICOGRAPHIC
                && list.size() > 0
                && list.get(0).getClass().equals(String.class)) {
            Collator collator = Collator.getInstance();
            collator.setStrength(Collator.TERTIARY);
            collator.setDecomposition(Collator.FULL_DECOMPOSITION);
            Collections.sort(list, collator);
        }
        return list;
    }

    /**
     * A list key-value pairs
     *
     * @return a string containing one key and value per line
     */
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (Type key : keySet()) {
            b.append(key).append(" ").append(get(key)).append("\n");
        }
        return b.toString();
    }

}
