package de.vorb.tesseract.gui.model;

import java.awt.image.BufferedImage;
import java.nio.file.Path;

import com.google.common.base.Optional;

public class PageThumbnail {
    private final Path file;
    private Optional<BufferedImage> thumbnail;

    public PageThumbnail(Path file, Optional<BufferedImage> thumbnail) {
        this.file = file;
        this.thumbnail = thumbnail;
    }

    public Path getFile() {
        return file;
    }

    public Optional<BufferedImage> getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Optional<BufferedImage> thumbnail) {
        this.thumbnail = thumbnail;
    }
}
