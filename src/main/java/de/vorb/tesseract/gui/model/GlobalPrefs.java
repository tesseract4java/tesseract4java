package de.vorb.tesseract.gui.model;

import java.nio.file.Paths;
import java.util.prefs.Preferences;

public final class GlobalPrefs {
    public static final String TESSDATA_DIR = "path.tessdata";
    public static final String TESSDATA_DIR_DEFAULT;

    static {
        String defaultDir = "";
        try {
            defaultDir = Paths.get(System.getenv("TESSDATA_PREFIX")).resolve(
                    "tessdata").toString();
        } catch (Exception e) {
        }

        TESSDATA_DIR_DEFAULT = defaultDir;
    }

    private static GlobalPrefs instance = null;

    private GlobalPrefs() {
    }

    public static Preferences getPrefs() {
        if (instance == null) {
            instance = new GlobalPrefs();
        }

        return Preferences.userNodeForPackage(instance.getClass());
    }
}
