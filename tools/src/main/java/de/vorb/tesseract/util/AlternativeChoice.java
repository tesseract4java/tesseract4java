package de.vorb.tesseract.util;

public class AlternativeChoice {
    private final String text;
    private final float confidence;

    public AlternativeChoice(String text, float confidence) {
        this.text = text;
        this.confidence = confidence;
    }

    public String getText() {
        return text;
    }

    public float getConfidence() {
        return confidence;
    }
}
