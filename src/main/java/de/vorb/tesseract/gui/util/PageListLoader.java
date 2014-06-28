package de.vorb.tesseract.gui.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.DirectoryStream.Filter;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.SwingWorker;

import com.google.common.base.Optional;

import de.vorb.tesseract.gui.model.PageThumbnail;
import de.vorb.tesseract.gui.view.NewProjectDialog;
import de.vorb.tesseract.gui.view.NewProjectDialog.Result;

public class PageListLoader extends SwingWorker<Void, PageThumbnail> {
    private final Result projectConfig;
    private final DirectoryStream.Filter<Path> filter;
    private final DefaultListModel<PageThumbnail> pages;

    public PageListLoader(final NewProjectDialog.Result projectConfig,
            DefaultListModel<PageThumbnail> pages) {
        this.projectConfig = projectConfig;
        this.pages = pages;

        pages.clear();

        // run garbage collector
        System.gc();

        filter = new Filter<Path>() {
            @Override
            public boolean accept(Path entry) throws IOException {
                final String e = entry.getFileName().toString();

                if (projectConfig.png && e.endsWith(".png"))
                    return true;
                else if (projectConfig.tiff && (e.endsWith(".tif")
                        || e.endsWith(".tiff")))
                    return true;
                else if (projectConfig.jpeg && (e.endsWith(".jpg")
                        || e.endsWith(".jpeg")))
                    return true;
                else
                    return false;
            }
        };
    }

    @Override
    protected Void doInBackground() throws Exception {
        final DirectoryStream<Path> imageStream =
                Files.newDirectoryStream(projectConfig.directory, filter);

        // no thumbnail
        final Optional<BufferedImage> thumbnail = Optional.absent();

        // publish a placeholder for every image file
        for (final Path file : imageStream) {
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

    @Override
    protected void done() {
        // run the tumbnail loader after the model is built
        final ThumbnailLoader thumbnailLoader =
                new ThumbnailLoader(projectConfig, pages);

        thumbnailLoader.execute();
    }
}
