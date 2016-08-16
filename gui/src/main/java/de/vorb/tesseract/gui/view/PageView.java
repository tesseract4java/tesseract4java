package de.vorb.tesseract.gui.view;

import com.google.common.base.Optional;

import de.vorb.tesseract.gui.model.PageModel;

public interface PageView {
    void setPageModel(Optional<PageModel> pageModel);

    Optional<PageModel> getPageModel();
}
