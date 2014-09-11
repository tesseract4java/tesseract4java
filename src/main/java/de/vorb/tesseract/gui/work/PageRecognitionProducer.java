package de.vorb.tesseract.gui.work;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import org.bridj.Pointer;

import com.google.common.base.Optional;

import de.vorb.leptonica.LibLept;
import de.vorb.leptonica.Pix;
import de.vorb.tesseract.INT_FEATURE_STRUCT;
import de.vorb.tesseract.LibTess;
import de.vorb.tesseract.OCREngineMode;
import de.vorb.tesseract.PageSegMode;
import de.vorb.tesseract.TBLOB;
import de.vorb.tesseract.gui.controller.TesseractController;
import de.vorb.tesseract.tools.recognition.RecognitionProducer;
import de.vorb.tesseract.util.feat.Feature3D;

public class PageRecognitionProducer extends RecognitionProducer {
    private final Path tessdataDir;
    private Optional<Pointer<Pix>> lastPix = Optional.absent();

    private final TesseractController controller;
    private final HashMap<String, String> variables = new HashMap<>();

    public PageRecognitionProducer(TesseractController controller,
            Path tessdataDir, String trainingFile) {
        super(trainingFile);

        this.controller = controller;
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
        // for (Entry<String, String> var : variables.entrySet()) {
        // LibTess.TessBaseAPISetVariable(getHandle(),
        // Pointer.pointerToCString(var.getKey()),
        // Pointer.pointerToCString(var.getValue()));
        // }
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

    public List<Feature3D> getFeaturesForSymbol(BufferedImage symbol) {
        if (!lastPix.isPresent()) {
            return new LinkedList<Feature3D>();
        }

        final int padding = 5;
        // draw a 5px white padding arround the symbol
        final BufferedImage symbWithPadding = new BufferedImage(
                symbol.getWidth() + padding + padding,
                symbol.getHeight() + padding + padding,
                BufferedImage.TYPE_BYTE_BINARY);

        // draw the symbol on the new image
        final Graphics2D g2d = symbWithPadding.createGraphics();
        g2d.setBackground(Color.WHITE);
        g2d.clearRect(0, 0, symbWithPadding.getWidth(),
                symbWithPadding.getHeight());
        g2d.drawImage(symbol, padding, padding, null);
        g2d.dispose();

        // FIXME
        if (!controller.getProjectModel().isPresent()) {
            return Collections.emptyList();
        }

        final String symbolFile = controller.getProjectModel().get().getProjectDir().resolve(
                "symbol.png").toString();
        try {
            ImageIO.write(symbWithPadding, "PNG", new File(symbolFile));
        } catch (IOException e) {
            e.printStackTrace();
            return new LinkedList<Feature3D>();
        }

        final Pointer<Pix> pixSymb =
                LibLept.pixRead(Pointer.pointerToCString(symbolFile));

        final Pointer<TBLOB> blob = LibTess.TessMakeTBLOB(pixSymb);
        LibLept.pixDestroy(Pointer.pointerToPointer(pixSymb));

        final Pointer<Integer> numFeatures = Pointer.allocateInt();
        final Pointer<Integer> outlineIndexes = Pointer.allocateInts(512);
        final Pointer<Byte> features = Pointer.allocateBytes(4 * 512);
        final Pointer<INT_FEATURE_STRUCT> intFeatures =
                features.as(INT_FEATURE_STRUCT.class);
        LibTess.TessBaseAPIGetFeaturesForBlob(getHandle(), blob, intFeatures,
                numFeatures, outlineIndexes);

        // make a list of Features3D
        final ArrayList<Feature3D> featureList = new ArrayList<>(
                numFeatures.getInt());

        for (int i = 0; i < numFeatures.get(); i++) {
            features.apply(i * 4);
            final int x = features.apply(i * 4) & 0xFF;
            final int y = features.apply(i * 4 + 1) & 0xFF;
            final int theta = features.apply(i * 4 + 2) & 0xFF;
            final byte cpMisses = features.apply(i * 4 + 3);
            final int outlineIndex = outlineIndexes.getIntAtIndex(i);
            final Feature3D feat = new Feature3D(x, y, theta, cpMisses,
                    outlineIndex);
            featureList.add(feat);
        }

        return featureList;
    }

    public void setVariable(String key, String value) {
        variables.put(key, value);
    }
}
