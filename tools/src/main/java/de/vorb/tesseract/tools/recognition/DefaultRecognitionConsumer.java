package de.vorb.tesseract.tools.recognition;

public abstract class DefaultRecognitionConsumer implements RecognitionConsumer {
    private RecognitionState state;

    @Override
    public void setState(RecognitionState state) {
        this.state = state;
    }

    @Override
    public RecognitionState getState() {
        return state;
    }

    @Override
    public void blockBegin() {
    }

    @Override
    public void blockEnd() {
    }

    @Override
    public void paragraphBegin() {
    }

    @Override
    public void paragraphEnd() {
    }

    @Override
    public void lineBegin() {
    }

    @Override
    public void lineEnd() {
    }

    @Override
    public void wordBegin() {
    }

    @Override
    public void wordEnd() {
    }

    @Override
    public void symbol() {
    }
}
