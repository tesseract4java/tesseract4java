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
package eu.digitisation.document;

import eu.digitisation.distance.EditDistance;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author rafa
 */
public class TermFrequencyVectorTest {

    String s1 = //"UN AN : PARIS, 8 Francs. — PROVINCE, 10 Francs. — ETRANGER, suivant le Tarif postal. "
             "A LA LIBRAIRIE, 10, RUE DE LA BOURSE. CHRONIQUE GOURMANDE UNE des gracieuses";
    String s2 = //"V AN : PA's»s*c8fFrancs. — Pr«vjnv-e, 11 > Fr.it:-*.— K: kvnobi. ', Tarif ;\".s:a!. 1- ni 7 
            "A LA LIBRAIRIE, 10. RUE DE LA BOURSE. TOUS PREMIER. I I VRAIS'< , CHRONIQUE GOURMANDE * ,' -~J.,' 1 Ii nk .!•'« gracieuses";

    public TermFrequencyVectorTest() {

    }

    /**
     * Test of distance method, of class TermFrequencyVector.
     */
    @Test
    public void testDistance() {
        System.out.println("distance");
        TermFrequencyVector tf1 = new TermFrequencyVector(s1);
        TermFrequencyVector tf2 = new TermFrequencyVector(s2);
        int expResult = EditDistance.wordDistance(s1, s2, 1000)[2];
        int result = tf1.distance(tf2);
        assertEquals(expResult, result);
    }

    /**
     * Test of total method, of class TermFrequencyVector.
     */
    @Test
    public void testTotal() {
        System.out.println("total");
        TermFrequencyVector tf1 = new TermFrequencyVector(s1);
        TermFrequencyVector tf2 = new TermFrequencyVector(s2);
        System.out.println("tf1=" + tf1.toString());
        System.out.println("f2=" + tf2.toString());
        assertEquals(13, tf1.total());
        assertEquals(20, tf2.total());
    }

}
