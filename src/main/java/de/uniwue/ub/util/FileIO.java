package de.uniwue.ub.util;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;

/**
 * File I/O utility methods.
 * 
 * @author Paul Vorbach
 */
public class FileIO {
  private static FileIO instance = null;

  private FileIO() {
  }

  /**
   * @return instance of this class.
   */
  public static FileIO getInstance() {
    if (instance == null)
      instance = new FileIO();

    return instance;
  }

  /**
   * Reads a whole text file into memory.
   * 
   * @param file
   *          path to the file
   * @param encoding
   *          character encoding of the file
   * @return string representation of the file
   * @throws IOException
   *           if the file could not be read
   */
  public String readFile(File file, Charset encoding) throws IOException {
    byte[] encoded = Files.readAllBytes(file.toPath());
    return encoding.decode(ByteBuffer.wrap(encoded)).toString();
  }
}
