package de.vorb.tesseract.traineddata;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputBuffer {
    protected final BufferedInputStream in;
    protected long buf;

    protected InputBuffer(BufferedInputStream in) {
        this.in = in;
    }

    protected InputBuffer(InputStream in, int capacity) {
        this(new BufferedInputStream(in, capacity));
    }

    public boolean readByte() throws IOException {
        buf = in.read();
        return buf != -1L;
    }

    public byte getByte() {
        return (byte) buf;
    }

    public boolean readShort() throws IOException {
        // 1st byte
        buf = in.read();
        buf <<= 8;

        // 2nd byte
        buf |= in.read();
        return buf != -1L;
    }

    public short getShort() {
        return (short) buf;
    }

    public boolean readInt() throws IOException {
        // 1st byte
        buf = in.read();
        buf <<= 8;

        // 2nd byte
        buf |= in.read();
        buf <<= 8;

        // 3rd byte
        buf |= in.read();
        buf <<= 8;

        // 4th byte
        buf |= in.read();
        return buf != -1L;
    }

    public int getInt() {
        return (int) buf;
    }

    public boolean readLong() throws IOException {
        // 1st byte
        buf = in.read();
        buf <<= 8;

        // 2nd byte
        buf |= in.read();
        buf <<= 8;

        // 3rd byte
        buf |= in.read();
        buf <<= 8;

        // 4th byte
        buf = in.read();
        buf <<= 8;

        // 5th byte
        buf |= in.read();
        buf <<= 8;

        // 6th byte
        buf |= in.read();
        buf <<= 8;

        // 7th byte
        buf |= in.read();
        buf <<= 8;

        // 8th byte
        buf |= in.read();
        return buf != -1L;
    }

    public long getLong() {
        return buf;
    }

    public boolean readChar() throws IOException {
        return readShort();
    }

    public char getChar() {
        return (char) buf;
    }

    public boolean readFloat() throws IOException {
        return readInt();
    }

    public float getFloat() {
        return Float.intBitsToFloat((int) buf);
    }

    public boolean readDouble() throws IOException {
        return readLong();
    }

    public double getDouble() {
        return Double.longBitsToDouble(buf);
    }

    public int readBuffer(byte[] buf) throws IOException {
        return in.read(buf);
    }

    public int readBuffer(byte[] buf, int off, int len) throws IOException {
        return in.read(buf, off, len);
    }

    public static InputBuffer allocate(BufferedInputStream in) {
        return new InputBuffer(in);
    }

    public static InputBuffer allocate(InputStream in, int capacity) {
        return new InputBuffer(in, capacity);
    }
}
