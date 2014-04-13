package de.vorb.tesseract.tools.training;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class for creating unicode character set (unicharset) files.
 * 
 * @author Paul Vorbach
 */
public class UnicharsetExtractor {
  private static UnicharsetExtractor instance = null;

  private UnicharsetExtractor() {
  }

  /**
   * @return singleton instance of this class.
   */
  public static UnicharsetExtractor getInstance() {
    if (instance == null)
      instance = new UnicharsetExtractor();

    return instance;
  }

  /**
   * Name of the unicharset_extractor executable file.
   */
  public final String executable = "unicharset_extractor";

  /**
   * Extracts the unicode character set file out of a
   * 
   * @param boxFiles
   * @throws IOException
   * @throws InterruptedException
   */
  public void extract(Collection<File> boxFiles) throws IOException,
      InterruptedException {
    if (boxFiles.isEmpty())
      return;

    // build a process
    final Iterator<File> it = boxFiles.iterator();
    final File first = it.next();

    final List<String> command = new LinkedList<String>();
    // FIXME command.add(Tesseract.getInstance().getPathForCommand(executable));
    command.add(first.getPath());
    while (it.hasNext())
      command.add(it.next().getPath());

    final ProcessBuilder pb = new ProcessBuilder(command);
    pb.directory(first.getParentFile());

    final Process p = pb.start();
    p.waitFor();
    if (p.exitValue() != 0)
      throw new IOException("bad box file");
  }
}
