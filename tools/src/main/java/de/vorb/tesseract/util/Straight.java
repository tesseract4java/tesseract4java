package de.vorb.tesseract.util;

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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "f(x) = " + m + " * x + " + c;
    }
}
