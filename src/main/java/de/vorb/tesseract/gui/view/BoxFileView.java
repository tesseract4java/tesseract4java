package de.vorb.tesseract.gui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.wb.swt.SWTResourceManager;

import de.vorb.tesseract.gui.model.PageModel;
import org.eclipse.swt.graphics.Point;

public class BoxFileView extends Composite implements PageView {
    private final Text text;
    private final Table boxEditorBoxTable;
    private final SashForm boxEditorSash;
    private final Canvas boxView;
    private final TabFolder tabFolder;

    private PageModel pageModel;
    private Image image;
    private Text text_1;
    private List symbolList;
    private ScrolledComposite boxEditorBoxView;

    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public BoxFileView(Composite parent, int style) {
        super(parent, style);

        setLayout(new FillLayout(SWT.HORIZONTAL));

        tabFolder = new TabFolder(this, SWT.NONE);

        TabItem tbtmBoxEditor = new TabItem(tabFolder, SWT.NONE);
        tbtmBoxEditor.setImage(SWTResourceManager.getImage(BoxFileView.class,
                "/icons/table_edit.png"));
        tbtmBoxEditor.setText("Box Editor");

        Composite boxEditor = new Composite(tabFolder, SWT.NONE);
        tbtmBoxEditor.setControl(boxEditor);
        boxEditor.setLayout(new FormLayout());

        Composite boxEditorToolbar = new Composite(boxEditor, SWT.NONE);
        FormData fd_boxEditorToolbar = new FormData();
        fd_boxEditorToolbar.right = new FormAttachment(100, 0);
        fd_boxEditorToolbar.top = new FormAttachment(0);
        fd_boxEditorToolbar.left = new FormAttachment(0);
        boxEditorToolbar.setLayoutData(fd_boxEditorToolbar);
        boxEditorToolbar.setLayout(new GridLayout(13, false));

        Label lblSymbol = new Label(boxEditorToolbar, SWT.NONE);
        lblSymbol.setText("Symbol");

        text = new Text(boxEditorToolbar, SWT.BORDER);
        GridData gd_text = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1,
                1);
        gd_text.widthHint = 49;
        text.setLayoutData(gd_text);
        text.setTextLimit(5);

        Label lblX = new Label(boxEditorToolbar, SWT.NONE);
        lblX.setText("X");

        Spinner spinX = new Spinner(boxEditorToolbar, SWT.BORDER);
        spinX.setMaximum(1000);

        Label lblY = new Label(boxEditorToolbar, SWT.NONE);
        lblY.setText("Y");

        Spinner spinY = new Spinner(boxEditorToolbar, SWT.BORDER);

        Label lblW = new Label(boxEditorToolbar, SWT.NONE);
        lblW.setText("W");

        Spinner spinW = new Spinner(boxEditorToolbar, SWT.BORDER);

        Label lblH = new Label(boxEditorToolbar, SWT.NONE);
        lblH.setText("H");

        Spinner spinH = new Spinner(boxEditorToolbar, SWT.BORDER);
        new Label(boxEditorToolbar, SWT.NONE);

        Button btnZoomIn = new Button(boxEditorToolbar, SWT.NONE);
        btnZoomIn.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        btnZoomIn.setImage(SWTResourceManager.getImage(BoxFileView.class,
                "/icons/zoom_in.png"));

        Button btnZoomOut = new Button(boxEditorToolbar, SWT.NONE);
        btnZoomOut.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        btnZoomOut.setImage(SWTResourceManager.getImage(BoxFileView.class,
                "/icons/zoom_out.png"));

        boxEditorSash = new SashForm(boxEditor, SWT.NONE);
        FormData fd_boxEditorSash = new FormData();
        fd_boxEditorSash.top = new FormAttachment(boxEditorToolbar, 0);
        fd_boxEditorSash.bottom = new FormAttachment(100, 0);
        fd_boxEditorSash.left = new FormAttachment(0, 0);
        fd_boxEditorSash.right = new FormAttachment(100, 0);
        boxEditorSash.setLayoutData(fd_boxEditorSash);

        boxEditorBoxTable = new Table(boxEditorSash, SWT.BORDER
                | SWT.FULL_SELECTION);
        boxEditorBoxTable.setLinesVisible(true);
        boxEditorBoxTable.setHeaderVisible(true);

        TableColumn tblclmnNo = new TableColumn(boxEditorBoxTable, SWT.NONE);
        tblclmnNo.setWidth(35);
        tblclmnNo.setText("No.");

        TableColumn tblclmnSymbol = new TableColumn(boxEditorBoxTable, SWT.NONE);
        tblclmnSymbol.setWidth(50);
        tblclmnSymbol.setText("Symbol");

        TableColumn tblclmnX = new TableColumn(boxEditorBoxTable, SWT.NONE);
        tblclmnX.setWidth(35);
        tblclmnX.setText("X");

        TableColumn tblclmnY = new TableColumn(boxEditorBoxTable, SWT.NONE);
        tblclmnY.setWidth(35);
        tblclmnY.setText("Y");

        TableColumn tblclmnWidth = new TableColumn(boxEditorBoxTable, SWT.NONE);
        tblclmnWidth.setWidth(35);
        tblclmnWidth.setText("W");

        TableColumn tblclmnH = new TableColumn(boxEditorBoxTable, SWT.NONE);
        tblclmnH.setWidth(35);
        tblclmnH.setText("H");
        boxEditorBoxTable.pack();

        boxEditorBoxView = new ScrolledComposite(
                boxEditorSash, SWT.BORDER
                        | SWT.H_SCROLL | SWT.V_SCROLL);
        boxEditorBoxView.setShowFocusedControl(true);

        boxView = new Canvas(boxEditorBoxView, SWT.NONE);
        boxView.setLayout(new FillLayout(SWT.HORIZONTAL));
        boxEditorBoxView.setContent(boxView);

        TabItem tbtmBoxOverview = new TabItem(tabFolder, SWT.NONE);
        tbtmBoxOverview.setImage(SWTResourceManager.getImage(BoxFileView.class,
                "/icons/table_refresh.png"));
        tbtmBoxOverview.setText("Box Review");

        Composite boxOverview = new Composite(tabFolder, SWT.NONE);
        tbtmBoxOverview.setControl(boxOverview);
        boxOverview.setLayout(new FormLayout());

        Composite boxOverviewAdditionalTools = new Composite(boxOverview,
                SWT.NONE);
        boxOverviewAdditionalTools.setLayout(new GridLayout(2, false));
        FormData fd_boxOverviewAdditionalTools = new FormData();
        fd_boxOverviewAdditionalTools.bottom = new FormAttachment(100, 0);
        fd_boxOverviewAdditionalTools.right = new FormAttachment(100, 0);
        fd_boxOverviewAdditionalTools.left = new FormAttachment(0);
        boxOverviewAdditionalTools.setLayoutData(fd_boxOverviewAdditionalTools);

        Label label = new Label(boxOverviewAdditionalTools, SWT.NONE);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1,
                1));

        Button btnExport = new Button(boxOverviewAdditionalTools, SWT.NONE);
        btnExport.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        btnExport.setText("Export ...");

        SashForm boxOverviewSash = new SashForm(boxOverview, SWT.NONE);
        FormData fd_boxOverviewSash = new FormData();
        fd_boxOverviewSash.top = new FormAttachment(0);
        fd_boxOverviewSash.left = new FormAttachment(0);
        fd_boxOverviewSash.right = new FormAttachment(100, 0);
        fd_boxOverviewSash.bottom = new FormAttachment(
                boxOverviewAdditionalTools);
        boxOverviewSash.setLayoutData(fd_boxOverviewSash);

        Composite grpSymbols = new Composite(boxOverviewSash, SWT.NONE);
        GridLayout gl_grpSymbols = new GridLayout(1, false);
        gl_grpSymbols.marginRight = -5;
        gl_grpSymbols.marginBottom = -5;
        grpSymbols.setLayout(gl_grpSymbols);

        Label lblNewLabel = new Label(grpSymbols, SWT.NONE);
        GridData gd_lblNewLabel = new GridData(SWT.LEFT, SWT.CENTER, true,
                false, 1, 1);
        gd_lblNewLabel.heightHint = 18;
        gd_lblNewLabel.verticalIndent = 3;
        lblNewLabel.setLayoutData(gd_lblNewLabel);
        lblNewLabel.setText("Symbols");

        symbolList = new List(grpSymbols, SWT.BORDER | SWT.H_SCROLL
                | SWT.V_SCROLL);
        symbolList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
                1, 1));
        symbolList.setItems(new String[] { "A", "B" });

        Composite composite = new Composite(boxOverviewSash, SWT.NONE);
        composite.setLayout(new FormLayout());

        Composite boxOverviewToolbar = new Composite(composite, SWT.NONE);
        FormData fd_boxOverviewToolbar = new FormData();
        fd_boxOverviewToolbar.top = new FormAttachment(0);
        fd_boxOverviewToolbar.left = new FormAttachment(0);
        fd_boxOverviewToolbar.right = new FormAttachment(100);
        boxOverviewToolbar.setLayoutData(fd_boxOverviewToolbar);
        boxOverviewToolbar.setLayout(new GridLayout(2, false));

        Label lblSymbol_1 = new Label(boxOverviewToolbar, SWT.NONE);
        lblSymbol_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
                false, 1, 1));
        lblSymbol_1.setText("Symbol");

        text_1 = new Text(boxOverviewToolbar, SWT.BORDER);
        text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
                1, 1));

        Composite symbolOverview = new Composite(composite, SWT.BORDER
                | SWT.V_SCROLL);
        FormData fd_symbolOverview = new FormData();
        fd_symbolOverview.bottom = new FormAttachment(100);
        fd_symbolOverview.right = new FormAttachment(100);
        fd_symbolOverview.top = new FormAttachment(boxOverviewToolbar);
        fd_symbolOverview.left = new FormAttachment(0);
        symbolOverview.setLayoutData(fd_symbolOverview);
        symbolOverview.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
        RowLayout rl_symbolOverview = new RowLayout(SWT.HORIZONTAL);
        symbolOverview.setLayout(rl_symbolOverview);

        Button lblNewLabel_4 = new Button(symbolOverview, SWT.CHECK);
        lblNewLabel_4.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        lblNewLabel_4.setImage(SWTResourceManager.getImage(BoxFileView.class,
                "/logos/logo_256.png"));

        Button btnCheckButton = new Button(symbolOverview, SWT.CHECK);
        btnCheckButton.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        btnCheckButton.setImage(SWTResourceManager.getImage(BoxFileView.class,
                "/logos/logo_256.png"));
        boxOverviewSash.setWeights(new int[] { 2, 3 });

        final int[] sashWeights = new int[2];

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

        int tableWidth = 1; // one pixel bonus ;)
        for (TableColumn col : boxEditorBoxTable.getColumns()) {
            tableWidth += col.getWidth() + 1;
        }

        sashWeights[0] = tableWidth;
        sashWeights[1] = Math.max(overallWidth - tableWidth
                - boxEditorSash.getSashWidth(), 0);

        boxEditorSash.setWeights(sashWeights);
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    @Override
    public void setPageModel(PageModel pageModel) {
        this.pageModel = pageModel;

        if (image != null) {
            image.dispose();
        }

        image = new Image(getDisplay(), pageModel.getImageFile().toString());
        boxView.setBackgroundImage(image);
        boxView.setSize(boxView.getSize());
    }

    @Override
    public PageModel getPageModel() {
        return pageModel;
    }

    public List getSymbolList() {
        return symbolList;
    }

    public Table getBoxTable() {
        return boxEditorBoxTable;
    }

    public ScrolledComposite getBoxView() {
        return boxEditorBoxView;
    }
}
