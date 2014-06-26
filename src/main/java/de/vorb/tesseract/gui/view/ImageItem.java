package de.vorb.tesseract.gui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ImageItem extends Composite {
    private final Label image;
    private final Label label;

    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public ImageItem(Composite parent, int style) {
        super(parent, style);
        setLayout(new GridLayout(1, false));

        image = new Label(this, SWT.NONE);
        image.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1,
                1));

        label = new Label(this, SWT.NONE);
        label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false,
                1, 1));
    }

    public void setImage(Image image) {
        this.image.setImage(image);
    }

    public void setLabel(String label) {
        this.label.setText(label);
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }
}
