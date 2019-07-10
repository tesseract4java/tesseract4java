package de.vorb.tesseract.gui.model;

import de.vorb.tesseract.util.Symbol;
import de.vorb.util.FileNames;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;

public class Page {

    private final Image image;
    private final de.vorb.tesseract.util.Page page;
    private final String transcription;

    public Page(Image image, de.vorb.tesseract.util.Page page, String string) {
        this.image = image;
        this.page = page;
        this.transcription = string;
    }

    public de.vorb.tesseract.util.Page getPage() {
        return page;
    }

    public Image getImage() {
        return image;
    }

    public String getTranscription() {
        return transcription;
    }

    public Page withTranscription(String transcription) {
        if (transcription.equals(this.transcription)) {
            return this;
        }

        return new Page(image, page, transcription);
    }

    public BoxFile toBoxFileModel() {
        final Path boxFile = FileNames.replaceExtension(image.getPreprocessedFile(), "box");
        final BufferedImage image = this.image.getPreprocessedImage();

        final LinkedList<Symbol> boxes = new LinkedList<>();
        final Iterator<Symbol> symbolIt = page.symbolIterator();
        while (symbolIt.hasNext()) {
            boxes.add(symbolIt.next());
        }

        return new BoxFile(boxFile, image, boxes);
    }

}
