package de.vorb.tesseract.tools.language;


public interface TokenStreamHandler {
    void handleToken(String word);

    void handleEndOfWordStream();
}
