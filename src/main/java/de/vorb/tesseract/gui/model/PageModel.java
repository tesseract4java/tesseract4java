package de.vorb.tesseract.gui.model;

import java.nio.file.Path;

import de.vorb.tesseract.util.Page;

public class PageModel {
    private final Page page;
    private final Path imageFile;

    public PageModel(Page page, Path imageFile) {
        this.page = page;
        this.imageFile = imageFile;
    }

    public Page getPage() {
        return page;
    }

    public Path getImageFile() {
        return imageFile;
    }
}
