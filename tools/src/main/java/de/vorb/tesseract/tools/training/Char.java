package de.vorb.tesseract.tools.training;


public class Char {
    private final String text;
    private final CharacterProperties props;
    private final CharacterDimensions dims;
    private final String script;
    private final int otherCase;
    private final int dir;
    private final int mirror;
    private final String normed;

    public Char(String text, CharacterProperties properties,
            CharacterDimensions dimensions, String script, int otherCase,
            int direction,
            int mirror, String normed) {
        this.text = text;
        this.props = properties;
        this.dims = dimensions;
        this.script = script;
        this.otherCase = otherCase;
        this.dir = direction;
        this.mirror = mirror;
        this.normed = normed;
    }

    public Char(String text, CharacterProperties properties, String script, int otherCase) {
        this(text, properties, CharacterDimensions.DEFAULT, script, otherCase, 0, 0, "");
    }

    public String getText() {
        return text;
    }

    public CharacterProperties getProperties() {
        return props;
    }

    public CharacterDimensions getDimensions() {
        return dims;
    }

    public String getScript() {
        return script;
    }

    public int getOtherCase() {
        return otherCase;
    }

    public int getDirection() {
        return dir;
    }

    public int getMirror() {
        return mirror;
    }

    public String getNormed() {
        return normed;
    }

    @Override
    public String toString() {
        if (text.equals(" ")) {
            return "NULL 0 NULL 0";
        } else {
            return String.format(
                    "%s %d %d,%d,%d,%d,%d,%d,%d,%d,%d,%d %s %d %d %d %s",
                    text, props.toByteCode(), dims.getMinBottom(),
                    dims.getMaxBottom(), dims.getMinTop(), dims.getMaxTop(),
                    dims.getMinWidth(), dims.getMaxWidth(),
                    dims.getMinBearing(), dims.getMaxBearing(),
                    dims.getMinAdvance(), dims.getMaxAdvance(), script,
                    otherCase, dir, mirror, normed);
        }
    }
}
