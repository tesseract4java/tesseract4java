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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Test elements against XPath expressions and include or exclude the elements
 *
 * @author R.C.C.
 */
public class XPathFilter {

    static XPath xpath = XPathFactory.newInstance().newXPath();
    List<String> inexpr;  // the inclussion expressions
    List<String> exexpr;  // the exclussion expressions
    List<XPathExpression> inclusions; // XPath expressions of included elements
    List<XPathExpression> exclusions; // XPath expressions of excluded elements

    private void include(String expression) throws XPathExpressionException {
        inexpr.add(expression);
        inclusions.add(xpath.compile("self::" + expression));
    }

    private void includeAll(String[] array) throws XPathExpressionException {
        if (array != null) {
            for (String s : array) {
                include(s);
            }
        }
    }

    private void exclude(String expression) throws XPathExpressionException {
        exexpr.add(expression);
        exclusions.add(xpath.compile("self::" + expression));
    }

    private void excludeAll(String[] array) throws XPathExpressionException {
        if (array != null) {
            for (String s : array) {
                exclude(s);
            }
        }
    }

    /**
     * Default constructor
     */
    public XPathFilter() {
        inexpr = new ArrayList<String>();
        exexpr = new ArrayList<String>();
        inclusions = new ArrayList<XPathExpression>();
        exclusions = new ArrayList<XPathExpression>();
    }

    /**
     * Create an XPathFilter from two arrays of XPath expressions
     *
     * @param inclusions an array of XPath inclusion expressions (possibly null)
     * @param exclusions array of XPath exclusion expressions (possibly null)
     * @throws javax.xml.xpath.XPathExpressionException
     */
    public XPathFilter(String[] inclusions, String[] exclusions)
            throws XPathExpressionException {
        this();
        includeAll(inclusions);
        excludeAll(exclusions);
    }

    /**
     * Read file into lines
     *
     * @param file the input file
     * @return the content as a list of strings, each one with the content in
     * one file line, excluding those starting with the character #
     * @throws IOException
     */
    private String[] lines(File file) throws IOException {
        List<String> list = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while (reader.ready()) {
            String line = reader.readLine().trim();
            if (line.length() > 0 && !line.startsWith("#")) {
                list.add(line);
            }
        }
        return list.toArray(new String[list.size()]);
    }

    /**
     * Create an ElementFilter from the XPath expressions in a file (one per
     * line)
     *
     * @param infile a file containing XPath inclusion expressions (one per
     * line)
     * @param exfile a file containing XPath exclusion expressions (one per
     * line)
     * @throws java.io.IOException
     * @throws javax.xml.xpath.XPathExpressionException
     */
    public XPathFilter(File infile, File exfile)
            throws IOException, XPathExpressionException {
        this();
        if (infile != null) {
            includeAll(lines(infile));
        }
        if (exfile != null) {
            excludeAll(lines(exfile));
        }
    }

    /**
     * Check if the element matches any valid inclusion expression
     *
     * @param element an XML element
     * @return true if the element matches any of the XPath inclusion
     * expressions
     */
    public boolean included(Element element) {

        for (int n = 0; n < inclusions.size(); ++n) {
            XPathExpression expression = inclusions.get(n);
            try {
                Boolean match = (Boolean) expression.evaluate(element,
                        XPathConstants.BOOLEAN);
                if (match) {
                    return true;
                }
            } catch (XPathExpressionException ex) {
                // not a valid match
            }
        }
        return false;
    }

    /**
     * Check if the element matches any valid inclusion expression
     *
     * @param element an XML element
     * @return true if the element matches any of the XPAth exclusion
     * expressions
     */
    public boolean excluded(Element element) {
        for (XPathExpression expression : exclusions) {
            try {
                Boolean match = (Boolean) expression.evaluate(element,
                        XPathConstants.BOOLEAN);
                if (match) {
                    return true;
                }
            } catch (XPathExpressionException ex) {
                // not a valid match
            }
        }
        return false;
    }

    /**
     *
     * @param element an XML element
     * @return true if the element matches any of the XPAth inclusion
     * expressions and none of the exclusion expressions
     */
    private boolean accepted(Element element) {
        return included(element) && !excluded(element);
    }

    /**
     * Select elements matching the XPath valid expression
     *
     * @param element a parent element
     * @return all descendent elements matching at least one of the XPath
     * expressions in this filter
     */
    public List<Element> selectElements(Element element) {
        NodeList nodeList = element.getElementsByTagName("*");
        List<Element> selection = new ArrayList<Element>();

        for (int n = 0; n < nodeList.getLength(); n++) {
            Node node = nodeList.item(n);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) node;
                if (accepted(e)) {
                    selection.add(e);
                }
            }
        }
        return selection;
    }

    /**
     * Select elements matching the XPath valid expression
     *
     * @param doc a container XML document
     * @return all elements in the document matching at least one of the XPath
     * expressions in this filter
     */
    public List<Element> selectElements(Document doc) {
        NodeList nodeList = doc.getElementsByTagName("*");
        List<Element> selection = new ArrayList<Element>();

        for (int n = 0; n < nodeList.getLength(); n++) {
            Node node = nodeList.item(n);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) node;
                if (accepted(e)) {
                    selection.add(e);
                }
            }
        }
        return selection;
//        return selectElements(doc.getDocumentElement());
    }

    /**
     * Select content under the filtered elements
     *
     * @param args
     * @throws java.io.IOException
     * @throws javax.xml.xpath.XPathExpressionException
     */
    public static void main(String[] args) throws IOException, XPathExpressionException {
        if (args.length < 2) {
            System.err.println("usage: XPathFilter xmlfile xpathfile xpathfile");
        } else {
            File xmlfile = new File(args[0]);
            File xpathinfile = new File(args[1]);
            File xpathexfile = new File(args[2]);
            XPathFilter filter = new XPathFilter(xpathinfile, xpathexfile);
            List<Element> selected = filter.selectElements(DocumentParser.parse(xmlfile));
            for (Element e : selected) {
                System.out.println(e.getNodeName());
            }
        }
    }
}
