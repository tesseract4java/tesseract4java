package de.vorb.tesseract.gui.model;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.vorb.tesseract.gui.event.LanguageChangeListener;

public class LanguageSelectionModel {
    private final List<String> languages;
    private int selectedIndex = -1;

    private LinkedList<LanguageChangeListener> listeners = new LinkedList<>();

    public LanguageSelectionModel(List<String> languages) {
        this.languages = languages;
    }

    public String getSelectedLanguage() {
        return languages.get(selectedIndex);
    }

    public void setSelectedIndex(int index) {
        selectedIndex = index;

        for (final LanguageChangeListener listener : listeners) {
            try {
                listener.languageChanged(getSelectedLanguage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isSelected() {
        return selectedIndex >= 0;
    }

    public List<String> getLanguages() {
        return Collections.unmodifiableList(languages);
    }

    public void addSelectionListener(LanguageChangeListener listener) {
        listeners.add(listener);
    }

    public void removeSelectionListener(LanguageChangeListener listener) {
        listeners.remove(listener);
    }
}
