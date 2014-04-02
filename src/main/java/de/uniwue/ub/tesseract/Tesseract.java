package de.uniwue.ub.tesseract;

import java.io.File;

/**
 * Common properties and methods to use Tesseract from Java.
 * 
 * @author Paul Vorbach
 */
public class Tesseract {
  private static Tesseract instance = null;

  private Tesseract() {
  }

  /**
   * @return singleton instance of this class.
   */
  public static Tesseract getInstance() {
    if (instance == null)
      instance = new Tesseract();

    return instance;
  }

  /**
   * Path to Tesseract.
   * 
   * If Tesseract is in the system's <code>Path</code> this may be
   * <code>null</code>. Otherwise it has to point to the directory that contains
   * Tesseract's executables.
   */
  public File path = null;

  /**
   * Returns the path of the given command's executable file.
   * 
   * @param command
   *          name of the command
   * @return path of given command's executable file
   */
  public String getPathForCommand(String command) {
    if (path == null)
      return command;
    else
      return new File(path, command).getAbsolutePath();
  }

  /**
   * Name of the tesseract executable file.
   */
  public final String executable = "tesseract";

  public void makeBoxFile(String lang, String font, int num) {
    
  }
}
