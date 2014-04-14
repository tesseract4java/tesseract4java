package de.vorb.tesseract.util;

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
    private boolean isBold;
    private boolean isItalic;
    private boolean isUnderlined;
    private boolean isMonospace;
    private boolean isSerif;
    private boolean isSmallcaps;
    private int size;
    private int fontID;

    public Builder() {
    }

    public Builder bold(boolean isBold) {
      this.isBold = isBold;
      return this;
    }

    public Builder italic(boolean isItalic) {
      this.isItalic = isItalic;
      return this;
    }

    public Builder underlined(boolean isUnderlined) {
      this.isUnderlined = isUnderlined;
      return this;
    }

    public Builder monospace(boolean isMonospace) {
      this.isMonospace = isMonospace;
      return this;
    }

    public Builder serif(boolean isSerif) {
      this.isSerif = isSerif;
      return this;
    }

    public Builder smallcaps(boolean isSmallcaps) {
      this.isSmallcaps = isSmallcaps;
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
      return new FontAttributes(isBold, isItalic, isUnderlined, isMonospace,
          isSerif, isSmallcaps, size, fontID);
    }
  }

  private final boolean isBold;
  private final boolean isItalic;
  private final boolean isUnderlined;
  private final boolean isMonospace;
  private final boolean isSerif;
  private final boolean isSmallcaps;
  private final int size;
  private final int fontID;

  /**
   * Create a FontAttributes object.
   * 
   * @param isBold
   * @param isItalic
   * @param isUnderlined
   * @param isMonospace
   * @param isSerif
   * @param isSmallcaps
   * @param size
   * @param fontID
   */
  protected FontAttributes(boolean isBold, boolean isItalic,
      boolean isUnderlined, boolean isMonospace, boolean isSerif,
      boolean isSmallcaps, int size,
      int fontID) {
    this.isBold = isBold;
    this.isItalic = isItalic;
    this.isUnderlined = isUnderlined;
    this.isMonospace = isMonospace;
    this.isSerif = isSerif;
    this.isSmallcaps = isSmallcaps;
    this.size = size;
    this.fontID = fontID;
  }

  /**
   * @return true if the word is bold.
   */
  public boolean isBold() {
    return isBold;
  }

  /**
   * @return true if the word is italic.
   */
  public boolean isItalic() {
    return isItalic;
  }

  /**
   * @return true if the word is underlined.
   */
  public boolean isUnderlined() {
    return isUnderlined;
  }

  /**
   * @return true if the word is set in a monospace font.
   */
  public boolean isMonospace() {
    return isMonospace;
  }

  /**
   * @return true if the word is set in a font with serifs.
   */
  public boolean isSerif() {
    return isSerif;
  }

  /**
   * @return true if the word is set in small-caps
   */
  public boolean isSmallcaps() {
    return isSmallcaps;
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
    return "FontAttributes(" + size + "pt, " + opt(isBold, "bold, ")
        + opt(isItalic, "italic, ") + opt(isUnderlined, "underlined, ")
        + opt(isMonospace, "monospace, ") + opt(isSerif, "serif, ")
        + opt(isSmallcaps, "smallcaps, ") + "ID = " + fontID + ")";
  }

  /**
   * Optionally return given String.
   * 
   * @param cond
   *          condition
   * @param str
   *          String
   * @return str if condition holds, empty String otherwise.
   */
  private static String opt(boolean cond, String str) {
    return cond ? str : "";
  }
}
