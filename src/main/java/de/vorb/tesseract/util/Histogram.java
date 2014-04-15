package de.vorb.tesseract.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import de.vorb.tesseract.util.Box;

public class Histogram {
  private static final int[] bits;
  static {
    // pre-calculate the number of bits for each possible byte
    bits = new int[256];
    byte b = 0;
    for (int i = 0; i < 256; i++) {
      bits[i] = countBits(b++ & 0xFF);
    }
  }

  private static int countBits(int value) {
    int count = 0;
    for (int i = 0; i < 8; i++) {
      count += (value >> i) & 1;
    }
    return count;
  }

  public static int[] calculateVerticalHistogram(BufferedImage img) {
    final int width = img.getWidth();
    final int height = img.getHeight();
    final int numOfPixels = width * height;
    final int bytesPerLine = width / 8;
    final int numOfBytes = numOfPixels / 8;

    final int[] hist = new int[height];

    final int imgType = img.getType();
    if (imgType != BufferedImage.TYPE_BYTE_BINARY) {
      throw new IllegalArgumentException("image is not binary");
    }

    final byte[] bytes = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();

    // TODO handle cutoffs
    @SuppressWarnings("unused")
    final int lastByteCutoff = numOfPixels % 8;
    @SuppressWarnings("unused")
    final int lineCutoff = width % 8;

    int x = 0, y = 0;
    for (int pos = 0; pos < numOfBytes; pos++) {
      hist[y] += bits[~bytes[pos] & 0xFF];
      x++;

      if (x == bytesPerLine) {
        x = 0;
        y++;
      }
    }

    return hist;
  }

  public static int[] ascenders(int[] hist, Box lineBBox) {
    int[] dy = new int[lineBBox.getHeight() - 1];

    final int endY = lineBBox.getY() + lineBBox.getHeight() - 1;

    int y, x = 0;
    for (y = lineBBox.getY(); y < endY; y++) {
      dy[x] = hist[y + 1] - hist[y];

      x++;
    }

    final int minPos = ArrayUtils.minPos(dy);
    final int maxPos = ArrayUtils.maxPos(dy);

    final int baseline, xheight;
    if (minPos > maxPos) {
      baseline = minPos;
      xheight = baseline - maxPos;
    } else {
      baseline = maxPos;
      xheight = baseline - minPos;
    }

    return new int[] { lineBBox.getY() + baseline, xheight, };
  }
}
