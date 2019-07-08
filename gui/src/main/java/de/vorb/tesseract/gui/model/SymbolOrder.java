package de.vorb.tesseract.gui.model;

public enum SymbolOrder {
    CONFIDENCE("Confidence"),
    SIZE("Size"),
    IMAGE_WEIGHT("Weight");

    private final String name;

    SymbolOrder(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
