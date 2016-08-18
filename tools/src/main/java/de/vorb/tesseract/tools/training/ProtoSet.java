package de.vorb.tesseract.tools.training;

import de.vorb.tesseract.util.feat.Feature4D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProtoSet {
    private final int[][][] pruner;
    private final ArrayList<Feature4D> protos;

    public ProtoSet(int[][][] pruner, ArrayList<Feature4D> protos) {
        this.pruner = pruner;
        this.protos = protos;
    }

    public long getPruner(int x, int y, int z) {
        return pruner[x][y][z] & 0xFFFF_FFFFL;
    }

    public List<Feature4D> getProtos() {
        return Collections.unmodifiableList(protos);
    }
}
