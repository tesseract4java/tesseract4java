package de.uniwue.ub.tesseract.model;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BoxFileModel {
  private final BufferedImage scan;
  private final LinkedList<BoxModel> boxes;

  public BoxFileModel(BufferedImage scan, LinkedList<BoxModel> boxes) {
    this.scan = scan;
    this.boxes = boxes;
  }

  public BufferedImage getScan() {
    return scan;
  }

  public List<BoxModel> getBoxes() {
    return Collections.unmodifiableList(boxes);
  }

  public void insertBoxAt(int index, BoxModel box) {
    boxes.add(index, box);
  }

  public void replaceBoxAt(int index, BoxModel box) {
    boxes.set(index, box);
  }

  public void removeBoxAt(int index) {
    boxes.remove(index);
  }

  public void removeBox(BoxModel box) {
    boxes.remove(box);
  }
}
