package de.vorb.tesseract.gui.work;

import de.vorb.tesseract.gui.controller.TesseractController;
import de.vorb.tesseract.gui.model.ImageModel;
import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.gui.view.dialogs.Dialogs;
import de.vorb.tesseract.tools.recognition.PageRecognitionConsumer;
import de.vorb.tesseract.util.Block;
import de.vorb.tesseract.util.Page;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

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
        SwingUtilities.invokeLater(() -> {
            controller.setPageModel(Optional.empty());
            controller.getView().getProgressBar().setIndeterminate(true);
        });

        if (trainingFile != null) {
            producer.setTrainingFile(trainingFile);
        }

        producer.reset();

        producer.loadImage(imageModel.getPreprocessedFile());

        final List<Block> blocks = new ArrayList<>(1);

        // Get images
        final BufferedImage image = imageModel.getPreprocessedImage();

        producer.recognize(new PageRecognitionConsumer(blocks) {
            @Override
            public boolean isCancelled() {
                return RecognitionWorker.this.isCancelled();
            }
        });

        final Page page = new Page(imageModel.getPreprocessedFile(),
                image.getWidth(), image.getHeight(), 300, blocks);

        return new PageModel(imageModel, page, "");
    }

    @Override
    protected void done() {
        try {
            controller.setPageModel(Optional.of(get()));
        } catch (ExecutionException e) {
            e.printStackTrace();

            final String message = "The image could not be recognized";

            controller.setPageModel(Optional.empty());

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
