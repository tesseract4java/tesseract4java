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
package eu.digitisation.math;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author rafa
 */
public class ArraysTest {

    /**
     * Test of sum method, of class ArrayMath.
     */
    @Test
    public void testSum_intArr() {
        System.out.println("sum");
        int[] array = {1, 2, 3, 0, -1};
        int expResult = 5;
        int result = Arrays.sum(array);
        assertEquals(expResult, result);
    }

    /**
     * Test of sum method, of class ArrayMath.
     */
    @Test
    public void testSum_doubleArr() {
        System.out.println("sum");
        double[] array = {1, 2, 3, 0, -1};
        double expResult = 5;
        double result = Arrays.sum(array);
        assertEquals(expResult, result, 0.01);
    }

    /**
     * Test of average method, of class ArrayMath.
     */
    @Test
    public void testAverage_intArr() {
        System.out.println("average");
        int[] array = {1, 2, 3, -2};
        double expResult = 1.0;
        double result = Arrays.average(array);
        assertEquals(expResult, result, 0.0001);
    }

    /**
     * Test of average method, of class ArrayMath.
     */
    @Test
    public void testAverage_doubleArr() {
        System.out.println("average");
        double[] array = {1, 2, 3, -2};
        double expResult = 1.0;
        double result = Arrays.average(array);
        assertEquals(expResult, result, 0.0001);
    }

    /**
     * Test of logaverage method, of class ArrayMath.
     */
    @Test
    public void testLogaverage_intArr() {
        System.out.println("logaverage");
        int[] array = {10, 100, 1000};
        double expResult = 100.0;
        double result = Arrays.logaverage(array);
        assertEquals(expResult, result, 0.001);
    }

    /**
     * Test of logaverage method, of class ArrayMath.
     */
    @Test
    public void testLogaverage_doubleArr() {
        System.out.println("logaverage");
        double[] array = {10, 100, 1000};
        double expResult = 100.0;
        double result = Arrays.logaverage(array);
        assertEquals(expResult, result, 0.001);
    }

    /**
     * Test of scalar method, of class ArrayMath.
     */
    @Test
    public void testScalar() {
        System.out.println("scalar");
        double[] x = {1, 2, 3};
        double[] y = {1, 2, 3};
        double expResult = 14.0;
        double result = Arrays.scalar(x, y);
        assertEquals(expResult, result, 0.0001);
    }

    /**
     * Test of max method, of class ArrayMath.
     */
    @Test
    public void testMax_intArr() {
        System.out.println("max");
        int[] array = {-5, 2, 3};
        int expResult = 3;
        int result = Arrays.max(array);
        assertEquals(expResult, result);
    }

    /**
     * Test of max method, of class ArrayMath.
     */
    @Test
    public void testMax_doubleArr() {
        System.out.println("max");
        double[] array = {-5, 2, 3};
        double expResult = 3;
        double result = Arrays.max(array);
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of min method, of class ArrayMath.
     */
    @Test
    public void testMin_intArr() {
        System.out.println("min");
        int[] array = {2, -1};
        int expResult = -1;
        int result = Arrays.min(array);
        assertEquals(expResult, result);
    }

    /**
     * Test of min method, of class ArrayMath.
     */
    @Test
    public void testMin_doubleArr() {
        System.out.println("min");
        double[] array = {2, 0, -1};
        double expResult = -1.0;
        double result = Arrays.min(array);
        assertEquals(expResult, result, 0.0001);
    }

    /**
     * Test of cov method, of class ArrayMath.
     */
    @Test
    public void testCov_intArr_intArr() {
        System.out.println("cov");
        int[] X = {1, 2, 3};
        int[] Y = {1, 2, 3};
        double expResult = 2.0/3;
        double result = Arrays.cov(X, Y);
        assertEquals(expResult, result, 0.001);
    }

    /**
     * Test of cov method, of class ArrayMath.
     */
    @Test
    public void testCov_doubleArr_doubleArr() {
        System.out.println("cov");
        double[] X = {1, 2, 3};
        double[] Y = {1, 2, 3};
        double expResult = 2.0/3;
        double result = Arrays.cov(X, Y);
        assertEquals(expResult, result, 0.001);
    }

    /**
     * Test of std method, of class ArrayMath.
     */
    @Test
    public void testStd_intArr() {
        System.out.println("std");
        int[] X = {1, 2, 2, 3};
        double expResult = Math.sqrt(0.5);
        double result = Arrays.std(X);
        assertEquals(expResult, result, 0.0001);
    }

    /**
     * Test of std method, of class ArrayMath.
     */
    @Test
    public void testStd_doubleArr() {
        System.out.println("std");
        double[] X = {1, 2, 2, 3};
        double expResult = Math.sqrt(0.5);
        double result = Arrays.std(X);
        assertEquals(expResult, result, 0.0001);
    }

}
