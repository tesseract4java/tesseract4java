package de.vorb.tesseract.gui.work;

import de.vorb.tesseract.gui.controller.TesseractController;
import de.vorb.tesseract.gui.model.Image;
import de.vorb.tesseract.gui.model.Page;
import de.vorb.tesseract.gui.view.dialogs.Dialogs;
import de.vorb.tesseract.tools.recognition.PageRecognitionConsumer;
import de.vorb.tesseract.util.Block;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RecognitionWorker extends SwingWorker<Page, Void> {
    private final TesseractController controller;
    private final Image image;
    private final String trainingFile;
    private final PageRecognitionProducer producer;

    public RecognitionWorker(TesseractController controller,
            Image image, String trainingFile) {
        this.controller = controller;
        this.image = image;
        this.trainingFile = trainingFile;
        this.producer = controller.getPageRecognitionProducer();
    }

    @Override
    protected Page doInBackground() throws Exception {
        // set the progress bar state to indeterminate
        SwingUtilities.invokeLater(() -> {
            controller.setPage(null);
            controller.getView().getProgressBar().setIndeterminate(true);
        });

        if (trainingFile != null) {
            producer.setTrainingFile(trainingFile);
        }

        producer.reset();

        producer.loadImage(image.getPreprocessedFile());

        final List<Block> blocks = new ArrayList<>(1);

        // Get images
        final BufferedImage image = this.image.getPreprocessedImage();

        producer.recognize(new PageRecognitionConsumer(blocks) {
            @Override
            public boolean isCancelled() {
                return RecognitionWorker.this.isCancelled();
            }
        });

        final de.vorb.tesseract.util.Page page = new de.vorb.tesseract.util.Page(this.image.getPreprocessedFile(),
                image.getWidth(), image.getHeight(), 300, blocks);

        return new Page(this.image, page, "");
    }

    @Override
    protected void done() {
        try {
            controller.setPage(get());
        } catch (ExecutionException e) {
            e.printStackTrace();

            final String message = "The image could not be recognized";

            controller.setPage(null);

            Dialogs.showError(controller.getView(), "Error during recognition",
                    message);
        } catch (InterruptedException e) {
            // unexpected: if it is thrown, it is a bug
            e.printStackTrace();

            Dialogs.showError(controller.getView(), "Error during recognition",
                    "The recognition process has been interrupted unexpectedly.");
        } finally {
            controller.getView().getProgressBar().setIndeterminate(false);
        }
    }
}
