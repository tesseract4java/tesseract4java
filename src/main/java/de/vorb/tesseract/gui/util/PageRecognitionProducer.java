package de.vorb.tesseract.gui.util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.bridj.Pointer;

import com.google.common.base.Optional;

import de.vorb.leptonica.LibLept;
import de.vorb.leptonica.Pix;
import de.vorb.tesseract.INT_FEATURE_STRUCT;
import de.vorb.tesseract.LibTess;
import de.vorb.tesseract.LibTess.TessPageIterator;
import de.vorb.tesseract.LibTess.TessResultIterator;
import de.vorb.tesseract.OCREngineMode;
import de.vorb.tesseract.PageIteratorLevel;
import de.vorb.tesseract.PageSegMode;
import de.vorb.tesseract.TBLOB;
import de.vorb.tesseract.tools.recognition.RecognitionProducer;
import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.feat.Features3D;

public class PageRecognitionProducer extends RecognitionProducer {
    private final Path tessdataDir;
    private Optional<Pointer<Pix>> lastPix = Optional.absent();

    private HashMap<String, String> variables = new HashMap<>();

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

        // set variables
        for (Entry<String, String> var : variables.entrySet()) {
            LibTess.TessBaseAPISetVariable(getHandle(),
                    Pointer.pointerToCString(var.getKey()),
                    Pointer.pointerToCString(var.getValue()));
        }
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

    public List<Features3D> getFeaturesForSymbol(Box box) {
        if (!lastPix.isPresent()) {
            return new LinkedList<Features3D>();
        }

        LibTess.TessBaseAPISetPageSegMode(getHandle(), PageSegMode.SINGLE_CHAR);
        LibTess.TessBaseAPISetRectangle(getHandle(), box.getX(), box.getY(),
                box.getWidth(), box.getHeight());
        LibTess.TessBaseAPIRecognize(getHandle(), null);

        // padding around clipping box
        final int padding = 5;

        // calculate the clipping box coords
        final Pix p = lastPix.get().get();
        final int x = Math.max(box.getX() - padding, 0);
        final int y = Math.max(box.getY() - padding, 0);
        final int w = Math.min(box.getWidth() + padding + padding, p.w() - x);
        final int h = Math.min(box.getHeight() + padding + padding, p.h() - y);

        // create the box
        final Pointer<de.vorb.leptonica.Box> pBox = LibLept.boxCreate(x, y, w,
                h);

        // take the clipped pix
        final Pointer<Pix> pixSymb = LibLept.pixClipRectangle(lastPix.get(),
                pBox, null);

        LibLept.pixWrite(
                Pointer.pointerToCString("C:\\Users\\Paul\\Desktop\\test.png"),
                pixSymb, LibLept.IFF_PNG);

        LibLept.boxDestroy(Pointer.pointerToPointer(pBox));

        final Pointer<TBLOB> blob = LibTess.TessMakeTBLOB(pixSymb);

        LibLept.pixDestroy(Pointer.pointerToPointer(pixSymb));

        final Pointer<Integer> numFeatures = Pointer.allocateInt();
        final Pointer<Integer> featOutlineIndex = Pointer.allocateInt();

        final Pointer<INT_FEATURE_STRUCT> intFeatures = Pointer.allocateArray(
                INT_FEATURE_STRUCT.class, 512);
        LibTess.TessBaseAPIGetFeaturesForBlob(getHandle(), blob, intFeatures,
                numFeatures, featOutlineIndex);

        final ArrayList<Features3D> featureList = new ArrayList<>(
                numFeatures.get());

        for (int i = 0; i < numFeatures.get(); i++) {
            final Features3D feat = Features3D.valueOf(intFeatures.get(i));
            featureList.add(feat);
            System.out.println(feat);
        }

        System.out.println(featureList.size());

        // reset window and page seg mode
        LibTess.TessBaseAPISetPageSegMode(getHandle(), PageSegMode.AUTO);
        LibTess.TessBaseAPISetRectangle(getHandle(), 0, 0, p.w(), p.h());

        return featureList;
    }

    public void setVariable(String key, String value) {
        variables.put(key, value);
    }
}
