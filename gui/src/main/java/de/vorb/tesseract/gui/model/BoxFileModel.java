package de.vorb.tesseract.gui.model;

import de.vorb.tesseract.util.Symbol;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class BoxFileModel {
    private final Path file;
    private final BufferedImage image;
    private final List<Symbol> boxes;

    public BoxFileModel(Path file, BufferedImage image, List<Symbol> boxes) {
        this.file = file;
        this.image = image;
        this.boxes = boxes;
    }

    public Path getFile() {
        return file;
    }

    public BufferedImage getImage() {
        return image;
    }

    public List<Symbol> getBoxes() {
        return Collections.unmodifiableList(boxes);
    }

    public void insertBoxAt(int index, Symbol box) {
        boxes.add(index, box);
    }

    public void replaceBoxAt(int index, Symbol box) {
        boxes.set(index, box);
    }

    public void removeBoxAt(int index) {
        boxes.remove(index);
    }

    public void removeBox(Symbol box) {
        boxes.remove(box);
    }
}
