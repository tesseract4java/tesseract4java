package de.vorb.tesseract.gui.view;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.RowData;

import de.vorb.tesseract.gui.model.PageModel;

public class ComparatorView extends Composite implements PageView {
    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public ComparatorView(Composite parent, int style) {
        super(parent, style);
        setLayout(new FormLayout());

        Composite composite = new Composite(this, SWT.NONE);
        RowLayout rl_composite = new RowLayout(SWT.HORIZONTAL);
        composite.setLayout(rl_composite);
        FormData fd_composite = new FormData();
        fd_composite.height = 32;
        fd_composite.top = new FormAttachment(0);
        fd_composite.left = new FormAttachment(0);
        fd_composite.right = new FormAttachment(100);
        composite.setLayoutData(fd_composite);

        SashForm sashForm = new SashForm(this, SWT.NONE);
        FormData fd_sashForm = new FormData();
        fd_sashForm.top = new FormAttachment(composite, 0);
        
        Button btWordBoxes = new Button(composite, SWT.TOGGLE);
        btWordBoxes.setSelection(true);
        btWordBoxes.setToolTipText("Show word boxes");
        btWordBoxes.setImage(SWTResourceManager.getImage(ComparatorView.class, "/icons/application_view_tile.png"));
        
        Button btSymbolBoxes = new Button(composite, SWT.TOGGLE);
        btSymbolBoxes.setToolTipText("Show symbol boxes");
        btSymbolBoxes.setImage(SWTResourceManager.getImage(ComparatorView.class, "/icons/application_view_icons.png"));
        
        Label label = new Label(composite, SWT.SEPARATOR);
        label.setLayoutData(new RowData(3, 26));
        
        Button btLineNumbers = new Button(composite, SWT.TOGGLE);
        btLineNumbers.setSelection(true);
        btLineNumbers.setToolTipText("Show line numbers");
        btLineNumbers.setImage(SWTResourceManager.getImage(ComparatorView.class, "/icons/text_list_numbers.png"));
        
        Button btBaselines = new Button(composite, SWT.TOGGLE);
        btBaselines.setToolTipText("Show baselines");
        btBaselines.setImage(SWTResourceManager.getImage(ComparatorView.class, "/icons/text_underline.png"));
        
        Label label_1 = new Label(composite, SWT.SEPARATOR | SWT.VERTICAL);
        label_1.setLayoutData(new RowData(SWT.DEFAULT, 26));
        
        Button btZoomIn = new Button(composite, SWT.NONE);
        btZoomIn.setToolTipText("Zoom in");
        btZoomIn.setImage(SWTResourceManager.getImage(ComparatorView.class, "/icons/zoom_in.png"));
        
        Button btZoomOut = new Button(composite, SWT.NONE);
        btZoomOut.setImage(SWTResourceManager.getImage(ComparatorView.class, "/icons/zoom_out.png"));
        btZoomOut.setToolTipText("Zoom out");
        fd_sashForm.bottom = new FormAttachment(100);
        fd_sashForm.right = new FormAttachment(100);
        fd_sashForm.left = new FormAttachment(0);
        sashForm.setLayoutData(fd_sashForm);
        
        ScrolledComposite scrolledComposite = new ScrolledComposite(sashForm, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);
        
        Canvas canvas = new Canvas(scrolledComposite, SWT.NONE);
        scrolledComposite.setContent(canvas);
        scrolledComposite.setMinSize(canvas.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        
        ScrolledComposite scrolledComposite_1 = new ScrolledComposite(sashForm, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite_1.setExpandHorizontal(true);
        scrolledComposite_1.setExpandVertical(true);
        
        Canvas canvas_1 = new Canvas(scrolledComposite_1, SWT.NONE);
        scrolledComposite_1.setContent(canvas_1);
        scrolledComposite_1.setMinSize(canvas_1.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        sashForm.setWeights(new int[] {1, 1});

    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    @Override
    public void setPageModel(PageModel pageModel) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public PageModel getPageModel() {
        // TODO Auto-generated method stub
        return null;
    }
}
