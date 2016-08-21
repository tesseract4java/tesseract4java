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
    private final Box boundingBox;

    @XmlElement(name = "paragraph")
    private final List<Paragraph> paragraphs;

    public Block(Box boundingBox, List<Paragraph> paragraphs) {
        this.boundingBox = boundingBox;
        this.paragraphs = paragraphs;
    }

    public List<Paragraph> getParagraphs() {
        return Collections.unmodifiableList(paragraphs);
    }

    public Box getBoundingBox() {
        return boundingBox;
    }

    @Override
    public String toString() {
        return String.format("Block(boundingBox = %s, paragraphs = [%d])", boundingBox,
                paragraphs.size());
    }
}
