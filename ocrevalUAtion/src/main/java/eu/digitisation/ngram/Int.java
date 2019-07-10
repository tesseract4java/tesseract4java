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

import java.io.Serializable;

/**
 * A mutable Integer (faster than boxing/un-boxing)
 *
 * @author Rafael C. Carrasco
 * @version 1.1
 */
public class Int implements Comparable<Int>, Serializable {
    private static final long serialVersionUID = 1L;

    int n;       // The value.

    /**
     * Constructs a new Int with the specified int value.
     *
     * @param n the integer value
     */
    public Int(int n) {
        this.n = n;
    }

    /**
     * Constructs a new Int with the value indicated by the string.
     *
     * @param s string representing the integer value
     */
    public Int(String s) {
        n = Integer.parseInt(s);
    }

    /**
     * Returns the value as int.
     *
     * @return he value as int
     */
    public int getValue() {
        return n;
    }

    /**
     * Assigns the specified int value.
     *
     * @param n the value for this Int
     * @return the Int itself
     */
    public Int setValue(int n) {
        this.n = n;
        return this;
    }

    /**
     * Pre-increments value by 1.
     *
     * @return the Int itself
     */
    public Int increment() {
        ++n;
        return this;
    }

    /**
     * Add to value.
     *
     * @param n the delta value
     * @return he Int itself
     */
    public Int add(int n) {
        this.n += n;
        return this;
    }

    /**
     * Add to value.
     *
     * @param n the delta value
     * @return he Int itself
     */
    public Int subtact(int n) {
        this.n -= n;
        return this;
    }

    /**
     * Pre-decrements value by 1.
     *
     * @return he Int itself
     */
    public Int decrement() {
        --n;
        return this;
    }

    /**
     * Post-increments value by one.
     *
     * @return he Int itself
     */
    public Int postIncValue() {
        ++n;
        return new Int(n - 1);
    }

    /**
     * Returns a new Int with incremented value.
     *
     * @return he Int itself
     */
    public Int nextInt() {
        return new Int(n + 1);
    }

    /**
     * Returns a String object representing the specified Int.
     */
    @Override
    public String toString() {
        return String.valueOf(n);
    }

    /**
     * Tests if two Int objects store the same value.
     * @param other another Int object
     * @return true if values are identical
     */
    public boolean equals(Int other) {
        return this.n == other.n;
    }

    /**
     * Tests if this Int objects stores a given value.
     * @param n an integer value
     * @return true if his Int objects stores n
     */
    public boolean equals(int n) {
        return this.n == n;
    }

    /**
     * Compares this object to the specified object. The result is true if and
     * only if the argument is not null and is an Int object that contains the
     * same int value as this object.
     * @param object another pair
     */
    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        } else if (this == object) {
            return true;
        }
        if (object instanceof Int) {
            return this.n == ((Int) object).n;
        } else {
            return false;
        }

    }

    @Override
    public int hashCode() {
        return n;
    }

    /**
     * Compares two Int objects numerically.
     * @param N another Int object
     */
    @Override
    public int compareTo(Int N) {
        if (n < N.n) {
            return -1;
        } else {
            return (n == N.n) ? 0 : 1;
        }
    }
}
