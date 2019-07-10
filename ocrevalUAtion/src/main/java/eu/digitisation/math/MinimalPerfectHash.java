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
package eu.digitisation.math;

import eu.digitisation.log.Messages;
import eu.digitisation.text.WordScanner;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Mapping between strings and integers A MinimalPerfectHash guarantees
 * consistency between TokenArrays since the mapping between words and integer
 * codes is shared by all TokenArrays created by the same factory and this
 * allows for the comparison of TokenArrays.
 *
 * @version 2013.12.10
 */
public class MinimalPerfectHash {

    /**
     * The codes assigned to strings (tokens)
     */
    private final HashMap<String, Integer> codes;  // token->code mapping
    private final List<String> dictionary;         // code->token mapping
    boolean caseSensitive;   // Case sensitive encoding

    /**
     * Create a new MinimalPerfectHash
     *
     * @param caseSensitive true if the TokenArrays must be case sensitive
     */
    public MinimalPerfectHash(boolean caseSensitive) {
        codes = new HashMap<String, Integer>();
        dictionary = new ArrayList<String>();
        this.caseSensitive = caseSensitive;
    }

    /**
     * Default constructor (case sensitive factory)
     */
    public MinimalPerfectHash() {
        this(true);
    }

    /**
     *
     * @param word a word
     * @return the integer code assigned to this word
     */
    private Integer hashCode(String word) {
        Integer code;
        String key = caseSensitive ? word : word.toLowerCase();

        if (codes.containsKey(key)) {
            code = codes.get(key);
        } else {
            code = codes.size();
            codes.put(key, code);
            dictionary.add(key);
        }
        return code;
    }

    /**
     *
     * @param code an integer code
     * @return the string or word associated with this code
     */
    public String decode(int code) {
        return dictionary.get(code);
    }

    /**
     * Build an array of hash codes from the file content
     *
     * @param file the input file
     * @param encoding the text encoding.
     * @return the list of hash codes representing the file content
     */
    public List<Integer> hashCodes(File file, Charset encoding) 
            throws RuntimeException {
        ArrayList<Integer> list = new ArrayList<Integer>();

        try {
            WordScanner scanner = new WordScanner(file, encoding, null);
            String word;

            while ((word = scanner.nextWord()) != null) {
                list.add(hashCode(word));
            }
        } catch (IOException ex) {
            Messages.info(MinimalPerfectHash.class.getName() + ": " + ex);
        }
        return list;
    }

    /**
     * Build a TokenArray from a String
     *
     * @param s the input string
     * @return the list of hash codes representing the file content
     */
    public List<Integer> hashCodes(String s) {
        ArrayList<Integer> list = new ArrayList<Integer>();

        try {
            WordScanner scanner = new WordScanner(s, null);
            String word;

            while ((word = scanner.nextWord()) != null) {
                list.add(hashCode(word));
            }
        } catch (IOException ex) {
            Messages.info(MinimalPerfectHash.class.getName() + ": " + ex);
        }

        return list;
    }
    
    /**
     * 
     * @return the list of all strings with a hash code in this map
     */
    public List<String> keys() {
        return dictionary;
    }
}
