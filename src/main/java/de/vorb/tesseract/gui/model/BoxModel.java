package de.vorb.tesseract.gui.model;

import de.vorb.tesseract.util.Box;

public class BoxModel {
  private final Box box;
  private final String character;

  public BoxModel(String character, Box box) {
    this.character = character;
    this.box = box;
  }

  public String getCharacer() {
    return character;
  }

  public Box getBox() {
    return box;
  }

  @Override
  public String toString() {
    return "Box(character = \"" + character + "\", box = " + box + ")";
  }
}
