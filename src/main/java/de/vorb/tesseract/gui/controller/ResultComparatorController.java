package de.vorb.tesseract.gui.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.xml.parsers.SAXParserFactory;

import org.bridj.BridJ;

import de.vorb.tesseract.bridj.Tesseract.TessPageIteratorLevel;
import de.vorb.tesseract.gui.event.PageChangeListener;
import de.vorb.tesseract.gui.event.ProjectChangeListener;
import de.vorb.tesseract.gui.view.ResultComparator;
import de.vorb.tesseract.tools.recognition.DefaultRecognitionConsumer;
import de.vorb.tesseract.util.Baseline;
import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.FontAttributes;
import de.vorb.tesseract.util.Line;
import de.vorb.tesseract.util.Page;
import de.vorb.tesseract.util.Project;
import de.vorb.tesseract.util.Symbol;
import de.vorb.tesseract.util.Word;

public class ResultComparatorController implements ProjectChangeListener,
    PageChangeListener {

  private final ResultComparator view;
  private SwingWorker<Page, Void> pageLoaderWorker = null;
  private PageLoader pageLoader = null;

  // Filter for image files
  private static final DirectoryStream.Filter<Path> IMG_FILTER =
      new DirectoryStream.Filter<Path>() {
        @Override
        public boolean accept(Path entry) throws IOException {
          return entry.toString().endsWith(".png")
              || entry.toString().endsWith(".tif")
              || entry.toString().endsWith(".tiff")
              || entry.toString().endsWith(".jpg")
              || entry.toString().endsWith(".jpeg");
        }
      };

  public static void main(String[] args) {
    BridJ.setNativeLibraryFile("tesseract", new File("libtesseract303.dll"));

    new ResultComparatorController();
  }

  public ResultComparatorController() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
    }

    try {
      pageLoader = new PageLoader();
    } catch (IOException e) {
      // won't happen, since PageLoader.init() doesn't do I/O
    }

    view = new ResultComparator();
    view.getLoadProjectDialog().addProjectChangeListener(this);

    view.setVisible(true);
  }

  @Override
  public void projectChanged(Path scanDir) {
    try {
      final ArrayList<Path> pages = new ArrayList<>();

      final Iterator<Path> dirIt = Files.newDirectoryStream(scanDir,
          IMG_FILTER).iterator();

      while (dirIt.hasNext()) {
        final Path file = dirIt.next();

        if (Files.isDirectory(file))
          continue;

        pages.add(file);
      }

      final Project project = new Project(scanDir, pages);
      project.addPageChangeListener(this);

      view.getPageSelectionPane().setModel(project);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void pageChanged(int pageIndex, final Path page) {
    pageLoaderWorker = new SwingWorker<Page, Void>() {
      @Override
      protected Page doInBackground() {
        try {
          return loadPageModel(page);
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

    pageLoaderWorker.execute();
  }

  private Page loadPageModel(Path scanFile) throws IOException {
    final Vector<Line> lines = new Vector<Line>();

    final BufferedImage originalImg = ImageIO.read(scanFile.toFile());
    pageLoader.setImage(originalImg);

    final BufferedImage thresholdedImg;
    if (originalImg.getType() == BufferedImage.TYPE_BYTE_BINARY) {
      thresholdedImg = originalImg;
    } else {
      thresholdedImg = pageLoader.getThresholdedImage();
    }

    pageLoader.recognize(new DefaultRecognitionConsumer() {
      LinkedList<Word> lineWords = null;
      LinkedList<Symbol> wordSymbols = null;

      @Override
      public void lineBegin() {
        lineWords = new LinkedList<>();
      }

      @Override
      public void wordBegin() {
        wordSymbols = new LinkedList<>();
      }

      @Override
      public void wordEnd() {
        final Box bbox = getState().getBoundingBox(
            TessPageIteratorLevel.RIL_WORD);
        final FontAttributes fontAttrs = getState().getWordFontAttributes();
        final float confidence = getState().getConfidence(
            TessPageIteratorLevel.RIL_WORD);

        lineWords.add(new Word(Collections.unmodifiableList(wordSymbols), bbox,
            confidence, fontAttrs));
      }

      @Override
      public void lineEnd() {
        final Box lineBox = getState().getBoundingBox(
            TessPageIteratorLevel.RIL_TEXTLINE);
        final Baseline baseline = getState().getBaseline(
            TessPageIteratorLevel.RIL_TEXTLINE);

        lines.add(new Line(lineBox, lineWords, baseline));
      }
    });

    System.out.println(lines.size());

    final Page result = new Page(scanFile, originalImg, thresholdedImg, lines);

    return result;
  }
}
