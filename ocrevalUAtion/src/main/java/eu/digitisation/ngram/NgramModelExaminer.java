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

import java.io.File;
import java.io.FileNotFoundException;

/**
 *
 * @author R.C.C.
 */
public class NgramModelExaminer {
  public static void main(String[] args) throws FileNotFoundException {
        NgramModel model = new NgramModel(new File(args[0]));
       

        for (int n = 1; n < args.length; ++n) {
            String key = args[n];
            String head = key.substring(0, key.length() - 1);
            int times  = model.occurrences(key);
            int total = model.occurrences(head);
            System.out.println(times + " / " + total);
        }
    }
}
