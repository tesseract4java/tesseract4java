package de.vorb.tesseract.gui.view;

import de.vorb.tesseract.gui.model.PageModel;

import com.google.common.base.Optional;

public interface PageView {
    void setPageModel(Optional<PageModel> pageModel);

    Optional<PageModel> getPageModel();
}
