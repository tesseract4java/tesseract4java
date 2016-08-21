package de.vorb.tesseract.tools.training;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CharacterPropertiesTest {
    final CharacterProperties semicolon = new CharacterProperties(false, false,
            false, false, true);
    final CharacterProperties b = new CharacterProperties(true, false, false,
            true, false);
    final CharacterProperties W = new CharacterProperties(true, false, true,
            false, false);
    final CharacterProperties digit7 = new CharacterProperties(false, true,
            false, false, false);
    final CharacterProperties equalSign = new CharacterProperties(false, false,
            false, false, false);
    final CharacterProperties sharpS = new CharacterProperties(true, false,
            false, true, false);
    final CharacterProperties umlautA = new CharacterProperties(true, false,
            true, false, false);

    @Test
    public void testForByteCode() {
        assertEquals(CharacterProperties.forByteCode((byte) 16), semicolon);
        assertEquals(CharacterProperties.forByteCode((byte) 3), b);
        assertEquals(CharacterProperties.forByteCode((byte) 5), W);
        assertEquals(CharacterProperties.forByteCode((byte) 8), digit7);
        assertEquals(CharacterProperties.forByteCode((byte) 0), equalSign);
    }

    @Test
    public void testFromHexString() {
        assertEquals(CharacterProperties.forHexString("10"), semicolon);
        assertEquals(CharacterProperties.forHexString("3"), b);
        assertEquals(CharacterProperties.forHexString("5"), W);
        assertEquals(CharacterProperties.forHexString("8"), digit7);
        assertEquals(CharacterProperties.forHexString("0"), equalSign);
    }

    @Test
    public void testForCharacter() {
        assertEquals(CharacterProperties.forCharacter(';'), semicolon);
        assertEquals(CharacterProperties.forCharacter('b'), b);
        assertEquals(CharacterProperties.forCharacter('W'), W);
        assertEquals(CharacterProperties.forCharacter('7'), digit7);
        assertEquals(CharacterProperties.forCharacter('='), equalSign);

        // unicode characters
        assertEquals(CharacterProperties.forCharacter('ß'), sharpS);
        assertEquals(CharacterProperties.forCharacter('Ä'), umlautA);
    }

    @Test
    public void testToByteCode() {
        assertEquals(semicolon.toByteCode(), (byte) 16);
        assertEquals(b.toByteCode(), (byte) 3);
        assertEquals(W.toByteCode(), (byte) 5);
        assertEquals(digit7.toByteCode(), (byte) 8);
        assertEquals(equalSign.toByteCode(), (byte) 0);
    }

    @Test
    public void testToHexString() {
        assertEquals(semicolon.toHexString(), "10");
        assertEquals(b.toHexString(), "3");
        assertEquals(W.toHexString(), "5");
        assertEquals(digit7.toHexString(), "8");
        assertEquals(equalSign.toHexString(), "0");
    }

    @Test
    public void testEquals() {
        assertEquals(b.equals(b), true);
        assertEquals(b.equals(W), false);
    }

    @Test
    public void testHashCode() {
        assertEquals(b.hashCode(), b.hashCode());
        assertNotEquals(b.hashCode(), W.hashCode());
    }
}
