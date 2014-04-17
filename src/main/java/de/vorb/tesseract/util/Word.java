package de.vorb.tesseract.util;

import java.io.Serializable;
import java.util.List;

public class Word implements Serializable {
  private static final long serialVersionUID = 1L;

  private boolean isCorrect = true;
  private final List<Symbol> symbols;
  private final Box bbox;
  private final float conf;
  private final Baseline baseline;
  private final FontAttributes fontAttrs;

  private boolean selected = false;

  public Word(List<Symbol> symbols, Box bbox, float conf, Baseline baseline,
      FontAttributes fontAttrs) {
    this.symbols = symbols;
    this.bbox = bbox;
    this.conf = conf;
    this.baseline = baseline;
    this.fontAttrs = fontAttrs;
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

  public Baseline getBaseline() {
    return baseline;
  }

  public FontAttributes getFontAttributes() {
    return fontAttrs;
  }

  public String getText() {
    final StringBuilder text = new StringBuilder();

    for (final Symbol s : symbols) {
      text.append(s.getText());
    }

    return text.toString();
  }
}
