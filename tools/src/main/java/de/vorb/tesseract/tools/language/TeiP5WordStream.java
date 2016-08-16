package de.vorb.tesseract.tools.language;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class TeiP5WordStream {
    private static final String TOKEN_DELIMITER = " \r\n\t";
    private static final Pattern PREFIX_DELIMITER = Pattern.compile("^[\\-]+");
    private static final Pattern NON_TEXT_PATTERN = Pattern.compile(
            "[\\-—￼,.\"'〟〃:;#*+!?§$%&/()=\\\\`„“”‚‘’»«›‹–…‴]+");
    private static final Pattern NON_PUNC_PATTERN = Pattern.compile("[0-9\\p{IsL}\\p{IsDigit}]+");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^((\\d+)[\"'.,:;+\\-*/%?!„“”()\\[\\]<>=]*)+$");

    private static final String[][] REPLACEMENTS = new String[][] {
            new String[] { "¬", "-" },
            new String[] { "aͤ", "ä" },
            new String[] { "oͤ", "ö" },
            new String[] { "uͤ", "ü" },
    };

    private final List<TokenStreamHandler> handlers = new ArrayList<>(1);
    private final SAXParser parser;

    private static final int MIN_FREQ = 20000;
    private static final int MIN_WORDS = 50;

    public TeiP5WordStream()
            throws ParserConfigurationException, SAXException {
        parser = SAXParserFactory.newInstance().newSAXParser();
    }

    public SAXParser getParser() {
        return parser;
    }

    public void addHandler(TokenStreamHandler handler) {
        handlers.add(handler);
    }

    public void removeHandler(TokenStreamHandler handler) {
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
                for (TokenStreamHandler handler : handlers) {
                    handler.handleToken(word);
                }
            }

            @Override
            public void endDocument() throws SAXException {
                for (TokenStreamHandler handler : handlers) {
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
        final Set<String> puncPatterns = new HashSet<>();
        final Set<String> numPatterns = new HashSet<>();

        final TeiP5WordStream tokenStream = new TeiP5WordStream();
        tokenStream.addHandler(new TokenStreamHandler() {
            @Override
            public void handleToken(String token) {
                final String word = NON_TEXT_PATTERN.matcher(token).replaceAll(
                        "");

                // only add words longer than 1
                if (word.length() > 1) {
                    if (!dictionary.containsKey(word)) {
                        dictionary.put(word, 1);
                    } else {
                        dictionary.put(word, dictionary.get(word) + 1);
                    }
                }

                if (!word.equals(token)) {
                    puncPatterns.add(NON_PUNC_PATTERN.matcher(token).replaceAll(
                            " "));
                }

                if (NUMBER_PATTERN.matcher(token).matches()) {
                    numPatterns.add(token.replaceAll("\\d", " "));
                }
            }

            @Override
            public void handleEndOfWordStream() {
            }
        });

        final Path dir = Paths.get("E:/Masterarbeit/Ressourcen/Wörterbücher/dta_kernkorpus_2014-03-10");
        final Path wordListFile = dir.resolve("../dta_kernkorpus_2014-03-10.word-list");
        final Path freqWordListFile = dir.resolve("../dta_kernkorpus_2014-03-10.freq-list");
        final Path numListFile = dir.resolve("../dta_kernkorpus_2014-03-10.number-list");
        final Path puncListFile = dir.resolve("../dta_kernkorpus_2014-03-10.punc-list");
        final Path statFile = dir.resolve("../dta_kernkorpus_2014-03-10.dat");
        final DirectoryStream<Path> files = Files.newDirectoryStream(dir);

        for (Path file : files) {
            tokenStream.parse(new BufferedInputStream(
                    Files.newInputStream(file)));
            System.out.println(file.getFileName());
        }

        final BufferedWriter freqWriter = Files.newBufferedWriter(
                freqWordListFile, StandardCharsets.UTF_8);

        final BufferedWriter wordWriter = Files.newBufferedWriter(wordListFile,
                StandardCharsets.UTF_8);

        int totalWords = 0;
        int occurrences = 0;
        for (String word : dictionary.keySet()) {
            occurrences = dictionary.get(word);

            if (occurrences >= MIN_WORDS) {
                if (occurrences >= MIN_FREQ) {
                    freqWriter.write(String.format("%s %d\n", word, occurrences));
                }

                wordWriter.write(String.format("%s %d\n", word, occurrences));
            }

            totalWords += occurrences;
        }

        freqWriter.close();
        wordWriter.close();

        final BufferedWriter numWriter = Files.newBufferedWriter(numListFile,
                StandardCharsets.UTF_8);
        for (String numPattern : numPatterns) {
            numWriter.write(numPattern);
            numWriter.write('\n');
        }
        numWriter.close();
        numPatterns.clear();

        final BufferedWriter puncWriter = Files.newBufferedWriter(puncListFile,
                StandardCharsets.UTF_8);
        for (String puncPattern : puncPatterns) {
            puncWriter.write(puncPattern);
            puncWriter.write('\n');
        }
        puncWriter.close();
        puncPatterns.clear();

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
