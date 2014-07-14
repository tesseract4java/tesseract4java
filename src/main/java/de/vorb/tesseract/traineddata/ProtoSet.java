package de.vorb.tesseract.traineddata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProtoSet {
    private final int[][][] pruner;
    private final ArrayList<IntProto> protos;

    public ProtoSet(int[][][] pruner, ArrayList<IntProto> protos) {
        this.pruner = pruner;
        this.protos = protos;
    }

    public long getPruner(int x, int y, int z) {
        return pruner[x][y][z] & 0xFFFF_FFFFL;
    }

    public List<IntProto> getProtos() {
        return Collections.unmodifiableList(protos);
    }
}
