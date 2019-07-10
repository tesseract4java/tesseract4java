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

import java.util.List;
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
public class CounterTest {

    public CounterTest() {
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
     * Test of add method, of class Counter.
     */
    @Test
    public void test() {
        System.out.println("add");
        Object object = null;
        int value = 0;
        Counter<Integer> instance = new Counter<Integer>();
        instance.add(1, 3);
        instance.inc(1);
        instance.add(1, -1);
        assertEquals(instance.get(1).intValue(), 3);
    }

    @Test
    public void testKeyList() {
        System.out.println("keyList");
        Counter<Integer> instance = new Counter<Integer>();
        instance.add(1, 6);
        instance.add(2, 3);
        instance.add(3, 1);
        instance.add(4, 5);
        Integer[] expResult = {3, 2, 4, 1};
        Integer[] result = new Integer[4];
        List<Integer> list = instance.keyList(Counter.Order.ASCENDING_VALUE);
        System.out.println(list);
        list.toArray(result);
        assertArrayEquals(expResult, result);
    }

}
