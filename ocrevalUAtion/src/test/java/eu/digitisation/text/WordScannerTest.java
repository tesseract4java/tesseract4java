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
package eu.digitisation.text;

import java.io.IOException;
import static junit.framework.TestCase.assertEquals;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author R.C.C
 */
public class WordScannerTest {

    public WordScannerTest() {
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
     * Test of main method, of class WordScanner.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testnextWord() throws IOException {
        System.out.println("main");
        String input = "hola&amigo2\n3.14 mi casa, todos los días\n"
                + "mesa-camilla java4you i.b.m. i+d Dª María 3+100%";
        WordScanner scanner = new WordScanner(input, null);
        String word;
        int num = 0;
        while ((word = scanner.nextWord()) != null) {
            ++num;
            //System.out.println(word);
        }
        assertEquals(18, num);

    }
}
