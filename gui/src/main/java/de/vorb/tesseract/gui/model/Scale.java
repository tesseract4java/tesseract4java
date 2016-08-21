package de.vorb.tesseract.gui.model;

import java.util.NoSuchElementException;
import java.util.Observable;

public class Scale extends Observable {
    private static final float[] VALUES =
            new float[]{0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.75f, 1f, 2f};
    private int cursor = 2;

    public boolean hasNext() {
        return cursor < VALUES.length - 1;
    }

    public boolean hasPrevious() {
        return cursor != 0;
    }

    public float next() {
        if (!hasNext())
            throw new NoSuchElementException();

        cursor++;

        changed();

        return VALUES[cursor];
    }

    public float previous() {
        if (!hasPrevious())
            throw new NoSuchElementException();

        cursor--;

        changed();

        return VALUES[cursor];
    }

    public float current() {
        return VALUES[cursor];
    }

    public void setTo100Percent() {
        cursor = 6;

        changed();
    }

    private void changed() {
        setChanged();
        notifyObservers();
        clearChanged();
    }

    @Override
    public String toString() {
        final String result;

        switch (cursor) {
            case 0:
                result = "10%";
                break;
            case 1:
                result = "20%";
                break;
            case 2:
                result = "30%";
                break;
            case 3:
                result = "40%";
                break;
            case 4:
                result = "50%";
                break;
            case 5:
                result = "75%";
                break;
            case 6:
                result = "100%";
                break;
            case 7:
                result = "200%";
                break;
            default:
                throw new RuntimeException("illegal scale state");
        }

        return result;
    }

    public static int scaled(int coordinate, float scale) {
        return Math.round(coordinate * scale);
    }

    public static int unscaled(int coord, float scale) {
        return Math.round(coord / scale);
    }
}
