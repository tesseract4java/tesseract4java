package de.vorb.tesseract.gui.model;

import java.util.Collections;
import java.util.List;

public class LanguageSelectionModel {
    private final List<String> languages;
    private int selectedIndex = -1;

    public LanguageSelectionModel(List<String> languages) {
        this.languages = languages;
    }

    public String getSelectedLanguage() {
        return languages.get(selectedIndex);
    }

    public void setSelectedIndex(int index) {
        selectedIndex = index;
    }

    public boolean isSelected() {
        return selectedIndex >= 0;
    }

    public List<String> getLanguages() {
        return Collections.unmodifiableList(languages);
    }
}
