package de.vorb.tesseract.tools.training;

public class CharacterDimensions {
    public static final CharacterDimensions DEFAULT = new CharacterDimensions(
            0, 255, 0, 255, 0, 32767, 0, 32767, 0, 32767);

    private final int minBottom;
    private final int maxBottom;
    private final int minTop;
    private final int maxTop;
    private final int minWidth;
    private final int maxWidth;
    private final int minBearing;
    private final int maxBearing;
    private final int minAdvance;
    private final int maxAdvance;

    public CharacterDimensions(int minBottom, int maxBottom, int minTop,
            int maxTop, int minWidth, int maxWidth, int minBearing,
            int maxBearing, int minAdvance, int maxAdvance) {
        this.minBottom = minBottom;
        this.maxBottom = maxBottom;
        this.minTop = minTop;
        this.maxTop = maxTop;
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
        this.minBearing = minBearing;
        this.maxBearing = maxBearing;
        this.minAdvance = minAdvance;
        this.maxAdvance = maxAdvance;
    }

    public int getMinBottom() {
        return minBottom;
    }

    public int getMaxBottom() {
        return maxBottom;
    }

    public int getMinTop() {
        return minTop;
    }

    public int getMaxTop() {
        return maxTop;
    }

    public int getMinWidth() {
        return minWidth;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public int getMinBearing() {
        return minBearing;
    }

    public int getMaxBearing() {
        return maxBearing;
    }

    public int getMinAdvance() {
        return minAdvance;
    }

    public int getMaxAdvance() {
        return maxAdvance;
    }
}
