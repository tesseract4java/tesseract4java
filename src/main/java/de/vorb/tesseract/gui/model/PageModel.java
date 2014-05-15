package de.vorb.tesseract.gui.model;

import java.awt.image.BufferedImage;

import de.vorb.tesseract.util.Page;

public class PageModel {
    private final Page page;
    private final BufferedImage normal;
    private final BufferedImage blackAndWhite;

    public PageModel(Page page, BufferedImage normal,
            BufferedImage blackAndWhite) {
        this.page = page;
        this.normal = normal;
        this.blackAndWhite = blackAndWhite;
    }

    public Page getPage() {
        return page;
    }

    public BufferedImage getImage() {
        return normal;
    }

    public BufferedImage getBlackAndWhiteImage() {
        return blackAndWhite;
    }
}
