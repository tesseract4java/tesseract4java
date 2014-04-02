package de.uniwue.ub.tesseract.util;

import java.io.Serializable;

public class Word implements Serializable {
  private static final long serialVersionUID = 1L;

  private boolean isCorrect = true;
  private final String word;
  private final int line;
  private final Box bbox;
  private final int conf;
  private String correction = null;

  private boolean selected = false;

  public Word(String word, int line, Box bbox, int conf) {
    this.word = word;
    this.line = line;
    this.bbox = bbox;
    this.conf = conf;
  }

  public Box getBoundingBox() {
    return bbox;
  }

  public int getConfidence() {
    return conf;
  }

  public String getCorrection() {
    if (correction == null)
      return word;
    else
      return correction;
  }

  public int getLine() {
    return line;
  }

  public String getWord() {
    return word;
  }

  public boolean isCorrect() {
    return isCorrect;
  }

  public void setCorrect(boolean correct) {
    this.isCorrect = correct;
  }

  public void setCorrection(String correction) {
    this.correction = correction;
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }
}
