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
import java.io.File;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Evaluate XPath expressions.
 */
public class XMLPath {

    final static XPath xpath;

    static {
        xpath = XPathFactory.newInstance().newXPath();
    }

    /**
     * Evaluate XPath expression
     *
     * @param doc the container document
     * @param expression XPath expression
     * @return the list of nodes matching the query
     */
    public static NodeList evaluate(Document doc, String expression) {
        try {
            return (NodeList) xpath.evaluate(expression, doc,
                    XPathConstants.NODESET);
        } catch (XPathExpressionException ex) {
            Messages.info(XMLPath.class.getName() + ": " + ex);
        }
        return null;
    }

    /**
     * Evaluate XPath expression
     *
     * @param file the file containing the XML document
     * @param expression XPath expression
     * @return the list of nodes matching the query
     */
    public static NodeList evaluate(File file, String expression) {
        Document doc = DocumentParser.parse(file);
        try {
            return (NodeList) xpath.evaluate(expression, doc,
                    XPathConstants.NODESET);
        } catch (XPathExpressionException ex) {
            Messages.info(XMLPath.class.getName() + ": " + ex);
        }
        return null;
    }

    /**
     *
     * @param element an XML element
     * @param expression an XPath expression
     * @return the list of descendent nodes matching the query
     */
    public static NodeList evaluate(Element element, String expression) {
        try {
            return (NodeList) xpath.evaluate(expression, element,
                    XPathConstants.NODESET);
        } catch (XPathExpressionException ex) {
            Messages.info(XMLPath.class.getName() + ": " + ex);
        }
        return null;
    }

    /**
     * Test if an element matches the given expression
     *
     * @param element an XML element
     * @param expression an XPath expression with respect to the element it self
     * (e.g., an element tag)
     * @return true if the given element matches the query
     */
    public static boolean matches(Element element, String expression) {
        try {
            return (Boolean) xpath.evaluate("self::" + expression, element, XPathConstants.BOOLEAN);
        } catch (XPathExpressionException ex) {
            Messages.info(XMLPath.class.getName() + ": " + ex);
        }
        return false;
    }

    /**
     * Sample main
     *
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("usage: XMLpath filename xpath");
        } else {
            File file = new File(args[0]);
            String expr = args[1];
            try {
                NodeList nodes = XMLPath.evaluate(file, expr);
                System.out.println(nodes.getLength());
                for (int n = 0; n < nodes.getLength(); ++n) {
                    System.out.println(nodes.item(n));
                }
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }
}
