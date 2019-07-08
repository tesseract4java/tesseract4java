package de.vorb.tesseract.gui.util;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public final class Resources {

    private Resources() {}

    public static Icon getIcon(String name) {
        return new ImageIcon(Resources.class.getResource(String.format("/icons/%s.png", name)));
    }
}
