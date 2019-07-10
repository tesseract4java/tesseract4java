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
package eu.digitisation.document;

import eu.digitisation.math.MinimalPerfectHash;
import static junit.framework.TestCase.assertEquals;
import org.junit.Test;

/**
 *
 * @author R.C.C
 */
public class TokenArrayTest {

    public TokenArrayTest() {
    }

 

    /**
     * Test of encode method, of class TextFileEncoder.
     */
    @Test
    public void testEncode_String() {
        System.out.println("encode");
        String input = "hola&amigo2\n3.14 mi casa, todos los días\n"
                + "mesa-camilla java4you i.b.m. i+d Dª María 3+100%";
        String expOutput = "hola&amigo 2 3.14 mi casa todos los días"
                + " mesa-camilla java 4 you i.b.m i+d Dª María 3 100%";
        MinimalPerfectHash f = new MinimalPerfectHash(true);
        TokenArray array = new TokenArray(f, input);
        String output = array.toString();
        assertEquals(expOutput, output);

        int size = array.length();
        assertEquals(18, size);
    }
}
