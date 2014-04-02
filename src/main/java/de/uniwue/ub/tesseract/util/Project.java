package de.uniwue.ub.tesseract.util;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.uniwue.ub.tesseract.event.PageChangeListener;

public class Project implements Serializable {
  private static final long serialVersionUID = 1L;

  private final Path hocrDir;
  private final Path scanDir;
  private final List<String> pages;
  private int pageIndex = 0;
  private final List<PageChangeListener> pageChangeListeners;

  public Project(Path scanDir, Path hocrDir, List<String> pages) {
    this.scanDir = scanDir;
    this.hocrDir = hocrDir;
    this.pages = pages;

    this.pageChangeListeners = new LinkedList<PageChangeListener>();
  }

  public Path getHocrDir() {
    return hocrDir;
  }

  public Path getScanDir() {
    return scanDir;
  }

  public List<String> getPages() {
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

  public String getSelectedPage() {
    return pages.get(getSelectedPageIndex());
  }

  private void pageChanged() {
    final int index = getSelectedPageIndex();
    final String page = getSelectedPage();

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
