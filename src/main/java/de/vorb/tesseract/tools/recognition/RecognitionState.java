package de.vorb.tesseract.tools.recognition;

import org.bridj.Pointer;

import de.vorb.tesseract.bridj.Tesseract;
import de.vorb.tesseract.bridj.Tesseract.TessPageIteratorLevel;
import de.vorb.tesseract.util.Baseline;
import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.FontAttributes;

public class RecognitionState {
  @SuppressWarnings("unused")
  private final Pointer<Tesseract.TessBaseAPI> apiHandle;
  private final Pointer<Tesseract.TessResultIterator> resultIt;
  private final Pointer<Tesseract.TessPageIterator> pageIt;

  public RecognitionState(Pointer<Tesseract.TessBaseAPI> apiHandle,
      Pointer<Tesseract.TessResultIterator> resultIt,
      Pointer<Tesseract.TessPageIterator> pageIt) {
    this.apiHandle = apiHandle;
    this.resultIt = resultIt;
    this.pageIt = pageIt;
  }

  /**
   * Get the bounding box at the given iterator level.
   * 
   * @param level
   *          level of the requested box
   * @return requested box
   */
  public Box getBoundingBox(TessPageIteratorLevel level) {
    // pointers to the bounding box coordinates
    final Pointer<Integer> left = Pointer.allocateInt();
    final Pointer<Integer> top = Pointer.allocateInt();
    final Pointer<Integer> right = Pointer.allocateInt();
    final Pointer<Integer> bottom = Pointer.allocateInt();

    // get bounding box
    Tesseract.TessPageIteratorBoundingBox(pageIt, level, left, top, right,
        bottom);

    final int x = left.getInt();
    final int y = top.getInt();
    final int width = right.getInt() - x;
    final int height = bottom.getInt() - y;

    return new Box(x, y, width, height);
  }

  /**
   * Get the text content at the given iterator level.
   * 
   * @param level
   *          level of the requested text
   * @return requested text
   */
  public String getText(TessPageIteratorLevel level) {
    return Tesseract.TessResultIteratorGetUTF8Text(resultIt,
        level).getCString();
  }

  /**
   * Get the baseline information of the given iterator level.
   * 
   * @param level
   *          level of the requested baseline
   * @return baseline
   */
  public Baseline getBaseline(TessPageIteratorLevel level) {
    // pointers to the baseline coordinates
    final Pointer<Integer> x1 = Pointer.allocateInt();
    final Pointer<Integer> y1 = Pointer.allocateInt();
    final Pointer<Integer> x2 = Pointer.allocateInt();
    final Pointer<Integer> y2 = Pointer.allocateInt();

    Tesseract.TessPageIteratorBaseline(pageIt, level, x1, y1, x2, y2);

    final int width = x2.getInt() - x1.getInt();
    final float height = y2.getInt() - y1.getInt();
    final float slope = height / width;

    final Box bbox = getBoundingBox(TessPageIteratorLevel.RIL_WORD);
    final int yOffset = bbox.getY() + bbox.getHeight() - y1.getInt();

    return new Baseline(yOffset, slope);
  }

  /**
   * Get the confidence of the given iterator level.
   * 
   * @param level
   *          level of the requested confidence
   * @return recognition confidence
   */
  public float getConfidence(TessPageIteratorLevel level) {
    return Tesseract.TessResultIteratorConfidence(resultIt, level);
  }

  /**
   * @return font attributes for the current word.
   */
  public FontAttributes getWordFontAttributes() {
    final Pointer<Integer> isBold = Pointer.allocateInt();
    final Pointer<Integer> isItalic = Pointer.allocateInt();
    final Pointer<Integer> isUnderlined = Pointer.allocateInt();
    final Pointer<Integer> isMonospace = Pointer.allocateInt();
    final Pointer<Integer> isSerif = Pointer.allocateInt();
    final Pointer<Integer> isSmallcaps = Pointer.allocateInt();
    final Pointer<Integer> fontSize = Pointer.allocateInt();
    final Pointer<Integer> fontID = Pointer.allocateInt();

    Tesseract.TessResultIteratorWordFontAttributes(resultIt, isBold,
        isItalic, isUnderlined, isMonospace, isSerif, isSmallcaps,
        fontSize, fontID);

    return new FontAttributes.Builder().bold(isBold.getInt() > 0).italic(
        isItalic.getInt() > 0).underlined(isUnderlined.getInt() > 0).monospace(
        isMonospace.getInt() > 0).serif(isSerif.getInt() > 0).smallcaps(
        isSmallcaps.getInt() > 0).size(fontSize.getInt()).fontID(
        fontID.getInt()).build();
  }

  // TODO more getters
}
