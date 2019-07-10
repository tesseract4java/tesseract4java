package de.vorb.tesseract.gui.view;

import de.vorb.tesseract.gui.model.Image;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface ImageModelComponent extends MainComponent {

    void setImageModel(@Nullable Image model);

    @Nullable
    Image getImageModel();

}
