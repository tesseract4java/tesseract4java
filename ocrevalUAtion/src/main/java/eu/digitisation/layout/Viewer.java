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
package eu.digitisation.layout;

import eu.digitisation.image.Bimage;
import eu.digitisation.input.FileType;
import eu.digitisation.input.SchemaLocationException;
import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

/**
 * Shows text regions (as stored in PAGE XML) on image
 *
 * @author R.C.C
 */
public class Viewer {

    /**
     * Split a file name into path, base-name and extension
     *
     * @param filename
     * @return path (before last separator), base-name (before last dot) and
     * extension (after last dot)
     */
    private static String[] getFilenameTokens(String filename) {
        String[] tokens = new String[3];
        int first = filename.lastIndexOf(File.separator);
        int last = filename.lastIndexOf('.');
        tokens[0] = filename.substring(0, first);
        tokens[1] = filename.substring(first + 1, last);
        tokens[2] = filename.substring(last + 1);
        return tokens;
    }

    /**
     * Demo main
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, SchemaLocationException {
        if (args.length < 2) {
            System.err.println("Usage: Viewer image_file page_file [options]");
            System.exit(0);
        }

        File ifile = new File(args[0]);
        File xmlfile = new File(args[1]);
        String opts = (args.length > 2) ? args[2] : null;
        FileType ftype = FileType.valueOf(xmlfile);
        String[] tokens = getFilenameTokens(args[0]);
        String path = tokens[0];
        String id = tokens[1];
        String ext = tokens[2];
        File ofile = new File(path + File.separator + id + "_marked." + ext);

        Bimage page = null;
        Bimage scaled;
        float[] shortDash = {4f, 2f};
        float[] longDash = {8f, 4f};

        Page gt = null;

        if (ifile.exists()) {
            try {
                page = new Bimage(ifile).toRGB();
            } catch (NullPointerException ex) {
                throw new IOException("Unsupported format");
            }
        } else {
            throw new java.io.IOException(ifile.getCanonicalPath() + " not found");
        }
        if (xmlfile.exists()) {
            switch (ftype) {
                case PAGE: 
                    gt = new PAGEPage(xmlfile);
                    break;
                case HOCR:
                    gt = new HOCRPage(xmlfile);
                    break;
                case FR10:
                    gt = new FR10Page(xmlfile);
                    break;
                case ALTO:
                    gt = new ALTOPage(xmlfile);
                    break;
                default:
                    throw new java.lang.UnsupportedOperationException("Still not implemented");
            }
        } else {
            throw new java.io.IOException(xmlfile.getCanonicalPath() + " not found");
        }

        if (opts == null || opts.contains("b")) {
            page.add(gt.getFrontiers(ComponentType.BLOCK), Color.RED, 8f);
        }
        if (opts == null || opts.contains("l")) {
            page.add(gt.getFrontiers(ComponentType.LINE), Color.GREEN, 2f, longDash);
        }
        if (opts == null || opts.contains("w")) {
            page.add(gt.getFrontiers(ComponentType.WORD), Color.BLUE, 2f);
        }


        for (TextComponent component : gt.getComponents(ComponentType.WORD)) {
            System.out.println(component);
            // page.add(component.getFrontier(), Color.BLUE, 2f);
        }

        scaled = new Bimage(page, 1.0);
        scaled.write(ofile);
        System.out.println("output=" + ofile);

        if (opts != null && opts.contains("s")) {
            if (Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(ofile);
            }
        }

    }
}
