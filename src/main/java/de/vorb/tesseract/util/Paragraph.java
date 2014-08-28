package de.vorb.tesseract.util;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.vorb.tesseract.util.xml.BoxAdapter;

public class Paragraph {
    @XmlJavaTypeAdapter(BoxAdapter.class)
    @XmlAttribute(name = "bounding-box")
    private final Box bbox;

    @XmlElement(name = "line")
    private final List<Line> lines;

    public Paragraph(Box bbox, List<Line> lines) {
        this.bbox = bbox;
        this.lines = lines;
    }

    public List<Line> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public Box getBoundingBox() {
        return bbox;
    }

    @Override
    public String toString() {
        return String.format("Paragraph(bbox = %s, lines = [%d])", bbox,
                lines.size());
    }
}
