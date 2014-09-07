package de.vorb.tesseract.gui.view;

import com.google.common.base.Optional;

import de.vorb.tesseract.gui.model.BoxFileModel;

public interface BoxFileModelComponent extends PageModelComponent {
    void setBoxFileModel(Optional<BoxFileModel> model);
}
