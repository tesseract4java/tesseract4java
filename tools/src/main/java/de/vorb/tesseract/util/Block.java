package de.vorb.tesseract.util;

import de.vorb.tesseract.util.xml.BoxAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Collections;
import java.util.List;

public class Block {
    @XmlJavaTypeAdapter(BoxAdapter.class)
    @XmlAttribute(name = "bounding-box")
    private final Box bbox;

    @XmlElement(name = "paragraph")
    private final List<Paragraph> paragraphs;

    public Block(Box bbox, List<Paragraph> paragraphs) {
        this.bbox = bbox;
        this.paragraphs = paragraphs;
    }

    public List<Paragraph> getParagraphs() {
        return Collections.unmodifiableList(paragraphs);
    }

    public Box getBoundingBox() {
        return bbox;
    }

    @Override
    public String toString() {
        return String.format("Block(bbox = %s, paragraphs = [%d])", bbox,
                paragraphs.size());
    }
}
