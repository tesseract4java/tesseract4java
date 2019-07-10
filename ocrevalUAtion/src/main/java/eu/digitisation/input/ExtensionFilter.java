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
package eu.digitisation.input;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Locale;

/**
 *
 * @author R.C.C.
 */
public class ExtensionFilter implements FilenameFilter {

    private final String ext;

    /**
     * Create filter for files with the given extension
     * @param ext the extension required
     */
    public ExtensionFilter(String ext) {
        this.ext = ext.toLowerCase(Locale.ENGLISH);
    }

    @Override
    public boolean accept(File dir, String name) {
        return name.toLowerCase(Locale.ENGLISH).endsWith(ext);
    }

}
