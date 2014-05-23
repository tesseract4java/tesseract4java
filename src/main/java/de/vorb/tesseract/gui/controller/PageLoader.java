package de.vorb.tesseract.gui.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.bridj.Pointer;

import de.vorb.leptonica.LibLept;
import de.vorb.leptonica.Pix;
import de.vorb.leptonica.util.PixConversions;
import de.vorb.tesseract.LibTess;
import de.vorb.tesseract.OCREngineMode;
import de.vorb.tesseract.PageSegMode;
import de.vorb.tesseract.gui.event.LanguageChangeListener;
import de.vorb.tesseract.tools.recognition.Recognition;

public class PageLoader extends Recognition {
    private BufferedImage originalImg = null;
    private Pointer<Pix> originalRef = null;

    public PageLoader(String language) throws IOException {
        super(language);
    }

    @Override
    protected void init() throws IOException {
        setHandle(LibTess.TessBaseAPICreate());
    }

    @Override
    protected void reset() throws IOException {
        // init LibTess with data path, language and OCR engine mode
        LibTess.TessBaseAPIInit2(
                getHandle(),
                Pointer.pointerToCString("E:\\Masterarbeit\\Ressourcen\\tessdata"),
                Pointer.pointerToCString(getLanguage()), OCREngineMode.DEFAULT);

        // set page segmentation mode
        LibTess.TessBaseAPISetPageSegMode(getHandle(), PageSegMode.AUTO);
    }

    @Override
    protected void close() throws IOException {
        LibTess.TessBaseAPIDelete(getHandle());
    }

    public void setOriginalImage(BufferedImage image) {
        if (originalRef != null) {
            LibLept.pixDestroy(Pointer.pointerToPointer(originalRef));
        }

        originalImg = image;
        originalRef = PixConversions.img2pix(image);

        LibTess.TessBaseAPISetImage2(getHandle(), PixConversions.img2pix(image));
    }

    public BufferedImage getOriginalImage() {
        return originalImg;
    }

    public BufferedImage getThresholdedImage() {
        if (originalImg.getType() == BufferedImage.TYPE_BYTE_BINARY) {
            return originalImg;
        }

        final Pointer<Pix> img = LibTess.TessBaseAPIGetThresholdedImage(getHandle());

        return PixConversions.pix2img(img);
    }
}
