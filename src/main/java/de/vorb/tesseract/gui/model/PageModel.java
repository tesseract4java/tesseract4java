package de.vorb.tesseract.gui.model;

import java.awt.image.BufferedImage;

import de.vorb.tesseract.util.Page;

public class PageModel {
    private final Page page;
    private final BufferedImage image;

    public PageModel(Page page, BufferedImage image) {
        this.page = page;
        this.image = image;
    }

    public Page getPage() {
        return page;
    }

    public BufferedImage getImage() {
        return image;
    }
}
