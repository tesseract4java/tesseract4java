package de.vorb.tesseract.gui.event;

import java.io.IOException;

public interface LanguageChangeListener {
    public void languageSelectionChanged(String language) throws IOException;
}
