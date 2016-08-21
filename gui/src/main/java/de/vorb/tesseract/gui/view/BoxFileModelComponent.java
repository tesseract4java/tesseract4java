package de.vorb.tesseract.gui.view;

import de.vorb.tesseract.gui.model.BoxFileModel;

import java.util.Optional;

public interface BoxFileModelComponent extends PageModelComponent {
    void setBoxFileModel(Optional<BoxFileModel> model);
}
