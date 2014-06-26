package de.vorb.tesseract.gui.view;

import java.nio.file.Path;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

public class ImageItemProvider implements ItemProvider<Path> {
    private final Point maxImageSize;

    public ImageItemProvider(Point maxImageSize) {
        this.maxImageSize = maxImageSize;
    }

    @Override
    public Composite itemFor(Path imageFile, Composite parent) {
        final ImageItem item = new ImageItem(parent, SWT.NONE);

        item.setSize(maxImageSize);
        item.setLabel(imageFile.getFileName().toString());
        item.setImage(new Image(parent.getDisplay(), imageFile.toString()));

        return item;
    }
}
