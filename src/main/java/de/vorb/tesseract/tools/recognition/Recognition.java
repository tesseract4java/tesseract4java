package de.vorb.tesseract.tools.recognition;

import java.io.IOException;

import org.bridj.Pointer;

import de.vorb.tesseract.bridj.Tesseract;
import de.vorb.tesseract.bridj.Tesseract.TessBaseAPI;
import de.vorb.tesseract.bridj.Tesseract.TessPageIterator;
import de.vorb.tesseract.bridj.Tesseract.TessPageIteratorLevel;
import de.vorb.tesseract.bridj.Tesseract.TessResultIterator;

public abstract class Recognition {
  private final Pointer<TessBaseAPI> handle;
  private boolean closed = false;

  public Recognition() throws IOException {
    handle = Tesseract.TessBaseAPICreate();
    init();
  }

  public Pointer<TessBaseAPI> getHandle() {
    return this.handle;
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
    consumer.setState(new RecognitionState(handle, resultIt, pageIt));

    boolean inWord = false;

    do {
      // beginning of a symbol
      if (Tesseract.TessPageIteratorIsAtBeginningOf(pageIt,
          level) > 0) {

        // beginning of a word
        if (Tesseract.TessPageIteratorIsAtBeginningOf(pageIt,
            TessPageIteratorLevel.RIL_WORD) > 0) {

          // beginning of a text line
          if (Tesseract.TessPageIteratorIsAtBeginningOf(pageIt,
              TessPageIteratorLevel.RIL_TEXTLINE) > 0) {

            // beginning of a paragraph
            if (Tesseract.TessPageIteratorIsAtBeginningOf(pageIt,
                TessPageIteratorLevel.RIL_PARA) > 0) {

              // beginning of a block
              if (Tesseract.TessPageIteratorIsAtBeginningOf(pageIt,
                  TessPageIteratorLevel.RIL_BLOCK) > 0) {
                consumer.blockBegin();
              }

              consumer.paragraphBegin();
            }

            consumer.lineBegin();
          }

          consumer.wordBegin();

          inWord = true;
        }

        consumer.symbol();
      }

      if (!inWord) {
        continue;
      }

      // last symbol in word
      if (Tesseract.TessPageIteratorIsAtFinalElement(pageIt,
          TessPageIteratorLevel.RIL_WORD,
          TessPageIteratorLevel.RIL_SYMBOL) > 0) {

        consumer.wordEnd();

        inWord = false;

        // last word in line
        if (Tesseract.TessPageIteratorIsAtFinalElement(pageIt,
            TessPageIteratorLevel.RIL_TEXTLINE,
            TessPageIteratorLevel.RIL_WORD) > 0) {

          consumer.lineEnd();

          // last line in paragraph
          if (Tesseract.TessPageIteratorIsAtFinalElement(pageIt,
              TessPageIteratorLevel.RIL_PARA,
              TessPageIteratorLevel.RIL_TEXTLINE) > 0) {

            consumer.paragraphEnd();

            // last paragraph in a block
            if (Tesseract.TessPageIteratorIsAtFinalElement(pageIt,
                TessPageIteratorLevel.RIL_BLOCK,
                TessPageIteratorLevel.RIL_PARA) > 0) {

              consumer.blockEnd();

            }
          }
        }
      }
    } while (Tesseract.TessPageIteratorNext(pageIt, level) > 0); // next symbol
  }

  public void reset() throws IOException {
    if (closed) {
      throw new IllegalStateException("Recognition has been closed.");
    }

    init();
  }

  public void close() {
    Tesseract.TessBaseAPIDelete(handle);
    closed = true;
  }
}
