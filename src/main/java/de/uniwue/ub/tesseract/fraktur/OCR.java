package de.uniwue.ub.tesseract.fraktur;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import javax.imageio.IIOImage;

import net.sourceforge.tess4j.TessAPI;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.vietocr.ImageIOHelper;

import com.sun.jna.Pointer;

public class OCR {
  private final TessAPI api;

  public static class RecognitionResult {
    public String plain;
    public String hocr;
    public String unlv;

    public RecognitionResult() {
    }
  }

  public OCR() {
    // Instantiate Tesseract
    Tesseract.getInstance();
    api = TessAPI.INSTANCE;
  }

  public RecognitionResult recognize(File imageFile, String lang,
      boolean plain, boolean hocr, boolean unlv) throws IOException {
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

    final RecognitionResult result = new RecognitionResult();

    if (plain) {
      final Pointer plainText = api.TessBaseAPIGetUTF8Text(handle);
      result.plain = plainText.getString(0);
      api.TessDeleteText(plainText);
    }

    if (hocr) {
      final Pointer hocrText = api.TessBaseAPIGetHOCRText(handle, 0);
      result.hocr = hocrText.getString(0);
      api.TessDeleteText(hocrText);
    }

    if (unlv) {
      final Pointer unlvText = api.TessBaseAPIGetUNLVText(handle);
      result.unlv = unlvText.getString(0);
      api.TessDeleteText(unlvText);
    }

    // free resources
    api.TessBaseAPIDelete(handle);

    return result;
  }
}
