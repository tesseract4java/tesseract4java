package de.vorb.tesseract.gui.event;

import java.io.IOException;

public interface LanguageChangeListener {
    public void languageChanged(String language) throws IOException;
}
