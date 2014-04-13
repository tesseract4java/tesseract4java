package de.vorb.tesseract.tools;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.bridj.BridJ;
import org.bridj.Pointer;

import de.vorb.tesseract.bridj.Tesseract;
import de.vorb.tesseract.bridj.Tesseract.TessOcrEngineMode;
import de.vorb.tesseract.bridj.Tesseract.TessPageIteratorLevel;
import de.vorb.tesseract.tools.recognition.DefaultRecognitionConsumer;
import de.vorb.tesseract.tools.recognition.Recognition;

public class SymbolRecognition extends Recognition {
  public SymbolRecognition() throws IOException {
    super();
  }

  @Override
  protected void init() throws IOException {
    // init Tesseract with data path, language and OCR engine mode
    Tesseract.TessBaseAPIInit2(getHandle(),
        Pointer.pointerToCString("E:\\Masterarbeit\\Ressourcen\\tessdata"),
        Pointer.pointerToCString("deu-frak"), TessOcrEngineMode.OEM_DEFAULT);

    // set page segmentation mode
    Tesseract.TessBaseAPISetPageSegMode(getHandle(),
        Tesseract.TessPageSegMode.PSM_AUTO);

    // read the image into memory
    final BufferedImage inputImage = ImageIO.read(new File("input.png"));

    // get the image data
    final DataBuffer imageBuffer = inputImage.getRaster().getDataBuffer();
    final byte[] imageData = ((DataBufferByte) imageBuffer).getData();

    // image properties
    final int width = inputImage.getWidth();
    final int height = inputImage.getHeight();
    final int bitsPerPixel = inputImage.getColorModel().getPixelSize();
    final int bytesPerPixel = bitsPerPixel / 8;
    final int bytesPerLine = (int) Math.ceil(width * bitsPerPixel / 8.0);

    // set the image
    Tesseract.TessBaseAPISetImage(getHandle(),
        Pointer.pointerToBytes(ByteBuffer.wrap(imageData)), width, height,
        bytesPerPixel, bytesPerLine);
  }

  public static void main(String[] args) throws IOException {
    BridJ.setNativeLibraryFile("tesseract", new File("libtesseract303.dll"));

    final TessPageIteratorLevel level = TessPageIteratorLevel.RIL_SYMBOL;

    new SymbolRecognition().recognize(new DefaultRecognitionConsumer() {
      @Override
      public void wordBegin() {
        System.out.println(getState().getBaseline(
            TessPageIteratorLevel.RIL_WORD));
      }

      @Override
      public void symbol() {
        System.out.println(getState().getText(level) + ": "
            + getState().getBoundingBox(level)
            + ", conf: " + getState().getConfidence(level));
      }
    });
  }
}
