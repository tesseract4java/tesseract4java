package de.vorb.tesseract.gui.model;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Optional;

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((file == null) ? 0 : file.hashCode());
        result = prime * result
                + ((thumbnail == null) ? 0 : thumbnail.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof PageThumbnail))
            return false;
        PageThumbnail other = (PageThumbnail) obj;
        if (file == null) {
            if (other.file != null)
                return false;
        } else if (!file.equals(other.file))
            return false;
        if (thumbnail == null) {
            if (other.thumbnail != null)
                return false;
        } else if (!thumbnail.equals(other.thumbnail))
            return false;
        return true;
    }

}
