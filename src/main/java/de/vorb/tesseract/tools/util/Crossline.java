package de.vorb.tesseract.tools.util;

public abstract class Crossline {
  private final int yOffset;
  private final float slope;

  public Crossline(int yOffset, float slope) {
    this.yOffset = yOffset;
    this.slope = slope;
  }

  public int getYOffset() {
    return yOffset;
  }

  public float getSlope() {
    return slope;
  }
}
