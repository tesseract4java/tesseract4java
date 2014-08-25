package de.vorb.tesseract.gui.model;

import de.vorb.tesseract.util.Page;

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
}
