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
package eu.digitisation.input;

import eu.digitisation.log.Messages;
import eu.digitisation.text.StringNormalizer;
import eu.digitisation.xml.DocumentParser;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Supported input file types
 *
 * @author R.C.C.
 */
@SuppressWarnings("javadoc")
public enum FileType {

    TEXT, PAGE, FR10, HOCR, ALTO, UNKNOWN;
    String tag;
    String schemaLocation;  // schema URL

    static {
        reload();
    }

    public static void reload() {
        Properties props = Settings.properties();

        TEXT.tag = null;  // no tag for this type 
        TEXT.schemaLocation = null; // no schema associated to this type

        PAGE.tag = "PcGts";
        PAGE.schemaLocation = getSchemaLocation(props, "PAGE");

        FR10.tag = "document";
        FR10.schemaLocation = getSchemaLocation(props, "FR10");

        ALTO.tag = "alto";
        ALTO.schemaLocation = getSchemaLocation(props, "ALTO");

        HOCR.tag = "html";
        HOCR.schemaLocation = null;  // no schema for this type 
    }

    /**
     * Load the schemaLocation from properties
     *
     * @param props properties
     * @param suffix the schemaLocation suffix (e.g., ALTO, FR10)
     * @return the property value
     */
    public static String getSchemaLocation(Properties props, String suffix) {
        String location = props.getProperty("schemaLocation." + suffix);
        return (location == null) ? " " : StringNormalizer.reduceWS(location);
    }

    /**
     *
     * @param locations1 string of URL schema locations separated by spaces
     * @param locations2 string of URL schema locations separated by spaces
     * @return True if at least one URL is in both locations
     */
    private static boolean sameLocation(String locations1, String locations2) {
        String[] urls = locations2.split("\\p{Space}+");
        
        for (String url : urls) {
            if (locations1.contains(url)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param file a file
     * @return the FileType of file
     */
    public static FileType valueOf(File file) throws SchemaLocationException {
        String name = file.getName().toLowerCase(Locale.ENGLISH);

        if (name.endsWith(".txt")) {
            return TEXT;
        } else if (name.endsWith(".xml")) {
            Document doc = DocumentParser.parse(file);
            Element root = doc.getDocumentElement();
            String doctype = root.getTagName();
            String location;

            if (root.hasAttribute("xsi:schemaLocation")) {
                location = StringNormalizer
                        .reduceWS(root.getAttribute("xsi:schemaLocation"));
            } else if (root.hasAttribute("xsi:noNamespaceSchemaLocation")) {
                location = StringNormalizer
                        .reduceWS(root.getAttribute("xsi:noNamespaceSchemaLocation"));
            } else {
                location = null;
            }

            if (doctype.equals(PAGE.tag)) {
                if (sameLocation(location, PAGE.schemaLocation)) {
                    return PAGE;
                } else if (!location.isEmpty()) {
                    throw new SchemaLocationException(PAGE, location);
                }
            } else if (doctype.equals(FR10.tag)) {
                if (sameLocation(location, FR10.schemaLocation)) {
                    return FR10;
                } else if (!location.isEmpty()) {
                    throw new SchemaLocationException(FR10, location);
                }
            } else if (doctype.equals(ALTO.tag)) {
                Messages.info(ALTO.schemaLocation);
                if (sameLocation(location, ALTO.schemaLocation)) {
                    return ALTO;
                } else if (!location.isEmpty()) {
                    throw new SchemaLocationException(ALTO, location);
                }
            }
        } else if (name.endsWith(".html")) {
            try {
                org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(file, null);
                if (!doc.head().select("meta[name=ocr-system").isEmpty()) {
                    return HOCR;
                }
            } catch (IOException ex) {
                Messages.info(FileType.class
                        .getName() + ": " + ex);
            }
        }
        return UNKNOWN;
    }

    public static void main(String[] args) {
        for (String arg : args) {
            try {
                File file = new File(arg);
                System.out.println(FileType.valueOf(file));
            } catch (SchemaLocationException ex) {
                System.out.println(ex);
            }
        }
    }
}
