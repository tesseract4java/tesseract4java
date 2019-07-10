package de.vorb.tesseract.gui.view.renderer;

import de.vorb.tesseract.gui.model.Page;
import de.vorb.tesseract.gui.model.Scale;
import de.vorb.tesseract.gui.view.EvaluationPane;

import org.checkerframework.checker.nullness.qual.Nullable;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class EvaluationPaneRenderer implements PageRenderer {

    private final EvaluationPane evaluationPane;

    public EvaluationPaneRenderer(EvaluationPane evaluationPane) {
        this.evaluationPane = evaluationPane;
    }

    @Override
    public void render(@Nullable Page page, float scale) {
        final boolean modelPresent = page != null;

        evaluationPane.getSaveTranscriptionButton().setEnabled(modelPresent);
        evaluationPane.getGenerateReportButton().setEnabled(modelPresent);
        evaluationPane.getTextAreaTranscript().setEnabled(modelPresent);

        if (modelPresent) {
            final BufferedImage img = page.getImage().getSourceImage();

            final int w = Scale.scaled(img.getWidth(), scale);
            final int h = Scale.scaled(img.getHeight(), scale);

            evaluationPane.getOriginal().setIcon(new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH)));
            evaluationPane.getTextAreaTranscript().setText(page.getTranscription());
        } else {
            evaluationPane.getOriginal().setIcon(null);
            evaluationPane.getTextAreaTranscript().setText("");
        }
    }

}
