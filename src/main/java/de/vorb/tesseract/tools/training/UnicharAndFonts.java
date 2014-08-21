package de.vorb.tesseract.tools.training;

import java.io.IOException;

public class UnicharAndFonts {
    private final int unicharId;
    private final int[] fontIds;

    public UnicharAndFonts(int unicharId, int[] fontIds) {
        this.unicharId = unicharId;
        this.fontIds = fontIds;
    }

    public int getUnicharId() {
        return unicharId;
    }

    public int[] getFontIds() {
        return fontIds;
    }

    public static UnicharAndFonts readFrom(InputBuffer buf) throws IOException {
        if (!buf.readInt())
            throw new IOException("invalid input format");
        final int unicharId = buf.getInt();

        if (!buf.readInt())
            throw new IOException("invalid input format");
        final int numOfFonts = buf.getInt();

        final int[] fontIds = new int[numOfFonts];
        for (int i = 0; i < numOfFonts; i++) {
            if (!buf.readInt())
                throw new IOException("invalid input format");
            final int fontId = buf.getInt();
            fontIds[i] = fontId;
        }

        return new UnicharAndFonts(unicharId, fontIds);
    }
}
