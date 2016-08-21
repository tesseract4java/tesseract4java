package de.vorb.tesseract.tools.recognition;

import org.bytedeco.javacpp.tesseract;

import java.io.Closeable;
import java.io.IOException;

public abstract class RecognitionProducer implements Closeable {
    public static final String DEFAULT_TRAINING_FILE = "eng";

    private tesseract.TessBaseAPI handle;
    private String trainingFile = DEFAULT_TRAINING_FILE;

    public RecognitionProducer() {
    }

    public RecognitionProducer(String trainingFile) {
        setTrainingFile(trainingFile);
    }

    public tesseract.TessBaseAPI getHandle() {
        return this.handle;
    }

    public String getTrainingFile() {
        return trainingFile;
    }

    public void setTrainingFile(String trainingFile) {
        this.trainingFile = trainingFile;
    }

    protected void setHandle(tesseract.TessBaseAPI handle) {
        this.handle = handle;
    }

    public abstract void init() throws IOException;

    public abstract void reset() throws IOException;

    public abstract void close() throws IOException;

    @SuppressWarnings("unchecked")
    public void recognize(RecognitionConsumer consumer) {
        // text recognition
        tesseract.TessBaseAPIRecognize(getHandle(), null);

        // get the result iterator
        final tesseract.ResultIterator resultIt =
                tesseract.TessBaseAPIGetIterator(getHandle());

        // get the page iterator
        final tesseract.PageIterator pageIt =
                tesseract.TessResultIteratorGetPageIterator(resultIt);

        // set the recognition state
        consumer.setState(new RecognitionState(handle, resultIt, pageIt));

        boolean inWord = false;

        do {
            // beginning of a symbol
            if (tesseract.TessPageIteratorIsAtBeginningOf(pageIt, tesseract.RIL_SYMBOL)) {

                // beginning of a word
                if (tesseract.TessPageIteratorIsAtBeginningOf(pageIt, tesseract.RIL_WORD)) {

                    // beginning of a text line
                    if (tesseract.TessPageIteratorIsAtBeginningOf(pageIt, tesseract.RIL_TEXTLINE)) {

                        // beginning of a paragraph
                        if (tesseract.TessPageIteratorIsAtBeginningOf(pageIt, tesseract.RIL_PARA)) {

                            // beginning of a block
                            if (tesseract.TessPageIteratorIsAtBeginningOf(pageIt, tesseract.RIL_BLOCK)) {
                                consumer.blockBegin();

                                // handle cancellation
                                if (consumer.isCancelled()) {

                                    // end block
                                    consumer.blockEnd();

                                    // stop iteration
                                    break;
                                }
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
            if (tesseract.TessPageIteratorIsAtFinalElement(pageIt, tesseract.RIL_WORD, tesseract.RIL_SYMBOL)) {

                consumer.wordEnd();

                inWord = false;

                // last word in line
                if (tesseract.TessPageIteratorIsAtFinalElement(pageIt, tesseract.RIL_TEXTLINE, tesseract.RIL_WORD)) {

                    consumer.lineEnd();

                    // last line in paragraph
                    if (tesseract.TessPageIteratorIsAtFinalElement(pageIt, tesseract.RIL_PARA,
                            tesseract.RIL_TEXTLINE)) {

                        consumer.paragraphEnd();

                        // last paragraph in a block
                        if (tesseract.TessPageIteratorIsAtFinalElement(pageIt, tesseract.RIL_BLOCK,
                                tesseract.RIL_PARA)) {
                            consumer.blockEnd();
                        }
                    }
                }
            }
        } while (tesseract.TessPageIteratorNext(pageIt, tesseract.RIL_SYMBOL)); // next symbol

        // tesseract.TessResultIteratorDelete(resultIt);
        // tesseract.TessPageIteratorDelete(pageIt);
    }
}
