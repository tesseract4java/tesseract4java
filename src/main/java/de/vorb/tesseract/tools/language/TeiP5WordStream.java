package de.vorb.tesseract.tools.language;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TeiP5WordStream {
    private static final String TOKEN_DELIMITER =
            " —￼,.\n\r\t\"'〟〃:;#*+!?§$%&/()=\\`„“”‚‘’»«›‹–…";

    private static final Pattern PREFIX_DELIMITER = Pattern.compile("^[\\-]+");

    private static final String[][] REPLACEMENTS = new String[][] {
            new String[] { "¬", "-" },
            new String[] { "aͤ", "ä" },
            new String[] { "oͤ", "ö" },
            new String[] { "uͤ", "ü" },
    };

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

    public void parse(final InputStream xml) throws SAXException, IOException {
        parser.parse(xml, new DefaultHandler() {
            private boolean inTextEnv = false;
            private boolean inInitial = false;
            private String initial = null;

            @Override
            public void startElement(String uri, String localName,
                    String qName, Attributes attributes) throws SAXException {
                if (qName.equals("text")) {
                    inTextEnv = true;
                }

                if (qName.equals("hi")) {
                    final String rendition = attributes.getValue("rendition");
                    if ("#in".equals(rendition)) {
                        inInitial = true;
                    }
                }
            }

            @Override
            public void characters(char[] ch, int start, int length)
                    throws SAXException {
                if (inTextEnv) {
                    final String currentText =
                            String.copyValueOf(ch, start, length);

                    if (inInitial) {
                        initial = currentText;
                    } else {
                        String text;
                        if (initial != null) {
                            text = initial + currentText;
                            initial = null;
                        } else {
                            text = currentText;
                        }

                        for (String[] replacement : REPLACEMENTS) {
                            text = text.replace(replacement[0], replacement[1]);
                        }

                        final StringTokenizer tokenizer = new StringTokenizer(
                                text, TOKEN_DELIMITER);

                        String word;
                        while (tokenizer.hasMoreTokens()) {
                            word = PREFIX_DELIMITER.matcher(
                                    tokenizer.nextToken()).replaceFirst("");
                            if (word.length() > 0) {
                                propagateWord(word);
                            }
                        }
                    }
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName)
                    throws SAXException {
                if (qName.equals("text")) {
                    inTextEnv = false;
                }

                if (qName.equals("hi")) {
                    inInitial = false;
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

                try {
                    xml.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) throws ParserConfigurationException,
            SAXException, IOException {
        final TreeMap<String, Integer> dictionary = new TreeMap<>();

        final TeiP5WordStream stream = new TeiP5WordStream();
        stream.addHandler(new WordStreamHandler() {
            @Override
            public void handleWord(String word) {
                if (!dictionary.containsKey(word)) {
                    dictionary.put(word, 1);
                } else {
                    dictionary.put(word, dictionary.get(word) + 1);
                }
            }

            @Override
            public void handleEndOfWordStream() {
            }
        });

        final Path dir = Paths.get("E:/Masterarbeit/Ressourcen/Wörterbücher/dta_kernkorpus_2014-03-10");
        final Path dictFile = dir.resolve("../dta_kernkorpus_2014-03-10.txt");
        final Path statFile = dir.resolve("../dta_kernkorpus_2014-03-10.dat");
        final DirectoryStream<Path> files = Files.newDirectoryStream(dir);

        for (Path file : files) {
            stream.parse(new BufferedInputStream(Files.newInputStream(file)));
            System.out.println(file.getFileName());
        }

        final BufferedWriter out = Files.newBufferedWriter(dictFile,
                StandardCharsets.UTF_8);

        int totalWords = 0;
        int occurrences = 0;
        for (String word : dictionary.keySet()) {
            occurrences = dictionary.get(word);

            out.write(String.format("%s %d\n", word, occurrences));
            totalWords += occurrences;
        }

        out.close();

        final ArrayList<Integer> values = new ArrayList<>(dictionary.values());
        Collections.sort(values);
        Collections.reverse(values);

        final BufferedWriter stat = Files.newBufferedWriter(statFile,
                StandardCharsets.UTF_8);

        stat.write("occurrences distribution\n# comment\n");
        int i = 1;
        for (int value : values) {
            if (i % 100 == 0 || i == 1) {
                stat.write(String.format("%d %d\n", i, value));
            }

            if (i == 100000) {
                break;
            }

            i++;
        }
        stat.close();

        System.out.println(String.format("Total number of words: %d",
                totalWords));
    }
}
