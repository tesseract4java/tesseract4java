package de.vorb.tesseract.tools.recognition;

import java.io.IOException;

import org.bridj.Pointer;

import de.vorb.tesseract.bridj.Tesseract;
import de.vorb.tesseract.bridj.Tesseract.TessBaseAPI;
import de.vorb.tesseract.bridj.Tesseract.TessPageIterator;
import de.vorb.tesseract.bridj.Tesseract.TessPageIteratorLevel;
import de.vorb.tesseract.bridj.Tesseract.TessResultIterator;

public abstract class Recognition {
  private final Pointer<TessBaseAPI> apiHandle;
  private boolean closed = false;

  public Recognition() throws IOException {
    apiHandle = Tesseract.TessBaseAPICreate();
    init();
  }

  public Pointer<TessBaseAPI> getHandle() {
    return this.apiHandle;
  }

  protected abstract void init() throws IOException;

  @SuppressWarnings("unchecked")
  public void recognize(RecognitionConsumer consumer) {
    if (closed) {
      throw new IllegalStateException("Recognition has been closed.");
    }

    // text recognition
    Tesseract.TessBaseAPIRecognize(getHandle(), Pointer.NULL);

    // get the result iterator
    final Pointer<TessResultIterator> resultIt =
        Tesseract.TessBaseAPIGetIterator(getHandle());

    // get the page iterator
    final Pointer<TessPageIterator> pageIt =
        Tesseract.TessResultIteratorGetPageIterator(resultIt);

    // iterating over symbols
    final TessPageIteratorLevel level =
        Tesseract.TessPageIteratorLevel.RIL_SYMBOL;

    // set the recognition state
    consumer.setState(new RecognitionState(apiHandle, resultIt, pageIt));

    boolean isFirstBlock = true;

    do {
      // beginning of a block
      if (Tesseract.TessPageIteratorIsAtBeginningOf(pageIt,
          TessPageIteratorLevel.RIL_BLOCK) > 0) {
        if (!isFirstBlock) {
          consumer.blockEnd();
        } else {
          isFirstBlock = false;
        }

        consumer.blockBegin();
      }

      // beginning of a paragraph
      if (Tesseract.TessPageIteratorIsAtBeginningOf(pageIt,
          TessPageIteratorLevel.RIL_PARA) > 0) {
        consumer.paragraphBegin();
      }

      // beginning of a text line
      if (Tesseract.TessPageIteratorIsAtBeginningOf(pageIt,
          TessPageIteratorLevel.RIL_TEXTLINE) > 0) {
        consumer.lineBegin();
      }

      // beginning of a word
      if (Tesseract.TessPageIteratorIsAtBeginningOf(pageIt,
          TessPageIteratorLevel.RIL_WORD) > 0) {
        consumer.wordBegin();
      }

      // beginning of a symbol
      if (Tesseract.TessPageIteratorIsAtBeginningOf(pageIt, level) > 0) {
        consumer.symbol();
      }

    } while (Tesseract.TessPageIteratorNext(pageIt, level) > 0); // next symbol

    Tesseract.TessBaseAPIClear(apiHandle);
  }

  public void reset() throws IOException {
    if (closed) {
      throw new IllegalStateException("Recognition has been closed.");
    }

    Tesseract.TessBaseAPIClear(apiHandle);

    init();
  }

  public void close() {
    Tesseract.TessBaseAPIDelete(apiHandle);
    closed = true;
  }
}
