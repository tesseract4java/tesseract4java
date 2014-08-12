package de.vorb.tesseract.tools.language;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TeiP5WordStream {
    private static final String TOKEN_DELIMITER =
            " ,.\n\r\t\"'〟:;#*+!?§$%&/()=\\`„“”‚‘’»«›‹–…";

    private final List<WordStreamHandler> handlers = new ArrayList<>(1);
    private final SAXParser parser;

    public TeiP5WordStream()
            throws ParserConfigurationException, SAXException {
        parser = SAXParserFactory.newInstance().newSAXParser();
    }

    public SAXParser getParser() {
        return parser;
    }

    public void addHandler(WordStreamHandler handler) {
        handlers.add(handler);
    }

    public void removeHandler(WordStreamHandler handler) {
        handlers.remove(handler);
    }

    public void parse(InputStream xml) throws SAXException, IOException {
        parser.parse(xml, new DefaultHandler() {
            private boolean inTextEnv = false;

            @Override
            public void startElement(String uri, String localName,
                    String qName, Attributes attributes) throws SAXException {
                if (qName.equals("text")) {
                    inTextEnv = true;
                }
            }

            @Override
            public void characters(char[] ch, int start, int length)
                    throws SAXException {
                if (inTextEnv) {
                    final StringTokenizer tokenizer = new StringTokenizer(
                            String.copyValueOf(ch, start, length),
                            TOKEN_DELIMITER);

                    while (tokenizer.hasMoreTokens()) {
                        propagateWord(tokenizer.nextToken());
                    }
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName)
                    throws SAXException {
                if (qName.equals("text")) {
                    inTextEnv = false;
                }
            }

            private void propagateWord(String word) {
                for (WordStreamHandler handler : handlers) {
                    handler.handleWord(word);
                }
            }

            @Override
            public void endDocument() throws SAXException {
                for (WordStreamHandler handler : handlers) {
                    handler.handleEndOfWordStream();
                }
            }
        });
    }

    public static void main(String[] args) throws ParserConfigurationException,
            SAXException, IOException {
        final TeiP5WordStream stream = new TeiP5WordStream();
        stream.addHandler(new WordStreamHandler() {
            @Override
            public void handleWord(String word) {
                System.out.println(word);
            }

            @Override
            public void handleEndOfWordStream() {
            }
        });

        stream.parse(Files.newInputStream(Paths.get(
                "E:/Masterarbeit/Ressourcen/Wörterbücher/dta_kernkorpus_2014-03-10/moeser_phantasien01_1775.TEI-P5.xml")));
    }
}
