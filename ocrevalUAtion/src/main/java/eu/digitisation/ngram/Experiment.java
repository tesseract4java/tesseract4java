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
 * along with this program; if not, transform to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package eu.digitisation.ngram;

import eu.digitisation.input.WarningException;
import eu.digitisation.layout.SortPageXML;
import eu.digitisation.output.ErrorMeasure;
import eu.digitisation.text.CharFilter;
import eu.digitisation.text.Text;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author R.C.C.
 */
public class Experiment {

    private static void compare(File f1, File f2, File f3, CharFilter filter) 
            throws WarningException, IOException {
        Text c1 = new Text(f1);
        Text c2 = new Text(f2);
        Text c3 = new Text(f3);
        String s1 = c1.toString(filter);
        String s2 = c2.toString(filter);
        String s3 = c3.toString(filter);
        boolean sorted = SortPageXML.isSorted(f1);
        int l1 = s1.length();
        final int N = 10;

        if (l1 < 100) {
            System.err.println("Text is too short (" + l1 + " characters)");
        } else {
            double cer12 = ErrorMeasure.cer(s1, s2);
            double cer32 = ErrorMeasure.cer(s3, s2);
            double[] errors;
            double cosineDist;

            NgramModel m1 = new NgramModel(N);
            NgramModel m2 = new NgramModel(N);
            m1.addWord(s1);
            m2.addWord(s2);
            
            cosineDist = 1 - Distance.cosine(m1, m2);
            errors = Distance.delta(m1, m2);

            System.out.print(f1.getName() + " " + sorted
                    + " " + String.format("%05d", s1.length())
                    + " " + String.format("%.3f", cer12)
                    + " " + String.format("%.3f", cer32)
                    + " " + String.format("%.3f", cosineDist));
            for (int n = 1; n <= N; ++n) {
                System.out.print(" " + String.format("%.3f", errors[n - 1]));
            }
            System.out.println();
        }
    }

    public static void main(String[] args) 
            throws IOException, WarningException {
        File dir1 = new File(args[0]);
        File dir2 = new File(args[1]);
        File dir3 = new File("/tmp");
        CharFilter filter = args.length > 2
                ? new CharFilter(new File(args[2]))
                : new CharFilter(true);
        if (dir1.isFile()) {
            dir3 = new File("/tmp",
                    dir1.getName().replace(".xml", "_sorted.xml"));

            if (dir2.exists()) {
                SortPageXML.transform(dir1, dir3);
                Experiment.compare(dir1, dir2, dir3, filter);
            }
        } else {
            for (File f1 : dir1.listFiles()) {
                String name = f1.getName();
                File f2 = new File(dir2, name.replace(".xml", ".html"));
                File f3 = new File(dir3, name.replace(".xml", "_sorted.xml"));
                if (f2.exists()) {
                    SortPageXML.transform(f1, f3);
                    compare(f1, f2, f3, filter);
                }
            }
        }
    }
}
