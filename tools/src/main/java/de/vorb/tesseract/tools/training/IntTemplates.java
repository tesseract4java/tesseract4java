package de.vorb.tesseract.tools.training;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IntTemplates {
    public static final float PICO_FEATURE_LENGTH = 0.05f;
    public static final float PROTO_PRUNER_SCALE = 4.0f;
    public static final int INT_CHAR_NORM_RANGE = 256;

    public static final int MAX_NUM_CONFIGS = 64;
    public static final int NUM_CP_BUCKETS = 24;
    public static final int NUM_PP_BUCKETS = 64;
    public static final int CLASSES_PER_CP = 32;
    public static final int NUM_BITS_PER_CLASS = 2;
    public static final int BITS_PER_WERD = 32;
    public static final int BITS_PER_CP_VECTOR =
            CLASSES_PER_CP * NUM_BITS_PER_CLASS;
    public static final int WERDS_PER_CP_VECTOR =
            BITS_PER_CP_VECTOR / BITS_PER_WERD;
    public static final int PROTOS_PER_PROTO_SET = 64;
    public static final int WERDS_PER_CONFIG_VEC =
            (MAX_NUM_CONFIGS + BITS_PER_WERD - 1) / BITS_PER_WERD;

    private final ArrayList<IntClass> classes;
    private final ArrayList<ClassPruner> pruners;

    private IntTemplates(ArrayList<IntClass> classes,
            ArrayList<ClassPruner> pruners) {
        this.classes = classes;
        this.pruners = pruners;
    }

    public List<IntClass> getClasses() {
        return Collections.unmodifiableList(classes);
    }

    public List<ClassPruner> getClassPruners() {
        return Collections.unmodifiableList(pruners);
    }

    public static IntTemplates readFrom(InputBuffer buf)
            throws IOException {
        if (!buf.readInt())
            throw new IOException("invalid header");
        // only needed by older formats
        @SuppressWarnings({"unused", "UnusedAssignment"})
        final int unicharsetSize = buf.getInt();

        if (!buf.readInt())
            throw new IOException("invalid header");
        int numClasses = buf.getInt();

        if (!buf.readInt())
            throw new IOException("invalid header");
        final int numPruners = buf.getInt();

        final int versionId;
        if (numClasses < 0) {
            // this file has a version id!
            versionId = -numClasses;

            if (!buf.readInt())
                throw new IOException("invalid header");
            numClasses = buf.getInt();
        } else {
            versionId = 0;
        }

        if (versionId < 4) {
            throw new IOException(String.format(
                    "unsupported inttemp format version '%d'", versionId));
        }

        // read pruners
        final ArrayList<ClassPruner> pruners = new ArrayList<>(numPruners);
        for (int i = 0; i < numPruners; i++) {
            pruners.add(ClassPruner.readFromBuffer(buf));
        }

        // read classes
        final ArrayList<IntClass> classes = new ArrayList<>(numClasses);
        for (int i = 0; i < numClasses; i++) {
            classes.add(IntClass.readFromBuffer(buf));
        }

        return new IntTemplates(classes, pruners);
    }
}
