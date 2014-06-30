package de.vorb.tesseract.gui.util;

import java.io.IOException;
import java.nio.file.Path;

import org.bridj.Pointer;

import com.google.common.base.Optional;

import de.vorb.leptonica.LibLept;
import de.vorb.leptonica.Pix;
import de.vorb.tesseract.LibTess;
import de.vorb.tesseract.OCREngineMode;
import de.vorb.tesseract.PageSegMode;
import de.vorb.tesseract.tools.recognition.RecognitionProducer;

public class PageRecognitionProducer extends RecognitionProducer {
    private final Path tessdataDir;
    private Optional<Pointer<Pix>> lastPix = Optional.absent();

    public PageRecognitionProducer(Path tessdataDir, String trainingFile) {
        super(trainingFile);

        this.tessdataDir = tessdataDir;
    }

    @Override
    public void init() throws IOException {
        setHandle(LibTess.TessBaseAPICreate());

        reset();
    }

    @Override
    public void reset() throws IOException {
        // init LibTess with data path, language and OCR engine mode
        LibTess.TessBaseAPIInit2(getHandle(),
                Pointer.pointerToCString(tessdataDir.toString()),
                Pointer.pointerToCString(getTrainingFile()),
                OCREngineMode.DEFAULT);

        // set page segmentation mode
        LibTess.TessBaseAPISetPageSegMode(getHandle(), PageSegMode.AUTO);
    }

    @Override
    public void close() throws IOException {
        LibTess.TessBaseAPIDelete(getHandle());
    }

    public void loadImage(Path imageFile) {
        if (lastPix.isPresent()) {
            // destroy old pix
            LibLept.pixDestroy(Pointer.pointerToPointer(lastPix.get()));
        }

        final Pointer<Pix> pix =
                LibLept.pixRead(Pointer.pointerToCString(imageFile.toString()));

        LibTess.TessBaseAPISetImage2(getHandle(), pix);

        lastPix = Optional.of(pix);
    }

    public Optional<Pointer<Pix>> getImage() {
        return lastPix;
    }

    public Optional<Pointer<Pix>> getThresholdedImage() {
        return Optional.fromNullable(LibTess.TessBaseAPIGetThresholdedImage(
                getHandle()));
    }
}
