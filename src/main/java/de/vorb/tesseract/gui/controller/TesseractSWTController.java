package de.vorb.tesseract.gui.controller;

import org.eclipse.swt.widgets.Display;

import de.vorb.tesseract.gui.view.TesseractView;

public class TesseractSWTController {
    private final TesseractView view;

    public TesseractSWTController(Display display) {
        view = new TesseractView(display);
        view.open();
        view.layout();
    }

    public static void main(String[] args) {
        final Display display = Display.getDefault();
        final TesseractSWTController controller = new TesseractSWTController(
                display);

        while (!controller.view.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }
}
