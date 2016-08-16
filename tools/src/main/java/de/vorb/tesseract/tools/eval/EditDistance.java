package de.vorb.tesseract.tools.eval;

import java.io.IOException;
import java.io.Reader;

public abstract class EditDistance {
    public abstract int distance(String a, String b);

    public int distance(Reader a, Reader b) throws IOException {
        final StringBuilder a_ = new StringBuilder();
        final StringBuilder b_ = new StringBuilder();

        int c;
        while ((c = a.read()) != -1) {
            a_.append((char) c);
        }

        while ((c = b.read()) != -1) {
            b_.append((char) c);
        }

        return distance(a_.toString(), b_.toString());
    }
}
