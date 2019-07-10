package de.vorb.tesseract.gui.view;

import de.vorb.tesseract.gui.model.BoxFile;

import java.util.Optional;

public interface BoxFileModelComponent extends PageModelComponent {
    void setBoxFileModel(Optional<BoxFile> model);
}
