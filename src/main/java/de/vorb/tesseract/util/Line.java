package de.vorb.tesseract.util;

import java.util.Collections;
import java.util.List;

public class Line {
  private final Box bbox;
  private final List<Word> words;
  private final int baseline, xheight;

  public Line(Box bbox, List<Word> words, int baseline, int xheight) {
    this.bbox = bbox;
    this.words = words;
    this.baseline = baseline;
    this.xheight = xheight;
  }

  public List<Word> getWords() {
    return Collections.unmodifiableList(words);
  }

  public Box getBoundingBox() {
    return bbox;
  }

  public int getBaseline() {
    return baseline;
  }

  public int getXHeight() {
    return xheight;
  }

  @Override
  public String toString() {
    return "Line(bbox = " + bbox + ", words = [...], baseline = " + baseline
        + ", xheight = " + xheight + ")";
  }
}
