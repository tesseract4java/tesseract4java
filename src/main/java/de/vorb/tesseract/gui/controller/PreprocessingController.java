package de.vorb.tesseract.gui.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JProgressBar;

import com.google.common.base.Optional;

import de.vorb.tesseract.gui.model.ImageModel;
import de.vorb.tesseract.gui.model.ProjectModel;
import de.vorb.tesseract.gui.util.PreprocessingWorker;
import de.vorb.tesseract.gui.view.Dialogs;
import de.vorb.tesseract.gui.view.PreprocessingPane;
import de.vorb.tesseract.tools.preprocessing.Preprocessor;

public class PreprocessingController implements ActionListener {
    private final TesseractController controller;

    private Preprocessor defaultPreprocessor;
    private Map<Path, Preprocessor> preprocessors;

    private Set<Path> changedPreprocessors = new HashSet<>();

    public PreprocessingController(TesseractController controller) {
        this.controller = controller;

        final PreprocessingPane preprocessingPane =
                controller.getView().getPreprocessingPane();

        preprocessingPane.getPreviewButton().addActionListener(this);
        preprocessingPane.getApplyToPageButton().addActionListener(this);
        preprocessingPane.getApplyToAllPagesButton().addActionListener(this);
    }

    public Preprocessor getDefaultPreprocessor() {
        return defaultPreprocessor;
    }

    public Preprocessor getPreprocessor(Path sourceFile) {
        final Preprocessor preprocessor = preprocessors.get(sourceFile);

        if (preprocessor == null) {
            return defaultPreprocessor;
        }

        return preprocessors.get(sourceFile);
    }

    public boolean hasPreprocessorChanged(Path sourceFile) {
        // try to remove it and return true if the set contained the sourceFile
        return changedPreprocessors.contains(sourceFile);
    }

    public void setDefaultPreprocessor(Preprocessor preprocessor) {
        defaultPreprocessor = preprocessor;
    }

    public void setPreprocessor(Path sourceFile, Preprocessor preprocessor) {
        if (preprocessor.equals(defaultPreprocessor))
            preprocessors.remove(sourceFile);
        else
            preprocessors.put(sourceFile, preprocessor);
    }

    public void setPreprocessorChanged(Path sourceFile, boolean changed) {
        if (changed)
            changedPreprocessors.add(sourceFile);
        else
            changedPreprocessors.remove(sourceFile);
    }

    public void setImageModel(Path sourceFile, ImageModel imageModel) {
        final Optional<Path> selectedPage = controller.getSelectedPage();
        if (selectedPage.isPresent() && selectedPage.get().equals(sourceFile)) {
            // TODO set image
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final Object src = e.getSource();
        final PreprocessingPane preprocPane =
                controller.getView().getPreprocessingPane();

        if (preprocPane.getPreviewButton().equals(src)) {
            handlePreprocessorPreview();
        } else if (preprocPane.getApplyToPageButton().equals(src)) {
            handlePreprocessorChange(false);
        } else if (preprocPane.getApplyToAllPagesButton().equals(src)) {
            handlePreprocessorChange(true);
        }
    }

    private void handlePreprocessorPreview() {
        final Optional<Path> selectedPage = controller.getSelectedPage();

        // if no page is selected, simply ignore it
        if (!selectedPage.isPresent()) {
            return;
        }

        final JProgressBar progress = controller.getView().getProgressBar();
        progress.setIndeterminate(true);

        final Optional<ProjectModel> projectModel =
                controller.getProjectModel();

        if (!projectModel.isPresent()) {
            Dialogs.showWarning(controller.getView(), "No project",
                    "No project has been selected. You need to create a project first.");
            return;
        }

        new PreprocessingWorker(this, selectedPage.get(),
                projectModel.get().getProjectDir());
    }

    private void handlePreprocessorChange(boolean allPages) {

    }
}
