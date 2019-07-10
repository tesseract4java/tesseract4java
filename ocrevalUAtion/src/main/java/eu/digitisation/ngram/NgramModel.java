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
package eu.digitisation.ngram;

import eu.digitisation.log.Messages;
import eu.digitisation.text.StringNormalizer;
import eu.digitisation.text.WordScanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * A n-gram model for strings. N is the maximal order of the model (context
 * length plus one).
 */
public class NgramModel implements Serializable {

    static final long serialVersionUID = 1L;
    static final char BOS = '\u0002';   // Begin of string text marker.
    static final char EOS = '\u0003';   // End of text marker.
    int order;                   // The size of the context plus one (n-gram).
    HashMap<String, Int> occur;  // Number of occurrences.
    double[] lambda;             // Backoff parameters

    /**
     * Class constructor.
     *
     * @param order the size of the context plus one.
     */
    public NgramModel(int order) {
        if (order > 0) {
            this.order = order;
        } else {
            throw new IllegalArgumentException("N-gram Order must be grater than 0");
        }
        occur = new HashMap<String, Int>();
        lambda = null;
    }

    /**
     * @return number of different n-grams stored
     */
    public int size() {
        return occur.keySet().size();
    }

    /**
     * Save n-gram model to GZIP file
     *
     * @param file the output file
     */
    public void save(File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            GZIPOutputStream gos = new GZIPOutputStream(fos);
            ObjectOutputStream out = new ObjectOutputStream(gos);

            out.writeObject(this);
            out.close();
        } catch (IOException ex) {
            Messages.severe(NgramModel.class.getName() + ": " + ex);
        }
    }

    /**
     * Build n-gram model from file
     *
     * @param file the GZIP input file
     */
    public NgramModel(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            GZIPInputStream gis = new GZIPInputStream(fis);
            ObjectInputStream in = new ObjectInputStream(gis);
            NgramModel ngram = (NgramModel) in.readObject();

            this.order = ngram.order;
            this.occur = ngram.occur;
            this.lambda = ngram.lambda;
            in.close();

            Messages.info("Read " + order + "-gram model");
        } catch (IOException ex) {
            Messages.severe(NgramModel.class.getName() + ": " + ex);
        } catch (ClassNotFoundException ex) {
            Messages.severe(NgramModel.class.getName() + ": " + ex);
        }
    }

    /**
     * @return Good-Turing back-off parameters.
     */
    public double[] getGoodTuringPars() {
        double[] pars = new double[order];
        int[] total = new int[order];
        int[] singles = new int[order];
        for (String word : occur.keySet()) {
            if (word.length() > 0) {
                int k = word.length() - 1;
                int times = occur.get(word).getValue();
                total[k] += times;
                if (times == 1) {
                    ++singles[k];
                }
            }
        }
        for (int k = 0; k < order; ++k) {
            pars[k] = singles[k] / (double) total[k];
        }
        return pars;
    }

    /**
     * @param n n-gram order.
     * @return Good-Turing back-off parameter.
     */
    private double lambda(int n) {
        if (lambda == null) {
            lambda = getGoodTuringPars();
        }
        return lambda[n];
    }

    /**
     * @param s a string
     * @return the substring obtained by removing its first character.
     */
    private String tail(String s) {
        return s.substring(1);
    }

    /**
     * @param s a string
     * @return the substring obtained by removing its last character.
     */
    private String head(String s) {
        return s.substring(0, s.length() - 1);
    }

    /**
     *
     * @param s a string
     * @return the last character in the string
     */
    private char lastChar(String s) {
        return s.charAt(s.length() - 1);
    }

    /**
     * @return the number of strings in the sample used to build the model.
     */
    private int sampleSize() {
        return occur.get(String.valueOf(EOS)).getValue();
    }

    public int occurrences(String key) {
        if (occur.containsKey(key)) {
            return occur.get(key).getValue();
        } else {
            return 0;
        }
    }

    /**
     * @param s a non-empty string
     * @return the conditional probability of the string relative to the
     * probability of its head.
     */
    protected double prob(String s) {
        double result;
        if (s.charAt(0) == BOS) {
            result = occurrences(String.valueOf(BOS) + lastChar(s))
                    / (double) sampleSize();
        } else if (!occur.containsKey(s)) {
            result = 0;
        } else {
            result = occurrences(s) / (double) occurrences(head(s));
        }
        return result;
    }

    /**
     * @param s a a non-empty string
     * @return the conditional probability of the string relative to its head,
     * and interpolated with lower-order models.
     */
    protected double smoothProb(String s) {
        double result;

        if (s.length() > 1) {
            if (s.charAt(0) == BOS && s.length() > 2) {
                result = smoothProb(s.substring(s.length() - 2, s.length()));
            } else {
                double lam = lambda(s.length() - 1);
                result = (1 - lam) * prob(s) + lam * smoothProb(tail(s));
            }
        } else {
            result = prob(s);
        }
        return result;
    }

    /**
     * Increments number of occurrences of the given string.
     *
     * @param s a string
     */
    protected void addEntry(String s) {
        if (occur.containsKey(s)) {
            occur.get(s).increment();
        } else {
            occur.put(s, new Int(1));
        }
    }

    /**
     * Increments number of occurrences of s.
     *
     * @param s a k-gram.
     * @param n number of occurrences
     */
    protected void addEntries(String s, int n) {
        if (occur.containsKey(s)) {
            occur.get(s).add(n);
        } else {
            occur.put(s, new Int(n));
        }
    }

    /**
     * Extracts all k-grams in a word or text upto the maximal order. For
     * instance, if word = "ma" and order = 3, then 0-grams are: "" (three empty
     * strings, used to normalize 1-grams); three uni-grams: "m, a, $" ($
     * represents end-of-string); three bi-grams: "#m, ma, a$" (# is used to
     * differentiate #m from 1-gram m); and two tri-grams: "#ma, ma$"
     *
     * @remark It does not add uni-grams "#" to the model since they can never
     * appear in the middle of a word. Normalization of bi-grams starting with #
     * will use n($) instead, since n(#)=n($)
     *
     * @param word the word or text (string of characters) to be added.
     */
    public void addWord(String word) {
        if (word.length() < 1) {
            throw new IllegalArgumentException("Cannot extract n-grams from empty word");
        } else {
            String input = BOS + word + EOS;
            for (int high = 2; high <= input.length(); ++high) {
                for (int low = Math.max(0, high - order); low < high; ++low) {
                    String s = input.substring(low, high);
                    addEntry(s);
                }
            }
            addEntries("", word.length() + 1);
        }
    }

    /**
     * Reads text file and adds the words in text to model.
     *
     * @param file a text file
     * @param encoding the text encoding
     * @param caseSensitive true if extracted n-grams are case sensitive
     */
    public void addWords(File file, Charset encoding, boolean caseSensitive) {
        try {
            WordScanner scanner = new WordScanner(file, encoding, "^\\p{Space}+");
            String word;
            while ((word = scanner.nextWord()) != null) {
                if (caseSensitive) {
                    addWord(word);
                } else {
                    addWord(word.toLowerCase());
                }
            }
        } catch (IOException ex) {
            Messages.info(NgramModel.class
                    .getName() + ": " + ex);
        }
    }

    /**
     * Compute probability of a word or text
     *
     * @param word a non-empty a sequence of characters
     * @return the log-probability (base e) of this string
     */
    public double logWordProb(String word) {
        double res = 0;

        if (word.length() < 1) {
            throw new IllegalArgumentException("Cannot compute probability of empty word");
        } else {
            String input = BOS + word + EOS;
            for (int high = 2; high <= input.length(); ++high) {
                int low = Math.max(0, high - order);
                String s = input.substring(low, high);
                double p = smoothProb(s);
                if (p == 0) {
                    Messages.warning(s + " has 0 probability");
                    return Double.NEGATIVE_INFINITY;
                } else {
                    res += Math.log(p);
                }
            }
        }
        return res;
    }

    /**
     * Reads input text from standard input and computes per-word cross entropy.
     *
     * @param caseSensitive true if the model is case sensitive
     * @return the log-likelihood of input text (per word).
     */
    public double logPerWordLikelihood(boolean caseSensitive) {
        try {
            Charset encoding = Charset.forName(System.getProperty("file.encoding"));
            WordScanner scanner = new WordScanner(System.in, encoding);

            String word;
            double result = 0;
            int numWords = 0;
            while ((word = scanner.nextWord()) != null) {
                ++numWords;
                if (caseSensitive) {
                    result -= logWordProb(word);
                } else {
                    result -= logWordProb(word.toLowerCase());
                }
            }

            return result / numWords / Math.log(2);

        } catch (IOException ex) {
            Messages.severe(NgramModel.class
                    .getName() + ": " + ex);
        }
        return Double.POSITIVE_INFINITY;
    }

    /**
     * Add all the content in a text file
     *
     * @param is the input stream with text content
     */
    public void addText(InputStream is) {
        try {
            BufferedReader reader
                    = new BufferedReader(new InputStreamReader(is));
            String context = String.valueOf(BOS);

            while (reader.ready()) {
                String line = StringNormalizer.reduceWS(reader.readLine());
                if (!line.isEmpty()) {
                    String input
                            = (context.charAt(0) == BOS)
                            ? line
                            : " " + line;
                    addSubstrings(context, input);
                    if (input.length() >= order) {
                        context = input.substring(input.length() - order + 1);
                    } else {
                        String s = context + input;
                        context = s.substring(Math.max(0, s.length() - order + 1));
                    }
                }
            }
            addSubstrings(context, String.valueOf(EOS));
        } catch (FileNotFoundException ex) {
            Messages.severe(NgramModel.class.getName() + ": " + ex);
        } catch (IOException ex) {
            Messages.severe(NgramModel.class.getName() + ": " + ex);
        }
    }

    /**
     * Add all substrings in this string to the NgramModel
     *
     * @param context the preceding context, possibly empty
     * @param text the non-empty input string
     */
    protected void addSubstrings(String context, String text) {
        if (text.length() < 1) {
            throw new IllegalArgumentException("Cannot extract n-grams from empty text");
        }
        String s = context + text;
        // extract all substrings
        for (int high = context.length() + 1; high <= s.length(); ++high) {
            for (int low = Math.max(0, high - order); low < high; ++low) {
                addEntry(s.substring(low, high));
            }
        }
        // the normalization of 1-grams
        addEntries("", text.length());
    }

    /**
     * Compute the log-likelihood (per character) of the text contained in a
     * file
     *
     * @param is the InputStream containing the text
     * @param contextLength the length of the context for the evaluation of the
     * character probability
     * @return
     */
    public double logLikelihood(InputStream is, int contextLength) {
        int nchar = 0;
        double loglike = 0;
        try {
            BufferedReader reader
                    = new BufferedReader(new InputStreamReader(is));
            String context = String.valueOf(BOS);

            while (reader.ready()) {
                String line = StringNormalizer.reduceWS(reader.readLine());
                if (!line.isEmpty()) {
                    String input
                            = (context.charAt(0) == BOS)
                            ? line
                            : " " + line;

                    nchar += input.length();
                    loglike += logLikelihood(context, input);
                    if (input.length() > contextLength) {
                        context = input.substring(input.length() - contextLength);
                    } else {
                        String s = context + input;
                        context = s.substring(Math.max(0, s.length() - contextLength));
                    }
                }
                loglike += logLikelihood(context, String.valueOf(EOS));
                ++nchar;
            }
        } catch (IOException ex) {
            Messages.warning(NgramModel.class.getName() + ": " + ex.getMessage());
        }

        return loglike / nchar;
    }

    /**
     *
     * @param context the preceding context
     * @param input a non-empty string
     * @return the log probability of the string after the given context
     */
    public double logLikelihood(String context, String input) {
        int contextLength = context.length();
        double loglike = 0;
        for (int pos = 0; pos < input.length(); ++pos) {
            String s;
            if (pos >= contextLength) {
                s = input.substring(pos - contextLength, pos + 1);
            } else {
                s = context.substring(pos)
                        + input.substring(0, pos + 1);
            }
            loglike += Math.log(smoothProb(s));
        }
        return loglike;
    }

    /**
     * Compute probability of a character after a given context. This
     * implementation only takes into account the preceding context
     *
     * @param context a sequence of characters
     * @param c a character
     * @return the log-probability (base e) that the character c follows the
     * given context
     */
    public double logProb(String context, char c) {
        double res = 0;
        int len = context.length() + 1; // the length of the context + character
        String s = (len > order)
                ? context.substring(len - order) + c
                : context + c;
        double p = smoothProb(s);

        return (p > 0)
                ? Math.log(p)
                : Double.NEGATIVE_INFINITY;
    }

    /**
     *
     * @return string representation of the NgramModel: keys and values
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (String key : occur.keySet()) {
            String s = key.replaceAll(String.valueOf(BOS), "<BoS>")
                    .replaceAll(String.valueOf(EOS), "<EoS>");
            builder.append("'").append(s).append("' ")
                    .append(occur.get(key)).append('\n');
        }
        return builder.toString();
    }

    /**
     * Show differences between two NgramModels (debug function)
     *
     * @param other another NgramModel (order must coincide)
     */
    public void showDiff(NgramModel other) {
        if (this.order != other.order) {
            throw new IllegalArgumentException("Illegal comparison "
                    + "of n-gram models with different n");
        }
        for (String s : this.occur.keySet()) {
            if (s.length() > 0) {
                int val1 = this.occur.get(s).getValue();
                int val2 = other.occur.containsKey(s)
                        ? other.occur.get(s).getValue() : 0;
                if (val1 != val2) {
                    System.out.println(s.replaceAll(String.valueOf(BOS), "<BoS>")
                            .replaceAll(String.valueOf(EOS), "<EoS>")
                            + " " + val1 + " " + val2);
                }
            }
        }
        for (String s : other.occur.keySet()) {
            if (s.length() > 0 && !this.occur.containsKey(s)) {
                int val2 = other.occur.get(s).getValue();
                System.out.println(s.replaceAll(String.valueOf(BOS), "<BoS>")
                        .replaceAll(String.valueOf(EOS), "<EoS>")
                        + " 0 " + val2);
            }
        }
    }

    /**
     * Compare two NgramModels
     *
     * @param other
     * @return true if they store the same content
     */
    public boolean equals(NgramModel other) {
        if (this.order != other.order) {
            return false;
        } else {
            for (String s : this.occur.keySet()) {
                int val1 = this.occur.get(s).getValue();
                int val2 = other.occur.containsKey(s)
                        ? other.occur.get(s).getValue() : 0;
                if (val1 != val2) {
                    return false;
                }

            }
            for (String s : other.occur.keySet()) {
                if (!this.occur.containsKey(s)) {
                    int val2 = other.occur.get(s).getValue();
                    if (val2 != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof NgramModel) {
            NgramModel other = (NgramModel) o;
            return equals(other);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return occur.hashCode();
    }

    /**
     * Main function.
     *
     * @param args
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        NgramModel ngram = null;
        File fout = null;
        int order = 0;

        if (args.length == 0) {
            System.err.println("Usage: Ngram [-n NgramModelOrder]"
                    + " [-i InputNgramModelFile | -o OutputNgramFile]"
                    + " file1 file2 ....");
        } else {
            for (int k = 0; k < args.length; ++k) {
                String arg = args[k];

                if (arg.equals("-n")) {
                    order = Integer.parseInt(args[++k]);
                    ngram = new NgramModel(order);
                } else if (arg.equals("-i")) {
                    File fin = new File(args[++k]);
                    ngram = new NgramModel(fin);
                } else if (arg.equals("-o")) {
                    fout = new File(args[++k]);
                } else if (ngram != null) {
                    File file = new File(arg);
                    InputStream is = new FileInputStream(file);
                    if (fout != null) {
                        ngram.addText(is);
                    } else if (order > 1) {
                        double res = ngram.logLikelihood(is, order - 1);
                        System.out.println(res);
                    }
                }
            }
            if (fout != null) {
                ngram.save(fout);
            }
        }
    }
}
