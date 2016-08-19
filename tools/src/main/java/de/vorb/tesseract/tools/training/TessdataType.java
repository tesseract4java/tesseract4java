package de.vorb.tesseract.tools.training;

public enum TessdataType {
    LANG_CONFIG, // 0
    UNICHARSET, // 1
    AMBIGS, // 2
    INTTEMP, // 3
    PFFMTABLE, // 4
    NORMPROTO, // 5
    PUNC_DAWG, // 6
    SYSTEM_DAWG, // 7
    NUMBER_DAWG, // 8
    FREQ_DAWG, // 9
    FIXED_LENGTH_DAWGS, // 10 // deprecated
    CUBE_UNICHARSET, // 11
    CUBE_SYSTEM_DAWG, // 12
    SHAPE_TABLE, // 13
    BIGRAM_DAWG, // 14
    UNAMBIG_DAWG, // 15
    PARAMS_MODEL; // 16

    private static TessdataType[] values = TessdataType.values();

    /**
     * @param ord ordinal value
     * @return corresponding type.
     */
    public static TessdataType forOrdinal(int ord) {
        if (ord < 0 || ord > 16)
            throw new IllegalArgumentException("ordinal value out of range");

        return values[ord];
    }

    public static int size() {
        return values.length;
    }

    /**
     * @param type Tessdata type.
     * @return <code>true</code> if the type has got a binary encoding. For
     * UTF-8 encoded types, it returns <code>false</code>.
     */
    public static boolean isBinary(TessdataType type) {
        switch (type) {
            case LANG_CONFIG:
            case UNICHARSET:
            case AMBIGS:
            case PFFMTABLE:
            case NORMPROTO:
            case CUBE_UNICHARSET:
            case PARAMS_MODEL:
                return false;
            default:
                return true;
        }
    }
}
