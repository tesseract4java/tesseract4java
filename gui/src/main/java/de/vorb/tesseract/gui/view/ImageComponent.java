package de.vorb.tesseract.gui.view;

import de.vorb.tesseract.gui.model.Image;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface ImageComponent extends MainComponent {

    void setImage(@Nullable Image image);

    @Nullable
    Image getImage();

}
