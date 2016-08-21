package de.vorb.tesseract.gui.util;

import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class DocumentWriter {

    private DocumentWriter() {}

    private static Transformer transformer;

    static {
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
                    "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        } catch (TransformerConfigurationException
                | TransformerFactoryConfigurationError e) {
            e.printStackTrace();
        }
    }

    public static String writeToString(Document document)
            throws TransformerException {
        final StringWriter result = new StringWriter();
        transformer.transform(new DOMSource(document),
                new StreamResult(result));
        return result.toString();
    }

    public static void writeToFile(Document document, Path file)
            throws IOException, TransformerException {
        final BufferedWriter writer = Files.newBufferedWriter(file,
                StandardCharsets.UTF_8);
        transformer.transform(new DOMSource(document),
                new StreamResult(writer));
        writer.close();
    }
}
