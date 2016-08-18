package de.vorb.tesseract.tools.training;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class EmptyInputBufferTest {
    private InputBuffer empty;

    @Before
    public void setUp() throws Exception {
        empty = InputBuffer.allocate(new ByteArrayInputStream(new byte[0]),
                4096);
    }

    @Test
    public void testReadByte() {
        try {
            Assert.assertFalse("could read from empty buffer",
                    empty.readByte());
        } catch (IOException e) {
        }
    }

    @Test
    public void testReadShort() {
        try {
            Assert.assertFalse("could read from empty buffer",
                    empty.readShort());
        } catch (IOException e) {
        }
    }

    @Test
    public void testReadInt() {
        try {
            Assert.assertFalse("could read from empty buffer",
                    empty.readInt());
        } catch (IOException e) {
        }
    }

    @Test
    public void testReadLong() {
        try {
            Assert.assertFalse("could read from empty buffer",
                    empty.readLong());
        } catch (IOException e) {
        }
    }
}
