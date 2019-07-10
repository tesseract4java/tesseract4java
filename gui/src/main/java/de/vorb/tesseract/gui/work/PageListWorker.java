package de.vorb.tesseract.gui.work;

import de.vorb.tesseract.gui.model.PageThumbnail;
import de.vorb.tesseract.gui.model.Project;

import javax.swing.DefaultListModel;
import javax.swing.SwingWorker;
import java.nio.file.Path;
import java.util.List;

public class PageListWorker extends SwingWorker<Void, PageThumbnail> {
    private final Project project;
    private final DefaultListModel<PageThumbnail> pages;

    public PageListWorker(final Project project,
            DefaultListModel<PageThumbnail> pages) {
        this.project = project;
        this.pages = pages;

        pages.clear();
    }

    @Override
    protected Void doInBackground() throws Exception {
        // publish a placeholder (no thumbnail) for every image file
        for (final Path file : project.getImageFiles()) {
            publish(new PageThumbnail(file, null));
        }

        return null;
    }

    @Override
    protected void process(List<PageThumbnail> chunks) {
        chunks.forEach(pages::addElement);
    }
}
