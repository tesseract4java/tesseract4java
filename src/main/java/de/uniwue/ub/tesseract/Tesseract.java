package de.uniwue.ub.tesseract;

import java.io.File;

/**
 * Common properties and methods to use Tesseract from Java.
 * 
 * @author Paul Vorbach
 */
public class Tesseract {
  /**
   * Path to Tesseract.
   * 
   * If Tesseract is in the system's <code>Path</code> this may be
   * <code>null</code>. Otherwise it has to point to the directory that contains
   * Tesseract's executables.
   */
  public static File path = null;

  /**
   * Returns the path of the given command's executable file.
   * 
   * @param command
   *          name of the command
   * @return path of given command's executable file
   */
  public static String getPathForCommand(String command) {
    if (path == null)
      return command;
    else
      return new File(path, command).getAbsolutePath();
  }
}
