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
package eu.digitisation.input;

/**
 * A program parameter with a value, a name (a short description) and,
 * optionally, a help text or URL providing a longer description.
 *
 * @author R.C.C.
 * @param <Type> the type of parameter (Boolean, File)
 */
public class Parameter<Type> {

    String name;
    Type value;
    String help;  // text help or URL

    /**
     * Crete a Parameter with the given name (and null value)
     *
     * @param name the parameter's name
     */
    Parameter(String name) {
        this.name = name;
    }

    /**
     * Create Parameter with the given name and set this parameter's help text
     * and URL with additional help
     *
     * @param name the parameter's name
     * @param value this parameter's value
     * @param help help text or URL for this parameter
     */
    Parameter(String name, Type value, String help) {
        this.name = name;
        this.value = value;
        this.help = help;
    }

    /**
     * Set this parameter's value
     *
     * @param value the parameter's value
     */
    public void setValue(Type value) {
        this.value = value;
    }

    /**
     * Get this parameter's value
     *
     * @return the parameter's value
     */
    public Type getValue() {
        return value;
    }

    /**
     * Get the parameter value type (Boolean, File, Integer,...)
     *
     * @return
     */
    public Class<?> getType() {
        return value.getClass();
    }

     /**
     *
     * @returna short description of the parameter
     */
    public String getName() {
        return name;
    }
    
    /**
     *
     * @return the help text for this parameter
     */
    public String getHelp() {
        return help;
    }

    /**
     *
     * @return a string name:value
     */
    @Override
    public String toString() {
        return name + ":" + value;
    }
}
