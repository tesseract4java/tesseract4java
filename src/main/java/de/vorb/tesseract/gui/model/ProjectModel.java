package de.vorb.tesseract.gui.model;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProjectModel {
    public static final String PROJECT_DIR = ".tess";
    public static final String THUMBNAIL_DIR = "thumbs";
    public static final String PREPROCESSED_DIR = "preprocessed";

    private final Path imageDir;

    private final boolean tiffFiles;
    private final boolean pngFiles;
    private final boolean jpegFiles;

    private final DirectoryStream.Filter<Path> filter = new Filter<Path>() {
        @Override
        public boolean accept(Path entry) throws IOException {
            final String e = entry.getFileName().toString();

            if (png() && e.endsWith(".png"))
                return true;
            else if (tiff() && (e.endsWith(".tif") || e.endsWith(".tiff")))
                return true;
            else if (jpeg() && (e.endsWith(".jpg") || e.endsWith(".jpeg")))
                return true;
            else
                return false;
        }
    };

    public ProjectModel(Path projectDir, boolean tiffFiles, boolean pngFiles,
            boolean jpegFiles) {
        this.imageDir = projectDir;

        this.tiffFiles = tiffFiles;
        this.pngFiles = pngFiles;
        this.jpegFiles = jpegFiles;
    }

    public Path getImageDir() {
        return imageDir;
    }

    public Path getProjectDir() {
        return imageDir.resolve(PROJECT_DIR);
    }

    public Path getThumbnailDir() {
        return getProjectDir().resolve(THUMBNAIL_DIR);
    }

    public Path getPreprocessedDir() {
        return getProjectDir().resolve(PREPROCESSED_DIR);
    }

    public Iterable<Path> getImageFiles() throws IOException {
        return Files.newDirectoryStream(imageDir, filter);
    }

    public boolean tiff() {
        return tiffFiles;
    }

    public boolean png() {
        return pngFiles;
    }

    public boolean jpeg() {
        return jpegFiles;
    }
}
