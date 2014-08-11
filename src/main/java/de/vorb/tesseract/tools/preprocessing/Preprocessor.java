package de.vorb.tesseract.tools.preprocessing;

import java.awt.image.BufferedImage;

public interface Preprocessor {
    BufferedImage process(BufferedImage image);
}
