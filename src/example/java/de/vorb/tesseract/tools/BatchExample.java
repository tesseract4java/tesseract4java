package de.vorb.tesseract.tools;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import de.vorb.tesseract.tools.preprocessing.Batch;
import de.vorb.tesseract.tools.preprocessing.Binarization;

public class BatchExample {
  public static void main(String[] args) throws InterruptedException,
      IOException {
    final File from = new File("E:/Masterarbeit/Ressourcen/GoldStandard");
    final File to = new File("E:/Masterarbeit/Ressourcen/GoldStandard/sauvola");
    Files.createDirectories(to.toPath());

    // Sauvola binarization
    Batch.process(new File("E:/Masterarbeit/Ressourcen/GoldStandard"),
        new FileFilter() {
          @Override
          public boolean accept(File f) {
            return f.isFile() && f.getName().endsWith(".png");
          }
        }, new Batch() {
          final Binarization bin = Binarization.getInstance();

          @Override
          public Runnable getTask(final File src) {
            return new Runnable() {
              @Override
              public void run() {
                try {
                  ImageIO.write(bin.sauvola(ImageIO.read(src), 15), "PNG",
                      new File(to, src.getName()));
                } catch (IOException e) {
                  e.printStackTrace();
                }
              }
            };
          }
        }, 1, TimeUnit.DAYS);
  }
}
