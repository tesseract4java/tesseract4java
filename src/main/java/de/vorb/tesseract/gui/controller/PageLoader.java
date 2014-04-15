package de.vorb.tesseract.gui.controller;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.bridj.Pointer;

import de.vorb.tesseract.bridj.Tesseract;
import de.vorb.tesseract.bridj.Tesseract.Pix;
import de.vorb.tesseract.bridj.Tesseract.TessOcrEngineMode;
import de.vorb.tesseract.tools.recognition.Recognition;
import de.vorb.tesseract.util.PixUtils;

public class PageLoader extends Recognition {
  public PageLoader() throws IOException {
    super();
  }

  @Override
  protected void init() throws IOException {
    // init Tesseract with data path, language and OCR engine mode
    Tesseract.TessBaseAPIInit2(
        getHandle(),
        Pointer.pointerToCString(Paths.get(System.getenv("TESSDATA_PREFIX"),
            "tessdata").toString()),
        Pointer.pointerToCString("deu-frak"), TessOcrEngineMode.OEM_DEFAULT);

    // set page segmentation mode
    Tesseract.TessBaseAPISetPageSegMode(getHandle(),
        Tesseract.TessPageSegMode.PSM_AUTO);
  }

  public void setImage(BufferedImage image) {
    Tesseract.TessBaseAPISetImage2(getHandle(),
        Pointer.pointerTo(PixUtils.bufferedImageToPix(image)));
  }

  public BufferedImage getThresholdedImage() {
    final Pointer<Pix> img =
        Tesseract.TessBaseAPIGetThresholdedImage(getHandle());

    return PixUtils.pixToBufferedImage(img.get());
  }
}
