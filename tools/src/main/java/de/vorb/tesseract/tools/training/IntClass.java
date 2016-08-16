package de.vorb.tesseract.tools.training;

import de.vorb.tesseract.util.feat.Feature4D;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.vorb.tesseract.tools.training.IntTemplates.PROTOS_PER_PROTO_SET;
import static de.vorb.tesseract.tools.training.IntTemplates.WERDS_PER_CONFIG_VEC;

public class IntClass {
    private final int numProtos;
    private final ArrayList<ProtoSet> protoSets;
    private final byte[] protoLengths;
    private final short[] configLengths;
    private final int fontSetId;

    private IntClass(int numProtos, ArrayList<ProtoSet> protoSets,
            byte[] protoLengths,
            short[] configLengths, int fontSetId) {
        this.numProtos = numProtos;
        this.protoSets = protoSets;
        this.protoLengths = protoLengths;
        this.configLengths = configLengths;
        this.fontSetId = fontSetId;
    }

    public int getNumProtos() {
        return numProtos;
    }

    public List<ProtoSet> getProtoSets() {
        return Collections.unmodifiableList(protoSets);
    }

    public byte[] getProtoLengths() {
        return protoLengths;
    }

    public short[] getConfigLengths() {
        return configLengths;
    }

    public int getFontSetId() {
        return fontSetId;
    }

    public static IntClass readFromBuffer(InputBuffer buf)
            throws IOException {

        // see intproto.cpp@966
        if (!buf.readShort()) {
            throw new IOException("invalid int class header");
        }
        final int numProtos = buf.getShort() & 0xFFFF;

        if (!buf.readByte()) {
            throw new IOException("invalid int class header");
        }
        final int numProtoSets = buf.getByte() & 0xFF;

        if (!buf.readByte()) {
            throw new IOException("invalid int class header");
        }
        final int numConfigs = buf.getByte() & 0xFF;

        // read config lengths
        final short[] configLengths = new short[numConfigs];
        for (int i = 0; i < numConfigs; i++) {
            if (!buf.readShort()) {
                throw new IOException("not enough config lengths");
            }

            configLengths[i] = buf.getShort();
        }

        // read proto lengths
        final byte[] protoLengths = new byte[numProtoSets
                * PROTOS_PER_PROTO_SET];
        for (int i = 0; i < protoLengths.length; i++) {
            if (!buf.readByte()) {
                throw new IOException("not enough proto lengths");
            }

            protoLengths[i] = buf.getByte();
        }

        // read proto sets
        final ArrayList<ProtoSet> protoSets = new ArrayList<>(numProtoSets);
        for (int i = 0; i < numProtoSets; i++) {
            // read pruner
            final int[][][] protoPruner = new int[3][64][2];
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 64; y++) {
                    for (int z = 0; z < 2; z++) {
                        if (!buf.readInt()) {
                            throw new IOException("not enough proto pruners");
                        }

                        protoPruner[x][y][z] = buf.getInt();
                    }
                }
            }

            final ArrayList<Feature4D> protos =
                    new ArrayList<>(PROTOS_PER_PROTO_SET);
            for (int x = 0; x < PROTOS_PER_PROTO_SET; x++) {
                // get prototype information
                if (!buf.readByte())
                    throw new IOException("not enough protos");
                final byte a = buf.getByte();

                if (!buf.readByte())
                    throw new IOException("not enough protos");
                final byte b = buf.getByte();

                if (!buf.readByte())
                    throw new IOException("not enough protos");
                final byte c = buf.getByte();

                if (!buf.readByte())
                    throw new IOException("not enough protos");
                final byte angle = buf.getByte();

                final int[] configs = new int[WERDS_PER_CONFIG_VEC];
                for (int y = 0; y < WERDS_PER_CONFIG_VEC; y++) {
                    if (!buf.readInt())
                        throw new IOException("not enough prototype configs");
                    configs[y] = buf.getInt();
                }

                protos.add(new Feature4D(a, b, c, angle, configs));
            }

            protoSets.add(new ProtoSet(protoPruner, protos));
        }

        if (!buf.readInt())
            throw new IOException("missing font set id");
        final int fontSetId = buf.getInt();

        return new IntClass(numProtos, protoSets, protoLengths, configLengths,
                fontSetId);
    }
}
