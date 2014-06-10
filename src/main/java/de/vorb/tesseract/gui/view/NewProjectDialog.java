package de.vorb.tesseract.gui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class NewProjectDialog extends Dialog {
    protected Object result;
    protected final Shell dialog;
    private Text text;
    private Text text_1;

    /**
     * Create the dialog.
     * 
     * @param parent
     * @param style
     */
    private NewProjectDialog(Shell parent) {
        super(parent, SWT.NONE);

        dialog = new Shell(getParent(), SWT.DIALOG_TRIM);
        dialog.setMinimumSize(new Point(140, 160));
        dialog.setImage(SWTResourceManager.getImage(getClass(),
                "/logos/logo_16.png"));
        dialog.setSize(450, 387);
        dialog.setText("New Tesseract Project");
        GridLayout gl_dialogNewProject = new GridLayout(1, false);
        gl_dialogNewProject.verticalSpacing = 0;
        gl_dialogNewProject.marginWidth = 0;
        gl_dialogNewProject.marginHeight = 0;
        dialog.setLayout(gl_dialogNewProject);

        Composite composite_1 = new Composite(dialog, SWT.NONE);
        GridData gd_composite_1 = new GridData(SWT.FILL, SWT.TOP, true, false,
                1, 1);
        gd_composite_1.heightHint = 64;
        composite_1.setLayoutData(gd_composite_1);
        composite_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

        Label lblCreateANew = new Label(composite_1, SWT.NONE);
        lblCreateANew.setFont(SWTResourceManager.getFont("Segoe UI", 10,
                SWT.BOLD));
        lblCreateANew.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        lblCreateANew.setBounds(10, 10, 424, 17);
        lblCreateANew.setText("Create a new Tesseract project");

        Label label = new Label(dialog, SWT.SEPARATOR
                | SWT.HORIZONTAL);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1,
                1));

        Composite composite_2 = new Composite(dialog, SWT.NONE);
        GridLayout gl_composite_2 = new GridLayout(3, false);
        gl_composite_2.verticalSpacing = 10;
        gl_composite_2.horizontalSpacing = 10;
        gl_composite_2.marginWidth = 15;
        gl_composite_2.marginHeight = 15;
        composite_2.setLayout(gl_composite_2);
        composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true,
                1, 1));

        Label lblTitle = new Label(composite_2, SWT.NONE);
        lblTitle.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
                false, 1, 1));
        lblTitle.setText("Title:");

        text = new Text(composite_2, SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        new Label(composite_2, SWT.NONE);

        Button btnDefaultLocation = new Button(composite_2, SWT.CHECK);
        btnDefaultLocation.setSelection(true);
        btnDefaultLocation.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
                false, false, 3, 1));
        btnDefaultLocation.setText("Use default location");

        Label lblLocation = new Label(composite_2, SWT.NONE);
        lblLocation.setEnabled(false);
        lblLocation.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
                false, 1, 1));
        lblLocation.setText("Location:");

        text_1 = new Text(composite_2, SWT.BORDER);
        text_1.setEnabled(false);
        text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
                1));

        Button btnBrowse = new Button(composite_2, SWT.NONE);
        btnBrowse.setEnabled(false);
        btnBrowse.setText("Browse...");

        Group grpProject = new Group(composite_2, SWT.NONE);
        grpProject.setText("Preferences");
        GridLayout gl_grpProject = new GridLayout(2, false);
        gl_grpProject.marginHeight = 10;
        gl_grpProject.verticalSpacing = 10;
        gl_grpProject.horizontalSpacing = 10;
        gl_grpProject.marginWidth = 10;
        grpProject.setLayout(gl_grpProject);
        grpProject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
                false, 3, 1));

        Label btnCheckButton = new Label(grpProject, SWT.CHECK);
        btnCheckButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
                false, 1, 1));
        btnCheckButton.setText("Project type:");

        Combo combo_1 = new Combo(grpProject, SWT.NONE);
        combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
                1, 1));

        Label lblLanguage = new Label(grpProject, SWT.NONE);
        lblLanguage.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
                false, 1, 1));
        lblLanguage.setText("Language:");

        Combo combo = new Combo(grpProject, SWT.NONE);
        combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1,
                1));

        Label label_1 = new Label(dialog, SWT.SEPARATOR
                | SWT.HORIZONTAL);
        label_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
                1, 1));

        Composite composite = new Composite(dialog, SWT.NONE);
        GridLayout gl_composite = new GridLayout(3, false);
        gl_composite.verticalSpacing = 10;
        gl_composite.marginWidth = 10;
        gl_composite.marginHeight = 10;
        gl_composite.horizontalSpacing = 10;
        composite.setLayout(gl_composite);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
                false, 1, 1));

        Label lblNewLabel = new Label(composite, SWT.NONE);
        lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true,
                false, 1, 1));

        Button btnNewButton = new Button(composite, SWT.NONE);
        GridData gd_btnNewButton = new GridData(SWT.LEFT, SWT.CENTER, false,
                false, 1, 1);
        gd_btnNewButton.widthHint = 80;
        btnNewButton.setLayoutData(gd_btnNewButton);
        btnNewButton.setTouchEnabled(true);
        btnNewButton.setSelection(true);
        btnNewButton.setText("Create");

        Button btnCancel = new Button(composite, SWT.NONE);
        GridData gd_btnCancel = new GridData(SWT.LEFT, SWT.CENTER, false,
                false, 1, 1);
        gd_btnCancel.widthHint = 80;
        btnCancel.setLayoutData(gd_btnCancel);
        btnCancel.setText("Cancel");
    }

    /**
     * Open the dialog.
     * 
     * @return the result
     */
    public static Object open(Shell parent) {
        NewProjectDialog dialog = new NewProjectDialog(parent);
        dialog.dialog.open();
        dialog.dialog.layout();
        Display display = dialog.getParent().getDisplay();
        while (!dialog.dialog.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        return dialog.result;
    }

    /**
     * Create contents of the dialog.
     */
    private void createContents() {

    }
}
