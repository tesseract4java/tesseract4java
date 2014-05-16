package de.vorb.tesseract.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import com.sun.xml.internal.txw2.IllegalAnnotationException;

import de.vorb.tesseract.util.xml.PathAdapter;

public class Page {
    @XmlJavaTypeAdapter(PathAdapter.class)
    @XmlAttribute
    private final Path file;

    @XmlAttribute
    private final int width;
    @XmlAttribute
    private final int height;
    @XmlAttribute
    private final int resolution;

    @XmlElement(name = "line")
    private final List<Line> lines;

    /**
     * Creates a new Page.
     * 
     * @param file
     *            original image file
     * @param width
     *            width of the image in pixels
     * @param height
     *            height of the image in pixels
     * @param resolution
     *            resolution of the image in dpi
     * @param lines
     *            list of lines
     */
    public Page(Path file, int width, int height, int resolution,
            List<Line> lines) {
        this.file = file;

        if (width < 1) {
            throw new IllegalArgumentException("width < 1");
        }

        this.width = width;

        if (height < 1) {
            throw new IllegalArgumentException("height < 1");
        }

        this.height = height;

        if (resolution < 1) {
            throw new IllegalAnnotationException("resolution < 1");
        }

        this.resolution = resolution;

        this.lines = lines;
    }

    public Path getFile() {
        return file;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getResolution() {
        return resolution;
    }

    public List<Line> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public void writeTo(OutputStream os)
            throws IOException, JAXBException {
        final JAXBContext jc = JAXBContext.newInstance(Page.class);
        final Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        final JAXBElement<Page> jaxbElement = new JAXBElement<Page>(new QName(
                "page"), Page.class, this);

        marshaller.marshal(jaxbElement, os);
    }
}
