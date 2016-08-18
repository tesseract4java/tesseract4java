package de.vorb.tesseract.util.feat;

public class Feature4D {
    private final byte a;
    private final byte b; // unsigned byte
    private final byte c;
    private final byte angle; // unsigned byte
    private final int[] configs;

    public Feature4D(int a, int b, int c, int angle, int[] configs) {
        this.a = (byte) a;
        this.b = (byte) b;
        this.c = (byte) c;
        this.angle = (byte) angle;
        this.configs = configs;
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b & 0xFF;
    }

    public int getC() {
        return c;
    }

    public int getAngle() {
        return angle & 0xFF;
    }

    public long getConfig(int i) {
        return configs[i] & 0xFFFF_FFFFL;
    }

    @Override
    public String toString() {
        return String.format("Feature4D(a=%d, b=%d, c=%d, angle=%d)", getA(),
                getB(), getC(), getAngle());
    }
}
