package de.vorb.tesseract.tools;

import de.vorb.tesseract.tools.training.InputBuffer;
import de.vorb.tesseract.tools.training.IntClass;
import de.vorb.tesseract.tools.training.IntTemplates;
import de.vorb.tesseract.tools.training.TessdataManager;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;

public class TraineddataExample {
    public static void main(String[] args) throws IOException {

        final Path tmp = Files.createTempDirectory("tess");
        final String prefix = "deu-frak-fries-ss-fries-combined.";
        TessdataManager.extract(Paths.get(
                "E:/Masterarbeit/Ressourcen/tessdata").resolve(
                prefix + "traineddata"), tmp.resolve(prefix));

        System.out.println(tmp);

        final InputStream is = Files.newInputStream(
                tmp.resolve(prefix + "inttemp"), StandardOpenOption.READ);

        final InputBuffer buf = InputBuffer.allocate(is, 4096);

        final IntTemplates it = IntTemplates.readFrom(buf);

        System.out.println("classes: " + it.getClasses().size());

        int cls = 0;
        int set = -1;
        int setIndex = -1;
        int id = 0;
        for (final IntClass ic : it.getClasses()) {
            BufferedImage bi = null;
            Graphics g = null;
            System.out.println(ic.getNumProtos());
            for (id = 0; id < ic.getNumProtos(); id++) {
                set = id / IntTemplates.PROTOS_PER_PROTO_SET;
                setIndex = id % IntTemplates.PROTOS_PER_PROTO_SET;

                if (setIndex == 0) {
                    if (bi != null) {
                        g.dispose();

                        ImageIO.write(bi, "PNG",
                                new File("protos/" + id + ".png"));
                    }

                    bi = new BufferedImage(256, 256,
                            BufferedImage.TYPE_BYTE_GRAY);
                    g = bi.getGraphics();
                    g.setColor(Color.WHITE);
                }

//                float[] coords = PrototypeRenderer.calcCoords(ic, id);
//
//                g.drawLine((int) coords[0], (int) coords[1], (int) coords[2],
//                        (int) coords[3]);
            }
            g.dispose();

            ImageIO.write(bi, "PNG", new File("protos/" + id + ".png"));
            cls++;
        }

        Files.walkFileTree(tmp, new FileVisitor<Path>() {
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                    throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir,
                    BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file,
                    BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc)
                    throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
        });

        // final Traineddata td = Traineddata.readFrom(Paths.get(
        // "E:/Masterarbeit/Ressourcen/tessdata/deu-frak-fries-ss-fries-combined.traineddata"));
        //
        // // td.getUnicharset();
        // final IntTemplates it = td.getIntTemplates();
        //
        // System.out.println("classes: " + it.getClasses().size());
        //
        // int cls = 0;
        // int set = -1;
        // int setIndex = -1;
        // int id = 0;
        // for (final IntClass ic : it.getClasses()) {
        // BufferedImage bi = null;
        // Graphics g = null;
        // System.out.println(ic.getNumProtos());
        // for (id = 0; id < ic.getNumProtos(); id++) {
        // set = id / IntTemplates.PROTOS_PER_PROTO_SET;
        // setIndex = id % IntTemplates.PROTOS_PER_PROTO_SET;
        //
        // if (setIndex == 0) {
        // if (bi != null) {
        // g.dispose();
        //
        // ImageIO.write(bi, "PNG",
        // new File("protos/" + id + ".png"));
        // }
        //
        // bi = new BufferedImage(256, 256,
        // BufferedImage.TYPE_BYTE_GRAY);
        // g = bi.getGraphics();
        // g.setColor(Color.WHITE);
        // }
        //
        // float[] coords = PrototypeRenderer.calcCoords(ic, id);
        //
        // g.drawLine((int) coords[0], (int) coords[1], (int) coords[2],
        // (int) coords[3]);
        // }
        // g.dispose();
        //
        // ImageIO.write(bi, "PNG", new File("protos/" + id + ".png"));
        // cls++;
        // }
    }
}
