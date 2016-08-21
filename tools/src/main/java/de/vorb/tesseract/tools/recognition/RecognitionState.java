package de.vorb.tesseract.tools.recognition;

import de.vorb.tesseract.util.AlternativeChoice;
import de.vorb.tesseract.util.Baseline;
import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.FontAttributes;

import org.bytedeco.javacpp.BoolPointer;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.lept;
import org.bytedeco.javacpp.tesseract;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RecognitionState {

    private final tesseract.TessBaseAPI apiHandle;
    private final tesseract.ResultIterator resultIt;
    private final tesseract.PageIterator pageIt;

    public RecognitionState(tesseract.TessBaseAPI apiHandle, tesseract.ResultIterator resultIt,
            tesseract.PageIterator pageIt) {
        this.apiHandle = apiHandle;
        this.resultIt = resultIt;
        this.pageIt = pageIt;
    }

    /**
     * Get the bounding box at the given iterator level.
     *
     * @param level level of the requested box
     * @return requested box
     */
    public Box getBoundingBox(int level) {
        try (final IntPointer left = new IntPointer(1);
             final IntPointer top = new IntPointer(1);
             final IntPointer right = new IntPointer(1);
             final IntPointer bottom = new IntPointer(1)) {

            tesseract.TessPageIteratorBoundingBox(pageIt, level, left, top, right, bottom);

            final int x = left.get();
            final int y = top.get();
            final int width = right.get() - x;
            final int height = bottom.get() - y;

            return new Box(x, y, width, height);
        }
    }

    /**
     * Get the text content at the given iterator level.
     *
     * @param level level of the requested text
     * @return requested text
     */
    public String getText(int level) {
        final BytePointer pText = tesseract.TessResultIteratorGetUTF8Text(resultIt, level);
        final String text = new String(pText.getStringBytes(), StandardCharsets.UTF_8);
        tesseract.TessDeleteText(pText);
        return text;
    }

    /**
     * Get the baseline information of the given iterator level.
     *
     * @param level level of the requested baseline
     * @return baseline
     */
    public Baseline getBaseline(int level) {
        try (final IntPointer x1 = new IntPointer(1);
             final IntPointer y1 = new IntPointer(1);
             final IntPointer x2 = new IntPointer(1);
             final IntPointer y2 = new IntPointer(1)) {

            tesseract.TessPageIteratorBaseline(pageIt, level, x1, y1, x2, y2);

            final int width = x2.get() - x1.get();
            final float height = y2.get() - y1.get();
            final float slope = height / width;

            final Box boundingBox = getBoundingBox(tesseract.RIL_WORD);
            final int yOffset = boundingBox.getY() + boundingBox.getHeight() - y1.get();

            return new Baseline(yOffset, slope);
        }
    }

    /**
     * Get the confidence of the given iterator level.
     *
     * @param level level of the requested confidence
     * @return recognition confidence
     */
    public float getConfidence(int level) {
        return tesseract.TessResultIteratorConfidence(resultIt, level);
    }

    public List<AlternativeChoice> getAlternatives() {
        final List<AlternativeChoice> alternatives = new ArrayList<>();

        final tesseract.ChoiceIterator choiceIt = tesseract.TessResultIteratorGetChoiceIterator(resultIt);

        // pull out all choices
        do {
            final BytePointer choice = tesseract.TessChoiceIteratorGetUTF8Text(choiceIt);
            final float conf = tesseract.TessChoiceIteratorConfidence(choiceIt);

            alternatives.add(new AlternativeChoice(new String(choice.getStringBytes(), StandardCharsets.UTF_8), conf));
        } while (tesseract.TessChoiceIteratorNext(choiceIt));

        return alternatives;
    }

    private void getSymbolFeatures(lept.PIX image) {

        try (final IntPointer left = new IntPointer(1);
             final IntPointer top = new IntPointer(1);
             final IntPointer numFeatures = new IntPointer(1);
             final IntPointer featOutlineIndex = new IntPointer(1);
             final tesseract.INT_FEATURE_STRUCT intFeatures = new tesseract.INT_FEATURE_STRUCT(
                     new BytePointer(4 * 512))) {

            final lept.PIX pix = tesseract.TessPageIteratorGetImage(pageIt, tesseract.RIL_SYMBOL, 1, image, left, top);
            final tesseract.TBLOB blob = tesseract.TessMakeTBLOB(pix);

            tesseract.TessBaseAPIGetFeaturesForBlob(apiHandle, blob, intFeatures, numFeatures, featOutlineIndex);
        }
    }

    /**
     * @return font attributes for the current word.
     */
    public FontAttributes getWordFontAttributes() {

        try (final BoolPointer isBold = new BoolPointer(1);
             final BoolPointer isItalic = new BoolPointer(1);
             final BoolPointer isUnderlined = new BoolPointer(1);
             final BoolPointer isMonospace = new BoolPointer(1);
             final BoolPointer isSerif = new BoolPointer(1);
             final BoolPointer isSmallCaps = new BoolPointer(1);
             final IntPointer fontSize = new IntPointer(1);
             final IntPointer fontID = new IntPointer(1)) {

            // set values
            tesseract.TessResultIteratorWordFontAttributes(resultIt, isBold, isItalic, isUnderlined, isMonospace,
                    isSerif,
                    isSmallCaps, fontSize, fontID);

            // build and return FontAttributes

            return new FontAttributes.Builder()
                    .bold(isBold.get())
                    .italic(isItalic.get())
                    .underlined(isUnderlined.get())
                    .monospace(isMonospace.get())
                    .serif(isSerif.get())
                    .smallCaps(isSmallCaps.get())
                    .size(fontSize.get())
                    .fontID(fontID.get())
                    .build();
        }
    }

    /**
     * @return true if word exists in dictionary
     */
    public boolean isWordFromDictionary() {
        return tesseract.TessResultIteratorWordIsFromDictionary(resultIt);
    }

    /**
     * @return true if word is numeric
     */
    public boolean isWordNumeric() {
        return tesseract.TessResultIteratorWordIsNumeric(resultIt);
    }

    /**
     * @return true if current symbol is a drop cap
     */
    public boolean isSymbolDropCap() {
        return tesseract.TessResultIteratorSymbolIsDropcap(resultIt);
    }

    /**
     * @return true if current symbol is subscript
     */
    public boolean isSymbolSubscript() {
        return tesseract.TessResultIteratorSymbolIsSubscript(resultIt);
    }

    /**
     * @return true if current symbol is superscript
     */
    public boolean isSymbolSuperscript() {
        return tesseract.TessResultIteratorSymbolIsSuperscript(resultIt);
    }
}
