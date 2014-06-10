package de.vorb.tesseract.gui.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class BoxFileView extends Composite {
    private Text text;
    private Table table;
    private final SashForm sash;

    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public BoxFileView(Composite parent, int style) {
        super(parent, style);
        setLayout(new FormLayout());

        Composite composite = new Composite(this, SWT.NONE);
        composite.setLayout(new GridLayout(13, false));
        FormData fd_composite = new FormData();
        fd_composite.top = new FormAttachment(0);
        fd_composite.left = new FormAttachment(0);
        fd_composite.bottom = new FormAttachment(0, 36);
        fd_composite.right = new FormAttachment(100);
        composite.setLayoutData(fd_composite);

        Label lblSymbol = new Label(composite, SWT.NONE);
        lblSymbol.setText("Symbol");

        text = new Text(composite, SWT.BORDER);
        GridData gd_text = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1,
                1);
        gd_text.widthHint = 49;
        text.setLayoutData(gd_text);
        text.setTextLimit(5);

        Label lblX = new Label(composite, SWT.NONE);
        lblX.setText("X");

        Spinner spinner = new Spinner(composite, SWT.BORDER);
        spinner.setMaximum(1000);

        Label lblY = new Label(composite, SWT.NONE);
        lblY.setText("Y");

        Spinner spinner_1 = new Spinner(composite, SWT.BORDER);

        Label lblW = new Label(composite, SWT.NONE);
        lblW.setText("W");

        Spinner spinner_2 = new Spinner(composite, SWT.BORDER);

        Label lblH = new Label(composite, SWT.NONE);
        lblH.setText("H");

        Spinner spinner_3 = new Spinner(composite, SWT.BORDER);
        new Label(composite, SWT.NONE);

        Button button = new Button(composite, SWT.NONE);
        button.setImage(SWTResourceManager.getImage(BoxFileView.class,
                "/icons/zoom_in.png"));

        Button button_1 = new Button(composite, SWT.NONE);
        button_1.setImage(SWTResourceManager.getImage(BoxFileView.class,
                "/icons/zoom_out.png"));

        sash = new SashForm(this, SWT.NONE);
        FormData fd_sashForm = new FormData();
        fd_sashForm.top = new FormAttachment(composite, 0);
        fd_sashForm.right = new FormAttachment(composite, 0, SWT.RIGHT);
        fd_sashForm.left = new FormAttachment(composite, 0, SWT.LEFT);
        fd_sashForm.bottom = new FormAttachment(100);
        sash.setLayoutData(fd_sashForm);

        table = new Table(sash, SWT.BORDER | SWT.FULL_SELECTION);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        TableColumn tblclmnNo = new TableColumn(table, SWT.NONE);
        tblclmnNo.setWidth(35);
        tblclmnNo.setText("No.");

        TableColumn tblclmnSymbol = new TableColumn(table, SWT.NONE);
        tblclmnSymbol.setWidth(50);
        tblclmnSymbol.setText("Symbol");

        TableColumn tblclmnX = new TableColumn(table, SWT.NONE);
        tblclmnX.setWidth(35);
        tblclmnX.setText("X");

        TableColumn tblclmnY = new TableColumn(table, SWT.NONE);
        tblclmnY.setWidth(35);
        tblclmnY.setText("Y");

        TableColumn tblclmnWidth = new TableColumn(table, SWT.NONE);
        tblclmnWidth.setWidth(35);
        tblclmnWidth.setText("W");

        TableColumn tblclmnH = new TableColumn(table, SWT.NONE);
        tblclmnH.setWidth(35);
        tblclmnH.setText("H");
        table.pack();

        ScrolledComposite scrolledComposite_1 = new ScrolledComposite(sash,
                SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite_1.setExpandHorizontal(true);
        scrolledComposite_1.setExpandVertical(true);

        Canvas canvas = new Canvas(scrolledComposite_1, SWT.NONE);
        scrolledComposite_1.setContent(canvas);
        scrolledComposite_1.setMinSize(canvas.computeSize(SWT.DEFAULT,
                SWT.DEFAULT));

        this.pack();

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
        final int overallWidth = this.getSize().x;

        int tableWidth = 0;
        for (TableColumn col : table.getColumns()) {
            tableWidth += col.getWidth() + 1;
        }

        sashWeights[0] = tableWidth;
        sashWeights[1] = overallWidth - tableWidth - sash.getSashWidth();
        sash.setWeights(sashWeights);
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }
}
