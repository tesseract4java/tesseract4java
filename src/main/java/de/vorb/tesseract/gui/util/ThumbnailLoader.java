package de.vorb.tesseract.gui.util;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.DefaultListModel;
import javax.swing.SwingWorker;

import com.google.common.base.Optional;

import de.vorb.tesseract.gui.model.PageThumbnail;
import de.vorb.tesseract.gui.view.NewProjectDialog;
import de.vorb.tesseract.gui.view.NewProjectDialog.Result;

public class ThumbnailLoader extends
        SwingWorker<Void, ThumbnailLoader.Chunk> {
    private final Result projectConfig;
    private final DefaultListModel<PageThumbnail> pages;

    static class Chunk {
        public final int index;
        public final PageThumbnail thumbnail;

        public Chunk(int index, PageThumbnail thumbnail) {
            this.index = index;
            this.thumbnail = thumbnail;
        }
    }

    public ThumbnailLoader(final NewProjectDialog.Result projectConfig,
            DefaultListModel<PageThumbnail> pages) {
        this.projectConfig = projectConfig;
        this.pages = pages;
    }

    @Override
    protected Void doInBackground() throws Exception {
        final Path thumbsDir =
                projectConfig.directory.resolve(".tess/thumbs");

        // mkdir -p thumbsDir
        Files.createDirectories(thumbsDir);

        final int size = pages.size();
        for (int index = 0; index < size; index++) {
            final PageThumbnail pageThumb = pages.get(index);
            final Path imageFile = pageThumb.getFile();
            final Path thumbFile =
                    thumbsDir.resolve(imageFile.getFileName());

            final BufferedImage thumb;

            if (Files.isReadable(thumbFile)) {
                // if the thumbnail file exists already, load it directly
                thumb = ImageIO.read(thumbFile.toFile());
            } else {
                thumb = loadThumbnail(imageFile);

                // write the thumnail to disk
                ImageIO.write(thumb, "PNG", thumbFile.toFile());
            }

            // publish the thumbnail
            publish(new Chunk(index, new PageThumbnail(imageFile,
                    Optional.of(thumb))));
        }

        return null;
    }

    @Override
    protected void process(List<Chunk> chunks) {
        // update the list model
        for (Chunk chunk : chunks) {
            pages.set(chunk.index, chunk.thumbnail);
        }
    }

    private BufferedImage loadThumbnail(Path imageFile) throws IOException {
        final ImageInputStream iis =
                ImageIO.createImageInputStream(imageFile.toFile());

        final Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

        if (!readers.hasNext()) {
            throw new IOException(
                    "No reader available for supplied image stream.");
        }

        final ImageReader reader = readers.next();
        reader.setInput(iis);

        final int w1 = reader.getWidth(0);
        final int h1 = reader.getHeight(0);

        // calculate width according to aspect ratio
        final int w2 = (int) (100d / h1 * w1);
        final int h2 = 100;

        final ImageReadParam params = reader.getDefaultReadParam();
        params.setSourceSubsampling(Math.max(w1 / w2, 1), Math.max(h1 / h2, 1),
                0, 0);

        // no progress listener for the moment
        reader.addIIOReadProgressListener(null);

        final BufferedImage resampledImage = reader.read(0, params);
        reader.removeAllIIOReadProgressListeners();

        return resampledImage;
    }
}
