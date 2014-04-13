package de.vorb.tesseract.tools.recognition;

public interface RecognitionConsumer {
  public RecognitionState getState();

  public void pageBegin();

  public void pageEnd();

  public void blockBegin();

  public void blockEnd();

  public void paragraphBegin();

  public void paragraphEnd();

  public void lineBegin();

  public void lineEnd();

  public void wordBegin();

  public void wordEnd();

  public void symbolBegin();

  public void symbolEnd();
}
