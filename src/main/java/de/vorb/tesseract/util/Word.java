package de.vorb.tesseract.util;

import java.io.Serializable;
import java.util.List;

public class Word implements Serializable {
  private static final long serialVersionUID = 1L;

  private boolean isCorrect = true;
  private final List<Symbol> symbols;
  private final int line;
  private final Box bbox;
  private final int conf;

  private boolean selected = false;

  public Word(List<Symbol> symbols, int line, Box bbox, int conf) {
    this.symbols = symbols;
    this.line = line;
    this.bbox = bbox;
    this.conf = conf;
  }

  public List<Symbol> getSymbols() {
    return symbols;
  }

  public Box getBoundingBox() {
    return bbox;
  }

  public int getConfidence() {
    return conf;
  }

  public int getLine() {
    return line;
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
