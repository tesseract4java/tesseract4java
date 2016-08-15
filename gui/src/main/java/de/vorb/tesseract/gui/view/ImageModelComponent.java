package de.vorb.tesseract.gui.view;

import com.google.common.base.Optional;

import de.vorb.tesseract.gui.model.ImageModel;

public interface ImageModelComponent extends MainComponent {
    void setImageModel(Optional<ImageModel> model);

    Optional<ImageModel> getImageModel();
}
