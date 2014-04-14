package de.vorb.tesseract.util;

/**
 * Recognized Symbol. Can either be a single character or a ligature or
 * otherwise combined glyph.
 * 
 * @author Paul Vorbach
 */
public class Symbol {
  private final String text;
  private final Box boundingBox;
  private final float confidence;

  /**
   * Creates a new Symbol.
   * 
   * @param text
   *          recognized text
   * @param boundingBox
   *          bounding box
   * @param confidence
   *          recognition confidence
   */
  public Symbol(String text, Box boundingBox, float confidence) {
    this.text = text;
    this.boundingBox = boundingBox;
    this.confidence = confidence;
  }

  /**
   * @return recognized text
   */
  public String getText() {
    return text;
  }

  /**
   * @return recognition confidence
   */
  public float getConfidence() {
    return confidence;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Symbol(" + text + ", bounds = " + boundingBox + ", conf = "
        + confidence + ")";
  }
}
