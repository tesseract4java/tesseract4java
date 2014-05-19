package de.vorb.tesseract.gui.model;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

import de.vorb.tesseract.util.Symbol;

public class BoxFileModel {
    private final BufferedImage bwImage;
    private final List<Symbol> boxes;

    public BoxFileModel(BufferedImage blackAndWhiteImage, List<Symbol> boxes) {
        this.bwImage = blackAndWhiteImage;
        this.boxes = boxes;
    }

    public BufferedImage getScan() {
        return bwImage;
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
