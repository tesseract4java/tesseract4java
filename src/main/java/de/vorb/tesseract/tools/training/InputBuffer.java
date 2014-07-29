package de.vorb.tesseract.tools.training;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

public class InputBuffer implements Closeable, AutoCloseable {
    protected final BufferedInputStream in;
    protected long buf;
    protected boolean littleEndian = true;

    protected InputBuffer(BufferedInputStream in) {
        this.in = in;
    }

    protected InputBuffer(InputStream in, int capacity) {
        this(new BufferedInputStream(in, capacity));
    }

    public ByteOrder getByteOrder() {
        return littleEndian ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
    }

    public void setByteOrder(ByteOrder order) {
        littleEndian = order == ByteOrder.LITTLE_ENDIAN;
    }

    public boolean readByte() throws IOException {
        buf = in.read();

        return buf >= 0L;
    }

    public byte getByte() {
        return (byte) buf;
    }

    public boolean readShort() throws IOException {
        if (littleEndian) {
            buf = (in.read() << 8) // 1st byte
                    | in.read(); // 2nd byte
        } else {
            buf = in.read() // 1st byte
                    | (in.read() << 8); // 2nd byte
        }

        return buf >= 0L;
    }

    public short getShort() {
        return (short) buf;
    }

    public boolean readInt() throws IOException {
        if (littleEndian) {
            buf = (((long) in.read()) << 24) // 1st byte
                    | (((long) in.read()) << 16) // 2nd byte
                    | (((long) in.read()) << 8) // 3rd byte
                    | ((long) in.read()); // 4th byte
        } else {
            buf = ((long) in.read()) // 1st byte
                    | (((long) in.read()) << 8) // 2nd byte
                    | (((long) in.read()) << 16) // 3rd byte
                    | (((long) in.read()) << 24); // 4th byte
        }

        return buf >= 0L;
    }

    public int getInt() {
        return (int) buf;
    }

    public boolean readLong() throws IOException {
        if (littleEndian) {
            buf = (((long) in.read()) << 56) // 1st byte
                    | (((long) in.read()) << 48) // 2nd byte
                    | (((long) in.read()) << 40) // 3rd byte
                    | (((long) in.read()) << 32) // 4th byte
                    | (((long) in.read()) << 24) // 5th byte
                    | (((long) in.read()) << 16) // 6th byte
                    | (((long) in.read()) << 8) // 7th byte
                    | ((long) in.read()); // 8th byte
        } else {
            buf = ((long) in.read()) // 1st byte
                    | (((long) in.read()) << 8) // 2nd byte
                    | (((long) in.read()) << 16) // 3rd byte
                    | (((long) in.read()) << 24) // 4th byte
                    | (((long) in.read()) << 32) // 5th byte
                    | (((long) in.read()) << 40) // 6th byte
                    | (((long) in.read()) << 48) // 7th byte
                    | (((long) in.read()) << 56); // 8th byte
        }

        return buf >= 0L;
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

    @Override
    public void close() throws IOException {
        in.close();
    }

    public static InputBuffer allocate(BufferedInputStream in) {
        return new InputBuffer(in);
    }

    public static InputBuffer allocate(InputStream in, int capacity) {
        return new InputBuffer(in, capacity);
    }
}
