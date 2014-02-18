package de.uniwue.ub.tesseract.training;

import java.util.regex.Pattern;

public class Script {

  public static void main(String[] args) {
    System.out.println(Pattern.compile("[^\\p{L}\\p{Digit}]+").matcher(".~\"!“").matches());
  }

}
