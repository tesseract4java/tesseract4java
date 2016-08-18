package de.vorb.tesseract.gui.view;

import de.vorb.tesseract.gui.model.ImageModel;

import com.google.common.base.Optional;

public interface ImageModelComponent extends MainComponent {
    void setImageModel(Optional<ImageModel> model);

    Optional<ImageModel> getImageModel();
}
