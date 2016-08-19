package de.vorb.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * File I/O utility methods.
 *
 * @author Paul Vorbach
 */
public class FileIO {
    /**
     * Reads a whole text file into memory.
     *
     * @param file     path to the file
     * @param encoding character encoding of the file
     * @return string representation of the file
     * @throws IOException if the file could not be read.
     */
    public static String readFile(Path file, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(file);
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
    public static void writeFile(String content, Path file, Charset encoding)
            throws IOException {
        final PrintWriter to = new PrintWriter(file.toFile(), "UTF-8");
        to.print(content);
        to.close();
    }

    public static void deleteDirectory(Path dir) throws IOException {
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file,
                    BasicFileAttributes attrs)
                    throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException e)
                    throws IOException {
                // try to delete the file anyway, even if its attributes
                // could not be read, since delete-only access is
                // theoretically possible
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException e)
                    throws IOException {
                if (e == null) {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                } else {
                    // directory iteration failed; propagate exception
                    throw e;
                }
            }
        });
    }
}
