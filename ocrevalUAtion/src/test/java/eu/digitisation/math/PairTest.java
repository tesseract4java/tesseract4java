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

import org.junit.Test;

/**
 *
 * @author R.C.C
 */
public class PairTest {
    /**
     * Test of equals method, of class Pair.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object o = null;
        Pair<String, String> p1 = new Pair<String, String>("a", "b");
        Pair<String, String> p2 = new Pair<String, String>("a", "b");
        Pair<String, String> p3 = new Pair<String, String>("a", "c");
        assert (p1.equals(p2));
        assert (!p1.equals(p3));
    }
}
