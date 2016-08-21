package de.vorb.tesseract.util;

import de.vorb.tesseract.util.xml.PathAdapter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Page implements Iterable<Symbol> {
    @XmlJavaTypeAdapter(PathAdapter.class)
    @XmlAttribute
    private final Path file;

    @XmlAttribute
    private final int width;
    @XmlAttribute
    private final int height;
    @XmlAttribute
    private final int resolution;

    @XmlElement(name = "block")
    private final List<Block> blocks;

    public Page(Path file, int width, int height, int resolution, List<Block> blocks) {
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
            throw new IllegalArgumentException("resolution < 1");
        }

        this.resolution = resolution;

        this.blocks = blocks;
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

    public List<Block> getBlocks() {
        return Collections.unmodifiableList(blocks);
    }

    public void writeTo(OutputStream w)
            throws IOException, JAXBException {
        final JAXBContext jc = JAXBContext.newInstance(Page.class);
        final Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        final JAXBElement<Page> jaxbElement = new JAXBElement<>(new QName(
                "page"), Page.class, this);

        marshaller.marshal(jaxbElement, w);
    }

    public Iterator<Block> blockIterator() {
        return blocks.iterator();
    }

    public Iterator<Paragraph> paragraphIterator() {
        return new ParagraphIterator(blockIterator());
    }

    public Iterator<Line> lineIterator() {
        return new LineIterator(paragraphIterator());
    }

    public Iterator<Word> wordIterator() {
        return new WordIterator(lineIterator());
    }

    public Iterator<Symbol> symbolIterator() {
        return new SymbolIterator(wordIterator());
    }

    @Override
    public Iterator<Symbol> iterator() {
        return symbolIterator();
    }

    private static class ParagraphIterator implements Iterator<Paragraph> {
        final Iterator<Block> blockIt;
        Iterator<Paragraph> paraIt;

        ParagraphIterator(Iterator<Block> blockIt) {
            this.blockIt = blockIt;
        }

        @Override
        public boolean hasNext() {
            if (paraIt != null && paraIt.hasNext()) {
                return true;
            } else if (!blockIt.hasNext()) {
                return false;
            } else {
                paraIt = blockIt.next().getParagraphs().iterator();

                return paraIt.hasNext();
            }
        }

        @Override
        public Paragraph next() {
            return paraIt.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();

        }
    }

    private static class LineIterator implements Iterator<Line> {
        final Iterator<Paragraph> paraIt;
        Iterator<Line> lineIt;

        LineIterator(Iterator<Paragraph> paraIt) {
            this.paraIt = paraIt;
        }

        @Override
        public boolean hasNext() {
            if (lineIt != null && lineIt.hasNext()) {
                return true;
            } else if (!paraIt.hasNext()) {
                return false;
            } else {
                lineIt = paraIt.next().getLines().iterator();

                return lineIt.hasNext();
            }
        }

        @Override
        public Line next() {
            return lineIt.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static class WordIterator implements Iterator<Word> {
        final Iterator<Line> lineIt;
        Iterator<Word> wordIt;

        WordIterator(Iterator<Line> lineIt) {
            this.lineIt = lineIt;
        }

        @Override
        public boolean hasNext() {
            if (wordIt != null && wordIt.hasNext()) {
                return true;
            } else if (!lineIt.hasNext()) {
                return false;
            } else {
                wordIt = lineIt.next().getWords().iterator();

                return wordIt.hasNext();
            }
        }

        @Override
        public Word next() {
            return wordIt.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static class SymbolIterator implements Iterator<Symbol> {
        final Iterator<Word> wordIt;
        Iterator<Symbol> symbolIt;

        SymbolIterator(Iterator<Word> wordIt) {
            this.wordIt = wordIt;
        }

        @Override
        public boolean hasNext() {
            if (symbolIt != null && symbolIt.hasNext()) {
                return true;
            } else if (!wordIt.hasNext()) {
                return false;
            } else {
                symbolIt = wordIt.next().getSymbols().iterator();

                return symbolIt.hasNext();
            }
        }

        @Override
        public Symbol next() {
            return symbolIt.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
