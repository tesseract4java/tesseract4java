package de.vorb.tesseract.gui.view;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;

public class GenericList<T> extends Composite {
    private final ItemProvider<T> listItemProvider;

    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public GenericList(Composite parent, int style, ItemProvider<T> listItemProvider) {
        super(parent, SWT.NONE);

        this.listItemProvider = listItemProvider;

        setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));

        final int orientation;
        if ((style & SWT.HORIZONTAL) == SWT.HORIZONTAL) {
            orientation = SWT.HORIZONTAL;
        } else {
            // default
            orientation = SWT.VERTICAL;
        }

        RowLayout rowLayout = new RowLayout(orientation);
        rowLayout.spacing = 5;
        setLayout(rowLayout);
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }
}
