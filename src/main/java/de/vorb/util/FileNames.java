package de.vorb.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileNames {
    public static Path replaceExtension(Path file, String newExtension) {
        final String fname = file.getFileName().toString();
        final int lastDot = fname.lastIndexOf('.');
        if (file.getParent() == null) {
            return Paths.get(fname.substring(0, lastDot + 1) + newExtension);
        } else {
            return file.getParent().resolve(
                    fname.substring(0, lastDot + 1) + newExtension);
        }
    }
}
