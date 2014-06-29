package de.vorb.tesseract.tools;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.bridj.BridJ;
import org.bridj.Pointer;

import de.vorb.tesseract.LibTess;
import de.vorb.tesseract.OCREngineMode;
import de.vorb.tesseract.PageIteratorLevel;
import de.vorb.tesseract.PageSegMode;
import de.vorb.tesseract.tools.recognition.DefaultRecognitionConsumer;
import de.vorb.tesseract.tools.recognition.Recognition;
import de.vorb.leptonica.Pix;
import de.vorb.leptonica.util.PixConversions;

public class SymbolRecognition extends Recognition {
    public SymbolRecognition(String language) throws IOException {
        super(language);
    }

    @Override
    protected void init() throws IOException {
        setHandle(LibTess.TessBaseAPICreate());
    }

    @Override
    public void reset() throws IOException {
        // init LibTess with data path, language and OCR engine mode
        LibTess.TessBaseAPIInit2(
                getHandle(),
                Pointer.pointerToCString("E:\\Masterarbeit\\Ressourcen\\tessdata"),
                Pointer.pointerToCString(getLanguage()),
                OCREngineMode.DEFAULT);

        // set page segmentation mode
        LibTess.TessBaseAPISetPageSegMode(getHandle(),
                PageSegMode.AUTO);

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
        LibTess.TessBaseAPISetImage(getHandle(),
                Pointer.pointerToBytes(ByteBuffer.wrap(imageData)), width,
                height,
                bytesPerPixel, bytesPerLine);

        final Pointer<Pix> bin = LibTess.TessBaseAPIGetThresholdedImage(getHandle());
        ImageIO.write(PixConversions.pix2img(bin), "PNG", new File(
                "fine.png"));
    }

    @Override
    public void close() throws IOException {
        LibTess.TessBaseAPIDelete(getHandle());
    }

    public static void main(String[] args) throws IOException {
        BridJ.setNativeLibraryFile("tesseract", new File("libtesseract303.dll"));

        final PageIteratorLevel level = PageIteratorLevel.SYMBOL;

        long start = System.currentTimeMillis();
        new SymbolRecognition("deu-frak").recognize(new DefaultRecognitionConsumer() {
            @Override
            public void wordBegin() {
                System.out.println(getState().getBaseline(
                        PageIteratorLevel.WORD) + ", attrs: "
                        + getState().getWordFontAttributes());
                System.out.println();
            }

            @Override
            public void symbol() {
                System.out.println(getState().getText(level) + ": " +
                        getState().getBoundingBox(level) + ", conf: " +
                        getState().getConfidence(level));
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

        System.out.println("time: " + (System.currentTimeMillis() - start)
                + "ms");
    }
}
