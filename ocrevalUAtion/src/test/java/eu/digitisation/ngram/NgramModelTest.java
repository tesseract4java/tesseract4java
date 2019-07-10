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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author carrasco@ua.es
 */
public class NgramModelTest {

    @Test
    public void testSize() {
        System.out.println("size");
        NgramModel ngrams = new NgramModel(2);
        ngrams.addWord("0000");
        ngrams.addWord("0100");

        int expResult = 9;  // 5 bi-grams plus 3 uni-grams plus 1 0-gram
        int result = ngrams.size();

        assertEquals(expResult, result);

        ngrams = new NgramModel(3);
        ngrams.addWord("0000");
        ngrams.addWord("0100");

        expResult = 15;  // 6 tri-grams + 5 bi-grams +3 uni-grams + 1 0-gram
        result = ngrams.size();

        assertEquals(expResult, result);

    }

    @Test
    public void testGetGoodTuringPars() {
        System.out.println("size");
        NgramModel ngrams = new NgramModel(3);
        ngrams.addWord("0000");
        ngrams.addWord("0100");
        double[] expResult = {0.1, 0.2, 0.5};
        double[] result = ngrams.getGoodTuringPars();
        assertEquals(expResult.length, result.length);
        for (int n = 0; n < result.length; ++n) {
            assertEquals(expResult[n], result[n], 0.001);
        }

    }

    @Test
    public void testProb() {
        System.out.println("prob");
        NgramModel ngrams = new NgramModel(3);
        ngrams.addWord("0000");
        ngrams.addWord("0100");

        assertEquals(4 / (double) 7, ngrams.prob("00"), 0.001);
        assertEquals(0.7, ngrams.prob("0"), 0.001);
    }

    @Test
    public void testSmoothProb() {
        System.out.println("prob");
        NgramModel ngrams = new NgramModel(3);
        ngrams.addWord("0000");
        ngrams.addWord("0100");

        double expResult = 0.8 * (4 / (double) 7) + 0.2 * 0.7;
        double result = ngrams.smoothProb("00");
        assertEquals(expResult, result, 0.001);

        expResult = 0.8 * (2 / (double) 7) + 0.2 * 0.2;
        result = ngrams.smoothProb("0" + ngrams.EOS);
        assertEquals(expResult, result, 0.001);
    }

    @Test
    public void testWordLogProb() {
        System.out.println("wordLogProb");
        NgramModel instance = new NgramModel(1);
        instance.addWord("lava");
        double expResult = (3 * Math.log(0.2) + 2 * Math.log(0.4));
        double result = instance.logWordProb("lava");
        assertEquals(expResult, result, 0.01);
    }

    @Test
    public void testLogProb() {
        System.out.println("logProb");
        NgramModel instance = new NgramModel(1);
        instance.addWord("lava");
        double expResult = -Math.log(5);
        double result = instance.logProb("baba", 'v');
        assertEquals(expResult, result, 0.01);

        instance = new NgramModel(2);
        instance.addWord("lava");
        expResult = Math.log(0.2);
        result = instance.logProb("ca", 'v');
        assertEquals(expResult, result, 0.01);
    }

    @Test
    public void testAddSubstrings() {
        System.out.println("addSubstrings");
        String EOS = String.valueOf(NgramModel.EOS);
        NgramModel ngrams = new NgramModel(3);
        NgramModel ref = new NgramModel(3);

        ngrams.addSubstrings("b", "cde");

        // 3-grams
//        ref.addEntry("abc");
        ref.addEntry("bcd");
        ref.addEntry("cde");

        // 2-grams
        ref.addEntry("bc");
        ref.addEntry("cd");
        ref.addEntry("de");

        // 1-grams
        ref.addEntry("c");
        ref.addEntry("d");
        ref.addEntry("e");

        // 0-grams
        ref.addEntries("", 3);
        ref.showDiff(ngrams);

        assertEquals(ref, ngrams);

    }

    @Test
    public void testAddText() {
        System.out.println("addText");
        String BOS = String.valueOf(NgramModel.BOS);
        String EOS = String.valueOf(NgramModel.EOS);
        NgramModel ngrams = new NgramModel(3);
        NgramModel ref = new NgramModel(3);
        String input = "ab\nc";
        InputStream is = new ByteArrayInputStream(input.getBytes());

        // result
        ngrams.addText(is);

        // expected result
        // 3-grams
        ref.addEntry(BOS + "ab");
        ref.addEntry("ab ");
        ref.addEntry("b c");
        ref.addEntry(" c" + EOS);

        //2-grams
        ref.addEntry(BOS + 'a');
        ref.addEntry("ab");
        ref.addEntry("b ");
        ref.addEntry(" c");
        ref.addEntry("c" + EOS);
        // 1-grams
        ref.addEntry("a");
        ref.addEntry("b");
        ref.addEntry(" ");
        ref.addEntry("c");
        ref.addEntry(EOS);
        // 0-grams
        ref.addEntries("", 5);

        ref.showDiff(ngrams);

        assertEquals(ref, ngrams);

        String text = "ab";

        is = new ByteArrayInputStream(text.getBytes());
        double expectedResult = Math.log(0.2);
        double result = ngrams.logLikelihood(is, 0);
        assertEquals(expectedResult, result, 0.0001);
    }
}
