package de.vorb.tesseract.gui.model;

import de.vorb.tesseract.util.Page;
import de.vorb.tesseract.util.Symbol;
import de.vorb.util.FileNames;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;

public class PageModel {
    private final ImageModel imageModel;
    private final Page page;
    private final String transcription;

    public PageModel(ImageModel imageModel, Page page, String string) {
        this.imageModel = imageModel;
        this.page = page;
        this.transcription = string;
    }

    public Page getPage() {
        return page;
    }

    public ImageModel getImageModel() {
        return imageModel;
    }

    public String getTranscription() {
        return transcription;
    }

    public PageModel withTranscription(String transcription) {
        if (transcription.equals(this.transcription))
            return this;

        return new PageModel(imageModel, page, transcription);
    }

    public BoxFileModel toBoxFileModel() {
        final Path boxFile = FileNames.replaceExtension(
                imageModel.getPreprocessedFile(), "box");
        final BufferedImage image = imageModel.getPreprocessedImage();

        final LinkedList<Symbol> boxes = new LinkedList<>();
        final Iterator<Symbol> symbolIt = page.symbolIterator();
        while (symbolIt.hasNext()) {
            boxes.add(symbolIt.next());
        }

        return new BoxFileModel(boxFile, image, boxes);
    }
}
