package de.vorb.tesseract.gui.view.renderer;

import java.awt.image.BufferedImage;

import de.vorb.tesseract.util.Page;

/**
 * Page renderer.
 * 
 * @author Paul Vorbach
 */
public interface PageRenderer {

    /**
     * Renders the information of a page on an optionally given background.
     * 
     * @param page
     *            page model to render
     * @param pageBackground
     *            background image to render below (may also be null)
     */
    public void render(Page page, BufferedImage pageBackground);
}
