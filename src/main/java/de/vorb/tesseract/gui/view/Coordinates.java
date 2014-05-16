package de.vorb.tesseract.gui.view;

public class Coordinates {
    private Coordinates() {
    }

    public static int scaled(int coord, float scale) {
        return Math.round(coord * scale);
    }

    public static int unscaled(int coord, float scale) {
        return Math.round(coord / scale);
    }
}
