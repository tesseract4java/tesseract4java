package de.vorb.tesseract.gui.view;

import java.awt.Component;

import com.google.common.base.Optional;

import de.vorb.tesseract.gui.model.PageModel;

public interface MainComponent {
    void setModel(Optional<PageModel> model);

    Optional<PageModel> getModel();

    Component asComponent();
}
