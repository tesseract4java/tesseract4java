package de.vorb.tesseract.util;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public final class TraineddataFiles {
    private TraineddataFiles() {
    }

    private static final DirectoryStream.Filter<Path> traineddataFilter =
            f -> Files.isRegularFile(f)
                    && f.getFileName().toString().endsWith(
                    ".traineddata");

    /**
     * Lists all available traineddata files in the given directory.
     *
     * @return List of available traineddata files.
     * @throws IOException if the directory does not exist or cannot be read
     */
    public static List<String> getAvailable(Path tessdataDir)
            throws IOException {
        final DirectoryStream<Path> dir = Files.newDirectoryStream(tessdataDir,
                traineddataFilter);

        final LinkedList<String> languages = new LinkedList<>();
        for (final Path languageFile : dir) {
            final String language = languageFile.getFileName().toString().replaceFirst("\\.traineddata$", "");
            languages.add(language);
        }

        return languages;
    }

    /**
     * Lists all available traineddata files in the directory
     * {@code $TESSDATA_PREFIX/tessdata}.
     *
     * @return List of available traineddata files.
     * @throws IOException if the directory does not exist or cannot be read
     */
    public static List<String> getAvailable() throws IOException {
        return getAvailable(getTessdataDir());
    }

    public static Path getTessdataDir() {
        final String tessdataPrefix = System.getenv("TESSDATA_PREFIX");
        Path tessdataDir;
        if (tessdataPrefix != null) {
            tessdataDir = Paths.get(tessdataPrefix).resolve("tessdata");

            if (Files.isDirectory(tessdataDir) && Files.isReadable(tessdataDir)) {
                return tessdataDir;
            }
        }

        tessdataDir = Paths.get("tessdata").toAbsolutePath();

        if (Files.isDirectory(tessdataDir) && Files.isReadable(tessdataDir)) {
            return tessdataDir;
        } else {
            return Paths.get("");
        }
    }
}
