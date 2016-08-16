package de.vorb.tesseract.gui.util;

import javax.swing.*;

public class Resources {
    public static final Icon getIcon(String name) {
        return new ImageIcon(Resources.class.getResource(String.format("/icons/%s.png", name)));
    }
}
