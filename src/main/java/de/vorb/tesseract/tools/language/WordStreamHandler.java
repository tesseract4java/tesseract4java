package de.vorb.tesseract.tools.language;

public interface WordStreamHandler {
    void handleWord(String word);

    void handleEndOfWordStream();
}
