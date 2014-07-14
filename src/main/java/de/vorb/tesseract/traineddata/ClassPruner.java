package de.vorb.tesseract.traineddata;

import static de.vorb.tesseract.traineddata.IntTemplates.NUM_CP_BUCKETS;
import static de.vorb.tesseract.traineddata.IntTemplates.WERDS_PER_CP_VECTOR;

import java.io.IOException;

public class ClassPruner {
    private final int[][][][] p;

    private ClassPruner(int[][][][] p) {
        this.p = p;
    }

    public long get(int x, int y, int z, int w) {
        return p[x][y][z][w] & 0xFFFF_FFFFL;
    }

    public void set(int x, int y, int z, int w, long value) {
        p[x][y][z][w] = (int) value;
    }

    public static ClassPruner readFromBuffer(ReadableByteBuffer buf)
            throws IOException {
        final int[][][][] p =
                new int[NUM_CP_BUCKETS][NUM_CP_BUCKETS][NUM_CP_BUCKETS][WERDS_PER_CP_VECTOR];

        // read the class pruners
        int x, y, z, w;
        for (x = 0; x < NUM_CP_BUCKETS; x++) {
            for (y = 0; y < NUM_CP_BUCKETS; y++) {
                for (z = 0; z < NUM_CP_BUCKETS; z++) {
                    for (w = 0; w < WERDS_PER_CP_VECTOR; w++) {
                        if (!buf.hasNext(4)) {
                            throw new IOException("end of stream");
                        }

                        final int val = buf.getInt();
                        p[x][y][z][w] = val;
                    }
                }
            }
        }

        return new ClassPruner(p);
    }
}
