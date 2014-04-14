package de.vorb.tesseract.util;

import java.io.Serializable;
import java.util.List;

public class Word implements Serializable {
  private static final long serialVersionUID = 1L;

  private boolean isCorrect = true;
  private final List<Symbol> symbols;
  private final Box bbox;
  private final float conf;

  private boolean selected = false;

  public Word(List<Symbol> symbols, Box bbox, float conf) {
    this.symbols = symbols;
    this.bbox = bbox;
    this.conf = conf;
  }

  public List<Symbol> getSymbols() {
    return symbols;
  }

  public Box getBoundingBox() {
    return bbox;
  }

  public float getConfidence() {
    return conf;
  }

  public boolean isCorrect() {
    return isCorrect;
  }

  public void setCorrect(boolean correct) {
    this.isCorrect = correct;
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }
}
