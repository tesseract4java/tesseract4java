package de.vorb.tesseract.tools.recognition;

import java.util.ArrayList;
import java.util.List;

import de.vorb.tesseract.PageIteratorLevel;
import de.vorb.tesseract.util.Block;
import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.Line;
import de.vorb.tesseract.util.Paragraph;
import de.vorb.tesseract.util.Symbol;
import de.vorb.tesseract.util.Word;

public abstract class PageRecognitionConsumer extends
        DefaultRecognitionConsumer {

    private final List<Block> blocks;
    private ArrayList<Paragraph> blockParagraphs;
    private ArrayList<Line> paragraphLines;
    private ArrayList<Word> lineWords;
    private ArrayList<Symbol> wordSymbols;

    public PageRecognitionConsumer(List<Block> blocks) {
        this.blocks = blocks;
    }

    @Override
    public void blockBegin() {
        blockParagraphs = new ArrayList<>();
    }

    @Override
    public void blockEnd() {
        final PageIteratorLevel level = PageIteratorLevel.BLOCK;
        blocks.add(new Block(getState().getBoundingBox(level),
                blockParagraphs));
    }

    @Override
    public void paragraphBegin() {
        paragraphLines = new ArrayList<>();
    }

    @Override
    public void paragraphEnd() {
        final PageIteratorLevel level = PageIteratorLevel.PARA;
        blockParagraphs.add(new Paragraph(getState().getBoundingBox(level),
                paragraphLines));
    }

    @Override
    public void lineBegin() {
        lineWords = new ArrayList<>();
    }

    @Override
    public void lineEnd() {
        final PageIteratorLevel level = PageIteratorLevel.TEXTLINE;
        paragraphLines.add(new Line(getState().getBoundingBox(level),
                lineWords, getState().getBaseline(level)));
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
    }
}
