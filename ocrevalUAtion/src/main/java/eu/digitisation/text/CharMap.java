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
package eu.digitisation.text;

import eu.digitisation.input.ExtensionFilter;
import eu.digitisation.log.Messages;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * Compares characters and strings according to a mapping between equivalent
 * characters
 *
 * @author R.C.C.
 */
public class CharMap {

    /**
     * Typical Options for character comparison
     */
    public enum Option {

        /**
         * True if case (uppercase or lower) matters
         */
        CASE_AWARE,
        /**
         * True if punctuation marks, currency symbols and all other
         * non-letter/non-number symbols matter
         */
        PUNCTUATION_AWARE,
        /**
         * True if diacritics matter
         */
        DIACRITICS_AWARE,
        /**
         * True if Unicode compatibility is active (e.g., between ligatures and
         * non-ligatures)
         */
        UNICODE_COMPATIBILITY
    };

    EnumMap<Option, Boolean> options;    // equivalence options
    HashMap<Character, String> equivalences; // specific equivalences for Unicode characters

    /**
     * Default constructor: all options true by default
     */
    public CharMap() {
        options = new EnumMap<Option, Boolean>(Option.class);
        for (Option option : Option.values()) {
            options.put(option, Boolean.TRUE);
        }
        equivalences = new HashMap<Character, String>();
    }

    /**
     * Constructor with selection of options
     *
     * @param ops the options whose value must be set to to true (all the other
     * being false)
     */
    public CharMap(Option[] ops) {
        options = new EnumMap<Option, Boolean>(Option.class);
        for (Option option : Option.values()) {
            options.put(option, Boolean.FALSE);
        }
        for (Option op : ops) {
            options.put(op, Boolean.TRUE);
        }
        equivalences = new HashMap<Character, String>();
    }

    /**
     * Set a value for an option
     *
     * @param option the option to be set
     * @param value its value (true or false)
     */
    public void setOption(Option option, boolean value) {
        options.put(option, value);
    }

    /**
     * Read files containing equivalences between characters and sequences
     *
     * @param file the CSV file (or directory with CSV files) with the
     * equivalences between chars and  sequences
     */
    public void addFilter(File file) {
        if (file.isDirectory()) {
            String[] filenames = file.list(new ExtensionFilter(".csv"));
            for (String filename : filenames) {
                addCSV(new File(filename));
            }
        } else if (file.isFile()) {
            addCSV(file);
        }
    }

    /**
     * Add the equivalences contained in a CSV file
     *
     * @param file the CSV file
     */
    private void addCSV(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while (reader.ready()) {
                String line = reader.readLine();
                String[] tokens = line.split("([,;\t])");
                if (tokens.length > 1) {  // allow comments in line
                    Character key = (char) Integer.parseInt(tokens[0].trim(), 16);
                    String value = UnicodeReader.codepointsToString(tokens[1]);
                    equivalences.put(key, value);
                } else {
                    throw new IOException("Wrong line" + line
                            + " at file " + file);
                }
            }
            reader.close();
        } catch (IOException ex) {
            Messages.info(CharFilter.class.getName() + ": " + ex);
        }
    }

    /**
     * Normalize characters in a string
     *
     * @param s a string of characters
     * @return the normal form of s for string comparison
     */
    public String normalForm(String s) {
        String result = s;
        System.out.println("S=" + s);
        if (!options.get(Option.CASE_AWARE)) {
            result = result.toLowerCase(Locale.getDefault());
        }
        if (!options.get(Option.DIACRITICS_AWARE)) {
            result = StringNormalizer.removeDiacritics(result);
        }
        if (!options.get(Option.PUNCTUATION_AWARE)) {
            // keep only letters, diacritic marks an numbers
            result = result.replaceAll("[^\\p{L}\\p{M}\\p{N}]", " ");
        }
        if (!options.get(Option.UNICODE_COMPATIBILITY)) {
            result = StringNormalizer.compatible(result);
        }

        for (Map.Entry<Character, String> entry : equivalences.entrySet()) {
            result = result.replaceAll(String.valueOf(entry.getKey()), entry.getValue());
        }

        return StringNormalizer.reduceWS(result);
    }

    /**
     * Normalize a character
     *
     * @param c a character
     * @return the normal form of c for character comparison
     */
    public String normalForm(char c) {
        return normalForm(String.valueOf(c));
    }

    /**
     * Check if two characters are equivalent
     *
     * @param c1 the first character
     * @param c2 the second character
     * @return True if c1 is equivalent to c2
     */
    public boolean equiv(char c1, char c2) {
        return normalForm(c1).equals(normalForm(c2));
    }
}
