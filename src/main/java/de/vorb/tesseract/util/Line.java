package de.vorb.tesseract.util;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.vorb.tesseract.util.xml.BoxAdapter;

public class Line {
    @XmlJavaTypeAdapter(BoxAdapter.class)
    @XmlAttribute(name = "bounding-box")
    private final Box bbox;

    // @XmlAttribute(name = "baseline")
    private final Baseline baseline;

    @XmlElement(name = "word")
    private final List<Word> words;

    public Line(Box bbox, List<Word> words, Baseline baseline) {
        this.bbox = bbox;
        this.words = words;
        this.baseline = baseline;
    }

    public List<Word> getWords() {
        return Collections.unmodifiableList(words);
    }

    public Box getBoundingBox() {
        return bbox;
    }

    public Baseline getBaseline() {
        return baseline;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Line(bbox = " + bbox + ", words = [...], baseline = "
                + baseline
                + ")";
    }
}
