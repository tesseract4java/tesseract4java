package de.vorb.tesseract.util;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class Languages {
    private Languages() {
    }

    private static final DirectoryStream.Filter<Path> traineddataFilter = new DirectoryStream.Filter<Path>() {
        @Override
        public boolean accept(Path f) throws IOException {
            return Files.isRegularFile(f)
                    && f.getFileName().toString().endsWith(".traineddata");
        }
    };

    public static List<String> getLanguageList(Path tessdataDir)
            throws IOException {
        final DirectoryStream<Path> dir = Files.newDirectoryStream(tessdataDir,
                traineddataFilter);

        final LinkedList<String> langs = new LinkedList<>();
        for (final Path langFile : dir) {
            final String lang = langFile.getFileName().toString().replaceFirst(
                    "\\.traineddata$",
                    "");
            langs.add(lang);
        }

        return langs;
    }

    public static List<String> getLanguageList() throws IOException {
        final String tessdataPrefix = System.getenv("TESSDATA_PREFIX");
        final Path tessdataDir = Paths.get(tessdataPrefix).resolve("tessdata");
        return getLanguageList(tessdataDir);
    }
}
