package de.vorb.tesseract.engine.model;

class DefaultTextSegment implements TextSegment {

    private final Box boundingBox;
    private final TextSegmentationLevel segmentationLevel;

    DefaultTextSegment(Box boundingBox, TextSegmentationLevel segmentationLevel) {
        this.boundingBox = boundingBox;
        this.segmentationLevel = segmentationLevel;
    }

    @Override
    public Box getBoundingBox() {
        return null;
    }

    @Override
    public TextSegmentationLevel getLevel() {
        return null;
    }
}
