package de.vorb.tesseract.tools.visualization;

import de.vorb.tesseract.tools.training.IntClass;
import de.vorb.tesseract.tools.training.ProtoSet;
import de.vorb.tesseract.util.feat.Feature4D;

import java.awt.geom.Line2D;

import static de.vorb.tesseract.tools.training.IntTemplates.BITS_PER_WERD;
import static de.vorb.tesseract.tools.training.IntTemplates.INT_CHAR_NORM_RANGE;
import static de.vorb.tesseract.tools.training.IntTemplates.NUM_PP_BUCKETS;
import static de.vorb.tesseract.tools.training.IntTemplates.PICO_FEATURE_LENGTH;
import static de.vorb.tesseract.tools.training.IntTemplates.PROTOS_PER_PROTO_SET;
import static de.vorb.tesseract.tools.training.IntTemplates.PROTO_PRUNER_SCALE;

public class PrototypeRenderer {
    public static void updateLine(Line2D l2d, IntClass ic, int id) {
        final ProtoSet protoSet =
                ic.getProtoSets().get(id / PROTOS_PER_PROTO_SET);
        final int protoSetIndex = id % PROTOS_PER_PROTO_SET;
        final Feature4D proto = protoSet.getProtos().get(protoSetIndex);
        final float length = (ic.getProtoLengths()[id] & 0xFF)
                * PICO_FEATURE_LENGTH * INT_CHAR_NORM_RANGE;

        final int protoMask = 1 << id % BITS_PER_WERD;
        final int protoWordIndex = protoSetIndex / BITS_PER_WERD; // TODO

        int xmin = NUM_PP_BUCKETS;
        int ymin = NUM_PP_BUCKETS;
        int xmax = 0;
        int ymax = 0;

        for (int bucket = 0; bucket < NUM_PP_BUCKETS; bucket++) {
            // x coord
            if ((protoMask & protoSet.getPruner(0, bucket,
                    protoWordIndex)) != 0) {
                xmin = Math.min(bucket, xmin);
                xmax = Math.max(bucket, xmax);
            }

            // y coord
            if ((protoMask & protoSet.getPruner(1, bucket,
                    protoWordIndex)) != 0) {
                ymin = Math.min(bucket, ymin);
                ymax = Math.max(bucket, ymax);
            }
        }

        final float x = (xmin + xmax + 1) / 2.0f * PROTO_PRUNER_SCALE;
        final float y = (ymin + ymax + 1) / 2.0f * PROTO_PRUNER_SCALE;

        final float dx = (length / 2.0f)
                * (float) Math.cos((proto.getAngle() / 256.0) * 2.0 * Math.PI
                - Math.PI);
        final float dy = (length / 2.0f)
                * (float) Math.sin((proto.getAngle() / 256.0) * 2.0 * Math.PI
                - Math.PI);

        l2d.setLine(x - dx, 256 - y + dy, x + dx, 256 - y - dy);
    }
}
