package de.vorb.tesseract.tools.training;

import java.io.IOException;
import java.util.ArrayList;

public class ShapeTable {
    public static ShapeTable readFrom(InputBuffer buf) throws IOException {
        if (!buf.readInt())
            throw new IOException("invalid input format");
        final int size = buf.getInt();

        System.out.println(size);

        final ArrayList<Shape> table = new ArrayList<>((int) size);
        for (int i = 0; i < size; i++) {
            if (!buf.readByte())
                throw new IOException("invalid input format");

            if (buf.getByte() != 0)
                table.add(Shape.readFrom(buf));
            else
                table.add(null);
        }

        return new ShapeTable();
    }
}
