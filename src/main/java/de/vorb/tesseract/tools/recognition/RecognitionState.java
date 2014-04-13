package de.vorb.tesseract.tools.recognition;

import org.bridj.Pointer;

import de.vorb.tesseract.bridj.Tesseract;
import de.vorb.tesseract.bridj.Tesseract.TessPageIteratorLevel;
import de.vorb.tesseract.tools.util.Baseline;
import de.vorb.tesseract.tools.util.Box;

public class RecognitionState {
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

  // TODO more getters
}
