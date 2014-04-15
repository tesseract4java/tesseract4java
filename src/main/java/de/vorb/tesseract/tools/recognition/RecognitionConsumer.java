package de.vorb.tesseract.tools.recognition;

/**
 * Consumes the OCR results of Tesseract.
 * 
 * @author Paul Vorbach
 */
public interface RecognitionConsumer {
  /**
   * @param state
   *          state of the recognition process
   */
  public void setState(RecognitionState state);

  /**
   * @return current state of the recognition process
   */
  public RecognitionState getState();

  /**
   * Beginning of a page.
   */
  public void pageBegin();

  /**
   * End of a page.
   */
  public void pageEnd();

  /**
   * Beginning of a block.
   */
  public void blockBegin();

  /**
   * End of a block.
   */
  public void blockEnd();

  /**
   * Beginning of a paragraph.
   */
  public void paragraphBegin();

  /**
   * End of a paragraph.
   */
  public void paragraphEnd();

  /**
   * Beginning of a text line.
   */
  public void lineBegin();

  /**
   * End of a text line.
   */
  public void lineEnd();

  /**
   * Beginning of a word.
   */
  public void wordBegin();

  /**
   * End of a word.
   */
  public void wordEnd();

  /**
   * Symbol within a word.
   */
  public void symbol();
}
