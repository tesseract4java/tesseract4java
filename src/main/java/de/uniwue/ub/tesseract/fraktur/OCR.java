package de.uniwue.ub.tesseract.fraktur;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.IIOImage;

import com.sun.jna.Pointer;

import net.sourceforge.tess4j.TessAPI;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.vietocr.ImageIOHelper;

public class OCR {
  private final File in, out;
  private final TessAPI api;

  public OCR(File in, File out) throws IOException {
    if (!in.isDirectory())
      throw new IllegalArgumentException("not a directory");

    if (!out.exists())
      Files.createDirectories(out.toPath());

    this.in = in;
    this.out = out;

    // Instantiate Tesseract
    Tesseract.getInstance();
    api = TessAPI.INSTANCE;
  }

  public String recognize(File imageFile, String lang, boolean hocr)
      throws IOException {
    final List<IIOImage> images = ImageIOHelper.getIIOImageList(imageFile);

    final IIOImage image = images.get(0);
    final RenderedImage ri = image.getRenderedImage();
    final ByteBuffer buf = ImageIOHelper.getImageByteBuffer(image);

    final TessAPI.TessBaseAPI handle = api.TessBaseAPICreate();
    api.TessBaseAPIInit2(handle, "tessdata", lang,
        TessAPI.TessOcrEngineMode.OEM_DEFAULT);
    api.TessBaseAPISetPageSegMode(handle, TessAPI.TessPageSegMode.PSM_AUTO);

    final int width = ri.getWidth();
    final int height = ri.getHeight();
    final int bpp = ri.getColorModel().getPixelSize();
    final int bytespp = bpp / 8;
    final int bytespl = (int) Math.ceil(width * bpp / 8.0);
    api.TessBaseAPISetImage(handle, buf, width, height, bytespp, bytespl);

    final Pointer text = hocr ? api.TessBaseAPIGetHOCRText(handle, 0) : api
        .TessBaseAPIGetUTF8Text(handle);
    final String result = text.getString(0);

    // free resources
    api.TessDeleteText(text);
    api.TessBaseAPIDelete(handle);

    return result;
  }

  public void recognizeAll(final String lang, final boolean hocr)
      throws IOException {
    final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime
        .getRuntime().availableProcessors() - 1);

    for (final File imageFile : in.listFiles()) {
      if (!imageFile.isFile())
        continue;

      threadPool.execute(new Runnable() {
        @Override
        public void run() {
          try {
            final String text = recognize(imageFile, lang, hocr);
            final String fname = imageFile.getName()
                + (hocr ? ".html" : ".txt");

            final PrintWriter to = new PrintWriter(new File(out, fname),
                "UTF-8");
            to.print(text);
            to.print('\n');
            to.close();

            System.out.println("Wrote " + fname);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      });
    }

    threadPool.shutdown();
    try {
      threadPool.awaitTermination(1, TimeUnit.DAYS);
    } catch (InterruptedException e) {
      System.err.println("timeout");
    }
  }

  public static void main(String[] args) throws IOException {
    final File in = new File("E:/Masterarbeit/Ressourcen");
    final OCR ocr = new OCR(new File(new File(in,
        "DE-20__32_AM_49000_L869_G927-1"), "sauvola"), new File(new File(in,
        "DE-20__32_AM_49000_L869_G927-1"), "ocr"));
    ocr.recognizeAll("deu-frak", false);
  }
}
