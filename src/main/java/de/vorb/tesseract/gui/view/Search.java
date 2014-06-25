package de.vorb.tesseract.gui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class Search extends Composite {
    private final Text searchText;
    private final Label iconCross;

    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public Search(Composite parent, int style) {
        super(parent, SWT.BORDER);
        setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        setLayout(new FormLayout());

        Label iconMagnifier = new Label(this, SWT.NONE);
        FormData fd_label = new FormData();
        fd_label.top = new FormAttachment(0, 1);
        fd_label.left = new FormAttachment(0, 1);
        fd_label.bottom = new FormAttachment(100, -1);
        iconMagnifier.setLayoutData(fd_label);
        iconMagnifier.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        iconMagnifier.setImage(SWTResourceManager.getImage(Search.class,
                "/icons/magnifier.png"));

        searchText = new Text(this, SWT.NONE);
        FormData fd_text = new FormData();
        fd_text.top = new FormAttachment(iconMagnifier, 0, SWT.TOP);
        fd_text.left = new FormAttachment(iconMagnifier, 2);
        searchText.setLayoutData(fd_text);

        iconCross = new Label(this, SWT.NONE);
        fd_text.right = new FormAttachment(iconCross, -2);
        FormData fd_iconCross = new FormData();
        fd_iconCross.top = new FormAttachment(iconMagnifier, 0, SWT.TOP);
        fd_iconCross.right = new FormAttachment(100, -1);
        iconCross.setLayoutData(fd_iconCross);
        iconCross.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        iconCross.setImage(SWTResourceManager.getImage(Search.class,
                "/icons/cross.png"));
        iconCross.setVisible(false);
        iconCross.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent evt) {
                searchText.setText("");
            }
        });

        searchText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent evt) {
                iconCross.setVisible(!searchText.getText().isEmpty());
            }
        });

        pack();
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    public Text getSearchText() {
        return searchText;
    }
}
