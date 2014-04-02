package de.uniwue.ub;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

import de.uniwue.ub.tesseract.fraktur.OCR;
import de.uniwue.ub.tesseract.preprocessing.Batch;
import de.uniwue.ub.util.FileIO;

public class OCRExample {
  public static void main(String[] args) throws IOException,
      InterruptedException {
    final File book = new File(
        "E:/Masterarbeit/Ressourcen/DE-20__32_AM_49000_L869_G927-1");

    final String lang = "deu-frak";

    final File in = new File(book, "sauvola");
    final File out = new File(book, lang);
    final File outPlain = new File(out, "plain");
    final File outHOCR = new File(out, "hocr");
    final File outUNLV = new File(out, "unlv");

    Files.createDirectories(outPlain.toPath());
    Files.createDirectories(outHOCR.toPath());
    Files.createDirectories(outUNLV.toPath());

    final FileFilter filter = new FileFilter() {
      @Override
      public boolean accept(File f) {
        return f.isFile() && f.getName().endsWith(".png");
      }
    };

    // OCR engine
    final OCR ocr = new OCR();

    final Batch task = new Batch() {
      @Override
      public Runnable getTask(final File src) {
        return new Runnable() {
          @Override
          public void run() {
            try {
              final OCR.RecognitionResult result = ocr.recognize(src, lang,
                  true, true, true);

              final File destPlain = new File(outPlain,
                  src.getName().replaceFirst("\\.png", ".txt"));
              final File destHOCR = new File(outHOCR,
                  src.getName().replaceFirst("\\.png", ".html"));
              final File destUNLV = new File(outUNLV,
                  src.getName().replaceFirst("\\.png", ".txt"));

              FileIO.getInstance().writeFile(result.plain, destPlain,
                  StandardCharsets.UTF_8);
              System.out.println("Plain: " + destPlain);

              FileIO.getInstance().writeFile(result.hocr, destHOCR,
                  StandardCharsets.UTF_8);
              System.out.println("HOCR:  " + destHOCR);

              FileIO.getInstance().writeFile(result.unlv, destUNLV,
                  StandardCharsets.UTF_8);
              System.out.println("UNLV:  " + destUNLV);
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        };
      }
    };

    final long start = System.currentTimeMillis();
    Batch.process(in, filter, task, 1, TimeUnit.DAYS);
    System.out.println("time: " + ((System.currentTimeMillis() - start) / 1000)
        + "s");
  }
}
