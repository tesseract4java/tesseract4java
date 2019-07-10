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
package eu.digitisation.text;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author rafa
 */
public class StringNormalizerTest {

    public StringNormalizerTest() {
    }

    /**
     * Test of reduceWS method, of class StringNormalizer.
     */
    @Test
    public void testReduceWS() {
        System.out.println("reduceWS");
        String s = "one  \rtwo\nthree";
        String expResult = "one two three";
        String result = StringNormalizer.reduceWS(s);
        assertEquals(expResult, result);
    }

    /**
     * Test of composed method, of class StringNormalizer.
     */
    @Test
    public void testComposed() {
        System.out.println("composed");
        String s = "n\u0303";
        String expResult = "ñ";
        String result = StringNormalizer.composed(s);
        assertEquals(expResult, result);
    }

    /**
     * Test of compatible method, of class StringNormalizer.
     */
    @Test
    public void testCompatible() {
        System.out.println("compatible");
        String s = "\ufb00";  // ff ligature
        String expResult = "ff";
        String result = StringNormalizer.compatible(s);
        assertEquals(expResult, result);
    }

    /**
     * Test of removeDiacritics method, of class StringNormalizer.
     */
    @Test
    public void testRemoveDiacritics() {
        System.out.println("removeDiacritics");
        String s = "cañón";
        String expResult = "canon";
        String result = StringNormalizer.removeDiacritics(s);
        assertEquals(expResult, result);
    }

    /**
     * Test of removePunctuation method, of class StringNormalizer.
     */
    @Test
    public void testRemovePunctuation() {
        System.out.println("removePunctuation");
        String s = "!\"#}-"; // + is not in punctuation block
        String expResult = "";
        String result = StringNormalizer.removePunctuation(s);
        assertEquals(expResult, result);
    }

    @Test
    public void testTrim() {
        System.out.println("trim");
        String s = "! \"#lin?ks+!\"#}-"; // + is not in punctuation block
        String expResult = "lin?ks+";
        String result = StringNormalizer.trim(s);
        assertEquals(expResult, result);
    }

    @Test
    public void testStrip() {
        System.out.println("strip");
        String s = "Stra\u00dfe+ links+!\"#}-"; // ª is a letter!
        String expResult = "Stra\u00dfe links";
        String result = StringNormalizer.strip(s);
        assertEquals(expResult, result);
    }

    /**
     * Test of encode method, of class StringNormalizer.
     */
    @Test
    public void testEncode() {
        System.out.println("encode");
        String s = "<\">";
        String expResult = "&lt;&quot;&gt;";
        String result = StringNormalizer.encode(s);
        assertEquals(expResult, result);

    }
}
