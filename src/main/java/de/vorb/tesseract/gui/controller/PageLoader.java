package de.vorb.tesseract.gui.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.bridj.Pointer;

import de.vorb.leptonica.Pix;
import de.vorb.leptonica.util.PixConversions;
import de.vorb.tesseract.LibTess;
import de.vorb.tesseract.OCREngineMode;
import de.vorb.tesseract.PageSegMode;
import de.vorb.tesseract.tools.recognition.Recognition;

public class PageLoader extends Recognition {
    BufferedImage originalImg = null;

    public PageLoader() throws IOException {
        super();
    }

    @Override
    protected void init() throws IOException {
        handle = LibTess.TessBaseAPICreate();

        LibTess.TessBaseAPISetVariable(handle, Pointer.pointerToCString(""),
                Pointer.pointerToCString(""));

        // init LibTess with data path, language and OCR engine mode
        LibTess.TessBaseAPIInit2(
                getHandle(),
                Pointer.pointerToCString("E:\\Masterarbeit\\Ressourcen\\tessdata"),
                Pointer.pointerToCString("deu-frak"), OCREngineMode.DEFAULT);

        // set page segmentation mode
        LibTess.TessBaseAPISetPageSegMode(getHandle(), PageSegMode.AUTO);
    }

    public void setOriginalImage(BufferedImage image) {
        this.originalImg = image;

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
