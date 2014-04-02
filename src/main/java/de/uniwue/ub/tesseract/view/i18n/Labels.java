package de.uniwue.ub.tesseract.view.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Labels {
  public static String getLabel(Locale locale, String key) {
    final ResourceBundle labels = ResourceBundle.getBundle("labels", locale);
    try {
      return labels.getString(key);
    } catch (MissingResourceException e) {
      return "?";
    }
  }
}
