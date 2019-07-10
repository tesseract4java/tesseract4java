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

package eu.digitisation.layout;

import eu.digitisation.input.FileType;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author R.C.C
 */
public class ComponentTagTest {
    
    /**
     * Test of shortTag method, of class ComponentTag.
     */
    @Test
    public void testShortTag() {
        System.out.println("shortTag");
        ComponentTag tag = ComponentTag.valueOf(FileType.PAGE, "TextRegion");
        String expResult = "TextRegion";
        String result = ComponentTag.shortTag(tag);
        assertEquals(expResult, result);
    }
    
}
