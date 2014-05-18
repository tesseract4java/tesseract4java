package de.vorb.tesseract.util;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.vorb.tesseract.util.xml.BoxAdapter;

/**
 * Recognized Symbol. Can either be a single character or a ligature or
 * otherwise combined glyph.
 * 
 * @author Paul Vorbach
 */
public class Symbol {
    @XmlValue
    private String text;

    @XmlAttribute(name = "bounding-box")
    @XmlJavaTypeAdapter(BoxAdapter.class)
    private Box bbox;

    @XmlAttribute
    private final float confidence;

    /**
     * Creates a new Symbol.
     * 
     * @param text
     *            recognized text
     * @param boundingBox
     *            bounding box
     * @param confidence
     *            recognition confidence
     */
    public Symbol(String text, Box boundingBox, float confidence) {
        this.text = text;
        this.bbox = boundingBox;
        this.confidence = confidence;
    }

    /**
     * @return recognized text
     */
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
    public float getConfidence() {
        return confidence;
    }

    /**
     * @return bounding box
     */
    public Box getBoundingBox() {
        return bbox;
    }

    /**
     * Sets the bounding box.
     * 
     * @param boundingBox
     */
    public void setBoundingBox(Box boundingBox) {
        bbox = boundingBox;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Symbol(" + text + ", bounds = " + bbox + ", conf = "
                + confidence + ")";
    }
}
