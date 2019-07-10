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
package eu.digitisation.distance;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author R.C.C
 */
public class EditTableTest {

    public EditTableTest() {
    }
/*
    @Test
    public void testSet() {
        System.out.println("set");
        byte b = 0;
        byte result = EditTable.setBit(b, 0, true);
        System.out.println(result);
        assertEquals(1, result);
        assertEquals(true, EditTable.getBit(result,0));
    }
*/
    /**
     * Test of get method, of class EditTable.
     */
    @Test
    public void testGet() {
        System.out.println("get");
        EditTable instance = new EditTable(2, 2);
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 2; ++j) {
                if (i == j) {
                    instance.set(i, j, EdOp.KEEP);
                } else {
                    instance.set(i, j, EdOp.SUBSTITUTE);
                }
            }
        }
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 2; ++j) {
                if (i == j) {
                    assertEquals(EdOp.KEEP, instance.get(i, j));
                } else {
                    assertEquals(EdOp.SUBSTITUTE, instance.get(i, j));
                }
            }
        }
    }
}
