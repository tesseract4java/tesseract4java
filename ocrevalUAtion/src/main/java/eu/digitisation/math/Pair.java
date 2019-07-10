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

/**
 * A pair of objects (not necessarily of identical type)
 *
 * @param <T1> the type of first object
 * @param <T2> the type of second object
 */
public class Pair<T1 extends Comparable<T1>, T2 extends Comparable<T2>>
        implements Comparable<Pair<T1, T2>> {

    public T1 first;   // first element in pair
    public T2 second;  // second element in pair

    /**
     * Default class constructor
     */
    public Pair() {
    }

    /**
     * Class constructor
     * @param first first component
     * @param second second component
     */
    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Comparator
     * @param other another pair
     */
    @Override
    public int compareTo(Pair<T1, T2> other) {
        if (this.first.equals(other.first)) {
            return this.second.compareTo(other.second);
        } else {
            return this.first.compareTo(other.first);
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        Pair<T1, T2> other;
        if (o == null) {
            return false;
        } else {
            if (o instanceof Pair) {
                other = (Pair<T1, T2>) o;
            } else {
                throw new ClassCastException(Pair.class
                        + " cannot be compared with "
                        + o.getClass());
            }
            return this.first.equals(other.first) && this.second.equals(other.second);
        }
    }

    @Override
    public int hashCode() {
        return first.hashCode() ^ 31 * second.hashCode();
    }

    @Override
    public String toString() {
        return "(" + first.toString() + "," + second.toString() + ")";
    }
}
