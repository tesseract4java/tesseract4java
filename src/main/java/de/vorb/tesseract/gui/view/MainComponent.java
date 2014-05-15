package de.vorb.tesseract.gui.view;

import java.awt.Component;

import de.vorb.tesseract.util.Page;

public interface MainComponent {
    public void setModel(Page page);

    public Page getModel();

    public Component asComponent();
}
