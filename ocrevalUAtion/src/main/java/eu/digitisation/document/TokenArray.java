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
package eu.digitisation.document;

import eu.digitisation.distance.ArrayEditDistance;
import eu.digitisation.distance.EditDistanceType;
import eu.digitisation.math.MinimalPerfectHash;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A TokenArray is a tokenized string: every word is internally stored as an
 * integer. The mapping between words and integer codes is shared by all
 * TokenArrays created with the same MinimalPerfectHash.
 *
 * @version 2013.12.10
 */
public class TokenArray implements Iterable<Integer> {

    MinimalPerfectHash mph; // the creator mph
    Integer[] tokens;  // the content 

    /**
     * Default constructor
     *
     * @param interpretation the dictionary of codes
     * @param tokens the integer representation
     */
    TokenArray(MinimalPerfectHash mph, Integer[] tokens) {
        this.mph = mph;
        this.tokens = tokens;
    }

    /**
     * Default constructor
     *
     * @param interpretation the dictionary of codes
     * @param tokens the integer representation
     *
     */
    TokenArray(MinimalPerfectHash mph, List<Integer> tokens) {
        this.mph = mph;
        this.tokens = tokens.toArray(new Integer[tokens.size()]);

    }
    
    /**
     * 
     * @param mph the hashing scheme
     * @param s the input string to be tokenized
     */
    public TokenArray(MinimalPerfectHash mph, String s) {
        this(mph, mph.hashCodes(s));
    }

    /**
     * The length of the token array
     *
     * @return the length of the token array
     */
    public int length() {
        return tokens.length;
    }

    /**
     *
     * @return the internal representation as an array of integer codes
     */
    public Integer[] tokens() {
        return tokens;
    }

    /**
     * Value of token at a given position
     *
     * @param pos a position in the array
     * @return the value associated to this position
     */
    public int tokenAt(int pos) {
        return tokens[pos];
    }
    
    /**
     * 
     * @param pos a position in the array
     * @return the word or string at this position in the array
     */
    public String wordAt(int pos) {
        Integer token = tokens[pos];
        return mph.decode(token);
    }

    /**
     * Create a TokenArray with a range of another TokenArray
     * @param fromIndex low endpoint (inclusive) of the subArray
     * @param toIndex high endpoint (exclusive) of the subArray
     * @return 
     */
    public TokenArray subArray(int fromIndex, int toIndex) {
        List<Integer> sublist = Arrays.asList(tokens).subList(fromIndex, toIndex);
        return new TokenArray(mph, sublist);
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Integer token : tokens) {
            builder.append(mph.decode(token)).append(" ");
        }

        return builder.toString().trim();
    }

    /**
     * Distance between TokenArrays
     *
     * @param other another TokenArray
     * @param type the distance type
     * @return the distance between this and the other TokenArray
     */
    public int distance(TokenArray other, EditDistanceType type) {
        return ArrayEditDistance.distance(this.tokens, other.tokens, type);
    }

    /**
     * Return the TokenArray as array
     *
     * @return the array of tokens
     */
    public String array() {
        return java.util.Arrays.toString(tokens);
    }

    @Override
    public Iterator<Integer> iterator() {
        return Arrays.asList(tokens).iterator();
    }
}
