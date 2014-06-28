package de.vorb.tesseract.gui.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.DirectoryStream.Filter;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

import de.vorb.tesseract.gui.model.PageThumbnail;
import de.vorb.tesseract.gui.view.NewProjectDialog;
import de.vorb.tesseract.gui.view.NewProjectDialog.Result;

public class ThumbnailLoader extends SwingWorker<Void, PageThumbnail> {
    private final Result projectConfig;
    private final DirectoryStream.Filter<Path> filter;
    private final DefaultListModel<PageThumbnail> pages;

    public ThumbnailLoader(final NewProjectDialog.Result projectConfig,
            DefaultListModel<PageThumbnail> pages) {
        this.projectConfig = projectConfig;
        this.pages = pages;

        pages.clear();

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
        // directory stream of image files
        final DirectoryStream<Path> ds =
                Files.newDirectoryStream(
                        projectConfig.directory, filter);

        final Path thumbsDir =
                projectConfig.directory.resolve(".tess/thumbs");

        // mkdir -p thumbsDir
        Files.createDirectories(thumbsDir);

        // add image files to the page list
        for (final Path imageFile : ds) {
            final Path thumbFile =
                    thumbsDir.resolve(imageFile.getFileName());

            final BufferedImage thumb;

            if (Files.isReadable(thumbFile)) {
                thumb = ImageIO.read(thumbFile.toFile());
            } else {
                final BufferedImage img =
                        ImageIO.read(imageFile.toFile());

                final int width = (int) (100d / img.getHeight()
                        * img.getWidth());

                thumb = new BufferedImage(width, 100,
                        BufferedImage.TYPE_BYTE_GRAY);

                final Graphics2D g2d = (Graphics2D) thumb.getGraphics();
                g2d.drawImage(img.getScaledInstance(width, 100,
                        BufferedImage.SCALE_SMOOTH), 0, 0, null);
                g2d.setColor(Color.GRAY);
                g2d.drawRect(0, 0, width - 1, 99);
                g2d.dispose();

                img.flush();

                ImageIO.write(thumb, "PNG", thumbFile.toFile());
            }

            publish(new PageThumbnail(imageFile, new ImageIcon(
                    thumb)));
        }

        return null;
    }

    @Override
    protected void process(List<PageThumbnail> chunks) {
        for (PageThumbnail chunk : chunks) {
            pages.addElement(chunk);
        }
    }
}
