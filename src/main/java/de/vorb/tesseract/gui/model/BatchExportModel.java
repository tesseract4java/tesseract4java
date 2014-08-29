package de.vorb.tesseract.gui.model;

import java.nio.file.Path;

public class BatchExportModel {

    private final Path destinationDir;
    private final boolean exportTXT;
    private final boolean exportHTML;
    private final boolean exportXML;
    private final boolean exportImages;
    private final boolean exportReports;
    private final boolean openDestination;
    private final int numThreads;

    public BatchExportModel(Path destinationDir, boolean exportTXT,
            boolean exportHTML, boolean exportXML, boolean exportImages,
            boolean exportReports, int numThreads, boolean openDestination) {
        this.destinationDir = destinationDir;
        this.exportTXT = exportTXT;
        this.exportHTML = exportHTML;
        this.exportXML = exportXML;
        this.exportImages = exportImages;
        this.exportReports = exportReports;
        this.numThreads = numThreads;
        this.openDestination = openDestination;
    }

    public Path getDestinationDir() {
        return destinationDir;
    }

    public boolean exportTXT() {
        return exportTXT;
    }

    public boolean exportHTML() {
        return exportHTML;
    }

    public boolean exportXML() {
        return exportXML;
    }

    public boolean exportImages() {
        return exportImages;
    }

    public boolean exportReports() {
        return exportReports;
    }

    public boolean openDestination() {
        return openDestination;
    }

    public int getNumThreads() {
        return numThreads;
    }
}
