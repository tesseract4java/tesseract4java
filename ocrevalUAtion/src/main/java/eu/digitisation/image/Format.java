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
package eu.digitisation.image;

import java.io.IOException;
import java.util.Locale;

/**
 * Image input formats supported 
 *
 * @author R.C.C
 */
@SuppressWarnings("javadoc")
public enum Format {

    BMP, FlashPix, GIF, JPEG, PNG, PNM, TIFF, WBMP;

    /**
     *
     * @param file the container file
     * @return the Format associated with this file name extension
     * @throws IOException
     */
    public static Format valueOf(java.io.File file) throws IOException {
        String name = file.getName();
        String ext = name
                .substring(name.lastIndexOf('.') + 1)
                .toLowerCase(Locale.ENGLISH);
        if (ext.equals("bpm")) {
            return BMP;
        } else if (ext.equals("fpx")) {
            return FlashPix;
        } else if (ext.equals("gif")) {
            return GIF;
        } else if (ext.equals("jpg") || ext.equals("jpeg")) {
            return JPEG;
        } else if (ext.equals("png")) {
            return PNG;
        } else if (ext.equals("pnm")) {
            return PNM;
        } else if (ext.equals("tif") || ext.equals("tiff")) {
            return TIFF;
        } else if (ext.equals("wbmp")) {
            return WBMP;
        } else {
            throw new IOException("Unsupported image format " + ext);
        }
    }
}
