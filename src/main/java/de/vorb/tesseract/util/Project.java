package de.vorb.tesseract.util;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.vorb.tesseract.gui.event.PageChangeListener;

public class Project implements Serializable {
  private static final long serialVersionUID = 1L;

  private final Path scanDir;
  private final List<Path> pages;
  private int pageIndex = 0;
  private final List<PageChangeListener> pageChangeListeners;

  public Project(Path scanDir, List<Path> pages) {
    this.scanDir = scanDir;
    this.pages = pages;

    this.pageChangeListeners = new LinkedList<PageChangeListener>();
  }

  public Path getScanDir() {
    return scanDir;
  }

  public List<Path> getPages() {
    return Collections.unmodifiableList(this.pages);
  }

  public void addPageChangeListener(PageChangeListener listener) {
    pageChangeListeners.add(listener);
  }

  public void removePageChangeListener(PageChangeListener listener) {
    pageChangeListeners.remove(listener);
  }

  public int getSelectedPageIndex() {
    return pageIndex;
  }

  public int getMinimumPageIndex() {
    return 0;
  }

  public int getMaximumPageIndex() {
    return Math.max(pages.size() - 1, 0);
  }

  public Path getSelectedPage() {
    return pages.get(getSelectedPageIndex());
  }

  private void pageChanged() {
    final int index = getSelectedPageIndex();
    final Path page = getSelectedPage();

    for (PageChangeListener listener : pageChangeListeners) {
      listener.pageChanged(index, page);
    }
  }

  public void setSelectedPageIndex(int index) {
    if (index < 0)
      throw new IllegalArgumentException("index < 0");
    if (index > getMaximumPageIndex())
      throw new IllegalArgumentException("index > maxIndex");

    pageIndex = index;

    pageChanged();
  }
}
