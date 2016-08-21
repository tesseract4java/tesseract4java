package de.vorb.tesseract.gui.event;

import java.nio.file.Path;

public interface ProjectChangeListener {
    void projectChanged(Path scanDir);
}
