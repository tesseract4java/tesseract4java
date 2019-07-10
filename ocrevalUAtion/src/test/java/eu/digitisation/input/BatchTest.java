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
package eu.digitisation.input;

import eu.digitisation.input.Batch;
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
public class BatchTest {

    public BatchTest() {
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
     * Test of prefix method, of class Batch.
     */
    @Test
    public void testLcp() {
        System.out.println("lcp");
        String s1 = "compare";
        String s2 = "competence";
        String expResult = "comp";
        String result = Batch.prefix(s1, s2);
        assertEquals(expResult, result);
    }

    /**
     * Test of suffix method, of class Batch.
     */
    @Test
    public void testLcs() {
        System.out.println("lcs");
        String s1 = "switzerland";
        String s2 = "disneyland";
        String expResult = "land";
        String result = Batch.suffix(s1, s2);
        assertEquals(expResult, result);
        s2 = "sweden";
        expResult = "";
        result = Batch.suffix(s1, s2);
        assertEquals(expResult, result);
    }

}
