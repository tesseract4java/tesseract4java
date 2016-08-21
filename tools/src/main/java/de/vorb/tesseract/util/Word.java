package de.vorb.tesseract.util;

import de.vorb.tesseract.util.xml.BaselineAdapter;
import de.vorb.tesseract.util.xml.BoxAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

public class Word {
    @XmlElement(name = "symbol")
    private final List<Symbol> symbols;

    @XmlJavaTypeAdapter(BoxAdapter.class)
    @XmlAttribute(name = "bounding-box")
    private final Box boundingBox;

    private final float conf;

    @XmlJavaTypeAdapter(BaselineAdapter.class)
    @XmlAttribute
    private final Baseline baseline;

    @XmlElement(name = "font-attributes")
    private final FontAttributes fontAttrs;

    private boolean correct = true;

    public Word(List<Symbol> symbols, Box boundingBox, float conf, Baseline baseline,
            FontAttributes fontAttrs) {
        this.symbols = symbols;
        this.boundingBox = boundingBox;
        this.conf = conf;
        this.baseline = baseline;
        this.fontAttrs = fontAttrs;
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    public Box getBoundingBox() {
        return boundingBox;
    }

    @XmlAttribute(name = "confidence")
    public float getConfidence() {
        return conf;
    }

    @XmlAttribute(name = "correct")
    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public Baseline getBaseline() {
        return baseline;
    }

    public FontAttributes getFontAttributes() {
        return fontAttrs;
    }

    public String getText() {
        final StringBuilder text = new StringBuilder();

        for (final Symbol s : symbols) {
            text.append(s.getText());
        }

        return text.toString();
    }
}
