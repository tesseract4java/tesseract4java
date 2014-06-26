package de.vorb.tesseract.gui.view.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Labels {
    public static String getLabel(Locale locale, String key) {
        return getLabel(locale, key, "?");
    }

    public static String getLabel(Locale locale, String key, String defaultValue) {
        final ResourceBundle labels = ResourceBundle.getBundle("l10n/labels",
                locale);

        try {
            return labels.getString(key);
        } catch (MissingResourceException e) {
            return defaultValue;
        }
    }
}
