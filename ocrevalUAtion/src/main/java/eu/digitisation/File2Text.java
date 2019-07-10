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
package eu.digitisation;

import eu.digitisation.input.SchemaLocationException;
import eu.digitisation.input.WarningException;
import eu.digitisation.text.CharFilter;
import eu.digitisation.text.Text;
import java.io.File;

/**
 *
 * @author R.C.C
 */
public class File2Text {

    public static void main(String[] args) throws WarningException,
            SchemaLocationException {
        if (args.length > 0) {
            File file = new File(args[0]);
            CharFilter filter = null;
            if (args.length > 1) {
                filter = new CharFilter(new File(args[1]));
            }
            Text content = new Text(file);
            System.out.println(content.toString(filter));
        }
    }
}
