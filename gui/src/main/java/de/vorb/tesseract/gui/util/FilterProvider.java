package de.vorb.tesseract.gui.util;

import java.util.Optional;

public interface FilterProvider<T> {
    Optional<Filter<T>> getFilterFor(String filterText);
}
