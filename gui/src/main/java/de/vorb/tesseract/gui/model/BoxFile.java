package de.vorb.tesseract.gui.model;

import de.vorb.tesseract.util.Symbol;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class BoxFile {
    private final Path filePath;
    private final BufferedImage image;
    private final List<Symbol> boxes;

    public BoxFile(Path filePath, BufferedImage image, List<Symbol> boxes) {
        this.filePath = filePath;
        this.image = image;
        this.boxes = boxes;
    }

    public Path getFilePath() {
        return filePath;
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
