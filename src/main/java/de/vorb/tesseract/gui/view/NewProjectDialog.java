package de.vorb.tesseract.gui.view;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import de.vorb.swt.dialog.MessageDialog;
import de.vorb.swt.dialog.MessageDialog.Type;
import de.vorb.swt.dialog.Modality;
import de.vorb.tesseract.util.Project;

import org.eclipse.swt.widgets.Combo;

public class NewProjectDialog extends Dialog {
    /**
     * @see {@link https://stackoverflow.com/questions/6730009}
     */
    private static final Pattern PATTERN_TITLE = Pattern.compile(
            "# Match a valid Windows filename (unspecified file system).          \n"
                    + "^                                # Anchor to start of string.        \n"
                    + "(?!                              # Assert filename is not: CON, PRN, \n"
                    + "  (?:                            # AUX, NUL, COM1, COM2, COM3, COM4, \n"
                    + "    CON|PRN|AUX|NUL|             # COM5, COM6, COM7, COM8, COM9,     \n"
                    + "    COM[1-9]|LPT[1-9]            # LPT1, LPT2, LPT3, LPT4, LPT5,     \n"
                    + "  )                              # LPT6, LPT7, LPT8, and LPT9...     \n"
                    + "  (?:\\.[^.]*)?                  # followed by optional extension    \n"
                    + "  $                              # and end of string                 \n"
                    + ")                                # End negative lookahead assertion. \n"
                    + "[^<>:\"/\\\\|?*\\x00-\\x1F]*     # Zero or more valid filename chars.\n"
                    + "[^<>:\"/\\\\|?*\\x00-\\x1F\\ .]  # Last char is not a space or dot.  \n"
                    + "$                                # Anchor to end of string.            ",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.COMMENTS);

    private static final Color COLOR_VALID = Display.getCurrent().getSystemColor(
            SWT.COLOR_LIST_BACKGROUND);
    private static final Color COLOR_INVALID = new Color(Display.getCurrent(),
            255, 200, 200);

    protected Project result;
    protected final Shell dialog;
    private final Text txtTitle;
    private final Combo comboDir;

    private final Button btnPng;
    private final Button btnTiff;
    private final Button btnJpeg;

    private final DirectoryStream.Filter<Path> pageFilter = new DirectoryStream.Filter<Path>() {
        @Override
        public boolean accept(Path entry) throws IOException {
            final String fname = entry.getFileName().toString();

            // files must end with one of the file extensions and that file ext
            // must be selected to be accepted
            if (fname.endsWith(".png")) {
                return btnPng.getSelection();
            } else if (fname.endsWith(".tif") || fname.endsWith(".tiff")) {
                return btnTiff.getSelection();
            } else if (fname.endsWith(".jpg") || fname.endsWith(".jpeg")) {
                return btnJpeg.getSelection();
            }

            return false;
        }
    };

    /**
     * Create the dialog.
     * 
     * @param parent
     * @param style
     */
    public NewProjectDialog(Shell parent) {
        super(parent, SWT.NONE);

        dialog = new Shell(getParent(), SWT.DIALOG_TRIM);
        dialog.setMinimumSize(new Point(140, 160));
        dialog.setImage(SWTResourceManager.getImage(getClass(),
                "/logos/logo_16.png"));
        dialog.setSize(450, 293);
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

        Label lblReadImagesFrom = new Label(composite_1, SWT.NONE);
        lblReadImagesFrom.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        lblReadImagesFrom.setBounds(10, 33, 424, 15);
        lblReadImagesFrom.setText("Read images from a directory");

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
        composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
                1, 1));

        Label lblTitle = new Label(composite_2, SWT.NONE);
        lblTitle.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
                false, 1, 1));
        lblTitle.setText("Title:");

        txtTitle = new Text(composite_2, SWT.BORDER);
        txtTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
                1, 1));
        txtTitle.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                validateTitle();
            }
        });
        new Label(composite_2, SWT.NONE);

        Label lblLocation = new Label(composite_2, SWT.NONE);
        lblLocation.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
                false, 1, 1));
        lblLocation.setText("Location:");

        comboDir = new Combo(composite_2, SWT.BORDER);
        comboDir.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
                1, 1));
        comboDir.setItems(new String[] {
                "C:\\Users\\Paul\\Studium\\Masterarbeit\\Ressourcen\\tessdata\\experiment04-complete-training",
                "C:\\Users\\Paul\\Studium\\Masterarbeit\\Ressourcen\\DE-20__32_AM_49000_L869_G927-1\\sauvola" });

        Button btnBrowse = new Button(composite_2, SWT.NONE);
        btnBrowse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final DirectoryDialog dirDialog = new DirectoryDialog(dialog);
                dirDialog.setText("Project directory");
                dirDialog.setMessage("Select a directory that contains this project's images.");
                final String dir = dirDialog.open();
                if (dir != null) {
                    comboDir.setText(dir);
                }
            }
        });
        btnBrowse.setText("Browse...");

        Group grpImageTypes = new Group(composite_2, SWT.NONE);
        grpImageTypes.setToolTipText("Only import selected image formats");
        grpImageTypes.setText("Image Types");
        GridLayout gl_grpImageTypes = new GridLayout(3, false);
        gl_grpImageTypes.marginWidth = 10;
        gl_grpImageTypes.marginHeight = 10;
        grpImageTypes.setLayout(gl_grpImageTypes);
        grpImageTypes.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
                true, 3, 1));

        btnPng = new Button(grpImageTypes, SWT.CHECK);
        btnPng.setSelection(true);
        btnPng.setText("PNG");

        btnTiff = new Button(grpImageTypes, SWT.CHECK);
        btnTiff.setSelection(true);
        btnTiff.setText("TIFF");

        btnJpeg = new Button(grpImageTypes, SWT.CHECK);
        btnJpeg.setText("JPEG");

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

        Button btnCreate = new Button(composite, SWT.NONE);
        btnCreate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent evt) {
                if (validate()) {
                    final Path dir = Paths.get(comboDir.getText());
                    DirectoryStream<Path> pages;
                    try {
                        pages = Files.newDirectoryStream(
                                dir, pageFilter);
                        final LinkedList<Path> pageList = new LinkedList<>();

                        for (final Path page : pages) {
                            pageList.add(page);
                        }

                        result = new Project(dir, pageList);
                        dialog.dispose();
                        return;
                    } catch (IOException e) {
                        // ignore exception, show message
                    }
                }

                MessageDialog.show(dialog, Type.ERROR, "Invalid Data",
                        "Please correct your input.",
                        Modality.APPLICATION);
            }
        });
        GridData gd_btnNewButton = new GridData(SWT.LEFT, SWT.CENTER, false,
                false, 1, 1);
        gd_btnNewButton.widthHint = 80;
        btnCreate.setLayoutData(gd_btnNewButton);
        btnCreate.setTouchEnabled(true);
        btnCreate.setSelection(true);
        btnCreate.setText("Create");

        Button btnCancel = new Button(composite, SWT.NONE);
        btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                dialog.dispose();
            }
        });
        GridData gd_btnCancel = new GridData(SWT.LEFT, SWT.CENTER, false,
                false, 1, 1);
        gd_btnCancel.widthHint = 80;
        btnCancel.setLayoutData(gd_btnCancel);
        btnCancel.setText("Cancel");
    }

    protected boolean validate() {
        return validateTitle() && validateDir();
    }

    private boolean validateTitle() {
        final boolean valid = PATTERN_TITLE.matcher(txtTitle.getText()).matches();
        setValid(txtTitle, valid);
        return valid;
    }

    private boolean validateDir() {
        try {
            final Path projDir = Paths.get(comboDir.getText());
            if (Files.isDirectory(projDir) && Files.isReadable(projDir)) {
                setValid(comboDir, true);
                return true;
            }
        } catch (InvalidPathException e) {
        }

        setValid(comboDir, false);
        return false;
    }

    protected void setValid(Control control, boolean valid) {
        control.setBackground(valid ? COLOR_VALID : COLOR_INVALID);
    }

    /**
     * Open the dialog.
     * 
     * @return the result
     */
    public Project open() {
        dialog.open();
        dialog.layout();
        Display display = dialog.getDisplay();
        while (!dialog.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        return result;
    }
}
