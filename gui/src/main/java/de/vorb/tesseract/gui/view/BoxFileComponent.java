package de.vorb.tesseract.gui.view;

import de.vorb.tesseract.gui.model.BoxFile;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface BoxFileComponent extends PageComponent {

    void setBoxFile(@Nullable BoxFile boxFile);

}
