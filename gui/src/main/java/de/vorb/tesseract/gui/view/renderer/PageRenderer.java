package de.vorb.tesseract.gui.view.renderer;

import com.google.common.base.Optional;

import de.vorb.tesseract.gui.model.PageModel;

/**
 * Page renderer.
 * 
 * @author Paul Vorbach
 */
public interface PageRenderer {

    /**
     * 
     * Renders the information of a page on an optionally given background.
     * 
     * @param pageModel
     *            page model to render
     * @param scale
     *            scaling factor
     */
    public void render(final Optional<PageModel> pageModel, final float scale);
}
