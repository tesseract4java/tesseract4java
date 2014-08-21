package de.vorb.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileNames {
    public static Path replaceExtension(Path fileName, String newExtension) {
        final String fname = fileName.toString();
        final int lastDot = fname.lastIndexOf('.');
        return Paths.get(fname.substring(0, lastDot + 1) + newExtension);
    }
}
