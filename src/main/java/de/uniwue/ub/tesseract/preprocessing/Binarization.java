package de.uniwue.ub.tesseract.preprocessing;

import ij.ImagePlus;
import ij.gui.NewImage;
import ij.plugin.filter.RankFilters;
import ij.process.Blitter;
import ij.process.ImageConverter;
import ij.process.ImageProcessor;

import java.awt.image.BufferedImage;

/**
 * Algorithms for binarizing images.
 * 
 * @author Paul Vorbach
 */
public class Binarization {
  private static Binarization instance = null;

  private Binarization() {
  }

  /**
   * @return singleton instance of this class.
   */
  public static Binarization getInstance() {
    if (instance == null)
      instance = new Binarization();

    return instance;
  }

  private final byte white = (byte) 0xFF;
  private final byte black = (byte) 0x00;

  /**
   * Image binarization with Sauvola's method.
   * 
   * 
   * 
   * @param src
   * @param radius
   * @param k
   * @param R
   * @return binarized version of the image.
   * 
   * @see <a
   *      href="http://www.ee.oulu.fi/research/mvmp/mvg/files/pdf/pdf_24.pdf">Sauvola
   *      et al. 2000 - Adaptive document image binarization</a>
   */
  public BufferedImage sauvola(BufferedImage src, int radius, double k, int R) {
    if (src.getType() == BufferedImage.TYPE_BYTE_BINARY) {
      return src;
    }

    final ImagePlus imp = new ImagePlus("", src);
    ImageProcessor ip = imp.getProcessor();
    final ImagePlus meanImp = duplicateImage(ip);
    ImageConverter ic = new ImageConverter(meanImp);
    ic.convertToGray32();

    ip = imp.getProcessor();
    final ImagePlus varImp = duplicateImage(ip);
    ic = new ImageConverter(varImp);
    ic.convertToGray32();

    final ImageProcessor ipMean = meanImp.getProcessor();
    final RankFilters rf = new RankFilters();
    rf.rank(ipMean, radius, RankFilters.MEAN);

    final ImageProcessor ipVar = varImp.getProcessor();
    rf.rank(ipVar, radius, RankFilters.VARIANCE);

    final byte[] pixels = (byte[]) ip.getPixels();
    final float[] mean = (float[]) ipMean.getPixels();
    final float[] var = (float[]) ipVar.getPixels();

    for (int i = 0; i < pixels.length; i++) {
      pixels[i] = ((int) (pixels[i] & 0xff) > (int) (mean[i] * (1.0 + k
          * ((Math.sqrt(var[i]) / R) - 1.0)))) ? white : black;
    }

    final BufferedImage binary = new BufferedImage(src.getWidth(),
        src.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
    imp.getBufferedImage().copyData(binary.getRaster());
    return binary;
  }

  /**
   * Image binarization with Sauvola's method.
   * 
   * Uses default values <i>k</i> = 0.5, <i>R</i> = 128.
   * 
   * @param src
   * @param radius
   * @return
   */
  public BufferedImage sauvola(BufferedImage src, int radius) {
    return sauvola(src, radius, 0.5, 128);
  }

  private ImagePlus duplicateImage(ImageProcessor ip) {
    int w = ip.getWidth();
    int h = ip.getHeight();
    ImagePlus imp = NewImage.createByteImage("Image", w, h, 1,
        NewImage.FILL_BLACK);
    ImageProcessor imageProcessor = imp.getProcessor();
    imageProcessor.copyBits(ip, 0, 0, Blitter.COPY);
    return imp;
  }
}
