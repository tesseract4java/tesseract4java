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
import eu.digitisation.input.SchemaLocationException;
import java.awt.Polygon;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Interface to process input files and extract content and geometry
 *
 * @author R.C.C.
 */
public abstract class Page {

    TextComponent root;                                    // the main component
    List<TextComponent> components;                        // all components
    Map<TextComponent, List<TextComponent>> subcomponents; // contained components

    /**
     * The basic constructor
     */
    Page() {
        components = new ArrayList<TextComponent>();
        subcomponents = new HashMap<TextComponent, List<TextComponent>>();
    }

    /**
     * Parse an input file
     *
     * @param file the input (XML or HTML) file
     */
    public abstract void parse(File file) throws IOException;

    /**
     * Get all the components in this document
     *
     * @return all the components in this document
     */
    public List<TextComponent> getComponents() {
        return components;
    }

    /**
     * Get all subcomponents of a given component
     *
     * @param component a component of the document being parsed
     * @return all subcomponents of this component
     */
    public List<TextComponent> getComponents(TextComponent component) {
        return subcomponents.get(component);
    }

    /**
     * List only components of a given type
     *
     * @param type a component type
     * @return the list of components with this type
     */
    public List<TextComponent> getComponents(ComponentType type) {
        List<TextComponent> list = new ArrayList<TextComponent>();
        for (TextComponent component : components) {
            if (component.getType() == type) {
                list.add(component);
            }
        }
        return list;
    }

    /**
     * List subcomponents of a given type
     *
     * @param type a component type
     * @return the list of components with this type
     */
    public List<TextComponent> getComponents(TextComponent component, ComponentType type) {
        List<TextComponent> list = new ArrayList<TextComponent>();
        for (TextComponent subcomponent : subcomponents.get(component)) {
            if (subcomponent.getType() == type) {
                list.add(subcomponent);
            }
        }
        return list;
    }

    /**
     * Get the textual content of the document
     *
     * @return the textual content of the document
     */
    public String getText() {
        return root.getContent();
    }

    /**
     * Get the text content of a given component
     *
     * @param component a component of the document being parsed
     * @return text under this component
     */
    public String getText(TextComponent component) {
        return component.getContent();
    }

    /**
     * Get the text content of all components of a given type
     *
     * @param type a component type
     * @return text content under components of this type
     */
    public String getText(ComponentType type) {
        StringBuilder builder = new StringBuilder();
        for (TextComponent component : components) {
            if (component.getType() == type) {
                if (builder.length() > 0) {
                    builder.append(' ');
                }
                builder.append(component.getContent());
            }
        }
        return builder.toString();
    }

    /**
     * Text content in subcomponents of a given type
     *
     * @param component a component
     * @param type a component type
     * @return the text content under subcomponents with this type
     */
    public String getText(TextComponent component, ComponentType type) {
        StringBuilder builder = new StringBuilder();
        for (TextComponent subcomponent : subcomponents.get(component)) {
            if (subcomponent.getType() == type) {
                if (builder.length() > 0) {
                    builder.append(' ');
                }
                builder.append(subcomponent.getContent());
            }
        }
        return builder.toString();
    }

    /**
     * Transform a list of components into a list of polygonal frontiers
     *
     * @param components
     * @return
     */
    private List<Polygon> frontiers(List<TextComponent> components) {
        List<Polygon> frontiers = new ArrayList<Polygon>(components.size());
        for (TextComponent component : components) {
            frontiers.add(component.getFrontier());
        }
        return frontiers;
    }

    /**
     * Get all the components in this document
     *
     * @return all the components in this document
     */
    public List<Polygon> getFrontiers() {
        return frontiers(components);
    }

    /**
     * Get the frontier a given component
     *
     * @param component a component of the document being parsed
     * @return all subcomponents of this component
     */
    public Polygon getFrontier(TextComponent component) {
        return component.getFrontier();
    }

    /**
     * List of (non-null) frontiers of components with a given type
     *
     * @param type a component type
     * @return the list of polygonal frontiers of components with this type
     */
    public List<Polygon> getFrontiers(ComponentType type) {
        List<Polygon> list = new ArrayList<Polygon>();
        for (TextComponent component : components) {
            if (component.getType() == type) {
                Polygon p = component.getFrontier();
                if (p != null) {
                    list.add(p);
                }
            }
        }
        return list;
    }

    /**
     * List (non-null) frontiers of subcomponents with a given type
     *
     * @param type a component type
     * @return the list of frontiers of subcomponents with this type
     */
    public List<Polygon> getFrontiers(TextComponent component, ComponentType type) {
        List<Polygon> list = new ArrayList<Polygon>();
        for (TextComponent subcomponent : subcomponents.get(component)) {
            if (subcomponent.getType() == type) {
                Polygon p = subcomponent.getFrontier();
                if (p != null) {
                    list.add(p);
                }
                list.add(subcomponent.getFrontier());
            }
        }
        return list;
    }

    public static void main(String[] args) 
            throws SchemaLocationException, IOException {
        File file = new File(args[0]);
        FileType ftype = FileType.valueOf(file);

        if (ftype == FileType.PAGE) {
            Page page = new PAGEPage(file);
            System.out.println("<page>");
            for (TextComponent component : page.getComponents()) {
                System.out.println(component);
            }
            System.out.println("</page>");
        }
    }
}
