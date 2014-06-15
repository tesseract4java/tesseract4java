package de.vorb.tesseract.gui.view;

import java.awt.Desktop;
import java.net.URI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

public class AboutDialog extends Dialog {
    protected final Shell shell;

    /**
     * Create the dialog.
     * 
     * @param parent
     * @param style
     */
    public AboutDialog(Shell parent) {
        super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        setText("SWT Dialog");

        shell = new Shell(getParent(), getStyle());
        shell.setImage(SWTResourceManager.getImage(AboutDialog.class, "/icons/information.png"));
        shell.setSize(340, 270);
        shell.setText(getText());
        GridLayout gl_shell = new GridLayout(2, false);
        gl_shell.verticalSpacing = 10;
        gl_shell.marginWidth = 15;
        gl_shell.marginHeight = 15;
        gl_shell.horizontalSpacing = 10;
        shell.setLayout(gl_shell);

        final SelectionAdapter linkHandler = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent evt) {
                if (!Desktop.isDesktopSupported()) {
                    showError();
                }

                try {
                    // open link in browser
                    Desktop.getDesktop().browse(new URI(evt.text));
                } catch (Exception e) {
                    showError();
                }
            }

            private void showError() {
                System.err.println("error");
            }
        };

        Label lblLogo = new Label(shell, SWT.NONE);
        lblLogo.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1,
                2));
        lblLogo.setImage(SWTResourceManager.getImage(AboutDialog.class,
                "/logos/logo_96.png"));

        Link lblCopyright = new Link(shell, SWT.NONE);
        lblCopyright.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true,
                false, 1, 1));
        lblCopyright.setText("© 2014 <a href=\"http://paul.vorba.ch/\">Paul Vorbach</a>. All rights reserved.");
        lblCopyright.addSelectionListener(linkHandler);

        Link lblAttributions = new Link(shell, SWT.NONE);
        lblAttributions.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true,
                true, 1,
                1));
        lblAttributions.setText("This application uses icons from the <a href=\"http://www.famfamfam.com/lab/icons/silk/\">Silk icon set</a> by Mark James, which were released under a <a href=\"http://creativecommons.org/licenses/by/2.5/\">Creative Commons Attribution 2.5 License</a>.\r\n\r\nIt also uses several variants of the font <a href=\"https://www.google.com/fonts/specimen/Roboto\">Roboto</a> by Christian Robertson, which were released under an <a href=\"http://www.apache.org/licenses/LICENSE-2.0.html\">Apache License 2.0</a>, as well as <a href=\"http://unifraktur.sourceforge.net/maguntia.html\">Unifraktur Maguntia</a>, which was released under the <a href=\"http://scripts.sil.org/cms/scripts/page.php?site_id=nrsi&id=OFL\">SIL Open Font License</a>.");
        lblAttributions.addSelectionListener(linkHandler);

        Button btnNewButton = new Button(shell, SWT.NONE);
        btnNewButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                shell.dispose();
            }
        });
        GridData gd_btnNewButton = new GridData(SWT.CENTER, SWT.CENTER, false,
                false, 2, 1);
        gd_btnNewButton.widthHint = 70;
        btnNewButton.setLayoutData(gd_btnNewButton);
        btnNewButton.setText("OK");
    }

    /**
     * Open the dialog.
     */
    public void open() {
        shell.open();
        shell.layout();
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }
}
