package de.vorb.tesseract.tools.recognition;

import java.util.ArrayList;
import java.util.Vector;

import de.vorb.tesseract.PageIteratorLevel;
import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.Line;
import de.vorb.tesseract.util.Symbol;
import de.vorb.tesseract.util.Word;

public abstract class PageRecognitionConsumer extends
        DefaultRecognitionConsumer {
    private final Vector<Line> lines;
    private ArrayList<Word> lineWords;
    private ArrayList<Symbol> wordSymbols;

    public PageRecognitionConsumer(Vector<Line> lines) {
        this.lines = lines;
    }

    @Override
    public void lineBegin() {
        lineWords = new ArrayList<>();
    }

    @Override
    public void lineEnd() {
        final PageIteratorLevel level = PageIteratorLevel.TEXTLINE;
        lines.add(new Line(getState().getBoundingBox(level), lineWords,
                getState().getBaseline(level)));
    }

    @Override
    public void wordBegin() {
        wordSymbols = new ArrayList<>();
    }

    @Override
    public void wordEnd() {
        final RecognitionState state = getState();
        final PageIteratorLevel level = PageIteratorLevel.WORD;
        final Box bbox = state.getBoundingBox(level);
        lineWords.add(new Word(wordSymbols, bbox,
                state.getConfidence(level),
                state.getBaseline(PageIteratorLevel.WORD),
                state.getWordFontAttributes()));
    }

    @Override
    public void symbol() {
        final PageIteratorLevel level = PageIteratorLevel.SYMBOL;
        wordSymbols.add(new Symbol(getState().getText(level),
                getState().getBoundingBox(level),
                getState().getConfidence(level)));

        // Optional<Pointer<Pix>> img = producer.getImage();
        // if (img.isPresent()) {
        // getState().getSymbolFeatures(img.get());
        // }
    }
}
