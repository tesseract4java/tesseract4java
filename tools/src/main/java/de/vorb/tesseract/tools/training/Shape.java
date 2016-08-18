package de.vorb.tesseract.tools.training;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Shape {
    private final boolean sorted;
    private final ArrayList<UnicharAndFonts> uaf;

    public Shape(boolean sorted, ArrayList<UnicharAndFonts> uaf) {
        this.sorted = sorted;
        this.uaf = uaf;
    }

    public boolean isSorted() {
        return sorted;
    }

    public List<UnicharAndFonts> getUnicharAndFonts() {
        return Collections.unmodifiableList(uaf);
    }

    public static Shape readFrom(InputBuffer buf) throws IOException {
        if (!buf.readByte())
            throw new IOException("invalid input format");
        final boolean sorted = buf.getByte() != 0;

        if (!buf.readInt())
            throw new IOException("invalid input format");
        final int size = buf.getInt();

        final ArrayList<UnicharAndFonts> uaf = new ArrayList<>(size);
        // read data
        for (int i = 0; i < size; i++) {
            uaf.add(UnicharAndFonts.readFrom(buf));
        }

        return new Shape(sorted, uaf);
    }
}
