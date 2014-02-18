package de.uniwue.ub.tesseract.training;

import java.io.File;

import net.sourceforge.tess4j.TessAPI;
import net.sourceforge.tess4j.TessAPI.TessBaseAPI;

public class Tess {
  private final String lang;
  private static TessAPI api = null;

  public Tess(String lang) {
    this.lang = lang;
  }

  public String getLanguage() {
    return lang;
  }

  private TessAPI getInstance() {
    if (api == null) {
      // Instantiate Tess4J
      net.sourceforge.tess4j.Tesseract.getInstance();
      api = TessAPI.INSTANCE;
    }

    return api;
  }

  private TessBaseAPI getHandle() {
    return api.TessBaseAPICreate();
  }

  public void recognize(File image) {
    TessBaseAPI handle = getHandle();

    // Recognition

    api.TessBaseAPIDelete(handle);
  }
}
