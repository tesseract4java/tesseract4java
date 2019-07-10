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
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;

/**
 * Writes XML document to String or File
 *
 * @author R.C.C.
 */
public class DocumentWriter {

    static javax.xml.transform.Transformer transformer;

    javax.xml.transform.dom.DOMSource source;
    javax.xml.transform.stream.StreamResult result;

    static {
        try {
            transformer = javax.xml.transform.TransformerFactory
                    .newInstance().newTransformer();
        } catch (TransformerConfigurationException ex) {
            Messages.info(DocumentWriter.class.getName() + ": " + ex);
        }
    }

    /**
     * Create a DocumentWriter for a given document
     *
     * @param document the XML document
     */
    public DocumentWriter(Document document) {
        source = new javax.xml.transform.dom.DOMSource(document);
    }

    /**
     * Write XML to string
     *
     * @return string representation
     */
    @Override
    public String toString() {
        result = new javax.xml.transform.stream.StreamResult(new java.io.StringWriter());
        try {
            transformer.transform(source, result);
        } catch (TransformerException ex) {
            Messages.info(DocumentParser.class.getName() + ": " + ex);
        }
        return result.getWriter().toString();
    }

    /**
     * Dump content to file
     *
     * @param file the output file
     */
    public void write(java.io.File file) {
        result = new javax.xml.transform.stream.StreamResult(file);
        try {
            transformer.transform(source, result);
        } catch (TransformerException ex) {
            Messages.info(DocumentParser.class.getName() + ": " + ex);
        }

    }
}
