package de.vorb.tesseract.gui.work;

import de.vorb.tesseract.gui.model.PageThumbnail;
import de.vorb.tesseract.gui.model.ProjectModel;

import com.google.common.base.Optional;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.List;

public class PageListWorker extends SwingWorker<Void, PageThumbnail> {
    private final ProjectModel projectModel;
    private final DefaultListModel<PageThumbnail> pages;

    public PageListWorker(final ProjectModel projectModel,
            DefaultListModel<PageThumbnail> pages) {
        this.projectModel = projectModel;
        this.pages = pages;

        pages.clear();
    }

    @Override
    protected Void doInBackground() throws Exception {
        // no thumbnail
        final Optional<BufferedImage> thumbnail = Optional.absent();

        // publish a placeholder (no thumbnail) for every image file
        for (final Path file : projectModel.getImageFiles()) {
            publish(new PageThumbnail(file, thumbnail));
        }

        return null;
    }

    @Override
    protected void process(List<PageThumbnail> chunks) {
        // add thumbnail to the list model
        for (final PageThumbnail chunk : chunks) {
            pages.addElement(chunk);
        }
    }
}
