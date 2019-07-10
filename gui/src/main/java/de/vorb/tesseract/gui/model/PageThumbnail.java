package de.vorb.tesseract.gui.model;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Objects;

public class PageThumbnail {

    @NonNull
    private final Path file;

    @Nullable
    private BufferedImage thumbnail;

    public PageThumbnail(@NonNull Path file, @Nullable BufferedImage thumbnail) {
        this.file = file;
        this.thumbnail = thumbnail;
    }

    @NonNull
    public Path getFile() {
        return file;
    }

    @Nullable
    public BufferedImage getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(@Nullable BufferedImage thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PageThumbnail that = (PageThumbnail) o;

        if (!file.equals(that.file)) {
            return false;
        }
        return Objects.equals(thumbnail, that.thumbnail);
    }

    @Override
    public int hashCode() {
        int result = file.hashCode();
        result = 31 * result + (thumbnail != null ? thumbnail.hashCode() : 0);
        return result;
    }

}
