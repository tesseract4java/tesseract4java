package de.vorb.tesseract.gui.model;

import de.vorb.tesseract.util.Page;

public class PageModel {
    private final ImageModel imageModel;
    private final Page page;

    public PageModel(ImageModel imageModel, Page page) {
        this.imageModel = imageModel;
        this.page = page;
    }

    public Page getPage() {
        return page;
    }

    public ImageModel getImageModel() {
        return imageModel;
    }
}
