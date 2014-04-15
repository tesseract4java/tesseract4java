package de.vorb.tesseract.util;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class Page {
  private final Path file;
  private final BufferedImage originalImg;
  private final BufferedImage thresholdedImg;
  private final List<Line> lines;

  private int lineIndex = -1;
  private int wordIndex = -1;

  public Page(Path file, BufferedImage originalScan,
      BufferedImage thresholdedImg, List<Line> lines) {
    this.file = file;
    this.originalImg = originalScan;
    this.thresholdedImg = thresholdedImg;
    this.lines = lines;
  }

  public Path getFile() {
    return file;
  }

  public BufferedImage getOriginalImage() {
    return originalImg;
  }

  public BufferedImage getThresholdedImage() {
    return thresholdedImg;
  }

  public List<Line> getLines() {
    return Collections.unmodifiableList(lines);
  }

  public void setSelectedLineIndex(int lineIndex) {
    this.lineIndex = lineIndex;
  }

  public void setSelectedWordIndex(int wordIndex) {
    this.wordIndex = wordIndex;
  }

  public Word getSelected() {
    if (lineIndex < 0 || wordIndex < 0)
      return null;

    return getLines().get(lineIndex).getWords().get(wordIndex);
  }

  public boolean hasSelected() {
    return lineIndex > 0 && wordIndex > 0;
  }

  public int getSelectedLineIndex() {
    return lineIndex;
  }

  public int getSelectedWordIndex() {
    return wordIndex;
  }

  public boolean isAscendersEnabled() {
    // only enabled for binary images
    return originalImg.getType() == BufferedImage.TYPE_BYTE_BINARY;
  }
}
