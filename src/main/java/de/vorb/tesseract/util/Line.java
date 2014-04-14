package de.vorb.tesseract.util;

import java.util.Collections;
import java.util.List;

public class Line {
  private final Box bbox;
  private final List<Word> words;
  private final Baseline baseline;

  public Line(Box bbox, List<Word> words, Baseline baseline) {
    this.bbox = bbox;
    this.words = words;
    this.baseline = baseline;
  }

  public List<Word> getWords() {
    return Collections.unmodifiableList(words);
  }

  public Box getBoundingBox() {
    return bbox;
  }

  public Baseline getBaseline() {
    return baseline;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Line(bbox = " + bbox + ", words = [...], baseline = " + baseline
        + ")";
  }
}
