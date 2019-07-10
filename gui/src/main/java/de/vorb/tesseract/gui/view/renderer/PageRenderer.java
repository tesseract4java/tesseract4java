package de.vorb.tesseract.gui.view.renderer;

import de.vorb.tesseract.gui.model.Page;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface PageRenderer {

    void render(@Nullable final Page page, final float scale);

}
