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

import eu.digitisation.input.FileType;
import java.util.EnumMap;

/**
 * Types of text components in a document
 *
 * @author R.C.C.
 */
public enum ComponentType {

    PAGE, BLOCK, LINE, WORD, OTHER;

    final static EnumMap<ComponentTag, ComponentType> types
            = new EnumMap<ComponentTag, ComponentType>(ComponentTag.class);

    static {
        types.put(ComponentTag.PAGE_Page, PAGE);
        types.put(ComponentTag.PAGE_TextRegion, BLOCK);
        types.put(ComponentTag.PAGE_TextLine, LINE);
        types.put(ComponentTag.PAGE_Word, WORD);
        types.put(ComponentTag.HOCR_ocr_page, PAGE);
        types.put(ComponentTag.HOCR_ocr_carea, OTHER);  // page content-area
        types.put(ComponentTag.HOCR_ocr_par, BLOCK);
        types.put(ComponentTag.HOCR_ocr_line, LINE);
        types.put(ComponentTag.HOCR_ocr_word, WORD);
        types.put(ComponentTag.HOCR_ocrx_word, WORD);
        types.put(ComponentTag.FR10_page, PAGE);
        types.put(ComponentTag.FR10_block, BLOCK);
        types.put(ComponentTag.FR10_text, OTHER);  // text in block
        types.put(ComponentTag.FR10_par, LINE);
        types.put(ComponentTag.FR10_line, LINE);
        types.put(ComponentTag.FR10_formatting, OTHER);  // text in line
        types.put(ComponentTag.FR10_word, WORD);
        types.put(ComponentTag.ALTO_Page, PAGE);
        types.put(ComponentTag.ALTO_PrintSpace, OTHER); // page main area
        types.put(ComponentTag.ALTO_ComposedBlock, OTHER);
        types.put(ComponentTag.ALTO_TextBlock, BLOCK);
        types.put(ComponentTag.ALTO_TextLine, LINE);
        types.put(ComponentTag.ALTO_String, WORD);
    }

    public static ComponentType valueOf(ComponentTag tag) {
        return types.get(tag);
    }

    /**
     *
     * @param ftype the type of files
     * @param tag the tag of the component
     * @return the component type for this tag and type of file
     */
    public static ComponentType valueOf(FileType ftype, String tag) {
        return types.get(ComponentTag.valueOf(ftype, tag));
    }
}
