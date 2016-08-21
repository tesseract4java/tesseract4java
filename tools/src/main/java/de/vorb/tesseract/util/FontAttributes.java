package de.vorb.tesseract.util;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Font attributes of a recognized word.
 *
 * @author Paul Vorbach
 */

/**
 * @author Paul Vorbach
 *
 */
public class FontAttributes {

    /**
     * FontAttributes builder.
     *
     * @author Paul Vorbach
     */
    public static class Builder {
        private boolean bold;
        private boolean italic;
        private boolean underlined;
        private boolean monospace;
        private boolean serif;
        private boolean smallCaps;
        private int size;
        private int fontID;

        public Builder() {
        }

        public Builder bold(boolean bold) {
            this.bold = bold;
            return this;
        }

        public Builder italic(boolean italic) {
            this.italic = italic;
            return this;
        }

        public Builder underlined(boolean underlined) {
            this.underlined = underlined;
            return this;
        }

        public Builder monospace(boolean monospace) {
            this.monospace = monospace;
            return this;
        }

        public Builder serif(boolean serif) {
            this.serif = serif;
            return this;
        }

        public Builder smallCaps(boolean smallCaps) {
            this.smallCaps = smallCaps;
            return this;
        }

        public Builder size(int size) {
            this.size = size;
            return this;
        }

        public Builder fontID(int fontID) {
            this.fontID = fontID;
            return this;
        }

        /**
         * Finalize the FontAttributes object.
         *
         * @return FontAttributes object
         */
        public FontAttributes build() {
            return new FontAttributes(bold, italic, underlined, monospace,
                    serif, smallCaps, size, fontID);
        }
    }

    @XmlAttribute
    private final boolean bold;

    @XmlAttribute
    private final boolean italic;

    @XmlAttribute
    private final boolean underlined;

    @XmlAttribute
    private final boolean monospace;

    @XmlAttribute
    private final boolean serif;

    @XmlAttribute
    private final boolean smallCaps;

    @XmlAttribute
    private final int size;

    @XmlAttribute
    private final int fontID;

    /**
     * Create a FontAttributes object.
     *
     * @param bold
     * @param italic
     * @param underlined
     * @param monospace
     * @param serif
     * @param smallCaps
     * @param size
     * @param fontID
     */
    protected FontAttributes(boolean bold, boolean italic, boolean underlined,
            boolean monospace, boolean serif, boolean smallCaps, int size,
            int fontID) {
        this.bold = bold;
        this.italic = italic;
        this.underlined = underlined;
        this.monospace = monospace;
        this.serif = serif;
        this.smallCaps = smallCaps;
        this.size = size;
        this.fontID = fontID;
    }

    /**
     * @return true if the word is bold.
     */
    public boolean isBold() {
        return bold;
    }

    /**
     * @return true if the word is italic.
     */
    public boolean isItalic() {
        return italic;
    }

    /**
     * @return true if the word is underlined.
     */
    public boolean isUnderlined() {
        return underlined;
    }

    /**
     * @return true if the word is set in a monospace font.
     */
    public boolean isMonospace() {
        return monospace;
    }

    /**
     * @return true if the word is set in a font with serifs.
     */
    public boolean isSerif() {
        return serif;
    }

    /**
     * @return true if the word is set in small-caps
     */
    public boolean isSmallCaps() {
        return smallCaps;
    }

    /**
     * @return size of the font in pt.
     */
    public int getSize() {
        return size;
    }

    /**
     * @return ID of the font as defined in the *.traineddata file.
     */
    public int getFontID() {
        return fontID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format(
                "size: %dpx, bold: %b, italic: %b, underlined: %b, monospace: %b, serif: %b, small caps: %b, font ID:"
                        + " %d",
                size, bold, italic, underlined, monospace, serif, smallCaps,
                fontID);
    }

    /**
     * Optionally return given String.
     *
     * @param cond
     *            condition
     * @param str
     *            string
     * @return str if condition holds, empty String otherwise.
     */
    private static String opt(boolean cond, String str) {
        return cond ? str : "";
    }
}
