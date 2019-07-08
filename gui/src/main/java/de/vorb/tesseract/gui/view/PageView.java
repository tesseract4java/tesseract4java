package de.vorb.tesseract.gui.view;

import de.vorb.tesseract.gui.model.PageModel;

import java.util.Optional;

public interface PageView {
    void setPageModel(Optional<PageModel> pageModel);

    Optional<PageModel> getPageModel();
}
