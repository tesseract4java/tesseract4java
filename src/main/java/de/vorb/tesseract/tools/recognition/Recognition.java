package de.vorb.tesseract.tools.recognition;

import java.io.IOException;

import org.bridj.Pointer;

import de.vorb.tesseract.LibTess;
import de.vorb.tesseract.LibTess.TessBaseAPI;
import de.vorb.tesseract.LibTess.TessPageIterator;
import de.vorb.tesseract.LibTess.TessResultIterator;
import de.vorb.tesseract.PageIteratorLevel;

public abstract class Recognition {
    public static final String LANGUAGE_DEFAULT = "eng";

    private Pointer<TessBaseAPI> handle;
    private String language = LANGUAGE_DEFAULT;

    public Recognition() throws IOException {
        init();
    }

    public Recognition(String initialLanguage) throws IOException {
        setLanguage(initialLanguage);

        init();
    }

    public Pointer<TessBaseAPI> getHandle() {
        return this.handle;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) throws IOException {
        this.language = language;
    }

    protected void setHandle(Pointer<TessBaseAPI> handle) {
        this.handle = handle;
    }

    protected abstract void init() throws IOException;

    public abstract void reset() throws IOException;

    public abstract void close() throws IOException;

    @SuppressWarnings("unchecked")
    public void recognize(RecognitionConsumer consumer) {
        // text recognition
        LibTess.TessBaseAPIRecognize(getHandle(), Pointer.NULL);

        // get the result iterator
        final Pointer<TessResultIterator> resultIt =
                LibTess.TessBaseAPIGetIterator(getHandle());

        // get the page iterator
        final Pointer<TessPageIterator> pageIt =
                LibTess.TessResultIteratorGetPageIterator(resultIt);

        // iterating over symbols
        final PageIteratorLevel level = PageIteratorLevel.SYMBOL;

        // set the recognition state
        consumer.setState(new RecognitionState(handle, resultIt, pageIt));

        boolean inWord = false;

        do {
            // beginning of a symbol
            if (LibTess.TessPageIteratorIsAtBeginningOf(pageIt,
                    level) == LibTess.TRUE) {

                // beginning of a word
                if (LibTess.TessPageIteratorIsAtBeginningOf(pageIt,
                        PageIteratorLevel.WORD) == LibTess.TRUE) {

                    // beginning of a text line
                    if (LibTess.TessPageIteratorIsAtBeginningOf(pageIt,
                            PageIteratorLevel.TEXTLINE) == LibTess.TRUE) {

                        // beginning of a paragraph
                        if (LibTess.TessPageIteratorIsAtBeginningOf(pageIt,
                                PageIteratorLevel.PARA) == LibTess.TRUE) {

                            // beginning of a block
                            if (LibTess.TessPageIteratorIsAtBeginningOf(pageIt,
                                    PageIteratorLevel.BLOCK) == LibTess.TRUE) {
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
            if (LibTess.TessPageIteratorIsAtFinalElement(pageIt,
                    PageIteratorLevel.WORD,
                    PageIteratorLevel.SYMBOL) == LibTess.TRUE) {

                consumer.wordEnd();

                inWord = false;

                // last word in line
                if (LibTess.TessPageIteratorIsAtFinalElement(pageIt,
                        PageIteratorLevel.TEXTLINE,
                        PageIteratorLevel.WORD) == LibTess.TRUE) {

                    consumer.lineEnd();

                    // last line in paragraph
                    if (LibTess.TessPageIteratorIsAtFinalElement(pageIt,
                            PageIteratorLevel.PARA,
                            PageIteratorLevel.TEXTLINE) == LibTess.TRUE) {

                        consumer.paragraphEnd();

                        // last paragraph in a block
                        if (LibTess.TessPageIteratorIsAtFinalElement(pageIt,
                                PageIteratorLevel.BLOCK,
                                PageIteratorLevel.PARA) == LibTess.TRUE) {

                            consumer.blockEnd();

                        }
                    }
                }
            }
        } while (LibTess.TessPageIteratorNext(pageIt, level) > 0); // next symb

        // LibTess.TessResultIteratorDelete(resultIt);
        // LibTess.TessPageIteratorDelete(pageIt);
    }
}
