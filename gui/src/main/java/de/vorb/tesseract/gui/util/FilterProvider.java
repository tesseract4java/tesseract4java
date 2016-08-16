package de.vorb.tesseract.gui.util;

import com.google.common.base.Optional;

public interface FilterProvider<T> {
    Optional<Filter<T>> getFilterFor(String filterText);
}
