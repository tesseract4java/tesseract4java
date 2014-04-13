package de.vorb.tesseract.tools.util;

public abstract class Straight {
  private final float m;
  private final int c;

  public Straight(int yOffset, float slope) {
    this.c = yOffset;
    this.m = slope;
  }

  public int getYOffset() {
    return c;
  }

  public float getSlope() {
    return m;
  }

  @Override
  public String toString() {
    return "f(x) = " + m + " * x + " + c;
  }
}
