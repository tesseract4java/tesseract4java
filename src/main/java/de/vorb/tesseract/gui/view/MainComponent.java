package de.vorb.tesseract.gui.view;

import java.awt.Component;

import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.util.Page;

public interface MainComponent {
    public void setModel(PageModel model);

    public PageModel getModel();

    public Component asComponent();
}
