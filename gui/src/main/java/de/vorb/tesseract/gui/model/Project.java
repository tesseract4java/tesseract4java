package de.vorb.tesseract.gui.model;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Project {

    private static final String PROJECT_DIR = "tesseract-project";
    private static final String THUMBNAIL_DIR = "tesseract-project/thumbs";
    private static final String PREPROCESSED_DIR = "tesseract-project/preprocessed";
    private static final String TRANSCRIPTION_DIR = "tesseract-project/transcriptions";
    private static final String OCR_DIR = "tesseract-project/ocr";
    private static final String EVALUATION_DIR = "tesseract-project/evaluation";

    private final String name;

    private final Path imageDir;

    private final DirectoryStream.Filter<Path> filter;

    public Project(Path projectDir, boolean includeTiffFiles, boolean includePngFiles, boolean includeJpegFiles) {

        name = projectDir.getFileName().toString();

        this.imageDir = projectDir;

        //noinspection Convert2Lambda
        this.filter = new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(Path entry) {
                final String fileName = entry.getFileName().toString();

                if (includePngFiles && fileName.endsWith(".png")) {
                    return true;
                } else if (includeTiffFiles && (fileName.endsWith(".tif") || fileName.endsWith(".tiff"))) {
                    return true;
                } else {
                    return includeJpegFiles && (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg"));
                }
            }
        };
    }

    public String getName() {
        return name;
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

    public DirectoryStream<Path> getImageFiles() throws IOException {
        return Files.newDirectoryStream(imageDir, filter);
    }

}
