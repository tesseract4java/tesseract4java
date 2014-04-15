package de.vorb.tesseract.util;

import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.util.Hashtable;

import org.bridj.Pointer;

import de.vorb.tesseract.bridj.Tesseract.Pix;

/**
 * Utility methods for Pix image objects.
 * 
 * @author Paul Vorbach
 */
public class PixUtils {
  private PixUtils() {
  }

  private static final int BYTE_BITS = 8;
  private static final int INT_BITS = 4 * BYTE_BITS;

  /**
   * Takes a Pix as used by Tesseract and converts it to a BufferedImage.
   * 
   * @param pix
   *          Pix
   * @return BufferedImage
   */
  public static BufferedImage pixToBufferedImage(Pix pix) {
    final int width = pix.w();
    final int height = pix.h();
    final int bitDepth = pix.d();

    int sizeInBits = width * height * bitDepth;
    // binary image data as an int array
    final int[] pixData = (int[]) pix.data().getArray(sizeInBits / INT_BITS);

    // create int image data buffer
    final DataBufferInt dataBuf = new DataBufferInt(pixData, pixData.length);

    // ... and a writable raster
    final WritableRaster raster = Raster.createPackedRaster(dataBuf, width,
        height, bitDepth, new Point(0, 0));

    // determine the color model
    // This is partially taken from java.awt.image.BufferedImage
    final ColorModel cm;
    if (bitDepth == 1) {
      final byte[] arr = { (byte) 0xFF, (byte) 0x00 };

      cm = new IndexColorModel(1, 2, arr, arr, arr);
    } else if (bitDepth == 8) {
      final ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
      int[] nBits = { 8 };
      cm = new ComponentColorModel(cs, nBits, false, true, Transparency.OPAQUE,
          DataBuffer.TYPE_INT);
    } else {
      throw new IllegalArgumentException(
          "Only binary and grayscale images allowed.");
    }

    // create and return the buffered image
    return new BufferedImage(cm, raster, false, new Hashtable<>());
  }

  public static Pix bufferedImageToPix(BufferedImage bufferedImage) {
    final Pix pix = new Pix();

    pix.w(bufferedImage.getWidth());
    pix.h(bufferedImage.getHeight());

    switch (bufferedImage.getType()) {
    case BufferedImage.TYPE_BYTE_BINARY:
      pix.d(1);
      pix.spp(1);
      break;
    case BufferedImage.TYPE_BYTE_GRAY:
      pix.d(8);
      pix.spp(1);
      break;
    default:
      // if the given image is neither binary or grayscale, convert it to
      // grayscale
      final ColorConvertOp op = new ColorConvertOp(
          ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
      final BufferedImage gray = new BufferedImage(bufferedImage.getWidth(),
          bufferedImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
      op.filter(bufferedImage, gray);

      // replace input image with grayscale version
      bufferedImage = gray;

      pix.d(8);
      pix.spp(1);
    }

    final DataBufferByte dataBuf =
        (DataBufferByte) bufferedImage.getData().getDataBuffer();
    final ByteBuffer dataBufBytes = ByteBuffer.wrap(dataBuf.getData());

    // cast this bytebuffer to an int buffer
    final Pointer<Integer> data = Pointer.pointerToBytes(dataBufBytes).as(
        Integer.TYPE);

    pix.data(data);

    return pix;
  }
}
