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

import eu.digitisation.log.Messages;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.parser.txt.CharsetDetector;

/**
 * Detect the encoding of a text file
 *
 * @author R.C.C.
 */
public class Encoding {

    /**
     *
     * @param file a text file
     * @return the encoding or Charset
     */
    public static Charset detect(File file) {
        try {
            InputStream is = TikaInputStream.get(new FileInputStream(file));
            CharsetDetector detector = new CharsetDetector();
            detector.setText(is);
            return Charset.forName(detector.detect().getName());
        } catch (IOException ex) {
            Messages.info(Encoding.class.getName() + ": " + ex);
        }
        return null;
    }
}
