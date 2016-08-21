package de.vorb.tesseract.tools.recognition;

import de.vorb.tesseract.util.Block;
import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.Line;
import de.vorb.tesseract.util.Paragraph;
import de.vorb.tesseract.util.Symbol;
import de.vorb.tesseract.util.Word;

import org.bytedeco.javacpp.tesseract;

import java.util.ArrayList;
import java.util.List;

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
        final int level = tesseract.RIL_BLOCK;
        blocks.add(new Block(getState().getBoundingBox(level),
                blockParagraphs));
    }

    @Override
    public void paragraphBegin() {
        paragraphLines = new ArrayList<>();
    }

    @Override
    public void paragraphEnd() {
        final int level = tesseract.RIL_PARA;
        blockParagraphs.add(new Paragraph(getState().getBoundingBox(level),
                paragraphLines));
    }

    @Override
    public void lineBegin() {
        lineWords = new ArrayList<>();
    }

    @Override
    public void lineEnd() {
        final int level = tesseract.RIL_TEXTLINE;
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
        final int level = tesseract.RIL_WORD;
        final Box boundingBox = state.getBoundingBox(level);
        lineWords.add(new Word(wordSymbols, boundingBox,
                state.getConfidence(level),
                state.getBaseline(tesseract.RIL_WORD),
                state.getWordFontAttributes()));
    }

    @Override
    public void symbol() {
        final int level = tesseract.RIL_SYMBOL;
        wordSymbols.add(new Symbol(
                getState().getText(level),
                getState().getBoundingBox(level),
                getState().getConfidence(level),
                getState().getAlternatives()));
    }
}
