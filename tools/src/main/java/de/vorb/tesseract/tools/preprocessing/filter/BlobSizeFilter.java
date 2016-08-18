package de.vorb.tesseract.tools.preprocessing.filter;

import de.vorb.tesseract.tools.preprocessing.binarization.Binarization;
import de.vorb.tesseract.tools.preprocessing.binarization.Sauvola;
import de.vorb.tesseract.tools.preprocessing.conncomp.ConnectedComponent;
import de.vorb.tesseract.tools.preprocessing.conncomp.ConnectedComponentLabeler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class BlobSizeFilter implements ImageFilter {
    private final int minArea;
    private final int maxArea;

    public BlobSizeFilter(int minArea, int maxArea) {
        this.minArea = minArea;
        if (maxArea == 0) {
            this.maxArea = Integer.MAX_VALUE;
        } else {
            this.maxArea = maxArea;
        }
    }

    public int getMinArea() {
        return minArea;
    }

    public int getMaxArea() {
        return maxArea == Integer.MAX_VALUE ? 0 : maxArea;
    }

    @Override
    public void filter(BufferedImage image) {
        if (image.getType() != BufferedImage.TYPE_BYTE_BINARY) {
            throw new IllegalArgumentException("not a binary image");
        }

        final ConnectedComponentLabeler labeler =
                new ConnectedComponentLabeler(image, true);
        final List<ConnectedComponent> connectedComponents =
                labeler.apply();

        // clear the input image
        final Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

        g2d.setColor(Color.BLACK);
        for (final ConnectedComponent connComp : connectedComponents) {
            if (connComp.getArea() <= maxArea &&
                    connComp.getArea() >= minArea) {
                connComp.drawOn(g2d);
            }
        }

        g2d.dispose();
    }

    public static void main(String[] args) throws IOException {
        final Path dir =
                Paths.get("E:/Masterarbeit/Ressourcen/DE-20__32_AM_49000_L869_G927-1");
        final DirectoryStream<Path> ds = Files.newDirectoryStream(dir,
                new DirectoryStream.Filter<Path>() {
                    @Override
                    public boolean accept(Path path) throws IOException {
                        return path.getFileName().toString().endsWith(".png");
                    }
                });

        final Path outDir = dir.resolve("sauvola/filtered");
        Files.createDirectories(outDir);

        final Binarization binarization = new Sauvola(15);
        final BlobSizeFilter filter = new BlobSizeFilter(25,
                Integer.MAX_VALUE);

        int i = 0;
        for (final Path file : ds) {
            System.out.println(file.getFileName());
            final BufferedImage image = ImageIO.read(file.toFile());
            final BufferedImage binary = binarization.binarize(image);
            filter.filter(binary);
            ImageIO.write(binary, "PNG",
                    outDir.resolve(file.getFileName()).toFile());
            i++;
        }
    }
}
