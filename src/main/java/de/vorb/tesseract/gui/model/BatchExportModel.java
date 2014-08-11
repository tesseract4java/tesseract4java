package de.vorb.tesseract.gui.model;

import java.nio.file.Path;

public class BatchExportModel {

    private final Path destinationDir;
    private final boolean exportTXT;
    private final boolean exportHTML;
    private final boolean openDestination;
    private final int numThreads;

    public BatchExportModel(Path destinationDir, boolean exportTXT,
            boolean exportHTML, int numThreads, boolean openDestination) {

        this.destinationDir = destinationDir;
        this.exportTXT = exportTXT;
        this.exportHTML = exportHTML;
        this.numThreads = numThreads;
        this.openDestination = openDestination;
    }

    public Path getDestinationDir() {
        return destinationDir;
    }

    public boolean isExportTXT() {
        return exportTXT;
    }

    public boolean isExportHTML() {
        return exportHTML;
    }

    public boolean isOpenDestination() {
        return openDestination;
    }

    public int getNumThreads() {
        return numThreads;
    }
}
