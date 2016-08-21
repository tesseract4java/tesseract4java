package de.vorb.tesseract.util;

import de.vorb.tesseract.util.xml.BoxAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Collections;
import java.util.List;

public class Paragraph {
    @XmlJavaTypeAdapter(BoxAdapter.class)
    @XmlAttribute(name = "bounding-box")
    private final Box boundingBox;

    @XmlElement(name = "line")
    private final List<Line> lines;

    public Paragraph(Box boundingBox, List<Line> lines) {
        this.boundingBox = boundingBox;
        this.lines = lines;
    }

    public List<Line> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public Box getBoundingBox() {
        return boundingBox;
    }

    @Override
    public String toString() {
        return String.format("Paragraph(boundingBox = %s, lines = [%d])", boundingBox,
                lines.size());
    }
}
