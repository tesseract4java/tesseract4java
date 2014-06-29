package de.vorb.tesseract.gui.util;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

import de.vorb.tesseract.PageIteratorLevel;
import de.vorb.tesseract.gui.controller.TesseractController;
import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.tools.recognition.DefaultRecognitionConsumer;
import de.vorb.tesseract.tools.recognition.RecognitionState;
import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.Line;
import de.vorb.tesseract.util.Page;
import de.vorb.tesseract.util.Symbol;
import de.vorb.tesseract.util.Word;

public class PageModelLoader extends SwingWorker<PageModel, Void> {
    private final TesseractController controller;
    private final Path imageFile;

    public PageModelLoader(TesseractController controller, Path imageFile) {
        this.controller = controller;
        this.imageFile = imageFile;
    }

    @Override
    protected PageModel doInBackground() throws Exception {
        final PageRecognitionProducer producer =
                controller.getPageRecognitionProducer();

        producer.reset();

        producer.loadImage(imageFile);

        final Vector<Line> lines = new Vector<Line>();

        // Get images
        final BufferedImage image = ImageIO.read(imageFile.toFile());

        producer.recognize(new DefaultRecognitionConsumer() {
            private ArrayList<Word> lineWords;
            private ArrayList<Symbol> wordSymbols;

            @Override
            public void lineBegin() {
                lineWords = new ArrayList<>();
            }

            @Override
            public void lineEnd() {
                final PageIteratorLevel level = PageIteratorLevel.TEXTLINE;
                lines.add(new Line(getState().getBoundingBox(level), lineWords,
                        getState().getBaseline(level)));
            }

            @Override
            public void wordBegin() {
                wordSymbols = new ArrayList<>();
            }

            @Override
            public void wordEnd() {
                final RecognitionState state = getState();
                final PageIteratorLevel level = PageIteratorLevel.WORD;
                final Box bbox = state.getBoundingBox(level);
                lineWords.add(new Word(wordSymbols, bbox,
                        state.getConfidence(level),
                        state.getBaseline(PageIteratorLevel.WORD),
                        state.getWordFontAttributes()));
            }

            @Override
            public void symbol() {
                final PageIteratorLevel level = PageIteratorLevel.SYMBOL;
                wordSymbols.add(new Symbol(getState().getText(level),
                        getState().getBoundingBox(level),
                        getState().getConfidence(level)));
            }

            @Override
            public boolean isCancelled() {
                return PageModelLoader.this.isCancelled();
            }
        });

        final Page page = new Page(imageFile, image.getWidth(),
                image.getHeight(), 300, lines);

        // try {
        // page.writeTo(System.out);
        // } catch (JAXBException e) {
        // e.printStackTrace();
        // }

        return new PageModel(page, image);
    }

    @Override
    protected void done() {
        try {
            final PageModel model = get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.getCause().printStackTrace();
        }
    }
}
