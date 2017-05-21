package de.vorb.tesseract.tools.preprocessing;

import de.vorb.tesseract.tools.preprocessing.binarization.Binarization;
import de.vorb.tesseract.tools.preprocessing.binarization.Otsu;
import de.vorb.tesseract.tools.preprocessing.filter.ImageFilter;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

public class BinarizationWithFiltersPreprocessor implements Preprocessor {

    private final Binarization binarization;
    private final List<ImageFilter> filters;

    public BinarizationWithFiltersPreprocessor(Binarization binarization, List<ImageFilter> filters) {
        this.binarization = binarization;
        this.filters = filters;
    }

    public BinarizationWithFiltersPreprocessor() {
        this(new Otsu(), Collections.emptyList());
    }

    @Override
    public BufferedImage process(BufferedImage image) {

        final BufferedImage result = binarization.binarize(image);

        filters.forEach(filter -> filter.apply(result));

        return result;
    }

    public Binarization getBinarization() {
        return binarization;
    }

    public List<ImageFilter> getFilters() {
        return Collections.unmodifiableList(filters);
    }
}
