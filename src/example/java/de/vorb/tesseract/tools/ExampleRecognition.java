package de.vorb.tesseract.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

import org.bridj.BridJ;
import org.bridj.Pointer;

import de.vorb.leptonica.Pix;
import de.vorb.leptonica.util.PixConversions;
import de.vorb.tesseract.LibTess;
import de.vorb.tesseract.OCREngineMode;
import de.vorb.tesseract.PageIteratorLevel;
import de.vorb.tesseract.PageSegMode;
import de.vorb.tesseract.tools.recognition.DefaultRecognitionConsumer;
import de.vorb.tesseract.tools.recognition.RecognitionProducer;

public class ExampleRecognition extends RecognitionProducer {
    private static SwingWorker<Void, Void> worker;

    public ExampleRecognition(String language) throws IOException {
        super(language);
    }

    @Override
    public void reset() throws IOException {
        // init LibTess with data path, language and OCR engine mode
        LibTess.TessBaseAPIInit2(
                getHandle(),
                Pointer.pointerToCString("E:\\Masterarbeit\\Ressourcen\\tessdata"),
                Pointer.pointerToCString(getTrainingFile()), OCREngineMode.DEFAULT);

        // set page segmentation mode
        LibTess.TessBaseAPISetPageSegMode(getHandle(), PageSegMode.AUTO);

        // read the image into memory
        final BufferedImage inputImage = ImageIO.read(new File(
                "E:\\Masterarbeit\\Ressourcen\\DE-20__32_AM_49000_L869_G927-1\\sauvola\\DE-20__32_AM_49000_L869_G927-1__0001.png"));

        // convert bufferedimage to pix
        final long start1 = System.currentTimeMillis();
        final Pointer<Pix> pix = PixConversions.img2pix(inputImage);
        final long time1 = System.currentTimeMillis() - start1;
        System.out.println("buf2pix: " + time1 + "ms");

        final BufferedImage img = PixConversions.pix2img(pix);
        ImageIO.write(img, "PNG", new File("gray.png"));

        // set the image
        LibTess.TessBaseAPISetImage2(getHandle(), pix);

        final Pointer<Pix> binPix = LibTess.TessBaseAPIGetThresholdedImage(getHandle());

        // convert pix to bufferedimage
        final long start2 = System.currentTimeMillis();
        final BufferedImage binImg = PixConversions.pix2img(binPix);
        final long time2 = System.currentTimeMillis() - start2;
        System.out.println("pix2buf: " + time2 + "ms");

        ImageIO.write(binImg, "PNG", new File("bin.png"));
    }

    public static void main(String[] args) throws IOException,
            InterruptedException {
        BridJ.setNativeLibraryFile("tesseract", new File("libtesseract303.dll"));
        BridJ.setNativeLibraryFile("leptonica", new File("liblept170.dll"));

        final PageIteratorLevel level = PageIteratorLevel.SYMBOL;

        final long start = System.currentTimeMillis();
        final ExampleRecognition recognition = new ExampleRecognition(
                "deu-frak");

        recognition.recognize(new DefaultRecognitionConsumer() {
            @Override
            public void wordBegin() {
                System.out.println(getState().getBaseline(
                        PageIteratorLevel.WORD) + ", attrs: "
                        + getState().getWordFontAttributes());
                System.out.println();
            }

            @Override
            public void symbol() {
                System.out.println(getState().getText(level) + ": "
                        + getState().getBoundingBox(level)
                        + ", conf: " + getState().getConfidence(level));
            }

            @Override
            public void wordEnd() {
                System.out.println();
            }

            @Override
            public boolean isCancelled() {
                return false;
            }
        });

        final Pointer<Pix> tmp =
                LibTess.TessBaseAPIGetThresholdedImage(recognition.getHandle());

        worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                System.out.println(Thread.currentThread());

                // init LibTess with data path, language and OCR engine mode
                LibTess.TessBaseAPIInit2(
                        recognition.getHandle(),
                        Pointer.pointerToCString("E:\\Masterarbeit\\Ressourcen\\tessdata"),
                        Pointer.pointerToCString("deu-frak"),
                        OCREngineMode.DEFAULT);

                // set page segmentation mode
                LibTess.TessBaseAPISetPageSegMode(recognition.getHandle(),
                        PageSegMode.AUTO);

                final BufferedImage img = ImageIO.read(new File(
                        "E:\\Masterarbeit\\Ressourcen\\DE-20__32_AM_49000_L869_G927-1\\sauvola\\DE-20__32_AM_49000_L869_G927-1__0071__0044.png"));
                LibTess.TessBaseAPISetImage2(recognition.getHandle(),
                        PixConversions.img2pix(img));

                recognition.recognize(new DefaultRecognitionConsumer() {
                    @Override
                    public void wordBegin() {
                        System.out.println(getState().getBaseline(
                                PageIteratorLevel.WORD) + ", attrs: "
                                + getState().getWordFontAttributes());
                        System.out.println();
                    }

                    @Override
                    public void symbol() {
                        System.out.println(getState().getText(level) + ": "
                                + getState().getBoundingBox(level)
                                + ", conf: " + getState().getConfidence(level));
                    }

                    @Override
                    public void wordEnd() {
                        System.out.println();
                    }

                    @Override
                    public boolean isCancelled() {
                        return false;
                    }
                });

                notifyAll();

                return null;
            }

            @Override
            public void done() {
                System.out.println("time: "
                        + (System.currentTimeMillis() - start) +
                        "ms");
            }
        };

        worker.execute();

        while (!worker.isDone())
            Thread.sleep(1000L);
    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void init() throws IOException {
        // TODO Auto-generated method stub

    }
}
