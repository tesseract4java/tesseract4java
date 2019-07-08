package de.vorb.tesseract.gui.view.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class Labels {

    private Labels() {}

    public static String getLabel(Locale locale, String key) {
        final ResourceBundle labels = ResourceBundle.getBundle("l10n/labels",
                locale);

        try {
            return labels.getString(key);
        } catch (MissingResourceException e) {
            return "?";
        }
    }
}
