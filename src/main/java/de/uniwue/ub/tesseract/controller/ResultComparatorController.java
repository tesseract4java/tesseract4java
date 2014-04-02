package de.uniwue.ub.tesseract.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.uniwue.ub.tesseract.event.PageChangeListener;
import de.uniwue.ub.tesseract.event.ProjectChangeListener;
import de.uniwue.ub.tesseract.util.Box;
import de.uniwue.ub.tesseract.util.Histogram;
import de.uniwue.ub.tesseract.util.Line;
import de.uniwue.ub.tesseract.util.Page;
import de.uniwue.ub.tesseract.util.Project;
import de.uniwue.ub.tesseract.util.Word;
import de.uniwue.ub.tesseract.view.ResultComparator;

public class ResultComparatorController implements ProjectChangeListener,
    PageChangeListener {

  private final ResultComparator view;
  private SwingWorker<Page, Void> pageLoader = null;

  public static void main(String[] args) {
    new ResultComparatorController();
  }

  public ResultComparatorController() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
    }

    view = new ResultComparator();
    view.getLoadProjectDialog().addProjectChangeListener(this);

    view.setVisible(true);
  }

  @Override
  public void projectChanged(Path scanDir, Path hocrDir) {
    try {
      final ArrayList<String> pages = new ArrayList<String>();

      final Iterator<Path> dir = Files.newDirectoryStream(scanDir).iterator();
      while (dir.hasNext()) {
        final Path file = dir.next();

        if (Files.isDirectory(file))
          continue;

        final String fname = file.getFileName().toString();

        pages.add(fname.substring(0, fname.lastIndexOf('.')));
      }

      final Project project = new Project(scanDir, hocrDir, pages);
      project.addPageChangeListener(this);

      view.getPageSelectionPane().setModel(project);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void pageChanged(int pageIndex, String page) {
    final Project project = view.getPageSelectionPane().getModel();
    final Path scanDir = project.getScanDir();
    final Path hocrDir = project.getHocrDir();

    final Path scanFile = scanDir.resolve(page + ".png");
    final Path hocrFile = hocrDir.resolve(page + ".html");
    pageLoader = new SwingWorker<Page, Void>() {
      @Override
      protected Page doInBackground() {
        try {
          return loadPageModel(scanFile, hocrFile);
        } catch (IOException e) {
          e.printStackTrace();
        }
        return null;
      }

      @Override
      public void done() {
        try {
          final Page page = get();
          view.getComparatorPane().setModel(page);
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        }
      }
    };

    pageLoader.execute();
  }

  private final SAXParserFactory parserFactory = SAXParserFactory.newInstance();

  private Page loadPageModel(Path scanFile, Path hocrFile) throws IOException {
    final String fname = scanFile.getFileName().toString();
    final String name = fname.substring(fname.lastIndexOf('.'));

    final BufferedImage scan = ImageIO.read(scanFile.toFile());
    final Vector<Line> lines = new Vector<Line>();

    final Page result = new Page(name, scan, lines);

    final boolean ascendersEnabled = result.isAscendersEnabled();
    final int[] histogram;
    if (ascendersEnabled) {
      histogram = Histogram.calculateVerticalHistogram(scan);
    } else {
      histogram = new int[0];
    }

    try {
      final SAXParser parser = parserFactory.newSAXParser();
      parser.parse(hocrFile.toFile(), new DefaultHandler() {
        private boolean isLine = false;
        private boolean isWord = false;
        private List<Word> line = new Vector<Word>();
        private Box lineBBox = null;
        private int lineNumber = 0;
        private int x1 = -1;
        private int y1 = -1;
        private int x2 = -1;
        private int y2 = -1;
        private int conf = 0;
        private String text = "";

        @Override
        public void startElement(String namespaceURI, String localName,
            String qualName, Attributes attrs) throws SAXException {
          if ("ocr_line".equals(attrs.getValue("class"))) {
            isLine = true;

            final String title = attrs.getValue("title");
            final String[] tokens = title.split("[a-z ]+");

            int x1 = Integer.parseInt(tokens[1]);
            int y1 = Integer.parseInt(tokens[2]);
            int x2 = Integer.parseInt(tokens[3]);
            int y2 = Integer.parseInt(tokens[4]);

            lineBBox = new Box(x1, y1, x2 - x1, y2 - y1);
          } else if (!"ocrx_word".equals(attrs.getValue("class"))) {
            isWord = false;
          } else {
            final String title = attrs.getValue("title");
            final String[] tokens = title.split("[a-z_; ]+");

            x1 = Integer.parseInt(tokens[1]);
            y1 = Integer.parseInt(tokens[2]);
            x2 = Integer.parseInt(tokens[3]);
            y2 = Integer.parseInt(tokens[4]);

            conf = Integer.parseInt(tokens[5]);

            isWord = true;
          }
        }

        @Override
        public void characters(char[] chars, int start, int length) {
          if (isWord) {
            text = new String(chars, start, length);
          }
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
          if (!isWord) {
            if (isLine) {
              final int baseline, xheight;
              if (ascendersEnabled) {
                int[] ascenders = Histogram.ascenders(histogram, lineBBox);
                baseline = ascenders[0];
                xheight = ascenders[1];
              } else {
                baseline = lineBBox.getY() + lineBBox.getHeight();
                xheight = lineBBox.getHeight();
              }

              lines.add(new Line(lineBBox, line, baseline, xheight));
              line = new Vector<Word>();

              lineNumber++;
            }

            isLine = false;
          } else {
            final Box bbox = new Box(x1, y1, x2 - x1, y2 - y1);
            line.add(new Word(text, lineNumber, bbox, conf));

            isWord = false;
          }
        }
      });
    } catch (ParserConfigurationException | SAXException e) {
    } catch (IOException e) {
    }

    return new Page(name, scan, lines);
  }
}
