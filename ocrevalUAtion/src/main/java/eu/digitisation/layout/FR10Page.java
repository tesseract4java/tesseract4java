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
 * FR10Page information contained in one GT or OCR file. Pending: store nested
 * structure
 *
 * @author R.C.C.
 */
public class FR10Page extends Page {

    public FR10Page(File file) {
        try {
            parse(file);
        } catch (IOException ex) {
            Messages.info(FR10Page.class.getName() + ": " + ex);
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
        if (e.hasAttribute("l")
                && e.hasAttribute("t")
                && e.hasAttribute("r")
                && e.hasAttribute("b")) {
            int x0 = Integer.parseInt(e.getAttribute("l"));
            int y0 = Integer.parseInt(e.getAttribute("t"));
            int x1 = Integer.parseInt(e.getAttribute("r"));
            int y1 = Integer.parseInt(e.getAttribute("b"));
            return new BoundingBox(x0, y0, x1, y1);
        } else {
            return null;
        }
    }

    /**
     * Get the textual content under a given element
     *
     * @param e the container element
     * @return the text contained under this element
     */
    private static String getTextContent(Element e) {
        StringBuilder builder = new StringBuilder();
        NodeList nodes = e.getElementsByTagName("charParams");
        if (nodes.getLength() > 0) {
            int last = 0; // recognise new line
            for (int nchar = 0; nchar < nodes.getLength(); ++nchar) {
                Element charParam = (Element) nodes.item(nchar);
                String content = charParam.getTextContent();
                int left = Integer.parseInt(charParam.getAttribute("l"));
                if (left < last) {
                    builder.append('\n');
                }
                last = left;
                builder.append(content.length() > 0 ? content : ' ');
            }
        } else if (!e.getNodeName().equals("formatting")) {
            nodes = e.getElementsByTagName("formatting");
            for (int nline = 0; nline < nodes.getLength(); ++nline) {
                Element charParam = (Element) nodes.item(nline);
                String content = charParam.getTextContent();
                if (builder.length() > 0) {
                    builder.append('\n');
                }
                builder.append(content);
            }
        } else {  // a plain formatting element
            builder.append(e.getTextContent());
        }

        return builder.toString();
    }

    @Override
    public final void parse(File file) throws IOException {

        Document doc = DocumentParser.parse(file);
        String id = "";
        ComponentType type = ComponentType.PAGE;
        NodeList nodes = doc.getElementsByTagName("page");
        if (nodes.getLength() > 1) {
            throw new IOException("Multiple pages in document");
        } else {
            Element page = (Element) nodes.item(0);
            root = parse(page);
        }
    }

    private TextComponent parse(Element element) {
        String id = element.getAttribute("pageElemId");
        ComponentType type = ComponentType.valueOf(FileType.FR10, element.getTagName());
        String subtype = element.getAttribute("blockType"); // empty for non-blocks
        String content = getTextContent(element);
        BoundingBox bbox = getBBox(element);
        Polygon frontier = (bbox == null) ? null : bbox.asPolygon();

        List<TextComponent> array = new ArrayList<TextComponent>();
        NodeList children = element.getChildNodes();
        int number = components.size();

        components.add(null);  // add room for this component

        for (int nchild = 0; nchild < children.getLength(); ++nchild) {
            Node child = children.item(nchild);
            if (child instanceof Element) {
                Element subelement = (Element) child;
                String tag = subelement.getTagName();

                if (tag.equals("block")
                        || tag.equals("text")
                        || tag.equals("par")
                        || tag.equals("line")) {
                    TextComponent subcomponent = parse(subelement);
                    array.add(subcomponent);
                } else if (tag.equals("formatting")) {
                    List<TextComponent> words = parseWords(subelement);
                    array.addAll(words);
                }
            }
        }

        TextComponent component = new TextComponent(id, type, subtype,
                content, frontier);
        components.set(number, component);
        subcomponents.put(component, array);

        return component;
    }

    /**
     * Polygonal cover of a sequence of bounding boxes
     * @param points
     * @return 
     */
    private Polygon cover(Polygon points) {
        Polygon cover = new Polygon();
        for (int n = 0; n < points.npoints; ++n) {
            if (n % 2 == 0) {
                cover.addPoint(points.xpoints[n], points.ypoints[n]);
            } else {
                cover.addPoint(points.xpoints[n], points.ypoints[n - 1]);
            }
        }
        for (int n = points.npoints - 1; n >= 0; --n) {
            if (n % 2 == 1) {
                cover.addPoint(points.xpoints[n], points.ypoints[n]);
            } else {
                cover.addPoint(points.xpoints[n], points.ypoints[n + 1]);
            }
        }
        return cover;
    }

    /**
     * Specific function to split FR10 formatting elements into smaller
     * components (words). Formatting is a sequence of charParams elements
     * containing characters. An empty charParams element indicates word
     * boundaries.
     *
     * @param element a formatting element
     * @return the TextComponents (words) in this element
     */
    private List<TextComponent> parseWords(Element element) {
        List<TextComponent> words = new ArrayList<TextComponent>();
        NodeList charParams = element.getElementsByTagName("charParams");
        StringBuilder builder = new StringBuilder();
        Polygon points = new Polygon();

        for (int nchar = 0; nchar < charParams.getLength(); ++nchar) {
            Element charParam = (Element) charParams.item(nchar);
            String content = charParam.getTextContent().trim();
            if (!content.matches("\\p{Space}*")) {  // end of word
                int x0 = Integer.parseInt(charParam.getAttribute("l"));
                int y0 = Integer.parseInt(charParam.getAttribute("t"));
                int x1 = Integer.parseInt(charParam.getAttribute("r"));
                int y1 = Integer.parseInt(charParam.getAttribute("b"));
                points.addPoint(x0, y0);
                points.addPoint(x1, y1);
                builder.append(content);
            } else if (builder.length() > 0) {  // some content 
                TextComponent word
                        = new TextComponent(null, ComponentType.WORD, null,
                                builder.toString(), cover(points));
                words.add(word);
                builder = new StringBuilder();
                points = new Polygon();
            }
        }
        // flush 
        if (builder.length() > 0) {
            TextComponent word
                    = new TextComponent(null, ComponentType.WORD, null,
                            builder.toString(), cover(points));
            words.add(word);
        }
        components.addAll(words);
        return words;
    }

}
