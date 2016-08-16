package de.vorb.tesseract.util;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.vorb.tesseract.util.xml.BaselineAdapter;
import de.vorb.tesseract.util.xml.BoxAdapter;

public class Word {
    @XmlElement(name = "symbol")
    private final List<Symbol> symbols;

    @XmlJavaTypeAdapter(BoxAdapter.class)
    @XmlAttribute(name = "bounding-box")
    private final Box bbox;

    private final float conf;

    @XmlJavaTypeAdapter(BaselineAdapter.class)
    @XmlAttribute
    private final Baseline baseline;

    @XmlElement(name = "font-attributes")
    private final FontAttributes fontAttrs;

    private boolean correct = true;

    public Word(List<Symbol> symbols, Box bbox, float conf, Baseline baseline,
            FontAttributes fontAttrs) {
        this.symbols = symbols;
        this.bbox = bbox;
        this.conf = conf;
        this.baseline = baseline;
        this.fontAttrs = fontAttrs;
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    public Box getBoundingBox() {
        return bbox;
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
