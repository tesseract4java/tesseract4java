package de.vorb.tesseract.gui.view.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class MessageDialog extends Dialog {
    private final Shell shell;

    public static enum Type {
        INFO,
        WARNING,
        ERROR;
    }

    /**
     * Create the dialog.
     * 
     * @param parent
     * @param style
     */
    private MessageDialog(Shell parent, int style, Type type, String title,
            String message) {
        super(parent, style);
        final Display display = getParent().getDisplay();

        // get the dialog icon
        final Image icon;
        switch (type) {
        case INFO:
            icon = display.getSystemImage(SWT.ICON_INFORMATION);
            break;
        case WARNING:
            icon = display.getSystemImage(SWT.ICON_WARNING);
            break;
        default:
            icon = display.getSystemImage(SWT.ICON_ERROR);
        }

        shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        shell.setImage(icon);
        shell.setSize(340, 160);
        shell.setText(title);
        shell.setLayout(new FormLayout());

        Label lblIcon = new Label(shell, SWT.NONE);
        FormData fd_lblIcon = new FormData();
        fd_lblIcon.top = new FormAttachment(0, 10);
        fd_lblIcon.left = new FormAttachment(0, 10);
        lblIcon.setLayoutData(fd_lblIcon);
        lblIcon.setImage(icon);
        lblIcon.setSize(new Point(96, 96));

        Label lblMessage = new Label(shell, SWT.WRAP);
        FormData fd_lblMessage = new FormData();
        fd_lblMessage.top = new FormAttachment(0, 15);
        fd_lblMessage.right = new FormAttachment(100, -10);
        fd_lblMessage.left = new FormAttachment(lblIcon, 10);
        lblMessage.setLayoutData(fd_lblMessage);
        lblMessage.setText(message);

        Button btnOk = new Button(shell, SWT.NONE);
        btnOk.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                shell.dispose();
            }
        });

        fd_lblMessage.bottom = new FormAttachment(btnOk, -10);
        FormData fd_btnOk = new FormData();
        fd_btnOk.bottom = new FormAttachment(100, -10);
        fd_btnOk.left = new FormAttachment(50, -35);
        fd_btnOk.right = new FormAttachment(50, 35);
        btnOk.setLayoutData(fd_btnOk);
        btnOk.setText("OK");
    }

    private void open() {
        shell.open();
        shell.layout();
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    /**
     * Open the dialog.
     * 
     * @return the result
     */
    public static void show(Shell parent, Type type, String title,
            String message) {
        final MessageDialog dialog = new MessageDialog(parent, SWT.DIALOG_TRIM,
                type, title, message);
        dialog.open();
    }
}
