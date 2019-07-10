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
package eu.digitisation.xml;

import eu.digitisation.log.Messages;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Adds some useful auxiliary functions to handle XML documents
 *
 * @author R.C.C
 */
public class DocumentBuilder {

    Document doc;

    /**
     * Create an empty document
     *
     * @param doctype the document type
     */
    public DocumentBuilder(String doctype) {
        try {
            doc = javax.xml.parsers.DocumentBuilderFactory
                    .newInstance().newDocumentBuilder()
                    .newDocument();
            Element root = doc.createElement(doctype);
            doc.appendChild(root);
        } catch (ParserConfigurationException ex) {
            Messages.info(DocumentBuilder.class.getName() + ": " + ex);
        }
    }

    /**
     * Create a copy of another document
     *
     * @param other
     */
    public DocumentBuilder(Document other) {
        doc = clone(other);
    }

    /**
     * Create a copy of another document
     *
     * @param source a source document
     * @return a deep copy of the document
     */
    public static Document clone(Document source) {
        try {
            Document target = javax.xml.parsers.DocumentBuilderFactory
                    .newInstance().newDocumentBuilder()
                    .newDocument();
            Node node = target.importNode(source.getDocumentElement(), true);
            target.appendChild(node);
            return target;
        } catch (ParserConfigurationException ex) {
            Messages.info(DocumentBuilder.class.getName() + ": " + ex);
        }
        return null;
    }

    /**
     *
     * @return the org.w3c.dom.Document
     */
    public Document document() {
        return doc;
    }

    /**
     *
     * @return the root element in this document
     */
    public Element root() {
        return doc.getDocumentElement();
    }

    /**
     *
     * @param e The parent element
     * @param name The child element name
     * @return list of children of e with the given tag
     */
    public static List<Element> getChildElementsByTagName(Element e, String name) {
        ArrayList<Element> list = new ArrayList<Element>();
        NodeList children = e.getChildNodes();

        for (int n = 0; n < children.getLength(); ++n) {
            Node node = children.item(n);
            if (node instanceof Element && node.getNodeName().equals(name)) {
                list.add((Element) node);
            }
        }
        return list;
    }

    /**
     * Create a new element under the designated element in the document.
     *
     * @param parent the parent element
     * @param tag The tag of the new child element
     * @return the added element
     */
    public Element addElement(Element parent, String tag) {
        Element element = doc.createElement(tag);
        parent.appendChild(element);
        return element;
    }

    /**
     * Create a new element directly under the root element.
     *
     * @param tag The tag of the new child element
     * @return the added element
     */
    public Element addElement(String tag) {
        return addElement(root(), tag);
    }

    /**
     * Insert an element as a child of another element
     *
     * @param parent the parent element
     * @param child the child element (even external one)
     * @return the parent element
     */
    public Element addElement(Element parent, Element child) {
        if (parent.getOwnerDocument() == child.getOwnerDocument()) {
            parent.appendChild(child);
        } else {
            parent.appendChild(doc.importNode(child, true));
        }
        return parent;
    }

    /**
     * Add text content under the given element
     *
     * @param parent the container element
     * @param content the textual content
     */
    public void addText(Element parent, String content) {
        parent.appendChild(doc.createTextNode(content));
    }

    /**
     * Add a text element with the specified textual content under the
     * designated element in the document.
     *
     * @param parent the parent element
     * @param tag the new child element tag
     * @param content the textual content
     * @return the added element
     */
    public Element addTextElement(Element parent, String tag, String content) {
        Element element = doc.createElement(tag);
        element.appendChild(doc.createTextNode(content));
        parent.appendChild(element);
        return element;
    }

    /**
     * Dump content to string
     *
     * @return the content as a string
     */
    @Override
    public String toString() {
        return new DocumentWriter(doc).toString();
    }

    /**
     * Dump content to file
     *
     * @param file the output file
     */
    public void write(java.io.File file) {
        new DocumentWriter(doc).write(file);
    }

}
