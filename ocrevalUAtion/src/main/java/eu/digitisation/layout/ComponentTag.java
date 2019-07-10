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
package eu.digitisation.layout;

import eu.digitisation.input.FileType;

/**
 * The tag of a text component in a document
 *
 * @author R.C.C. Info about tags FR10
 * http://www.abbyy.com/FineReader_xml/FineReader10-schema-v1.xml ALTO
 * http://www.loc.gov/standards/alto/techcenter/layout.php hOCR
 * http://docs.google.com/View?docid=dfxcv4vc_67g844kf
 */
public enum ComponentTag {

    PAGE_Page, PAGE_TextRegion, PAGE_TextLine, PAGE_Word,
    HOCR_ocr_page, HOCR_ocr_carea, HOCR_ocr_par,
    HOCR_ocr_line, HOCR_ocr_word, HOCR_ocrx_word,
    FR10_page, FR10_block, FR10_text, FR10_par, FR10_line,
    FR10_formatting, FR10_word,
    ALTO_Page, ALTO_PrintSpace, ALTO_ComposedBlock, ALTO_TextBlock,
    ALTO_TextLine, ALTO_String;

    /**
     * The type for a given tag and type of file
     *
     * @param ftype the type of file (PAGE, hOCR, ALTO, etc)
     * @param tag the component tag (TextLine, ocr_par, etc)
     * @return the component type associated to this tag and type of file
     */
    public static ComponentTag valueOf(FileType ftype, String tag) {
        return valueOf(ftype.toString() + "_" + tag);
    }

    /**
     * Return the component tag without the file-type prefix
     *
     * @param tag a ComponentTag
     * @return the tag for this component without the file-type prefix
     */
    public static String shortTag(ComponentTag tag) {
        return tag.toString().replaceFirst("[^_]+_", "");
    }
}
