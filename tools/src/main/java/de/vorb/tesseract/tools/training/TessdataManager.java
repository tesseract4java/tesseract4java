package de.vorb.tesseract.tools.training;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TessdataManager {

    private TessdataManager() {}

    private static final String COMMAND = "combine_tessdata";

    public static void extract(Path tessdataFile, Path pathPrefix)
            throws IOException {
        if (!Files.isDirectory(pathPrefix.getParent())) {
            throw new IOException("non-existing destination directory");
        }

        if (!Files.isWritable(pathPrefix.getParent())) {
            throw new IOException("cannot write to destination directory");
        }

        final Process proc = new ProcessBuilder(COMMAND, "-u",
                tessdataFile.toString(), pathPrefix.toString()).start();

        try {
            proc.waitFor();
        } catch (InterruptedException e) {
            throw new IOException("extraction failed");
        }

        if (proc.exitValue() != 0) {
            throw new IOException("extraction failed");
        }
    }
}
