package de.vorb.tesseract.tools.preprocessing;

import de.vorb.tesseract.tools.preprocessing.binarization.Binarization;
import de.vorb.tesseract.tools.preprocessing.binarization.Otsu;
import de.vorb.tesseract.tools.preprocessing.filter.ImageFilter;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

public class DefaultPreprocessor implements Preprocessor {
    private final Binarization binarization;
    private final List<ImageFilter> filters;

    public DefaultPreprocessor(Binarization binarization,
            List<ImageFilter> filters) {
        this.binarization = binarization;
        this.filters = filters;
    }

    public DefaultPreprocessor() {
        this(new Otsu(), Collections.emptyList());
    }

    public DefaultPreprocessor(Binarization binarization) {
        this(binarization, Collections.emptyList());
    }

    @Override
    public BufferedImage process(BufferedImage image) {
        // apply binarization
        final BufferedImage result = binarization.binarize(image);

        // apply filters
        for (final ImageFilter f : filters) {
            f.filter(result);
        }

        return result;
    }

    public Binarization getBinarization() {
        return binarization;
    }

    public List<ImageFilter> getFilters() {
        return Collections.unmodifiableList(filters);
    }
}
