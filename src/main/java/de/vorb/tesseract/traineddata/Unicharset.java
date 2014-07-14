package de.vorb.tesseract.traineddata;

public class Unicharset {
    private final int size;

    public Unicharset(String str) {
        final String[] lines = str.split("\n");
        size = Integer.parseInt(lines[0]);
    }

    public int getSize() {
        return size;
    }
}
