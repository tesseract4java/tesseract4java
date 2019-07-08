package de.vorb.tesseract.gui.model;

import java.util.prefs.Preferences;

public final class PreferencesUtil {

    private PreferencesUtil() {
    }

    public static Preferences getPreferences() {
        return Preferences.userNodeForPackage(PreferencesUtil.class);
    }
}
