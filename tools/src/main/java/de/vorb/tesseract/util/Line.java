package de.vorb.tesseract.util;

import de.vorb.tesseract.util.xml.BoxAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Collections;
import java.util.List;

public class Line {
    @XmlJavaTypeAdapter(BoxAdapter.class)
    @XmlAttribute(name = "bounding-box")
    private final Box boundingBox;

    // @XmlAttribute(name = "baseline")
    private final Baseline baseline;

    @XmlElement(name = "word")
    private final List<Word> words;

    public Line(Box boundingBox, List<Word> words, Baseline baseline) {
        this.boundingBox = boundingBox;
        this.words = words;
        this.baseline = baseline;
    }

    public List<Word> getWords() {
        return Collections.unmodifiableList(words);
    }

    public Box getBoundingBox() {
        return boundingBox;
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
        return String.format("Line(boundingBox = %s, words = [%d], baseline = %s)",
                boundingBox, words.size(), baseline);
    }
}
