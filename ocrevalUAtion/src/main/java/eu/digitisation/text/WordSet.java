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

import eu.digitisation.input.WarningException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

/**
 *
 * @author R.C.C.
 */
public class WordSet extends HashSet<String> {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor
     *
     * @param file the file containing the list of stop-words (separated by
     * blanks or newlines)
     * @throws IOException
     */
    public WordSet(File file) throws WarningException {
        try {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while (reader.ready()) {
            String line = reader.readLine().trim();
            for (String word : line.split("\\p{Space}+")) {
                if (word.length() > 0) {
                    add(word);
                }
            }
        }
        } catch (IOException ex) {
            throw new WarningException("File " + file 
                    + " is not a valid stop word file");
        }
    }
}
