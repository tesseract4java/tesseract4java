package de.vorb.tesseract.util;

import java.awt.image.BufferedImage;
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

import de.vorb.tesseract.util.xml.PathAdapter;

public class Page {
    @XmlJavaTypeAdapter(PathAdapter.class)
    @XmlAttribute
    private final Path file;

    private final BufferedImage originalImg;
    private final BufferedImage thresholdedImg;

    @XmlElement(name = "line")
    private final List<Line> lines;

    public Page(Path file, BufferedImage originalScan,
            BufferedImage thresholdedImg, List<Line> lines) {
        this.file = file;
        this.originalImg = originalScan;
        this.thresholdedImg = thresholdedImg;
        this.lines = lines;
    }

    public Path getFile() {
        return file;
    }

    public BufferedImage getOriginalImage() {
        return originalImg;
    }

    public BufferedImage getThresholdedImage() {
        return thresholdedImg;
    }

    public List<Line> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public boolean isAscendersEnabled() {
        // only enabled for binary images
        return originalImg.getType() == BufferedImage.TYPE_BYTE_BINARY;
    }

    public static void writeTo(OutputStream os, Page p)
            throws IOException, JAXBException {
        final JAXBContext jc = JAXBContext.newInstance(Page.class);
        final Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        final JAXBElement<Page> jaxbElement = new JAXBElement<Page>(new QName(
                "page"), Page.class, p);

        marshaller.marshal(jaxbElement, os);
    }
}
