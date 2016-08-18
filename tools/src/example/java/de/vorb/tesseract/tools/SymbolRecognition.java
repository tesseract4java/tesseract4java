package de.vorb.tesseract.tools;

import de.vorb.tesseract.tools.recognition.DefaultRecognitionConsumer;
import de.vorb.tesseract.tools.recognition.RecognitionProducer;

import org.bytedeco.javacpp.lept;
import org.bytedeco.javacpp.tesseract;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class SymbolRecognition extends RecognitionProducer {
    public SymbolRecognition(String language) throws IOException {
        super(language);
    }

    @Override
    public void init() throws IOException {
        setHandle(tesseract.TessBaseAPICreate());
    }

    @Override
    public void reset() throws IOException {
        // init LibTess with data path, language and OCR engine mode
        tesseract.TessBaseAPIInit2(
                getHandle(),
                "E:\\Masterarbeit\\Ressourcen\\tessdata",
                getTrainingFile(),
                tesseract.OEM_DEFAULT);

        // set page segmentation mode
        tesseract.TessBaseAPISetPageSegMode(getHandle(), tesseract.PSM_AUTO);

        // read the image into memory
        final BufferedImage inputImage = ImageIO.read(new File("input4.png"));

        // get the image data
        final DataBuffer imageBuffer = inputImage.getRaster().getDataBuffer();
        final byte[] imageData = ((DataBufferByte) imageBuffer).getData();

        // image properties
        final int width = inputImage.getWidth();
        final int height = inputImage.getHeight();
        final int bitsPerPixel = inputImage.getColorModel().getPixelSize();
        final int bytesPerPixel = bitsPerPixel / 8;
        final int bytesPerLine = (width * bitsPerPixel + 7) / 8;

        // set the image
        tesseract.TessBaseAPISetImage(getHandle(),
                ByteBuffer.wrap(imageData), width, height,
                bytesPerPixel, bytesPerLine);

        final lept.PIX bin = tesseract.TessBaseAPIGetThresholdedImage(getHandle());
        try {
            lept.pixWrite("fine.png", bin, lept.IFF_PNG);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        tesseract.TessBaseAPIDelete(getHandle());
    }

    public static void main(String[] args) throws IOException {
        final int level = tesseract.RIL_SYMBOL;

        long start = System.currentTimeMillis();
        new SymbolRecognition("deu-frak").recognize(new DefaultRecognitionConsumer() {
            @Override
            public void wordBegin() {
                System.out.println(
                        getState().getBaseline(tesseract.RIL_WORD) + ", attrs: " + getState().getWordFontAttributes());
                System.out.println();
            }

            @Override
            public void symbol() {
                System.out.println(getState().getText(level) + ": " + getState()
                        .getBoundingBox(level) + ", conf: " + getState().getConfidence(level));
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

        System.out.println(String.format("time: %d ms", System.currentTimeMillis() - start));
    }
}
