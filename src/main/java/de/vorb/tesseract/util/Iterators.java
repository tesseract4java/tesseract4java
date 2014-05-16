package de.vorb.tesseract.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Iterators {
    public static Iterator<Symbol> symbolIterator(final Page p) {
        final Iterator<Line> lineIt = p.getLines().iterator();

        return new Iterator<Symbol>() {
            Iterator<Word> wordIt = null;
            Iterator<Symbol> symbolIt = null;

            @Override
            public boolean hasNext() {
                // initialize the iterators on start
                if (symbolIt == null) {
                    if (wordIt == null) {
                        if (!lineIt.hasNext()) {
                            return false;
                        }

                        wordIt = lineIt.next().getWords().iterator();
                    }

                    if (!wordIt.hasNext()) {
                        return false;
                    }

                    symbolIt = wordIt.next().getSymbols().iterator();
                }

                if (symbolIt.hasNext()) {
                    return true;
                } else if (wordIt.hasNext()) {
                    symbolIt = wordIt.next().getSymbols().iterator();
                    return symbolIt.hasNext();
                } else if (lineIt.hasNext()) {
                    wordIt = lineIt.next().getWords().iterator();

                    if (wordIt.hasNext()) {
                        symbolIt = wordIt.next().getSymbols().iterator();
                        return symbolIt.hasNext();
                    }
                }

                return false;
            }

            @Override
            public Symbol next() {
                if ((symbolIt == null || wordIt == null) && !hasNext()) {
                    throw new NoSuchElementException("no more elements");
                }

                return symbolIt.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException(
                        "cannot remove symbol from iterator");
            }
        };
    }
}
