package de.vorb.tesseract.gui.model;

import java.nio.file.Path;

import javax.swing.Icon;

public class PageThumbnail {
    private final Path file;
    private final Icon thumbnail;

    public PageThumbnail(Path file, Icon thumbnail) {
        this.file = file;
        this.thumbnail = thumbnail;
    }

    public Path getFile() {
        return file;
    }

    public Icon getThumbnail() {
        return thumbnail;
    }
}
