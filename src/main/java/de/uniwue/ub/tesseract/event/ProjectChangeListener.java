package de.uniwue.ub.tesseract.event;

import java.nio.file.Path;

public interface ProjectChangeListener {
  public void projectChanged(Path scanDir, Path hocrDir);
}
