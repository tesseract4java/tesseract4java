package de.vorb.tesseract.tools.training;

import java.util.regex.Pattern;

public class CharacterProperties {
    private final static int BIT_ALPHA = 0;
    private final static int BIT_LOWER = 1;
    private final static int BIT_UPPER = 2;
    private final static int BIT_DIGIT = 3;
    private final static int BIT_PUNCTUATION = 4;

    private final byte code;

    private CharacterProperties(byte code) {
        this.code = code;
    }

    private boolean getBit(int bit) {
        return ((code >> bit) & 1) == 1;
    }

    public CharacterProperties(boolean isAlpha, boolean isDigit,
            boolean isUpper, boolean isLower, boolean isPunctuation) {
        byte code = 0;
        code |= isAlpha ? 1 : 0;
        code |= isLower ? 2 : 0;
        code |= isUpper ? 4 : 0;
        code |= isDigit ? 8 : 0;
        code |= isPunctuation ? 16 : 0;
        this.code = code;
    }

    public static CharacterProperties forByteCode(byte code) {
        return new CharacterProperties(code);
    }

    public static CharacterProperties forHexString(String hexString) {
        return new CharacterProperties((byte) Integer.parseInt(hexString, 16));
    }

    private final static Pattern ALPHA = Pattern.compile("\\p{Alpha}+",
            Pattern.UNICODE_CHARACTER_CLASS);
    private final static Pattern DIGIT = Pattern.compile("\\p{Digit}+",
            Pattern.UNICODE_CHARACTER_CLASS);
    private final static Pattern UPPER = Pattern.compile("\\p{Upper}+",
            Pattern.UNICODE_CHARACTER_CLASS);
    private final static Pattern LOWER = Pattern.compile("\\p{Lower}+",
            Pattern.UNICODE_CHARACTER_CLASS);
    private final static Pattern PUNCTUATION = Pattern.compile("\\p{Punct}+",
            Pattern.UNICODE_CHARACTER_CLASS);

    public static CharacterProperties forCharacter(char c) {
        return forString("" + c);
    }

    public static CharacterProperties forString(String cs) {
        final boolean isAlpha = ALPHA.matcher(cs).matches();
        final boolean isDigit = DIGIT.matcher(cs).matches();
        final boolean isUpper = UPPER.matcher(cs).matches();
        final boolean isLower = LOWER.matcher(cs).matches();
        final boolean isPunctuation = PUNCTUATION.matcher(cs).matches();

        return new CharacterProperties(isAlpha, isDigit, isUpper, isLower,
                isPunctuation);
    }

    public boolean isAlpha() {
        return getBit(BIT_ALPHA);
    }

    public boolean isLower() {
        return getBit(BIT_LOWER);
    }

    public boolean isUpper() {
        return getBit(BIT_UPPER);
    }

    public boolean isDigit() {
        return getBit(BIT_DIGIT);
    }

    public boolean isPunctuation() {
        return getBit(BIT_PUNCTUATION);
    }

    public byte toByteCode() {
        return code;
    }

    public String toHexString() {
        return Integer.toHexString(code);
    }

    @Override
    public String toString() {
        return Integer.toBinaryString(code);
    }

    @Override
    public int hashCode() {
        return toByteCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof CharacterProperties)) {
            return false;
        }
        CharacterProperties other = (CharacterProperties) obj;
        if (isAlpha() != other.isAlpha()) {
            return false;
        } else if (isDigit() != other.isDigit()) {
            return false;
        } else if (isLower() != other.isLower()) {
            return false;
        } else if (isPunctuation() != other.isPunctuation()) {
            return false;
        }
        return isUpper() == other.isUpper();
    }
}
