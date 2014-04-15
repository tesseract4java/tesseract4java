package de.vorb.tesseract.util;

public class ArrayUtils {
  public static int minPos(int[] xs) {
    final int length = xs.length;

    int x = Integer.MAX_VALUE;
    int min = Integer.MAX_VALUE;
    int pos = -1;

    for (int i = 0; i < length; i++) {
      x = xs[i];
      if (x < min) {
        min = x;
        pos = i;
      }
    }

    return pos;
  }

  public static int maxPos(int[] xs) {
    final int length = xs.length;

    int x = Integer.MIN_VALUE;
    int max = Integer.MIN_VALUE;
    int pos = -1;

    for (int i = 0; i < length; i++) {
      x = xs[i];
      if (x > max) {
        max = x;
        pos = i;
      }
    }

    return pos;
  }
}
