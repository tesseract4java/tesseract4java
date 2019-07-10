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
package eu.digitisation.ngram;

import eu.digitisation.log.Messages;
import static eu.digitisation.ngram.NgramModel.BOS;
import eu.digitisation.text.StringNormalizer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author R.C.C.
 */
public class TextPerplexity {

    StringBuilder text;
    List<Double> perplexities;

    public TextPerplexity(NgramModel ngram, InputStream is, int contextLength) {
        text = new StringBuilder();
        perplexities = new ArrayList<Double>();
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

                    for (int pos = 0; pos < input.length(); ++pos) {
                        String s;
                        if (pos >= contextLength) {
                            s = input.substring(pos - contextLength, pos + 1);
                        } else {
                            s = context.substring(pos)
                                    + input.substring(0, pos + 1);
                        }
                        text.append(input.charAt(pos));
                        perplexities.add(Math.log(ngram.smoothProb(s)));

                        if (input.length() > contextLength) {
                            context = input.substring(input.length() - contextLength);
                        } else {
                            s = context + input;
                            context = s.substring(Math.max(0, s.length() - contextLength));
                        }
                    }

                }
            }
        } catch (IOException ex) {
            Messages.warning(NgramModel.class.getName() + ": " + ex.getMessage());
        }

    }

    public String getText() {
        return text.toString();
    }

    public double[] getPerplexities() {
        double[] array = new double[perplexities.size()];
        for (int n = 0; n < array.length; ++n) {
            array[n] = perplexities.get(n);
        }
        return array;
    }
}
