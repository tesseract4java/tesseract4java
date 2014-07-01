package de.vorb.tesseract.gui.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.SwingWorker;

import com.google.common.base.Optional;

import de.vorb.tesseract.gui.model.PageThumbnail;
import de.vorb.tesseract.gui.view.NewProjectDialog;
import de.vorb.tesseract.gui.view.NewProjectDialog.Result;

public class ThumbnailLoader extends
        SwingWorker<Void, ThumbnailLoader.Task> {
    private final Result projectConfig;
    private final DefaultListModel<PageThumbnail> pages;

    private final Queue<Task> tasks = new ConcurrentLinkedQueue<Task>();

    public static class Task {
        public final int index;
        public final PageThumbnail thumbnail;
        private boolean cancelled = false;

        public Task(int index, PageThumbnail thumbnail) {
            this.index = index;
            this.thumbnail = thumbnail;
        }

        public void cancel() {
            cancelled = true;
        }
    }

    public ThumbnailLoader(final NewProjectDialog.Result projectConfig,
            DefaultListModel<PageThumbnail> pages) {
        this.projectConfig = projectConfig;
        this.pages = pages;
    }

    public void submitTask(Task task) {
        tasks.add(task);
    }

    @Override
    protected Void doInBackground() throws Exception {
        final Path thumbsDir =
                projectConfig.directory.resolve(".tess/thumbs");

        // mkdir -p thumbsDir
        Files.createDirectories(thumbsDir);

        while (true) {
            if (isCancelled()) {
                break;
            }

            final Task task = tasks.poll();
            if (task != null) {
                if (task.cancelled) {
                    continue;
                }

                final Path imageFile = task.thumbnail.getFile();

                final Path thumbFile =
                        thumbsDir.resolve(imageFile.getFileName());

                final BufferedImage thumb;

                if (Files.isReadable(thumbFile)) {
                    // if the thumbnail file exists already, load it directly
                    thumb = ImageIO.read(thumbFile.toFile());
                } else {
                    // otherwise create a new thumbnail
                    final BufferedImage img =
                            ImageIO.read(imageFile.toFile());

                    // calculate width according to aspect ratio
                    final int width = (int) (100d / img.getHeight()
                            * img.getWidth());

                    thumb = new BufferedImage(width, 100,
                            BufferedImage.TYPE_BYTE_GRAY);

                    // draw a smoothly scaled version to the thumbnail
                    final Graphics2D g2d = (Graphics2D) thumb.getGraphics();
                    g2d.drawImage(img.getScaledInstance(width, 100,
                            BufferedImage.SCALE_SMOOTH), 0, 0, null);
                    g2d.dispose();

                    // release system resources used by this image
                    img.flush();

                    // write the thumnail to disk
                    ImageIO.write(thumb, "PNG", thumbFile.toFile());
                }

                publish(new Task(task.index, new PageThumbnail(imageFile,
                        Optional.of(thumb))));
            } else {
                Thread.sleep(500L);
            }
        }

        return null;
    }

    @Override
    protected void process(List<Task> chunks) {
        // update the list model
        for (Task chunk : chunks) {
            pages.set(chunk.index, chunk.thumbnail);
        }
    }
}
