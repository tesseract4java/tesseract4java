package de.uniwue.ub.tesseract.training;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.uniwue.ub.tesseract.Tesseract;

/**
 * Class for creating unicode character set (unicharset) files.
 * 
 * @author Paul Vorbach
 */
public class UnicharsetExtractor {
  /**
   * Name of the unicharset_extractor executable file.
   */
  public final static String executable = "unicharset_extractor";

  private UnicharsetExtractor() {
  }

  /**
   * Extracts the unicode character set file out of a
   * 
   * @param boxFiles
   * @throws IOException
   * @throws InterruptedException
   */
  public static void extract(Collection<File> boxFiles) throws IOException,
      InterruptedException {
    if (boxFiles.isEmpty())
      return;

    // build a process
    final Iterator<File> it = boxFiles.iterator();
    final File first = it.next();

    final List<String> command = new LinkedList<String>();
    command.add(Tesseract.getPathForCommand(executable));
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
