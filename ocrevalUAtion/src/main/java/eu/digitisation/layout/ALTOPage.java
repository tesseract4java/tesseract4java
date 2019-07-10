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
import eu.digitisation.log.Messages;
import eu.digitisation.xml.DocumentParser;
import java.awt.Polygon;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author R.C.C.
 */
public class ALTOPage extends Page {

    public ALTOPage(File file) {
        try {
            parse(file);
        } catch (IOException ex) {
            Messages.info(ALTOPage.class.getName() + ": " + ex);
        }
    }

    /**
     * Read the bounding-box information stored in the element attributes l, t,
     * b, r
     *
     * @param e the container element
     * @return the BoundingBox for this element or null if some attributes are
     * missing
     */
    private static BoundingBox getBBox(Element e) {
        if (e.hasAttribute("HEIGHT")
                && e.hasAttribute("WIDTH")
                && e.hasAttribute("VPOS")
                && e.hasAttribute("HPOS")) {
            int height = Integer.parseInt(e.getAttribute("HEIGHT"));
            int width = Integer.parseInt(e.getAttribute("WIDTH"));
            int y0 = Integer.parseInt(e.getAttribute("VPOS"));
            int x0 = Integer.parseInt(e.getAttribute("HPOS"));
            return new BoundingBox(x0, y0, x0 + width, y0 + height);
        } else {
            return null;
        }
    }

    @Override
    public final void parse(File file) throws IOException {
        Document doc = DocumentParser.parse(file);
        String id = "";
        ComponentType type = ComponentType.PAGE;

        NodeList nodes = doc.getElementsByTagName("Page");
        if (nodes.getLength() > 1) {
            throw new IOException("Multiple pages in document");
        } else {
            Element page = (Element) nodes.item(0);
            root = parse(page);
        }

    }

    /**
     * Parse an XML element and retrieve the associated text component
     *
     * @param element an XML element
     * @return the text component associated with this element
     */
    TextComponent parse(Element element) {
        String id = element.getAttribute("ID");
        ComponentType type = ComponentType.valueOf(FileType.ALTO, element.getTagName());
        String subtype = element.getAttribute("TYPE");
        String content = element.getAttribute("CONTENT");
        BoundingBox bbox = getBBox(element);
        Polygon frontier = (bbox == null) ? null : bbox.asPolygon();
        List<TextComponent> array = new ArrayList<TextComponent>();
        NodeList children = element.getChildNodes();
        int number = components.size();

        components.add(null);  // reserve the place for this component

        for (int nchild = 0; nchild < children.getLength(); ++nchild) {
            Node child = children.item(nchild);
            if (child instanceof Element) {
                Element e = (Element) child;
                String tag = e.getTagName();
                if (tag.equals("PrintSpace")
                        || tag.equals("ComposedBlock")
                        || tag.equals("TextBlock")
                        || tag.equals("TextLine")
                        || tag.equals("String")) {
                    TextComponent subcomponent = parse(e);
                    array.add(subcomponent);
                }
            }
        }
        TextComponent component = new TextComponent(id, type, subtype, content, frontier);
        components.set(number, component);
        subcomponents.put(component, array);
        return component;
    }
}
