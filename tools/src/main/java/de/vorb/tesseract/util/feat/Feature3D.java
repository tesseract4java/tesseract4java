package de.vorb.tesseract.util.feat;

import org.bytedeco.javacpp.tesseract;

import java.nio.ByteBuffer;

public class Feature3D {
    private final byte x, y, theta; // features
    private final byte cpMisses; // cp misses
    private final int outlineIndex; // index of the outline containing this feature

    public Feature3D(int x, int y, int theta) {
        this.x = (byte) x;
        this.y = (byte) y;
        this.theta = (byte) theta;
        cpMisses = 0;
        outlineIndex = 0;
    }

    public Feature3D(int x, int y, int theta, int cpMisses, int outlineIndex) {
        this.x = (byte) x;
        this.y = (byte) (0xFF - y);
        this.theta = (byte) (0xFF - theta);
        this.cpMisses = (byte) cpMisses;
        this.outlineIndex = outlineIndex;
    }

    public int getX() {
        return x & 0xFF;
    }

    public int getY() {
        return y & 0xFF;
    }

    public int getTheta() {
        return theta & 0xFF;
    }

    public int getCPMisses() {
        return cpMisses;
    }

    public int getOutlineIndex() {
        return outlineIndex;
    }

    public static Feature3D valueOf(tesseract.INT_FEATURE_STRUCT feat, int outlineIndex) {
        final ByteBuffer buf = feat.asByteBuffer();
        return new Feature3D(
                buf.get(0) & 0xFF,
                buf.get(1) & 0xFF,
                buf.get(2) & 0xFF,
                buf.get(3) & 0xFF,
                outlineIndex);
    }

    @Override
    public String toString() {
        return String.format("Feature3D(x = %d, y = %d, theta = %d)",
                getX(), getY(), getTheta());
    }
}
