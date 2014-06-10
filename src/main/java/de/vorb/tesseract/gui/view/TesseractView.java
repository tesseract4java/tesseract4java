package de.vorb.tesseract.gui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.wb.swt.SWTResourceManager;

public class TesseractView extends Shell {
    private final Composite statusBar;
    private final ProgressBar indeterminateProgressBar;
    private final ProgressBar normalProgressBar;
    private final StackLayout progressBarStack;

    private final MenuItem mntmNewProject;
    private final MenuItem mntmOpenProject;
    private final MenuItem mntmSaveProject;
    private final MenuItem mntmExit;

    public static enum ProgressState {
        DEFAULT,
        INDETERMINATE,
        NORMAL,
        ERROR,
        PAUSED;
    }

    /**
     * Create the shell.
     * 
     * @param display
     */
    public TesseractView(Display display) {
        super(display, SWT.SHELL_TRIM);

        // set the app icon
        setImages(new Image[] {
                SWTResourceManager.getImage(getClass(), "/logos/logo_16.png"),
                SWTResourceManager.getImage(getClass(), "/logos/logo_96.png"),
                SWTResourceManager.getImage(getClass(), "/logos/logo_256.png")
        });
        setText("Tesseract OCR GUI");

        setSize(960, 700);
        setMinimumSize(800, 500);
        setLayout(new FormLayout());

        Menu menu = new Menu(this, SWT.BAR);
        setMenuBar(menu);

        MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
        mntmFile.setText("File");

        Menu menu_1 = new Menu(mntmFile);
        mntmFile.setMenu(menu_1);

        mntmNewProject = new MenuItem(menu_1, SWT.NONE);
        mntmNewProject.setAccelerator(SWT.MOD1 + 'N');

        mntmNewProject.setText("New Project\tCtrl+N");

        mntmOpenProject = new MenuItem(menu_1, SWT.NONE);
        mntmOpenProject.setText("Open Project");

        new MenuItem(menu_1, SWT.SEPARATOR);

        MenuItem mntmSave = new MenuItem(menu_1, SWT.NONE);
        mntmSave.setImage(SWTResourceManager.getImage(TesseractView.class,
                "/icons/disk.png"));
        mntmSave.setAccelerator(SWT.MOD1 | 'S');
        mntmSave.setText("&Save\tCtrl+S");

        mntmSaveProject = new MenuItem(menu_1, SWT.NONE);
        mntmSaveProject.setImage(SWTResourceManager.getImage(
                TesseractView.class, "/icons/disk_multiple.png"));
        mntmSaveProject.setText("Save Project\tCtrl+Shift+S");

        new MenuItem(menu_1, SWT.SEPARATOR);

        mntmExit = new MenuItem(menu_1, SWT.NONE);
        mntmExit.setText("Exit");

        MenuItem mntmEdit = new MenuItem(menu, SWT.CASCADE);
        mntmEdit.setText("Edit");

        Menu menu_2 = new Menu(mntmEdit);
        mntmEdit.setMenu(menu_2);

        MenuItem mntmRecognize = new MenuItem(menu_2, SWT.NONE);
        mntmRecognize.setText("Recognize All");

        MenuItem mntmView = new MenuItem(menu, SWT.CASCADE);
        mntmView.setText("View");

        Menu menu_3 = new Menu(mntmView);
        mntmView.setMenu(menu_3);

        MenuItem mntmBoxEditor = new MenuItem(menu_3, SWT.RADIO);
        mntmBoxEditor.setImage(SWTResourceManager.getImage(TesseractView.class,
                "/icons/application_view_tile.png"));
        mntmBoxEditor.setText("Box Editor");

        MenuItem mntmComparison = new MenuItem(menu_3, SWT.RADIO);
        mntmComparison.setImage(SWTResourceManager.getImage(
                TesseractView.class, "/icons/application_tile_horizontal.png"));
        mntmComparison.setText("Result Comparison");

        MenuItem mntmGlyphExport = new MenuItem(menu_3, SWT.RADIO);
        mntmGlyphExport.setImage(SWTResourceManager.getImage(
                TesseractView.class, "/icons/application_put.png"));
        mntmGlyphExport.setText("Glyph Export");

        MenuItem mntmHelp = new MenuItem(menu, SWT.CASCADE);
        mntmHelp.setText("Help");

        Menu menu_4 = new Menu(mntmHelp);
        mntmHelp.setMenu(menu_4);

        MenuItem mntmAbout = new MenuItem(menu_4, SWT.NONE);
        mntmAbout.setImage(SWTResourceManager.getImage(TesseractView.class,
                "/icons/help.png"));
        mntmAbout.setText("About");

        statusBar = new Composite(this, SWT.NONE);
        FormData fd_statusBar = new FormData();
        fd_statusBar.bottom = new FormAttachment(100, 0);
        fd_statusBar.left = new FormAttachment(0);
        fd_statusBar.right = new FormAttachment(100);
        statusBar.setLayoutData(fd_statusBar);
        statusBar.setLayout(new GridLayout(2, false));

        SashForm sashForm = new SashForm(this, SWT.SMOOTH);
        FormData fd_sashForm = new FormData();
        fd_sashForm.top = new FormAttachment(0);
        fd_sashForm.bottom = new FormAttachment(statusBar, 0);

        Label label = new Label(statusBar, SWT.SHADOW_NONE);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1,
                1));

        Composite composite_3 = new Composite(statusBar, SWT.NONE);
        progressBarStack = new StackLayout();
        composite_3.setLayout(progressBarStack);
        composite_3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
                false, 1, 1));

        normalProgressBar = new ProgressBar(composite_3, SWT.NONE);
        indeterminateProgressBar = new ProgressBar(composite_3,
                SWT.INDETERMINATE);

        fd_sashForm.right = new FormAttachment(100, -4);
        fd_sashForm.left = new FormAttachment(0, 4);
        sashForm.setLayoutData(fd_sashForm);

        Composite composite_1 = new Composite(sashForm, SWT.NONE);

        composite_1.setLayout(new FormLayout());

        Composite composite = new Composite(composite_1, SWT.NONE);
        FormData fd_composite = new FormData();
        fd_composite.top = new FormAttachment(0);
        fd_composite.right = new FormAttachment(100);
        fd_composite.left = new FormAttachment(0);
        fd_composite.height = 25;
        composite.setLayoutData(fd_composite);

        SashForm sashForm_1 = new SashForm(composite_1, SWT.VERTICAL);
        FormData fd_composite_1 = new FormData();
        fd_composite_1.top = new FormAttachment(composite, 0);
        fd_composite_1.right = new FormAttachment(100);
        fd_composite_1.left = new FormAttachment(0);
        fd_composite_1.bottom = new FormAttachment(100);
        sashForm_1.setLayoutData(fd_composite_1);

        Group grpPages = new Group(sashForm_1, SWT.NONE);
        grpPages.setText("Page");
        FillLayout fl_grpPages = new FillLayout(SWT.HORIZONTAL);
        fl_grpPages.marginWidth = 4;
        fl_grpPages.marginHeight = 4;
        grpPages.setLayout(fl_grpPages);

        List list = new List(grpPages, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        list.setItems(new String[] { "a", "b", "c", "d", "e", "f", "g", "h",
                "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
                "u", "v", "w", "x", "y", "z" });

        Group grpLanguages = new Group(sashForm_1, SWT.NONE);
        grpLanguages.setText("Training File");
        FillLayout fl_grpLanguages = new FillLayout(SWT.HORIZONTAL);
        fl_grpLanguages.marginWidth = 4;
        fl_grpLanguages.marginHeight = 4;
        grpLanguages.setLayout(fl_grpLanguages);

        List list_1 = new List(grpLanguages, SWT.BORDER | SWT.H_SCROLL
                | SWT.V_SCROLL);
        sashForm_1.setWeights(new int[] { 3, 1 });
        Composite composite_2 = new BoxFileView(sashForm, SWT.NONE);

        sashForm.setWeights(new int[] { 1, 5 });

        setProgress(ProgressState.DEFAULT, 0);
    }

    public Widget getNewProjectWidget() {
        return mntmNewProject;
    }

    public Widget getOpenProjectWidget() {
        return mntmOpenProject;
    }

    public Widget getSaveWidget() {
        return mntmSaveProject;
    }

    public Widget getExitWidget() {
        return mntmExit;
    }

    /**
     * Sets the progress of the progress bar.
     * 
     * @param value
     *            value between 0 and 100
     */
    public void setProgress(ProgressState state, int value) {
        if (value < 0 || value > 100) {
            throw new IllegalArgumentException("value must be in [0, 100]");
        }

        // get the int value for the given progress state
        final int progressState;
        switch (state) {
        case INDETERMINATE:
            progressState = SWT.INDETERMINATE;
            break;
        case NORMAL:
            progressState = SWT.NORMAL;
            break;
        case ERROR:
            progressState = SWT.ERROR;
            break;
        case PAUSED:
            progressState = SWT.PAUSED;
            break;
        default:
            progressState = SWT.DEFAULT;
        }

        // show the correct progress bar
        if (state == ProgressState.INDETERMINATE) {
            progressBarStack.topControl = indeterminateProgressBar;
            normalProgressBar.setVisible(false);
            indeterminateProgressBar.setVisible(true);
        } else {
            progressBarStack.topControl = normalProgressBar;
            normalProgressBar.setSelection(value);
            indeterminateProgressBar.setVisible(false);
            normalProgressBar.setVisible(true);
        }

        // set taskbar progress state and value
        getDisplay().getSystemTaskBar().getItem(this).setProgressState(
                progressState);
        getDisplay().getSystemTaskBar().getItem(this).setProgress(value);
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }
}
