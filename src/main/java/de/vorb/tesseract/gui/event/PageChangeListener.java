package de.vorb.tesseract.gui.event;

import java.nio.file.Path;

public interface PageChangeListener {
  public void pageChanged(int pageIndex, Path page);
}
