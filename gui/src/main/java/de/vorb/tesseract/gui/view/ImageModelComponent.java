package de.vorb.tesseract.gui.view;

import de.vorb.tesseract.gui.model.Image;

import java.util.Optional;

public interface ImageModelComponent extends MainComponent {
    void setImageModel(Optional<Image> model);

    Optional<Image> getImageModel();
}
