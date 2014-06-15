package de.vorb.tesseract.gui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
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
    private final MenuItem mntmSave;
    private final MenuItem mntmSaveProject;
    private final MenuItem mntmExit;
    private final MenuItem mntmCloseProject;
    private final SashForm sash;
    private final List pageList;
    private final List languageList;
    private final MenuItem mntmAbout;
    private MenuItem mntmRefreshPage;

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

        Menu fileMenu = new Menu(mntmFile);
        mntmFile.setMenu(fileMenu);

        mntmNewProject = new MenuItem(fileMenu, SWT.NONE);
        mntmNewProject.setAccelerator(SWT.MOD1 + 'N');
        mntmNewProject.setText("New Project\tCtrl+N");

        mntmOpenProject = new MenuItem(fileMenu, SWT.NONE);
        mntmOpenProject.setAccelerator(SWT.MOD1 + 'O');
        mntmOpenProject.setText("Open Project\tCtrl+O");

        new MenuItem(fileMenu, SWT.SEPARATOR);

        mntmSave = new MenuItem(fileMenu, SWT.NONE);
        mntmSave.setEnabled(false);
        mntmSave.setImage(SWTResourceManager.getImage(TesseractView.class,
                "/icons/disk.png"));
        mntmSave.setAccelerator(SWT.MOD1 | 'S');
        mntmSave.setText("&Save\tCtrl+S");

        mntmSaveProject = new MenuItem(fileMenu, SWT.NONE);
        mntmSaveProject.setEnabled(false);
        mntmSaveProject.setImage(SWTResourceManager.getImage(
                TesseractView.class, "/icons/disk_multiple.png"));
        mntmSaveProject.setText("Save Project\tCtrl+Shift+S");

        new MenuItem(fileMenu, SWT.SEPARATOR);

        mntmCloseProject = new MenuItem(fileMenu, SWT.NONE);
        mntmCloseProject.setEnabled(false);
        mntmCloseProject.setText("Close Project");

        new MenuItem(fileMenu, SWT.SEPARATOR);

        mntmExit = new MenuItem(fileMenu, SWT.NONE);
        mntmExit.setText("Exit");

        MenuItem mntmEdit = new MenuItem(menu, SWT.CASCADE);
        mntmEdit.setText("Edit");

        Menu editMenu = new Menu(mntmEdit);
        mntmEdit.setMenu(editMenu);

        MenuItem mntmRecognize = new MenuItem(editMenu, SWT.NONE);
        mntmRecognize.setText("Recognize All");

        new MenuItem(editMenu, SWT.SEPARATOR);

        MenuItem mntmGlobalpreferences = new MenuItem(editMenu, SWT.NONE);
        mntmGlobalpreferences.setImage(SWTResourceManager.getImage(
                TesseractView.class, "/icons/wrench.png"));
        mntmGlobalpreferences.setText("GlobalPreferences");

        MenuItem mntmView = new MenuItem(menu, SWT.CASCADE);
        mntmView.setText("View");

        Menu menu_3 = new Menu(mntmView);
        mntmView.setMenu(menu_3);

        mntmRefreshPage = new MenuItem(menu_3, SWT.NONE);
        mntmRefreshPage.setImage(SWTResourceManager.getImage(
                TesseractView.class, "/icons/arrow_refresh.png"));
        mntmRefreshPage.setText("Refresh Page\tF5");
        mntmRefreshPage.setAccelerator(SWT.F5);

        new MenuItem(menu_3, SWT.SEPARATOR);

        MenuItem mntmBoxEditor = new MenuItem(menu_3, SWT.RADIO);
        mntmBoxEditor.setSelection(true);
        mntmBoxEditor.setImage(SWTResourceManager.getImage(TesseractView.class,
                "/icons/application_view_tile.png"));
        mntmBoxEditor.setText("Box Editor");

        MenuItem mntmGlyphExport = new MenuItem(menu_3, SWT.RADIO);
        mntmGlyphExport.setImage(SWTResourceManager.getImage(
                TesseractView.class, "/icons/application_put.png"));
        mntmGlyphExport.setText("Glyph Export");

        MenuItem mntmComparison = new MenuItem(menu_3, SWT.RADIO);
        mntmComparison.setImage(SWTResourceManager.getImage(
                TesseractView.class, "/icons/application_tile_horizontal.png"));
        mntmComparison.setText("Text Recognition");

        MenuItem mntmHelp = new MenuItem(menu, SWT.CASCADE);
        mntmHelp.setText("Help");

        Menu menu_4 = new Menu(mntmHelp);
        mntmHelp.setMenu(menu_4);

        mntmAbout = new MenuItem(menu_4, SWT.NONE);
        mntmAbout.setImage(SWTResourceManager.getImage(TesseractView.class,
                "/icons/information.png"));
        mntmAbout.setText("About");

        statusBar = new Composite(this, SWT.NONE);
        FormData fd_statusBar = new FormData();
        fd_statusBar.bottom = new FormAttachment(100, 0);
        fd_statusBar.left = new FormAttachment(0);
        fd_statusBar.right = new FormAttachment(100);
        statusBar.setLayoutData(fd_statusBar);
        statusBar.setLayout(new GridLayout(2, false));

        sash = new SashForm(this, SWT.SMOOTH);
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
        sash.setLayoutData(fd_sashForm);

        Composite composite_1 = new Composite(sash, SWT.NONE);

        composite_1.setLayout(new FormLayout());

        SashForm sashForm_1 = new SashForm(composite_1, SWT.VERTICAL);
        FormData fd_composite_1 = new FormData();
        fd_composite_1.top = new FormAttachment(0, 25);
        fd_composite_1.bottom = new FormAttachment(100);
        fd_composite_1.right = new FormAttachment(100);
        fd_composite_1.left = new FormAttachment(0);
        sashForm_1.setLayoutData(fd_composite_1);

        Group grpPages = new Group(sashForm_1, SWT.NONE);
        grpPages.setText("Page");
        FillLayout fl_grpPages = new FillLayout(SWT.HORIZONTAL);
        fl_grpPages.marginWidth = 4;
        fl_grpPages.marginHeight = 4;
        grpPages.setLayout(fl_grpPages);

        pageList = new List(grpPages, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        pageList.setItems(new String[] {});

        Group grpLanguages = new Group(sashForm_1, SWT.NONE);
        grpLanguages.setText("Training File");
        FillLayout fl_grpLanguages = new FillLayout(SWT.HORIZONTAL);
        fl_grpLanguages.marginWidth = 4;
        fl_grpLanguages.marginHeight = 4;
        grpLanguages.setLayout(fl_grpLanguages);

        languageList = new List(grpLanguages, SWT.BORDER | SWT.H_SCROLL
                | SWT.V_SCROLL);
        sashForm_1.setWeights(new int[] { 3, 1 });
        Composite composite_2 = new BoxFileView(sash, SWT.NONE);

        setProgress(ProgressState.DEFAULT, 0);

        final int[] sashWeights = new int[2];
        updateSashWeights(sashWeights);

        addListener(SWT.Resize, new Listener() {
            @Override
            public void handleEvent(Event e) {
                updateSashWeights(sashWeights);
            }
        });
    }

    private void updateSashWeights(int[] sashWeights) {
        // left column has fixed width
        final int overallWidth = getSize().x;
        final int selectionWidth = sash.getWeights()[0];

        sashWeights[0] = selectionWidth;
        sashWeights[1] = overallWidth - selectionWidth - sash.getSashWidth();
        sash.setWeights(sashWeights);
    }

    public Widget getNewProjectWidget() {
        return mntmNewProject;
    }

    public Widget getOpenProjectWidget() {
        return mntmOpenProject;
    }

    public Widget getCloseProjectWidget() {
        return mntmCloseProject;
    }

    public Widget getSaveWidget() {
        return mntmSaveProject;
    }

    public Widget getExitWidget() {
        return mntmExit;
    }

    public Widget getRefreshPageWidget() {
        return mntmRefreshPage;
    }

    public Widget getAboutWidget() {
        return mntmAbout;
    }

    public List getPageList() {
        return pageList;
    }

    public List getLanguageList() {
        return languageList;
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
