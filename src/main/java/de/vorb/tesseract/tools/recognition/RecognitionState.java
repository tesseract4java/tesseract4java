package de.vorb.tesseract.tools.recognition;

import java.awt.Rectangle;

import org.bridj.Pointer;

import de.vorb.tesseract.bridj.Tesseract;
import de.vorb.tesseract.tools.util.Box;

public class RecognitionState {
  private final Pointer<Tesseract.TessBaseAPI> apiHandle;
  private final Pointer<Tesseract.TessResultIterator> resultIt;
  private final Pointer<Tesseract.TessPageIterator> pageIt;

  // pointers to the bounding box coordinates
  private final Pointer<Integer> left = Pointer.allocateInt();
  private final Pointer<Integer> top = Pointer.allocateInt();
  private final Pointer<Integer> right = Pointer.allocateInt();
  private final Pointer<Integer> bottom = Pointer.allocateInt();

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
  public Box getBoundingBox(Tesseract.TessPageIteratorLevel level) {
    // get bounding box
    Tesseract.TessPageIteratorBoundingBox(pageIt, level, left, top, right,
        bottom);

    final int x = left.getInt();
    final int y = top.getInt();
    final int width = right.getInt() - x;
    final int height = bottom.getInt() - y;

    return new Box(x, y, width, height);
  }
}
