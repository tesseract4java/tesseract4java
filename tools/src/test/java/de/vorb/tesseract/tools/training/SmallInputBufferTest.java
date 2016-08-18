package de.vorb.tesseract.tools.training;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class SmallInputBufferTest {
    private static final byte[] bytes = new byte[256];

    @Test
    public void testReadByte() throws IOException {
        final InputBuffer buf = InputBuffer.allocate(new ByteArrayInputStream(
                bytes), 4096);

        int i = 0;
        while (buf.readByte()) {
            i++;
        }

        Assert.assertEquals("preliminary end of stream", bytes.length, i);
    }

    @Test
    public void testReadShort() throws IOException {
        final InputBuffer buf = InputBuffer.allocate(new ByteArrayInputStream(
                bytes), 4096);

        int i = 0;
        while (buf.readShort()) {
            i++;
        }

        Assert.assertEquals("preliminary end of stream", bytes.length / 2, i);
    }

    @Test
    public void testReadInt() throws IOException {
        final InputBuffer buf = InputBuffer.allocate(new ByteArrayInputStream(
                bytes), 4096);

        int i = 0;
        while (buf.readInt()) {
            i++;
        }

        Assert.assertEquals("preliminary end of stream", bytes.length / 4, i);
    }

    @Test
    public void testReadLong() throws IOException {
        final InputBuffer buf = InputBuffer.allocate(new ByteArrayInputStream(
                bytes), 4096);

        int i = 0;
        while (buf.readLong()) {
            i++;
        }

        Assert.assertEquals("preliminary end of stream", bytes.length / 8, i);
    }
}
