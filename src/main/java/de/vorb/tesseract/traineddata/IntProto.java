package de.vorb.tesseract.traineddata;

public class IntProto {
    private final byte a;
    private final byte b; // unsigned byte
    private final byte c;
    private final byte angle; // unsigned byte
    private final int[] configs;

    public IntProto(byte a, short b, byte c, short angle, int[] configs) {
        this.a = a;
        this.b = (byte) b;
        this.c = c;
        this.angle = (byte) angle;
        this.configs = configs;
    }

    public byte getA() {
        return a;
    }

    public short getB() {
        return (short) (b & 0xFF);
    }

    public byte getC() {
        return c;
    }

    public short getAngle() {
        return (short) (angle & 0xFF);
    }

    public long getConfig(int i) {
        return configs[i] & 0xFFFF_FFFFL;
    }
    
    

    @Override
    public String toString() {
        return String.format("IntProto(a=%d, b=%d, c=%d, angle=%d)", getA(),
                getB(), getC(), getAngle());
    }
}
