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

import eu.digitisation.text.CharFilter;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author R.C.C
 */
public class CharFilterTest {

    public CharFilterTest() {
    }


    /**
     * Test of translate method, of class CharFilter.
     *
     * @throws java.net.URISyntaxException
     */
    @Test
    public void testTranslate_String() throws URISyntaxException {
        System.out.println("translate");
        URL resourceUrl = getClass().getResource("/UnicodeCharEquivalences.txt");
        File file = new File(resourceUrl.toURI());
        CharFilter filter = new CharFilter(file);
        String s = "a\u0133";  // ij
        String expResult = "aij";
        String result = filter.translate(s);
        assertEquals(expResult.length(), result.length());
        assertEquals(expResult, result);
    }

    @Test
    public void testCompatibilityMode() {
        System.out.println("compatibility");
        CharFilter filter = new CharFilter();
        String s = "\u0133";
        String r = "ij";
        assert (!r.equals(filter.translate(s)));
        filter.setCompatibility(true);
        assertEquals(r, filter.translate(s));

    }
}
