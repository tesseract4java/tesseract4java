package de.uniwue.ub;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.uniwue.ub.tesseract.fraktur.OCR;
import de.uniwue.ub.tesseract.preprocessing.Batch;
import de.uniwue.ub.util.FileIO;

public class WordConfidences {
  public static void main(String[] args) throws IOException,
      InterruptedException {
    final File in = new File(
        "C:/Users/Paul/Studium/Masterarbeit/Ressourcen/DE-20__32_AM_49000_L869_G927-1/hocr");
    final File out = new File(
        "C:/Users/Paul/Studium/Masterarbeit/Ressourcen/DE-20__32_AM_49000_L869_G927-1/conf");

    Files.createDirectories(out.toPath());

    final FileFilter filter = new FileFilter() {
      @Override
      public boolean accept(File f) {
        return f.isFile() && f.getName().endsWith(".html");
      }
    };

    final AtomicInteger globalConf = new AtomicInteger();
    final AtomicInteger globalWordCount = new AtomicInteger();

    final Batch task = new Batch() {
      @Override
      public Runnable getTask(final File src) {
        return new Runnable() {
          @Override
          public void run() {
            try {
              final AtomicInteger totalConf = new AtomicInteger();
              final AtomicInteger wordCount = new AtomicInteger();

              final SAXParser parser = SAXParserFactory.newInstance()
                  .newSAXParser();
              parser.parse(src, new DefaultHandler() {
                @Override
                public void startElement(String namespaceURI, String localName,
                    String qualName, Attributes attrs) throws SAXException {
                  if (!"ocrx_word".equals(attrs.getValue("class")))
                    return;

                  String title = attrs.getValue("title");
                  int conf = Integer.parseInt(title.substring(title
                      .lastIndexOf(' ') + 1));

                  totalConf.addAndGet(conf);
                  wordCount.incrementAndGet();
                }

                @Override
                public void endDocument() {
                  globalConf.addAndGet(totalConf.get());
                  globalWordCount.addAndGet(wordCount.get());

                  double conf = (double) totalConf.get() / wordCount.get();
                  if (Double.isNaN(conf))
                    System.out.println(" 100.0%, " + src.getName());
                  else
                    System.out.println(conf + "%, " + src.getName());
                }
              });
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        };
      }
    };

    final long start = System.currentTimeMillis();
    Batch.process(in, filter, task, 1, TimeUnit.DAYS);

    System.out.println("---");
    double conf = (double) globalConf.get() / globalWordCount.get();
    System.out.println("Global confidence: " + conf + "%");

    System.out.println("time: " + (System.currentTimeMillis() - start) + "ms");
  }
}
