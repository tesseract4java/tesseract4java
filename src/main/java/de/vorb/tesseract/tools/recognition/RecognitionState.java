package de.vorb.tesseract.tools.recognition;

import org.bridj.Pointer;

import de.vorb.tesseract.LibTess;
import de.vorb.tesseract.PageIteratorLevel;
import de.vorb.tesseract.util.Baseline;
import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.FontAttributes;

public class RecognitionState {
    @SuppressWarnings("unused")
    private final Pointer<LibTess.TessBaseAPI> apiHandle;
    private final Pointer<LibTess.TessResultIterator> resultIt;
    private final Pointer<LibTess.TessPageIterator> pageIt;

    public RecognitionState(Pointer<LibTess.TessBaseAPI> apiHandle,
            Pointer<LibTess.TessResultIterator> resultIt,
            Pointer<LibTess.TessPageIterator> pageIt) {
        this.apiHandle = apiHandle;
        this.resultIt = resultIt;
        this.pageIt = pageIt;
    }

    /**
     * Get the bounding box at the given iterator level.
     * 
     * @param level
     *            level of the requested box
     * @return requested box
     */
    public Box getBoundingBox(PageIteratorLevel level) {
        // pointers to the bounding box coordinates
        final Pointer<Integer> left = Pointer.allocateInt();
        final Pointer<Integer> top = Pointer.allocateInt();
        final Pointer<Integer> right = Pointer.allocateInt();
        final Pointer<Integer> bottom = Pointer.allocateInt();

        // get bounding box
        LibTess.TessPageIteratorBoundingBox(pageIt, level, left, top, right,
                bottom);

        final int x = left.getInt();
        final int y = top.getInt();
        final int width = right.getInt() - x;
        final int height = bottom.getInt() - y;

        Pointer.release(left, top, right, bottom);

        return new Box(x, y, width, height);
    }

    /**
     * Get the text content at the given iterator level.
     * 
     * @param level
     *            level of the requested text
     * @return requested text
     */
    public String getText(PageIteratorLevel level) {
        final Pointer<Byte> pText = LibTess.TessResultIteratorGetUTF8Text(
                resultIt, level);
        final String text = pText.getCString();
        LibTess.TessDeleteText(pText);
        return text;
    }

    /**
     * Get the baseline information of the given iterator level.
     * 
     * @param level
     *            level of the requested baseline
     * @return baseline
     */
    public Baseline getBaseline(PageIteratorLevel level) {
        // pointers to the baseline coordinates
        final Pointer<Integer> x1 = Pointer.allocateInt();
        final Pointer<Integer> y1 = Pointer.allocateInt();
        final Pointer<Integer> x2 = Pointer.allocateInt();
        final Pointer<Integer> y2 = Pointer.allocateInt();

        LibTess.TessPageIteratorBaseline(pageIt, level, x1, y1, x2, y2);

        final int width = x2.getInt() - x1.getInt();
        final float height = y2.getInt() - y1.getInt();
        final float slope = height / width;

        final Box bbox = getBoundingBox(PageIteratorLevel.WORD);
        final int yOffset = bbox.getY() + bbox.getHeight() - y1.getInt();

        Pointer.release(x1, y1, x2, y2);

        return new Baseline(yOffset, slope);
    }

    /**
     * Get the confidence of the given iterator level.
     * 
     * @param level
     *            level of the requested confidence
     * @return recognition confidence
     */
    public float getConfidence(PageIteratorLevel level) {
        return LibTess.TessResultIteratorConfidence(resultIt, level);
    }

    /**
     * @return font attributes for the current word.
     */
    public FontAttributes getWordFontAttributes() {
        // pointers to integers for every attribute
        final Pointer<Integer> isBold = Pointer.allocateInt();
        final Pointer<Integer> isItalic = Pointer.allocateInt();
        final Pointer<Integer> isUnderlined = Pointer.allocateInt();
        final Pointer<Integer> isMonospace = Pointer.allocateInt();
        final Pointer<Integer> isSerif = Pointer.allocateInt();
        final Pointer<Integer> isSmallcaps = Pointer.allocateInt();
        final Pointer<Integer> fontSize = Pointer.allocateInt();
        final Pointer<Integer> fontID = Pointer.allocateInt();

        // set values
        LibTess.TessResultIteratorWordFontAttributes(resultIt, isBold,
                isItalic, isUnderlined, isMonospace, isSerif, isSmallcaps,
                fontSize, fontID);

        // build and return FontAttributes
        final FontAttributes fa = new FontAttributes.Builder().bold(
                isBold.getInt() > 0).italic(isItalic.getInt() > 0).underlined(
                isUnderlined.getInt() > 0).monospace(isMonospace.getInt() > 0).serif(
                isSerif.getInt() > 0).smallcaps(isSmallcaps.getInt() > 0).size(
                fontSize.getInt()).fontID(fontID.getInt()).build();

        Pointer.release(isBold, isItalic, isUnderlined, isMonospace, isSerif,
                isSmallcaps, fontSize, fontID);

        return fa;
    }

    /**
     * @return true if word exists in dictionary
     */
    public boolean isWordFromDictionary() {
        return LibTess.TessResultIteratorWordIsFromDictionary(resultIt) > 0;
    }

    /**
     * @return true if word is numeric
     */
    public boolean isWordNumeric() {
        return LibTess.TessResultIteratorWordIsNumeric(resultIt) > 0;
    }

    /**
     * @return true if current symbol is a dropcap
     */
    public boolean isSymbolDropcap() {
        return LibTess.TessResultIteratorSymbolIsDropcap(resultIt) > 0;
    }

    /**
     * @return true if current symbol is subscript
     */
    public boolean isSymbolSubscript() {
        return LibTess.TessResultIteratorSymbolIsSubscript(resultIt) > 0;
    }

    /**
     * @return true if current symbol is superscript
     */
    public boolean isSymbolSuperscript() {
        return LibTess.TessResultIteratorSymbolIsSuperscript(resultIt) > 0;
    }
}
