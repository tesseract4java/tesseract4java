package de.vorb.tesseract.gui.view;

import com.google.common.base.Optional;

import de.vorb.tesseract.gui.model.PageModel;

public interface PageModelComponent extends MainComponent {
    void setPageModel(Optional<PageModel> model);

    Optional<PageModel> getPageModel();
}
