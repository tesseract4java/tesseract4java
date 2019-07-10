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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author R.C.C
 */
public class BiCounterTest {

    public BiCounterTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of value method, of class BiCounter.
     */
    @Test
    public void testValue() {
        System.out.println("value");
        Object o1 = null;
        Object o2 = null;
        BiCounter<Integer, Integer> bc = new BiCounter<Integer, Integer>();
        bc.inc(1, 2);
        bc.inc(1, 3);
        bc.add(1, 3, 4);
        assertEquals(1, bc.value(1, 2));
        assertEquals(5, bc.value(1, 3));
        assertEquals(6, bc.value(1, null));
        assertEquals(6, bc.total());
    }

}
