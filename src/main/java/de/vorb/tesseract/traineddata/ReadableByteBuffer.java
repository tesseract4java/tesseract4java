package de.vorb.tesseract.traineddata;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public class ReadableByteBuffer {
    private final ReadableByteChannel in;
    private final ByteBuffer buf;
    private boolean endReached = false;

    private ReadableByteBuffer(ReadableByteChannel in, int capacity) {
        this.in = in;
        this.buf = ByteBuffer.allocate(capacity);
    }

    public boolean hasNext(int numBytes) {
        final int remaining = buf.remaining();
        final boolean enough = remaining >= numBytes;
        if (enough) {
            return true;
        }

        if (endReached) {
            return false;
        }

        if (remaining > 0) {
            // move the remaining bytes to the front
            buf.get(buf.array(), 0, remaining);

            // set the position of the buffer to the end of remaining bytes
            buf.position(remaining);
        } else {
            buf.clear();
        }

        int numRead = 0;
        try {
            // fill the buffer, so it can be read again
            if ((numRead = in.read(buf)) < numBytes - remaining) {
                endReached = true;
            }
        } catch (IOException e) {
            // ignore errors but set end reached
            endReached = true;
        } finally {
            buf.rewind();
        }

        return numRead >= numBytes;
    }

    public byte get() {
        return buf.get();
    }

    public char getChar() {
        return buf.getChar();
    }

    public double getDouble() {
        return buf.getDouble();
    }

    public float getFloat() {
        return buf.getFloat();
    }

    public int getInt() {
        return buf.getInt();
    }

    public long getLong() {
        return buf.getLong();
    }

    public short getShort() {
        return buf.getShort();
    }

    public ByteBuffer getBuffer() {
        return buf;
    }

    public static ReadableByteBuffer allocate(ReadableByteChannel in,
            int capacity) {
        return new ReadableByteBuffer(in, capacity);
    }
}
