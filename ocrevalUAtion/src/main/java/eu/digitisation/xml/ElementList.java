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
package eu.digitisation.xml;

import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A class transforming a org.w3c.dom.NodeList into a list of elements
 *
 * @author R.C.C
 */
public class ElementList extends ArrayList<Element> {

    /**
     * Create a list of elements from a org.w3c.dom.NodeList
     *
     * @param nodes
     */
    public ElementList(NodeList nodes) {
        for (int n = 0; n < nodes.getLength(); ++n) {
            Node node = nodes.item(n);
            if (node instanceof Element) {
                add((Element) node);
            } else {
                throw new IllegalArgumentException("ElementList: " 
                        + "source NodeList contains non-element nodes");
            }
        }
    }
}
