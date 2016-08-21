package de.vorb.tesseract.gui.view;

import de.vorb.tesseract.gui.model.ImageModel;

import java.util.Optional;

public interface ImageModelComponent extends MainComponent {
    void setImageModel(Optional<ImageModel> model);

    Optional<ImageModel> getImageModel();
}
