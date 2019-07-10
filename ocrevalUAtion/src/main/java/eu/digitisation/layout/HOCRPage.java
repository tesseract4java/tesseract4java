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
import java.awt.Polygon;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author R.C.C.
 */
public class HOCRPage extends Page {

    /**
     * Basic constructor
     *
     * @param file
     */
    public HOCRPage(File file) {
        try {
            parse(file);
        } catch (IOException ex) {
            Messages.info(HOCRPage.class.getName() + ": " + ex);
        }
    }

    @Override
    public final void parse(File file) throws IOException {
        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(file, null);

        String id = "";
        ComponentType type = ComponentType.PAGE;
        org.jsoup.select.Elements pages = doc.body().select("*[class=ocr_page");

        if (pages.size() > 1) {
            throw new IOException("Multiple pages in document");
        } else {
            org.jsoup.nodes.Element page = pages.first();
            root = parse(page);
        }

    }

    /**
     * Parse an XML element and retrieve the associated text component
     *
     * @param element an HTML element
     * @return the text component associated with this element
     */
    TextComponent parse(org.jsoup.nodes.Element element) {
        String id = element.attr("id");
        String subtype = element.attr("class");
        ComponentType type = ComponentType.valueOf(FileType.HOCR, subtype);
        String content = element.text();
        Polygon frontier = null;
        List<TextComponent> array = new ArrayList<TextComponent>();

        // extract coordinates
        String title = element.attr("title").trim();
        if (title.contains("bbox")) {
            int pos = title.indexOf("bbox");
            String[] coords = title.substring(pos).split("\\p{Space}+");
            int x0 = Integer.parseInt(coords[1]);
            int y0 = Integer.parseInt(coords[2]);
            int x1 = Integer.parseInt(coords[3]);
            int y1 = Integer.parseInt(coords[4]);
            
            frontier = new BoundingBox(x0, y0, x1, y1).asPolygon();
        } else if (title.contains("poly")) {
            int pos = title.indexOf("poly");
            String[] coords = title.substring(pos).split("\\p{Space}+");
            int n = 1;
            frontier = new Polygon();
            while (n + 1 < coords.length && !coords[n].equals(";")) {
                int x = Integer.parseInt(coords[n]);
                int y = Integer.parseInt(coords[n + 1]);
                frontier.addPoint(x, y);
                n += 2;
            }
        }
        // get subcomponents

        org.jsoup.select.Elements children = element.children();

        for (org.jsoup.nodes.Element child : children) {
            if (child.hasAttr("class")) {
                String cat = child.attr("class");
                if (cat.equals("ocr_carea") 
                        || cat.equals("ocr_par")
                        || cat.equals("ocr_line")
                        || cat.equals("ocr_word")
                        || cat.equals("ocrx_word")) {
                    TextComponent subcomponent = parse(child);
                    array.add(subcomponent);
                }
            }
        }

        TextComponent component = new TextComponent(id, type, subtype, content, frontier);
        components.add(component);
        System.out.println(component);
        subcomponents.put(component, array);
        return component;
    }
}
