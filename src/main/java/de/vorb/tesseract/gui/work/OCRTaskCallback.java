package de.vorb.tesseract.gui.work;

import java.nio.file.Path;

import com.google.common.base.Optional;

public interface OCRTaskCallback {
    void taskStart(Path sourceFile);
    void taskComplete(Optional<Exception> exception, Optional<Path> sourceFile);
}
