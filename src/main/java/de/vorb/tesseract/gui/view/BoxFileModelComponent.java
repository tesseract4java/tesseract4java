package de.vorb.tesseract.gui.view;

import java.awt.Component;

import com.google.common.base.Optional;

import de.vorb.tesseract.gui.model.BoxFileModel;

public interface BoxFileModelComponent extends MainComponent {
    Optional<BoxFileModel> getBoxFileModel();

    void setBoxFileModel(Optional<BoxFileModel> model);

    Component asComponent();
}
