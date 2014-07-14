package de.vorb.tesseract.traineddata;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;

import de.vorb.tesseract.LibTess;

public class Traineddata implements AutoCloseable {
    private final SeekableByteChannel byteChannel;
    private final EnumMap<TessdataType, Long> offsetTable;

    private Unicharset unicharset = null;
    private NormProtos normprotos = null;
    private IntTemplates inttemp = null;

    private Traineddata(SeekableByteChannel byteChannel,
            EnumMap<TessdataType, Long> offsetTable) {
        this.byteChannel = byteChannel;
        this.offsetTable = offsetTable;
    }

    public Unicharset getUnicharset() throws IOException {
        if (unicharset == null) {
            final long first = offsetTable.get(TessdataType.UNICHARSET);
            if (first == -1L) {
                return null;
            }
            final long last = getLastByteIndexOf(TessdataType.UNICHARSET);

            final ByteBuffer buf = ByteBuffer.allocate((int) (last - first));
            byteChannel.position(first);
            byteChannel.read(buf);
            final String str = new String(buf.array(), StandardCharsets.UTF_8);

            this.unicharset = new Unicharset(str);
        }

        return unicharset;
    }

    public NormProtos getNormProtos() throws IOException {
        if (normprotos == null) {
            final long first = offsetTable.get(TessdataType.NORMPROTO);
            if (first == -1L) {
                return null;
            }
            final long last = getLastByteIndexOf(TessdataType.NORMPROTO);

        }

        return normprotos;
    }

    public IntTemplates getIntTemplates() throws IOException {
        if (inttemp == null) {
            final long first = offsetTable.get(TessdataType.INTTEMP);
            if (first == -1L) {
                return null;
            }
            final long last = getLastByteIndexOf(TessdataType.INTTEMP);

            final ByteBuffer buf = ByteBuffer.allocate((int) (last - first));
            buf.order(ByteOrder.LITTLE_ENDIAN); // little endian byte order
            byteChannel.position(first);
            byteChannel.read(buf);
            buf.rewind();

            // inttemp = IntTemplates.readFromBuffer(buf);
        }

        return inttemp;
    }

    private long getLastByteIndexOf(TessdataType type) throws IOException {
        for (int i = type.ordinal() + 1; i < TessdataType.size(); i++) {
            final TessdataType nextType = TessdataType.forOrdinal(i);
            final long endOffset = offsetTable.get(nextType);
            if (endOffset != -1L) {
                return endOffset - 1L;
            }
        }

        return byteChannel.size() - 1L;
    }

    @Override
    public void close() throws Exception {
        byteChannel.close();
    }

    public static Traineddata readFrom(Path file) throws IOException {
        final SeekableByteChannel byteChannel = Files.newByteChannel(file);

        // allocate buffer for offset table
        final ByteBuffer bufOffsets =
                ByteBuffer.allocate(4 + 8 * 11);//LibTess.TESSDATA_NUM_ENTRIES);
        bufOffsets.order(ByteOrder.LITTLE_ENDIAN); // offset table has little
                                                   // endian format
        byteChannel.read(bufOffsets);
        bufOffsets.flip();

        final int numEntries = bufOffsets.getInt();

        final EnumMap<TessdataType, Long> offsetTable =
                new EnumMap<TessdataType, Long>(TessdataType.class);
        for (int i = 0; i < numEntries; i++) {
            final TessdataType type = TessdataType.forOrdinal(i);
            offsetTable.put(type, bufOffsets.getLong());
        }

        return new Traineddata(byteChannel, offsetTable);
    }
}
