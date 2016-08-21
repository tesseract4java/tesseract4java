package de.vorb.tesseract.gui.model;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProjectModel {
    public static final String PROJECT_DIR = "tesseract-project";
    public static final String THUMBNAIL_DIR = "tesseract-project/thumbs";
    public static final String PREPROCESSED_DIR = "tesseract-project/preprocessed";
    public static final String TRANSCRIPTION_DIR = "tesseract-project/transcriptions";
    public static final String OCR_DIR = "tesseract-project/ocr";
    public static final String EVALUATION_DIR = "tesseract-project/evaluation";

    private final String projectName;

    private final Path imageDir;

    private final boolean tiffFiles;
    private final boolean pngFiles;
    private final boolean jpegFiles;

    private final DirectoryStream.Filter<Path> filter = entry -> {
        final String e = entry.getFileName().toString();

        if (png() && e.endsWith(".png")) {
            return true;
        } else if (tiff() && (e.endsWith(".tif") || e.endsWith(".tiff"))) {
            return true;
        } else {
            return jpeg() && (e.endsWith(".jpg") || e.endsWith(".jpeg"));
        }
    };

    public ProjectModel(Path projectDir, boolean tiffFiles, boolean pngFiles, boolean jpegFiles) {
        projectName = projectDir.getFileName().toString();

        this.imageDir = projectDir;

        this.tiffFiles = tiffFiles;
        this.pngFiles = pngFiles;
        this.jpegFiles = jpegFiles;
    }

    public String getProjectName() {
        return projectName;
    }

    public Path getImageDir() {
        return imageDir;
    }

    public Path getProjectDir() {
        return imageDir.resolve(PROJECT_DIR);
    }

    public Path getThumbnailDir() {
        return imageDir.resolve(THUMBNAIL_DIR);
    }

    public Path getPreprocessedDir() {
        return imageDir.resolve(PREPROCESSED_DIR);
    }

    public Path getTranscriptionDir() {
        return imageDir.resolve(TRANSCRIPTION_DIR);
    }

    public Path getOCRDir() {
        return imageDir.resolve(OCR_DIR);
    }

    public Path getEvaluationDir() {
        return imageDir.resolve(EVALUATION_DIR);
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
