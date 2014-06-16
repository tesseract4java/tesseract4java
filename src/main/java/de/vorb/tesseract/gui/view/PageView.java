package de.vorb.tesseract.gui.view;

import de.vorb.tesseract.gui.model.PageModel;

public interface PageView {
    void setPageModel(PageModel pageModel);

    PageModel getPageModel();
}
