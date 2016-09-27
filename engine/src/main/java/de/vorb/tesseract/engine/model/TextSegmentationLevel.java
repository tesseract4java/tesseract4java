package de.vorb.tesseract.engine.model;

public enum TextSegmentationLevel {

    SYMBOL,
    WORD,
    TEXT_LINE,
    PARAGRAPH,
    BLOCK;

    public TextSegment getSegmentWith(Box boundingBox) {
        return new DefaultTextSegment(boundingBox, this);
    }

}
