package de.uniwue.ub.tesseract.training;

import java.util.regex.Pattern;

public class CharacterProperties {
  public final boolean isAlpha;
  public final boolean isDigit;
  public final boolean isUpper;
  public final boolean isLower;
  public final boolean isPunct;

  private CharacterProperties(byte code) {
    isAlpha = (code & 1) == 1;
    isLower = ((code >> 1) & 1) == 1;
    isUpper = ((code >> 2) & 1) == 1;
    isDigit = ((code >> 3) & 1) == 1;
    isPunct = ((code >> 4) & 1) == 1;
  }

  public CharacterProperties(boolean isAlpha, boolean isDigit, boolean isUpper,
      boolean isLower, boolean isPunct) {
    this.isAlpha = isAlpha;
    this.isDigit = isDigit;
    this.isUpper = isUpper;
    this.isLower = isLower;
    this.isPunct = isPunct;
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

  public byte toByteCode() {
    byte code = 0;
    code |= isAlpha ? 1 : 0;
    code |= isLower ? 2 : 0;
    code |= isUpper ? 4 : 0;
    code |= isDigit ? 8 : 0;
    code |= isPunct ? 16 : 0;
    return code;
  }

  public String toHexString() {
    return Integer.toHexString(toByteCode());
  }

  @Override
  public String toString() {
    return Integer.toBinaryString(toByteCode());
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
    if (isAlpha != other.isAlpha)
      return false;
    if (isDigit != other.isDigit)
      return false;
    if (isLower != other.isLower)
      return false;
    if (isPunct != other.isPunct)
      return false;
    if (isUpper != other.isUpper)
      return false;
    return true;
  }

  public static void main(String[] args) {
    System.out.println(CharacterProperties.forCharacter('Ä').toHexString());
    System.out.println(CharacterProperties.forCharacter('ß').toHexString());
    System.out.println(CharacterProperties.forCharacter('=').toHexString());
    System.out.println(CharacterProperties.forCharacter('7').toHexString());
    System.out.println(CharacterProperties.forCharacter(';').toHexString());
  }
}
