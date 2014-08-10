package de.vorb.tesseract.gui.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.google.common.base.Optional;

import de.vorb.tesseract.PageIteratorLevel;
import de.vorb.tesseract.gui.controller.TesseractController;
import de.vorb.tesseract.gui.model.ImageModel;
import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.gui.view.Dialogs;
import de.vorb.tesseract.tools.recognition.DefaultRecognitionConsumer;
import de.vorb.tesseract.tools.recognition.RecognitionState;
import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.Line;
import de.vorb.tesseract.util.Page;
import de.vorb.tesseract.util.Symbol;
import de.vorb.tesseract.util.Word;

public class RecognitionWorker extends SwingWorker<PageModel, Void> {
    private final TesseractController controller;
    private final ImageModel imageModel;
    private final String trainingFile;
    private final PageRecognitionProducer producer;

    public RecognitionWorker(TesseractController controller,
            ImageModel imageModel, String trainingFile) {
        this.controller = controller;
        this.imageModel = imageModel;
        this.trainingFile = trainingFile;
        this.producer = controller.getPageRecognitionProducer();
    }

    @Override
    protected PageModel doInBackground() throws Exception {
        // set the progress bar state to indeterminate
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                controller.setPageModel(Optional.<PageModel> absent());
                controller.getView().getProgressBar().setIndeterminate(true);
            }
        });

        if (trainingFile != null) {
            producer.setTrainingFile(trainingFile);
        }

        producer.reset();

        producer.loadImage(imageModel.getPreprocessedFile());

        final Vector<Line> lines = new Vector<Line>();

        // Get images
        final BufferedImage image = imageModel.getPreprocessedImage();

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

                // Optional<Pointer<Pix>> img = producer.getImage();
                // if (img.isPresent()) {
                // getState().getSymbolFeatures(img.get());
                // }
            }

            @Override
            public boolean isCancelled() {
                return RecognitionWorker.this.isCancelled();
            }
        });

        final Page page = new Page(imageModel.getPreprocessedFile(),
                image.getWidth(), image.getHeight(), 300, lines);

        return new PageModel(imageModel, page);
    }

    @Override
    protected void done() {
        try {
            controller.setPageModel(Optional.of(get()));
        } catch (ExecutionException e) {
            final String message;
            if (e.getCause() instanceof IOException) {
                message = "The page image file could not be read.";
            } else {
                message = "The page could not be recognized";

                e.printStackTrace();
            }

            controller.setPageModel(Optional.<PageModel> absent());

            Dialogs.showError(controller.getView(), "Error during recognition",
                    message);
        } catch (InterruptedException e) {
            // unexpected
            e.printStackTrace();

            Dialogs.showError(controller.getView(), "Error during recognition",
                    "The recognition process has been interrupted unexpectedly.");
        } finally {
            controller.getView().getProgressBar().setIndeterminate(false);
        }
    }
}
