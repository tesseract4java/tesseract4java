package de.vorb.tesseract.tools.recognition;

/**
 * Consumes the OCR results of Tesseract.
 *
 * @author Paul Vorbach
 */
public interface RecognitionConsumer {
    /**
     * @param state state of the recognition process
     */
    void setState(RecognitionState state);

    /**
     * @return current state of the recognition process
     */
    RecognitionState getState();

    /**
     * Beginning of a block.
     */
    void blockBegin();

    /**
     * End of a block.
     */
    void blockEnd();

    /**
     * Beginning of a paragraph.
     */
    void paragraphBegin();

    /**
     * End of a paragraph.
     */
    void paragraphEnd();

    /**
     * Beginning of a text line.
     */
    void lineBegin();

    /**
     * End of a text line.
     */
    void lineEnd();

    /**
     * Beginning of a word.
     */
    void wordBegin();

    /**
     * End of a word.
     */
    void wordEnd();

    /**
     * Symbol within a word.
     */
    void symbol();

    /**
     * Provides cancellation information for the recognition provider.
     *
     * @return {@code true} if the task shall be cancelled, {@code false}
     * otherwise.
     */
    boolean isCancelled();
}
