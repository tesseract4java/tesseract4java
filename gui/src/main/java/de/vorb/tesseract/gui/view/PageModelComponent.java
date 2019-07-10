package de.vorb.tesseract.gui.view;

import de.vorb.tesseract.gui.model.BoxFile;
import de.vorb.tesseract.gui.model.PageModel;

import java.util.Optional;

public interface PageModelComponent extends MainComponent {
    void setPageModel(Optional<PageModel> model);

    Optional<PageModel> getPageModel();

    Optional<BoxFile> getBoxFileModel();
}
