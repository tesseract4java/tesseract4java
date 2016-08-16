package de.vorb.tesseract.tools;

import de.vorb.tesseract.tools.training.InputBuffer;
import de.vorb.tesseract.tools.training.IntClass;
import de.vorb.tesseract.tools.training.IntTemplates;
import de.vorb.tesseract.tools.training.ShapeTable;
import de.vorb.tesseract.tools.training.Unicharset;
import de.vorb.tesseract.tools.visualization.PrototypeRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class IntTempExample {

    public static void main(String[] args) throws IOException {
        // Read unicharset
        final BufferedReader reader = Files.newBufferedReader(
                Paths.get("C:\\Users\\Paul\\Desktop\\tessdata\\unspecified.unicharset"),
                StandardCharsets.UTF_8);

        final Unicharset unicharset = Unicharset.readFrom(reader);

        // Read int templates
        final InputStream in =
                Files.newInputStream(Paths.get("C:\\Users\\Paul\\Desktop\\tessdata\\unspecified.inttemp"));
        final InputBuffer buf = InputBuffer.allocate(new BufferedInputStream(in));
        buf.setByteOrder(ByteOrder.BIG_ENDIAN);
        final IntTemplates it = IntTemplates.readFrom(buf);

        System.out.println("------------ shapetable: -----------");
        final InputStream in2 =
                Files.newInputStream(Paths.get("C:\\Users\\Paul\\Desktop\\tessdata\\unspecified.shapetable"));
        final InputBuffer buf2 = InputBuffer.allocate(new BufferedInputStream(
                in2));
        buf2.setByteOrder(ByteOrder.BIG_ENDIAN);
        ShapeTable.readFrom(buf2);

        final Random random = new Random(System.nanoTime());

        System.out.println(it.getClasses().size());

        int i = 0;
        for (IntClass ic : it.getClasses()) {
            final int numProtos = ic.getNumProtos();

            final BufferedImage img = new BufferedImage(256, 256,
                    BufferedImage.TYPE_INT_RGB);

            final Graphics2D g2d = img.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            final Line2D line = new Line2D.Float();

            for (int id = 0; id < numProtos; id++) {
                final Color c = new Color(random.nextInt(128) * 2,
                        random.nextInt(128) * 2,
                        random.nextInt(128) * 2);
                g2d.setColor(c);

                PrototypeRenderer.updateLine(line, ic, id);

                g2d.draw(line);
            }

            g2d.setColor(Color.white);
            g2d.drawString(unicharset.getCharacters().get(i).getText(), 1,
                    img.getHeight() - 2);

            g2d.dispose();

            boolean mult = ic.getProtoSets().size() > 1;

            ImageIO.write(img, "PNG", new File(
                    "C:\\Users\\Paul\\Desktop\\tessdata\\" + i++
                            + (mult ? "_mult" : "") + ".png"));
        }
    }
}
