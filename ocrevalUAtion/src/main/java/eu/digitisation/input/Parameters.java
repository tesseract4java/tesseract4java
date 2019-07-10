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

import java.io.File;

/**
 * Stores all the input parameters used by the program
 *
 * @author R.C.C.
 */
public class Parameters {

    private static final long serialVersionUID = 1L;
    // Define program parameters: input files 
    public final Parameter<File> gtfile;
    public final Parameter<File> ocrfile;
    public final Parameter<File> eqfile;   // equivalences
    public final Parameter<File> swfile;   // stop words
    public final Parameter<File> lmfile;   // language model
    public final Parameter<File> outfile;
    // Define program parameters: boolean options 
    public final Parameter<Boolean> ignoreCase;
    public final Parameter<Boolean> ignoreDiacritics;
    public final Parameter<Boolean> ignorePunctuation;
    public final Parameter<Boolean> compatibility;
    //  Define program parameters: String options 
    public final Parameter<String> encoding;
    // Set verbosity during debugging (unused)
    public final Parameter<Boolean> verbose;
    
    public Parameters() {
        gtfile = new Parameter<File>("ground-truth file");
        ocrfile = new Parameter<File>("OCR file");
        eqfile = new Parameter<File>("Unicode equivalences file");
        swfile = new Parameter<File>("stop-words file");
        lmfile = new Parameter<File>("Language model file");
        outfile = new Parameter<File>("output file");
        ignoreCase = new Parameter<Boolean>("Ignore case", false, "");
        ignoreDiacritics = new Parameter<Boolean>("Ignore diacritics", false, "");
        ignorePunctuation = new Parameter<Boolean>("Ignore punctuation", false, "");
        compatibility = new Parameter<Boolean>("Unicode compatibility characters", false,
                "http://unicode.org/reports/tr15/#Canon_Compat_Equivalence");
        encoding = new Parameter<String>("Text file encoding");
        verbose = new Parameter<Boolean>("Verbose", false, "");
    }

    public void clear() {
        gtfile.setValue(null);
        ocrfile.setValue(null);
        eqfile.setValue(null);
        swfile.setValue(null);
        lmfile.setValue(null);
        outfile.setValue(null);
        ignoreCase.setValue(null);
        ignoreDiacritics.setValue(null);
        ignorePunctuation.setValue(null);
        compatibility.setValue(null);
        encoding.setValue(null);
    }
}
