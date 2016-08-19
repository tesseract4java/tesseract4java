package de.vorb.tesseract.tools.preprocessing;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Batch utility.
 *
 * @author Paul Vorbach
 */
public abstract class Batch {
    /**
     * Applies a task to every file in a given directory that is not filtered.
     *
     * @param dir      input directory
     * @param task
     * @param timeout
     * @param timeUnit
     * @throws InterruptedException
     */
    public static void process(File dir, FileFilter filter, Batch task,
            long timeout, TimeUnit timeUnit) throws InterruptedException {
        if (!dir.isDirectory())
            throw new IllegalArgumentException("not a directory");

        process(dir.listFiles(filter), task, timeout, timeUnit);
    }

    /**
     * Applies a task for each given file.
     *
     * @param files
     * @param task
     * @param timeout
     * @param timeUnit
     * @throws InterruptedException
     */
    public static void process(File[] files, Batch task, long timeout,
            TimeUnit timeUnit) throws InterruptedException {
        final ExecutorService pool = Executors.newFixedThreadPool(Runtime
                .getRuntime().availableProcessors());

        // submit one task for each file that is not filtered
        for (File f : files) {
            pool.execute(task.getTask(f));
        }

        pool.shutdown();
        pool.awaitTermination(timeout, timeUnit);
    }

    /**
     * Returns a <code>Runnable</code> task for every given <code>File</code>.
     *
     * @param src source file
     * @return task.
     */
    public abstract Runnable getTask(final File src);
}
