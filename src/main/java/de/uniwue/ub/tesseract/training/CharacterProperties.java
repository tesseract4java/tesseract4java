package de.uniwue.ub.tesseract.training;

import java.util.regex.Pattern;

public class CharacterProperties {
  private final static int BIT_ALPHA = 0;
  private final static int BIT_LOWER = 1;
  private final static int BIT_UPPER = 2;
  private final static int BIT_DIGIT = 3;
  private final static int BIT_PUNCT = 4;

  private final byte code;

  private CharacterProperties(byte code) {
    this.code = code;
  }

  private boolean getBit(int bit) {
    return ((code >> bit) & 1) == 1;
  }

  public CharacterProperties(boolean isAlpha, boolean isDigit, boolean isUpper,
      boolean isLower, boolean isPunct) {
    byte code = 0;
    code |= isAlpha ? 1 : 0;
    code |= isLower ? 2 : 0;
    code |= isUpper ? 4 : 0;
    code |= isDigit ? 8 : 0;
    code |= isPunct ? 16 : 0;
    this.code = code;
  }

  public static CharacterProperties forByteCode(byte code) {
    return new CharacterProperties(code);
  }

  public static CharacterProperties forHexString(String hexString) {
    return new CharacterProperties((byte) Integer.parseInt(hexString, 16));
  }

  private final static Pattern ALPHA = Pattern.compile("\\p{Alpha}",
      Pattern.UNICODE_CHARACTER_CLASS);
  private final static Pattern DIGIT = Pattern.compile("\\p{Digit}",
      Pattern.UNICODE_CHARACTER_CLASS);
  private final static Pattern UPPER = Pattern.compile("\\p{Upper}",
      Pattern.UNICODE_CHARACTER_CLASS);
  private final static Pattern LOWER = Pattern.compile("\\p{Lower}",
      Pattern.UNICODE_CHARACTER_CLASS);
  private final static Pattern PUNCT = Pattern.compile("\\p{Punct}",
      Pattern.UNICODE_CHARACTER_CLASS);

  public static CharacterProperties forCharacter(char c) {
    String character = "" + c;

    boolean isAlpha = ALPHA.matcher(character).matches();
    boolean isDigit = DIGIT.matcher(character).matches();
    boolean isUpper = UPPER.matcher(character).matches();
    boolean isLower = LOWER.matcher(character).matches();
    boolean isPunct = PUNCT.matcher(character).matches();

    return new CharacterProperties(isAlpha, isDigit, isUpper, isLower, isPunct);
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

  public boolean isPunct() {
    return getBit(BIT_PUNCT);
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
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof CharacterProperties))
      return false;
    CharacterProperties other = (CharacterProperties) obj;
    if (isAlpha() != other.isAlpha())
      return false;
    if (isDigit() != other.isDigit())
      return false;
    if (isLower() != other.isLower())
      return false;
    if (isPunct() != other.isPunct())
      return false;
    if (isUpper() != other.isUpper())
      return false;
    return true;
  }
}
