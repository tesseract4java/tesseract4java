package de.vorb.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;

/**
 * File I/O utility methods.
 * 
 * @author Paul Vorbach
 */
public class FileIO {
  /**
   * Reads a whole text file into memory.
   * 
   * @param file
   *          path to the file
   * @param encoding
   *          character encoding of the file
   * @return string representation of the file
   * @throws IOException
   *           if the file could not be read.
   */
  public static String readFile(File file, Charset encoding) throws IOException {
    byte[] encoded = Files.readAllBytes(file.toPath());
    return encoding.decode(ByteBuffer.wrap(encoded)).toString();
  }

  /**
   * Creates or replaces the given file and writes the content.
   * 
   * @param content
   * @param file
   * @param encoding
   * @throws IOException
   */
  public static  void writeFile(String content, File file, Charset encoding)
      throws IOException {
    final PrintWriter to = new PrintWriter(file, "UTF-8");
    to.print(content);
    to.close();
  }
}
