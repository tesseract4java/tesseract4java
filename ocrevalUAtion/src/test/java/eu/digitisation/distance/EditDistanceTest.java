/*
 * Copyright (C) 2014 Uni. de Alicante
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
package eu.digitisation.distance;

import eu.digitisation.text.StringNormalizer;
import eu.digitisation.text.Text;
import java.io.File;
import java.net.URL;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author rafa
 */
public class EditDistanceTest {

    @Test
    public void testWeights() {
        System.out.println("Weighted distance");
        EdOpWeight w = new OcrOpWeight();
        String s1 = "a b";
        String s2 = "acb";
        int expResult = 2;
        int result = EditDistance.charDistance(s1, s2, w, 50);
        assertEquals(expResult, result);
    }

    /**
     * Test of wordDistance method, of class EditDistance.
     */
    @Test
    public void testWordDistance() {
        System.out.println("wordDistance");
        String s1 = "p a t a t a";
        String s2 = "a p t a";
        int expResult = 3;
        int[] result = EditDistance.wordDistance(s1, s2, 10);
        assertEquals(expResult, result[2]);
    }

    @Test
    public void testWeightedDistance() {
        String s1 = "ÁÁÁÁ";
        String s2 = "ÁAáa";

        OcrOpWeight W = new OcrOpWeight(); // fully-sensitive
        String r1 = StringNormalizer.canonical(s1, false, false, false);
        String r2 = StringNormalizer.canonical(s2, false, false, false);
        assertEquals(3, EditDistance.charDistance(r1, r2, W, 1000));

        W = new OcrOpWeight(true); //ignore everything
        r1 = StringNormalizer.canonical(s1, true, true, true);
        r2 = StringNormalizer.canonical(s2, true, true, true);
        assertEquals(0, EditDistance.charDistance(r1, r2, W, 1000));

        W = new OcrOpWeight(true); //ignore diacritics
        r1 = StringNormalizer.canonical(s1, false, true, true);
        r2 = StringNormalizer.canonical(s2, false, true, true);
        assertEquals(2, EditDistance.charDistance(r1, r2, W, 1000));

        W = new OcrOpWeight(true); //ignore case
        r1 = StringNormalizer.canonical(s1, true, false, true);
        r2 = StringNormalizer.canonical(s2, true, false, true);
        assertEquals(2, EditDistance.charDistance(r1, r2, W, 1000));
    }
}
