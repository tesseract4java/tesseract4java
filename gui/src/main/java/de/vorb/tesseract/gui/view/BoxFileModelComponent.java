package de.vorb.tesseract.gui.view;

import de.vorb.tesseract.gui.model.BoxFileModel;

import com.google.common.base.Optional;

public interface BoxFileModelComponent extends PageModelComponent {
    void setBoxFileModel(Optional<BoxFileModel> model);
}
