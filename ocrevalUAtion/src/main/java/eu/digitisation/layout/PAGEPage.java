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
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author R.C.C.
 */
public class PAGEPage extends Page {

    public PAGEPage(File file) {
        try {
            parse(file);
        } catch (IOException ex) {
            Messages.info(PAGEPage.class.getName() + ": " + ex);
        }
    }

    @Override
    public final void parse(File file) throws IOException {
        Document doc = DocumentParser.parse(file);
        String id = doc.getDocumentElement().getAttribute("pcGtsId");
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
        String id = element.getAttribute("id");
        ComponentType type = 
                ComponentType.valueOf(FileType.PAGE, element.getTagName());
        String subtype = element.getAttribute("type");
        String content = null;
        Polygon frontier = new Polygon();
        List<TextComponent> array = new ArrayList<TextComponent>();        
        NodeList children = element.getChildNodes();
        int number = components.size();
        
        components.add(null);  // reserve the place for this component
        
        for (int nchild = 0; nchild < children.getLength(); ++nchild) {
            Node child = children.item(nchild);
            if (child instanceof Element) {
                Element e = (Element) child;
                String tag = e.getTagName();
                if (tag.equals("TextEquiv")) {
                    if (content != null) {
                        throw new DOMException(DOMException.INVALID_ACCESS_ERR,
                                "Multiple content in region " + id);
                    }
                    content = child.getTextContent().trim();
                }
                if (tag.equals("Coords")) {
                    Element coords = (Element) child;
                    if (frontier.npoints > 0) {
                        throw new DOMException(DOMException.INVALID_ACCESS_ERR,
                                "Multiple Coords in region " + id);
                    }
                    NodeList nodes = coords.getChildNodes(); // points
                    for (int npoint = 0; npoint < nodes.getLength(); ++npoint) {
                        Node node = nodes.item(npoint);
                        if (node.getNodeName().equals("Point")) {
                            Element point = (Element) node;
                            int x = Integer.parseInt(point.getAttribute("x"));
                            int y = Integer.parseInt(point.getAttribute("y"));
                            frontier.addPoint(x, y);
                        }
                    }
                } else if (tag.equals("TextRegion")
                        || tag.equals("TextLine")
                        || tag.equals("Word")) {
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
