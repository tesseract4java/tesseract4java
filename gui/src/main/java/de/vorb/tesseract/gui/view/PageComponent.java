package de.vorb.tesseract.gui.view;

import de.vorb.tesseract.gui.model.BoxFile;
import de.vorb.tesseract.gui.model.Page;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface PageComponent extends MainComponent {

    void setPage(@Nullable Page model);

    @Nullable
    Page getPage();

    @Nullable
    BoxFile getBoxFile();

}
