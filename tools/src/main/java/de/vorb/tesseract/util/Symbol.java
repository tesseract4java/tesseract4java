package de.vorb.tesseract.util;

import de.vorb.tesseract.util.xml.BoxAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Collections;
import java.util.List;

/**
 * Recognized Symbol. Can either be a single character or a ligature or
 * otherwise combined glyph.
 *
 * @author Paul Vorbach
 */
public class Symbol {
    private String text;
    private Box boundingBox;
    private final float confidence;
    private final List<AlternativeChoice> alternatives;

    /**
     * Creates a new Symbol.
     *
     * @param text        recognized text
     * @param boundingBox bounding box
     * @param confidence  recognition confidence
     */
    public Symbol(String text, Box boundingBox, float confidence) {
        this.text = text;
        this.boundingBox = boundingBox;
        this.confidence = confidence;
        this.alternatives = Collections.emptyList();
    }

    /**
     * Creates a new Symbol.
     *
     * @param text         recognized text
     * @param boundingBox  bounding box
     * @param confidence   recognition confidence
     * @param alternatives alternative choices
     */
    public Symbol(String text, Box boundingBox, float confidence,
            List<AlternativeChoice> alternatives) {
        this.text = text;
        this.boundingBox = boundingBox;
        this.confidence = confidence;
        this.alternatives = alternatives;
    }

    /**
     * @return recognized text
     */
    @XmlValue
    public String getText() {
        return text;
    }

    /**
     * Sets text.
     *
     * @param text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return recognition confidence
     */
    @XmlAttribute
    public float getConfidence() {
        return confidence;
    }

    /**
     * @return bounding box
     */
    @XmlAttribute(name = "bounding-box")
    @XmlJavaTypeAdapter(BoxAdapter.class)
    public Box getBoundingBox() {
        return boundingBox;
    }

    /**
     * @return alternative choices
     */
    public List<AlternativeChoice> getAlternatives() {
        return alternatives;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Symbol(" + text + ", bounds = " + boundingBox + ", conf = "
                + confidence + ")";
    }
}
