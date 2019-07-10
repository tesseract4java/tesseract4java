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
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * A builder and parser for XML documents
 *
 * @author R.C.C.
 * @version 2011.03.10
 */
public class DocumentParser {

    static javax.xml.parsers.DocumentBuilder docBuilder;

    static {
        try {
            docBuilder = javax.xml.parsers.DocumentBuilderFactory
                    .newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Messages.info(DocumentParser.class.getName() + ": " + ex);
        }
    }

    /**
     * Create XML document from file content
     *
     * @param file the input file
     * @return an XML document
     */
    public static Document parse(java.io.File file) {
        try {
            return docBuilder.parse(file);
        } catch (SAXException ex) {
            Messages.info(DocumentParser.class.getName() + ": " + ex);
        } catch (IOException ex) {
            Messages.info(DocumentParser.class.getName() + ": " + ex);
        }
        return null;
    }

    /**
     * Create XML document from InputStream content
     *
     * @param is the InputStream with XML content
     * @return an XML document
     */
    public static Document parse(java.io.InputStream is) {
        try {
            return docBuilder.parse(is);
        } catch (SAXException ex) {
            Messages.info(DocumentParser.class.getName() + ": " + ex);
        } catch (IOException ex) {
            Messages.info(DocumentParser.class.getName() + ": " + ex);
        }

        return null;
    }
}
