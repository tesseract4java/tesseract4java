package de.vorb.tesseract.tools.training;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class WordFrequencyList {
    private WordFrequencyList() {
    }

    /**
     * Creates a frequency list from text files in a directory.
     *
     * @param in  input directory, contains .txt files
     * @param out output file
     * @throws IOException
     */
    public static void generateList(File in, File out, ExecutorService exec)
            throws IOException {
        if (!in.isDirectory())
            throw new IllegalArgumentException("not a directory");

        generateList(Arrays.asList(in.listFiles()), out, exec);
    }

    public static void generateList(Collection<File> in, File out,
            ExecutorService exec) throws IOException {
        final ConcurrentHashMap<String, AtomicInteger> frequencyList = new ConcurrentHashMap<String, AtomicInteger>();

        // delimiter that matches all non-word characters and
        final Pattern delim = Pattern.compile("[^\\p{L}\\p{M}\\p{Digit}]+");

        // ensure the parent directory for out exists
        if (!out.getParentFile().isDirectory()) {
            Files.createDirectories(out.getParentFile().toPath());
        }

        for (final File f : in) {
            if (!f.isFile())
                continue;

            exec.execute(() -> {
                try (Scanner scanner = new Scanner(f, "UTF-8")) {
                    scanner.useDelimiter(delim);

                    while (scanner.hasNext()) {
                        final String word = scanner.next();

                        // Ignore single character words
                        if (word.length() == 1)
                            continue;

                        frequencyList.putIfAbsent(word, new AtomicInteger(0));
                        frequencyList.get(word).incrementAndGet();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        exec.shutdown();
        try {
            exec.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            System.err.println("timeout");
        }

        final SortedSet<Entry<String, AtomicInteger>> sorted = new TreeSet<Entry<String, AtomicInteger>>(
                (a, b) -> {
                    if (a.getValue().get() < b.getValue().get())
                        return 1;
                    else if (a.getValue().get() > b.getValue().get())
                        return -1;
                    else
                        return a.getKey().compareTo(b.getKey());
                });
        sorted.addAll(frequencyList.entrySet());

        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(out), "UTF-8"));
        for (final Iterator<Entry<String, AtomicInteger>> it = sorted.iterator(); it
                .hasNext(); ) {
            final Entry<String, AtomicInteger> entry = it.next();
            writer.write(entry.getKey() + " " + entry.getValue().get() + "\n");
        }
        writer.close();
    }

    public static void main(String[] args) throws IOException {
        final List<File> in = new LinkedList<File>();

        for (File f : new File(
                "E:/Masterarbeit/Ressourcen/DE-20__53_Rp_5_367-1/ocr").listFiles())
            in.add(f);

        for (File f : new File(
                "E:/Masterarbeit/Ressourcen/DE-20__53_Rp_5_367-2/ocr").listFiles())
            in.add(f);

        generateList(in, new File(
                        "E:/Masterarbeit/Ressourcen/training/frequency_list"),
                Executors
                        .newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1));
    }
}
