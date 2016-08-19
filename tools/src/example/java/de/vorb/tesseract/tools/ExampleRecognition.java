package de.vorb.tesseract.tools;

import de.vorb.tesseract.tools.recognition.DefaultRecognitionConsumer;
import de.vorb.tesseract.tools.recognition.RecognitionProducer;

import org.bytedeco.javacpp.lept;
import org.bytedeco.javacpp.tesseract;

import javax.swing.SwingWorker;
import java.io.IOException;

import static org.bytedeco.javacpp.tesseract.RIL_WORD;

public class ExampleRecognition extends RecognitionProducer {

    private static SwingWorker<Void, Void> worker;

    public ExampleRecognition(String language) throws IOException {
        super(language);
    }

    @Override
    public void reset() throws IOException {
        // init LibTess with data path, language and OCR engine mode
        tesseract.TessBaseAPIInit2(
                getHandle(),
                "E:\\Masterarbeit\\Ressourcen\\tessdata",
                getTrainingFile(), tesseract.OEM_DEFAULT);

        // set page segmentation mode
        tesseract.TessBaseAPISetPageSegMode(getHandle(), tesseract.PSM_AUTO);

        final lept.PIX pix = lept.pixRead(
                "E:\\Masterarbeit\\Ressourcen\\DE-20__32_AM_49000_L869_G927-1\\sauvola\\DE-20__32_AM_49000_L869_G927"
                        + "-1__0001.png");

        // set the image
        tesseract.TessBaseAPISetImage2(getHandle(), pix);

        final lept.PIX binPix = tesseract.TessBaseAPIGetThresholdedImage(getHandle());

        // convert pix to bufferedimage
        lept.pixWrite("bin.png", binPix, lept.IFF_PNG);
    }

    public static void main(String[] args) throws IOException,
            InterruptedException {

        final int level = tesseract.RIL_SYMBOL;

        final long start = System.currentTimeMillis();
        final ExampleRecognition recognition = new ExampleRecognition(
                "deu-frak");

        recognition.recognize(new DefaultRecognitionConsumer() {
            @Override
            public void wordBegin() {
                System.out.println(
                        getState().getBaseline(RIL_WORD) + ", attrs: " + getState().getWordFontAttributes());
                System.out.println();
            }

            @Override
            public void symbol() {
                System.out.println(getState().getText(level) + ": " + getState().getBoundingBox(
                        level) + ", conf: " + getState().getConfidence(level));
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

        final lept.PIX tmp =
                tesseract.TessBaseAPIGetThresholdedImage(recognition.getHandle());

        worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                System.out.println(Thread.currentThread());

                // init LibTess with data path, language and OCR engine mode
                tesseract.TessBaseAPIInit2(
                        recognition.getHandle(),
                        "E:\\Masterarbeit\\Ressourcen\\tessdata",
                        "deu-frak",
                        tesseract.OEM_DEFAULT);

                // set page segmentation mode
                tesseract.TessBaseAPISetPageSegMode(recognition.getHandle(), tesseract.PSM_AUTO);

                final lept.PIX img = lept.pixRead(
                        "E:\\Masterarbeit\\Ressourcen\\DE-20__32_AM_49000_L869_G927-1\\sauvola\\DE"
                                + "-20__32_AM_49000_L869_G927-1__0071__0044.png");

                tesseract.TessBaseAPISetImage2(recognition.getHandle(), img);

                recognition.recognize(new DefaultRecognitionConsumer() {
                    @Override
                    public void wordBegin() {
                        System.out.println(getState().getBaseline(
                                tesseract.RIL_WORD) + ", attrs: "
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
