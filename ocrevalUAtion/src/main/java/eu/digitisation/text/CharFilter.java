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
package eu.digitisation.text;

import eu.digitisation.input.ExtensionFilter;
import eu.digitisation.log.Messages;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * Transform text according to a mapping between (source, target) Unicode
 * character sequences. This can be useful, for example, to replace Unicode
 * characters which are not supported by the browser or editor with printable
 * ones. It also performs canonicalization an returns the recommended normal
 * form (NFC = composed or NFKC if compatibility mode is selected).
 *
 * @version 2012.06.20
 */
public class CharFilter extends HashMap<String, String> {

    private static final long serialVersionUID = 1L;
    boolean compatibility; // Unicode compatibility mode

    /**
     * Default constructor
     */
    public CharFilter() {
        super();
        this.compatibility = false;
    }

    /**
     * Default constructor
     *
     * @param compatibility
     *            the Unicode compatibility mode (true means activated)
     * @param file
     *            a CSV file with one transformation per line, each line
     *            contains two Unicode hex sequences (and comments) separated
     *            with commas
     */
    public CharFilter(boolean compatibility, File file) {
        super();
        this.compatibility = compatibility;
        addFilter(file);
    }

    /**
     * Default constructor
     *
     * @param compatibility
     *            the Unicode compatibility mode (true means activated)
     */
    public CharFilter(boolean compatibility) {
        super();
        this.compatibility = compatibility;
    }

    /**
     * Constructor that inherits all entries of the given source map.
     * 
     * @param compatibility
     * @param source
     */
    public CharFilter(boolean compatibility, Map<String, String> source) {
        super(source.size());
        this.compatibility = compatibility;
        this.putAll(source);
    }

    /**
     * Load the transformation map from a CSV file: one transformation per line,
     * each line contains two Unicode hex sequences (and comments) separated
     * with commas
     *
     * @param file
     *            the CSV file (or directory with CSV files) with the equivalent
     *            sequences
     */
    public CharFilter(File file) {
        this.compatibility = false;
        addFilter(file);
    }

    /**
     * Add files to filter
     *
     * @param file
     *            the CSV file (or directory with CSV files) with the equivalent
     *            sequences
     */
    public final void addFilter(File file) {
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
     * @param file
     *            the CSV file
     */
    private void addCSV(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while (reader.ready()) {
                String line = reader.readLine();
                String[] tokens = line.split("([,;\t])");
                if (tokens.length > 1) { // allow comments in line
                    String key = UnicodeReader.codepointsToString(tokens[0]);
                    String value = UnicodeReader.codepointsToString(tokens[1]);
                    put(key, value);
                } else {
                    throw new IOException("Wrong line" + line
                            + " at file " + file);
                }
            }
            reader.close();
        } catch (IOException ex) {

        }
    }

    /**
     * Add the equivalences in CSV format
     *
     * @param reader
     *            a BufferedReader with CSV lines
     */
    public void addCSV(BufferedReader reader) {
        try {
            while (reader.ready()) {
                String line = reader.readLine();
                String[] tokens = line.split("([,;\t])");
                if (tokens.length > 1) { // allow comments in line
                    String key = UnicodeReader.codepointsToString(tokens[0]);
                    String value = UnicodeReader.codepointsToString(tokens[1]);
                    put(key, value);
                    System.out.println(key + ", " + value);
                } else {
                    throw new IOException("Wrong CSV line" + line);
                }
            }
            reader.close();
        } catch (IOException ex) {
            Messages.info(CharFilter.class.getName() + ": " + ex);
        }
    }

    /**
     * Set the compatibility mode
     *
     * @param compatibility
     *            the compatibility mode
     */
    public void setCompatibility(boolean compatibility) {
        this.compatibility = compatibility;
    }

    /**
     * Find all occurrences of characters in a sequence and substitute them with
     * the replacement specified by the transformation map. Remark: No
     * replacement priority is guaranteed in case of overlapping matches.
     *
     * @param s
     *            the string to be transformed
     * @return a new string with all the transformations performed
     */
    public String translate(String s) {
        String r = compatibility
                ? StringNormalizer.compatible(s)
                : StringNormalizer.composed(s);
        for (Map.Entry<String, String> entry : entrySet()) {
            r = r.replaceAll(entry.getKey(), entry.getValue());
        }
        return r;
    }

    /**
     * Converts the contents of a file into a CharSequence
     *
     * @param file
     *            the input file
     * @return the file content as a CharSequence
     */
    public CharSequence toCharSequence(File file) {
        try {
            FileInputStream input = new FileInputStream(file);
            FileChannel channel = input.getChannel();
            java.nio.ByteBuffer buffer = channel.map(
                    FileChannel.MapMode.READ_ONLY, 0, channel.size());
            return java.nio.charset.Charset.forName("utf-8").newDecoder()
                    .decode(buffer);
        } catch (IOException ex) {
            Messages.info(CharFilter.class.getName() + ": " + ex);
        }
        return null;
    }

    /**
     * Translate all characters according to the transformation map
     *
     * @param infile
     *            the input file
     * @param outfile
     *            the file where the output must be written
     */
    public void translate(File infile, File outfile) {
        try {
            FileWriter writer = new FileWriter(outfile);
            String input = toCharSequence(infile).toString();
            String output = translate(input);

            writer.write(output);
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            Messages.info(CharFilter.class.getName() + ": " + ex);
        }
    }

    /**
     * Translate (in place) all characters according to the transformation map
     *
     * @param file
     *            the input file
     *
     */
    public void translate(File file) {
        try {
            FileWriter writer = new FileWriter(file);
            String input = toCharSequence(file).toString();
            String output = translate(input);

            writer.write(output);
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            Messages.info(CharFilter.class.getName() + ": " + ex);
        }

    }
}
