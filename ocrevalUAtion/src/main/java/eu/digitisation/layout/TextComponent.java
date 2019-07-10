package eu.digitisation.layout;

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
import java.awt.Polygon;

/**
 * A region in a document (a page, a block, line, or word)
 *
 * @author R.C.C.
 */
public class TextComponent {

    private static final long serialVersionUID = 1L;

    String id;          // identifier
    ComponentType type; // the type of component (page, block, line, word)
    String subtype;     // the type of block (paragraph, header, TOC).
    String content;     // text content
    Polygon frontier;   // the component frontier

    /**
     * Basic constructor
     *
     * @param id identifier
     * @param type the type of component (page, block, line, word)
     * @param subtype the type of block (paragraph, header, TOC).
     * @param content text content
     * @param frontier the component frontier
     */
    public TextComponent(String id, ComponentType type, String subtype,
            String content, Polygon frontier) {
        this.id = id;
        this.type = type;
        this.subtype = subtype;
        this.content = content;
        this.frontier = frontier;
    }

    /**
     *
     * @return the component identifier
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @return the type of this component
     */
    public ComponentType getType() {
        return type;
    }

    /**
     *
     * @return the subtype of this component
     */
    public String getSubtype() {
        return subtype;
    }

    /**
     * Get the text content
     *
     * @return the textual contend of this component
     */
    public String getContent() {
        return content;
    }

    /**
     * Get the frontier
     *
     * @return the polygonal frontier of this component
     */
    public Polygon getFrontier() {
        return frontier;
    }

    /**
     *
     * @return a string representation of this TextComponent
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("<TextComponent>\n")
                .append("\t<id>").append(id).append("</id>\n")
                .append("\t<type>").append(type).append("</type>\n")
                .append("\t<subtype>").append(subtype).append("</subtype>\n")
                .append("\t<content>").append(content).append("</content>\n")
                .append("\t<frontier>\n");
        if (frontier != null) {
            for (int n = 0; n < frontier.npoints; ++n) {
                builder.append("\t\t<x>").append(frontier.xpoints[n]).append("</x>")
                        .append("<y>").append(frontier.ypoints[n]).append("</y>\n");
            }
        }
        builder.append("\t</frontier>\n");
        builder.append("</TextComponent>\n");
        return builder.toString();
    }
   }
