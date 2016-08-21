package de.vorb.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class FileNames {

    private FileNames() {}

    public static Path replaceExtension(Path file, String newExtension) {
        final String filename = file.getFileName().toString();
        final int lastDot = filename.lastIndexOf('.');
        if (file.getParent() == null) {
            return Paths.get(filename.substring(0, lastDot + 1) + newExtension);
        } else {
            return file.getParent().resolve(
                    filename.substring(0, lastDot + 1) + newExtension);
        }
    }
}
