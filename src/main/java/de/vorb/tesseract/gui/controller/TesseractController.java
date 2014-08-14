package de.vorb.tesseract.gui.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.Timer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import org.bridj.BridJ;

import com.google.common.base.Optional;

import de.vorb.tesseract.gui.model.FilteredListModel;
import de.vorb.tesseract.gui.model.GlobalPrefs;
import de.vorb.tesseract.gui.model.ImageModel;
import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.gui.model.PageThumbnail;
import de.vorb.tesseract.gui.model.ProjectModel;
import de.vorb.tesseract.gui.model.SymbolListModel;
import de.vorb.tesseract.gui.model.SymbolOrder;
import de.vorb.tesseract.gui.util.PageListWorker;
import de.vorb.tesseract.gui.util.PageRecognitionProducer;
import de.vorb.tesseract.gui.util.PreprocessingWorker;
import de.vorb.tesseract.gui.util.RecognitionWorker;
import de.vorb.tesseract.gui.util.ThumbnailWorker;
import de.vorb.tesseract.gui.util.ThumbnailWorker.Task;
import de.vorb.tesseract.gui.view.FeatureDebugger;
import de.vorb.tesseract.gui.view.ImageModelComponent;
import de.vorb.tesseract.gui.view.MainComponent;
import de.vorb.tesseract.gui.view.PageModelComponent;
import de.vorb.tesseract.gui.view.PreprocessingPane;
import de.vorb.tesseract.gui.view.SymbolOverview;
import de.vorb.tesseract.gui.view.TesseractFrame;
import de.vorb.tesseract.gui.view.dialogs.BatchExportDialog;
import de.vorb.tesseract.gui.view.dialogs.Dialogs;
import de.vorb.tesseract.gui.view.dialogs.NewProjectDialog;
import de.vorb.tesseract.gui.view.dialogs.RecognitionParametersDialog;
import de.vorb.tesseract.tools.preprocessing.DefaultPreprocessor;
import de.vorb.tesseract.tools.preprocessing.Preprocessor;
import de.vorb.tesseract.tools.recognition.RecognitionProducer;
import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.Symbol;
import de.vorb.tesseract.util.TrainingFiles;
import de.vorb.tesseract.util.feat.Feature3D;

public class TesseractController extends WindowAdapter implements
        ActionListener, ListSelectionListener, Observer, ChangeListener {

    public static void main(String[] args) {
        BridJ.setNativeLibraryFile("leptonica", new File("liblept170.dll"));
        BridJ.setNativeLibraryFile("tesseract", new File("libtesseract303.dll"));

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e1) {
            // fail silently
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e2) {
                // fail silently
            }

            // If the system LaF is not available, use whatever LaF is already
            // being used.
        }

        new TesseractController();
    }

    /*
     * constants
     */
    private static final String TRAINING_FILE = "training_file";

    public static final Preprocessor DEFAULT_PREPROCESSOR =
            new DefaultPreprocessor();

    /*
     * components references
     */
    private final TesseractFrame view;
    private final FeatureDebugger featureDebugger;
    private MainComponent activeComponent;

    private final PageRecognitionProducer pageRecognitionProducer;
    private Optional<PreprocessingWorker> preprocessingWorker =
            Optional.absent();

    /*
     * IO workers, timers and tasks
     */
    private Optional<ThumbnailWorker> thumbnailLoader = Optional.absent();
    private final Timer pageSelectionTimer = new Timer("PageSelectionTimer");

    private Optional<TimerTask> lastPageSelectionTask = Optional.absent();
    private final Timer thumbnailLoadTimer = new Timer("ThumbnailLoadTimer");

    private Optional<TimerTask> lastThumbnailLoadTask = Optional.absent();

    private final List<Task> tasks = new LinkedList<Task>();

    /*
     * models
     */
    private Optional<ProjectModel> projectModel = Optional.absent();
    private Optional<PageThumbnail> pageThumbnail = Optional.absent();

    private String lastTrainingFile;

    /*
     * preprocessing
     */
    private Preprocessor defaultPreprocessor;
    private final Map<Path, Preprocessor> preprocessors = new HashMap<>();

    private Set<Path> changedPreprocessors = new HashSet<>();

    private Optional<RecognitionWorker> recognitionWorker = Optional.absent();

    public TesseractController() {
        // create new tesseract frame
        view = new TesseractFrame();
        featureDebugger = new FeatureDebugger(view);

        handleActiveComponentChange();

        pageRecognitionProducer = new PageRecognitionProducer(
                TrainingFiles.getTessdataDir(),
                RecognitionProducer.DEFAULT_TRAINING_FILE);

        // init training files
        try {
            final List<String> trainingFiles = TrainingFiles.getAvailable();

            // prepare training file list model
            final DefaultListModel<String> trainingFilesModel =
                    new DefaultListModel<>();

            for (String trainingFile : trainingFiles) {
                trainingFilesModel.addElement(trainingFile);
            }

            final JList<String> trainingFilesList =
                    view.getTrainingFiles().getList();

            // wrap it in a filtered model
            trainingFilesList.setSelectionMode(
                    ListSelectionModel.SINGLE_SELECTION);
            trainingFilesList.setModel(
                    new FilteredListModel<String>(trainingFilesModel));

            lastTrainingFile = GlobalPrefs.getPrefs().get(
                    TRAINING_FILE, RecognitionProducer.DEFAULT_TRAINING_FILE);

            trainingFilesList.setSelectedValue(lastTrainingFile, true);

            // handle the new training file selection
            handleTrainingFileSelection();
        } catch (IOException e) {
            Dialogs.showError(view, "Error",
                    "Training files could not be found.");
        }

        try {
            pageRecognitionProducer.init();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // register listeners
        view.addWindowListener(this);
        view.getMainTabs().addChangeListener(this);

        {
            // menu
            view.getMenuItemNewProject().addActionListener(this);
            view.getMenuItemOpenProject().addActionListener(this);
            view.getMenuItemSaveProject().addActionListener(this);
            view.getMenuItemSaveBoxFile().addActionListener(this);
            view.getMenuItemSavePage().addActionListener(this);
            view.getMenuItemCloseProject().addActionListener(this);
            view.getMenuItemBatchExport().addActionListener(this);
            view.getMenuItemPreferences().addActionListener(this);
        }

        view.getPages().getList().addListSelectionListener(this);
        final JViewport pagesViewport =
                (JViewport) view.getPages().getList().getParent();
        pagesViewport.addChangeListener(this);
        view.getTrainingFiles().getList().addListSelectionListener(this);
        view.getScale().addObserver(this);

        {
            // preprocessing pane
            final PreprocessingPane preprocessingPane = view.getPreprocessingPane();

            preprocessingPane.getPreviewButton().addActionListener(this);
            preprocessingPane.getApplyPageButton().addActionListener(this);
            preprocessingPane.getApplyAllPagesButton().addActionListener(this);
        }

        {
            // glyph overview pane
            final SymbolOverview symbolOverview = view.getSymbolOverview();
            symbolOverview.getSymbolGroupList().getList()
                    .addListSelectionListener(this);
            symbolOverview.getSymbolVariantList().getList()
                    .addListSelectionListener(this);
            symbolOverview.getSymbolVariantList().getCompareToPrototype()
                    .addActionListener(this);
            symbolOverview.getSymbolVariantList().getShowInBoxEditor()
                    .addActionListener(this);
            symbolOverview.getSymbolVariantList().getOrderingComboBox()
                    .addActionListener(this);
        }

        // recognition pane
        view.getRecognitionPane().getParametersButton().addActionListener(this);

        {
            // batch export
        }

        view.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        final Object source = evt.getSource();
        final SymbolOverview symbolOverview = view.getSymbolOverview();
        final PreprocessingPane preprocPane = view.getPreprocessingPane();

        if (source.equals(view.getMenuItemNewProject())) {
            handleNewProject();
        } else if (source.equals(view.getMenuItemOpenProject())) {
            handleOpenProject();
        } else if (source.equals(view.getMenuItemSaveProject())) {
            handleSaveProject();
        } else if (source.equals(view.getMenuItemCloseProject())) {
            handleCloseProject();
        } else if (source.equals(view.getMenuItemBatchExport())) {
            handleBatchExport();
        } else if (preprocPane.getPreviewButton().equals(source)) {
            handlePreprocessorPreview();
        } else if (preprocPane.getApplyPageButton().equals(source)) {
            handlePreprocessorChange(false);
        } else if (preprocPane.getApplyAllPagesButton().equals(source)) {
            handlePreprocessorChange(true);
        } else if (source.equals(symbolOverview.getSymbolVariantList()
                .getCompareToPrototype())) {
            handleCompareSymbolToPrototype();
        } else if (source.equals(symbolOverview.getSymbolVariantList()
                .getShowInBoxEditor())) {
            handleShowSymbolInBoxEditor();
        } else if (source.equals(symbolOverview.getSymbolVariantList()
                .getOrderingComboBox())) {
            handleSymbolReordering();
        } else if (source.equals(view.getRecognitionPane()
                .getParametersButton())) {
            handleParametersButtonClick();
        } else {
            throw new UnsupportedOperationException("Unhandled ActionEvent "
                    + evt);
        }
    }

    public Optional<PageModel> getPageModel() {
        final MainComponent active = view.getActiveComponent();

        if (active instanceof PageModelComponent) {
            return ((PageModelComponent) active).getPageModel();
        }

        return Optional.<PageModel> absent();
    }

    public PageRecognitionProducer getPageRecognitionProducer() {
        return pageRecognitionProducer;
    }

    public Optional<ProjectModel> getProjectModel() {
        return projectModel;
    }

    public Optional<Path> getSelectedPage() {
        final PageThumbnail thumbnail =
                view.getPages().getList().getSelectedValue();

        if (thumbnail == null) {
            return Optional.absent();
        } else {
            return Optional.of(thumbnail.getFile());
        }
    }

    public Optional<String> getTrainingFile() {
        return Optional.fromNullable(
                view.getTrainingFiles().getList().getSelectedValue());
    }

    public TesseractFrame getView() {
        return view;
    }

    private void handleActiveComponentChange() {
        final MainComponent active = view.getActiveComponent();

        // didn't change
        if (active == activeComponent) {
            return;
        }

        if (active instanceof ImageModelComponent) {
            if (activeComponent instanceof ImageModelComponent) {
                setImageModel(((ImageModelComponent) activeComponent)
                        .getImageModel());
            } else if (activeComponent instanceof PageModelComponent) {
                final Optional<PageModel> pm =
                        ((PageModelComponent) activeComponent).getPageModel();

                if (pm.isPresent()) {
                    setImageModel(Optional.of(pm.get().getImageModel()));
                } else {
                    setImageModel(Optional.<ImageModel> absent());
                }
            } else {
                setImageModel(Optional.<ImageModel> absent());
            }
        } else if (active instanceof PageModelComponent) {
            if (activeComponent instanceof PageModelComponent) {
                setPageModel(((PageModelComponent) activeComponent)
                        .getPageModel());
            } else if (activeComponent instanceof ImageModelComponent) {
                setImageModel(((ImageModelComponent) activeComponent)
                        .getImageModel());
            } else {
                setPageModel(Optional.<PageModel> absent());
            }
        }

        activeComponent = active;
    }

    private void handleBatchExport() {
        BatchExportDialog.showBatchExportDialog(this);
    }

    private void handleCompareSymbolToPrototype() {
        final Symbol selected = view.getSymbolOverview().getSymbolVariantList()
                .getList().getSelectedValue();

        final Optional<PageModel> pm = getPageModel();
        if (pm.isPresent()) {
            final BufferedImage pageImg = pm.get().getImageModel()
                    .getPreprocessedImage();
            final Box symbolBox = selected.getBoundingBox();
            final BufferedImage symbolImg = pageImg.getSubimage(
                    symbolBox.getX(), symbolBox.getY(),
                    symbolBox.getWidth(), symbolBox.getHeight());

            final List<Feature3D> features =
                    pageRecognitionProducer.getFeaturesForSymbol(symbolImg);

            featureDebugger.setFeatures(features);
            featureDebugger.setVisible(true);
        }
    }

    private void handleNewProject() {
        final Optional<ProjectModel> result = NewProjectDialog.showDialog(view);

        if (!result.isPresent())
            return;

        setProjectEnabled(true);

        this.projectModel = result;
        final ProjectModel projectModel = result.get();

        final DefaultListModel<PageThumbnail> pages =
                view.getPages().getListModel();

        final ThumbnailWorker thumbnailLoader =
                new ThumbnailWorker(projectModel, pages);
        thumbnailLoader.execute();
        this.thumbnailLoader = Optional.of(thumbnailLoader);

        final PageListWorker pageListLoader =
                new PageListWorker(projectModel, pages);

        pageListLoader.execute();
    }

    private void setProjectEnabled(boolean b) {
        view.getMenuItemSaveProject().setEnabled(b);
        view.getMenuItemCloseProject().setEnabled(b);
        view.getMenuItemBatchExport().setEnabled(b);
    }

    private void handleOpenProject() {
        final JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileFilter() {
            @Override
            public String getDescription() {
                return "Tesseract Project Files (*.tesseract-project)";
            }

            @Override
            public boolean accept(File f) {
                return f.isFile() && f.getName().endsWith(".tesseract-project");
            }
        });
        final int result = fc.showOpenDialog(view);
        if (result == JFileChooser.APPROVE_OPTION) {
            // TODO load project

        }
    }

    private void handleSaveProject() {
        final JFileChooser fc = new JFileChooser(
                projectModel.get().getProjectDir().toFile());
        fc.setFileFilter(new FileFilter() {
            @Override
            public String getDescription() {
                return "Tesseract Project Files (*.tesseract-project)";
            }

            @Override
            public boolean accept(File f) {
                return f.isFile() && f.getName().endsWith(".tesseract-project");
            }
        });
        final int result = fc.showSaveDialog(view);
        if (result == JFileChooser.APPROVE_OPTION) {
            // TODO save project

        }
    }

    private void handleCloseProject() {
        // TODO Auto-generated method stub

    }

    private void handlePageSelection() {
        final PageThumbnail pt =
                view.getPages().getList().getSelectedValue();

        // if the page selection did not change, ignore it
        if (pageThumbnail.isPresent() && pageThumbnail.get().equals(pt)) {
            return;
        }

        // ask to save box file
        if (view.getActiveComponent() == view.getBoxEditor()
                && view.getBoxEditor().hasChanged()) {
            final boolean changePage = Dialogs.ask(
                    view,
                    "Unsaved Changes",
                    "The current box file has not been saved. Do you really want to change the page?");

            if (!changePage) {
                // reselect the old page
                view.getPages().getList().setSelectedValue(pageThumbnail.get(),
                        true);
                // don't change the page
                return;
            }
        }

        pageThumbnail = Optional.fromNullable(pt);

        // don't do anything, if no page is selected
        if (pt == null) {
            return;
        }

        // cancel the last page loading task if it is present
        if (lastPageSelectionTask.isPresent()) {
            lastPageSelectionTask.get().cancel();
        }

        // new task
        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // cancel last task
                if (preprocessingWorker.isPresent()) {
                    preprocessingWorker.get().cancel(false);
                }

                // create swingworker to preprocess page
                final PreprocessingWorker pw = new PreprocessingWorker(
                        TesseractController.this, pt.getFile(),
                        getProjectModel().get().getPreprocessedDir());

                // save reference
                preprocessingWorker = Optional.of(pw);

                // execute it
                pw.execute();
            }
        };

        // run the page loader with a delay of 1 second
        // the user has 1 second to change the page before it starts loading
        pageSelectionTimer.schedule(task, 500);

        // set as new timer task
        lastPageSelectionTask = Optional.of(task);
    }

    private void handleParametersButtonClick() {
        final Optional<Float> ratio =
                RecognitionParametersDialog.showDialog(view);

        if (ratio.isPresent()
                && !view.getPages().getList().isSelectionEmpty()) {
            pageRecognitionProducer.setVariable("textord_noise_hfract",
                    ratio.get().toString());
            handlePageSelection();
        }
    }

    private void handleShowSymbolInBoxEditor() {
        final Symbol selected = view.getSymbolOverview().getSymbolVariantList()
                .getList().getSelectedValue();

        if (selected == null) {
            return;
        }

        final ListModel<Symbol> symbols =
                view.getBoxEditor().getSymbols().getListModel();
        final int size = symbols.getSize();

        // find the selected symbol in
        for (int i = 0; i < size; i++) {
            if (selected == symbols.getElementAt(i)) {
                view.getBoxEditor().getSymbols().getTable()
                        .setRowSelectionInterval(i, i);
            }
        }

        view.getMainTabs().setSelectedComponent(view.getBoxEditor());
    }

    private void handleSymbolGroupSelection() {
        final JList<Entry<String, List<Symbol>>> selectionList =
                view.getSymbolOverview().getSymbolGroupList().getList();

        final int index = selectionList.getSelectedIndex();
        if (index == -1)
            return;

        final List<Symbol> symbols = selectionList.getModel().getElementAt(
                index).getValue();

        // build model
        final SymbolListModel model = new SymbolListModel(
                getPageModel().get().getImageModel().getPreprocessedImage());
        for (final Symbol symbol : symbols) {
            model.addElement(symbol);
        }

        // get combo box
        final JComboBox<SymbolOrder> ordering = view.getSymbolOverview()
                .getSymbolVariantList().getOrderingComboBox();

        // sort symbols
        model.sortBy(ordering.getItemAt(ordering.getSelectedIndex()));

        view.getSymbolOverview().getSymbolVariantList().getList().setModel(
                model);
    }

    private void handleSymbolReordering() {
        // get combo box
        final JComboBox<SymbolOrder> ordering = view.getSymbolOverview()
                .getSymbolVariantList().getOrderingComboBox();

        // get model
        final SymbolListModel model = (SymbolListModel) view.getSymbolOverview()
                .getSymbolVariantList().getList().getModel();

        // sort symbols
        model.sortBy(ordering.getItemAt(ordering.getSelectedIndex()));
    }

    private void handleThumbnailLoading() {
        if (!thumbnailLoader.isPresent())
            return;

        final ThumbnailWorker thumbnailLoader = this.thumbnailLoader.get();

        for (Task t : tasks) {
            t.cancel();
        }

        tasks.clear();

        if (lastThumbnailLoadTask.isPresent()) {
            lastThumbnailLoadTask.get().cancel();
        }

        thumbnailLoadTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        final JList<PageThumbnail> list =
                                view.getPages().getList();
                        final ListModel<PageThumbnail> model = list.getModel();

                        final int first = list.getFirstVisibleIndex();
                        final int last = list.getLastVisibleIndex();

                        for (int i = first; i <= last; i++) {
                            final PageThumbnail pt = model.getElementAt(i);

                            if (pt == null || pt.getThumbnail().isPresent())
                                continue;

                            final Task t = new Task(i, pt);
                            tasks.add(t);
                            thumbnailLoader.submitTask(t);
                        }
                    }
                });
            }
        }, 500); // 500ms delay
    }

    private void handleTrainingFileSelection() {
        final String trainingFile =
                view.getTrainingFiles().getList().getSelectedValue();

        GlobalPrefs.getPrefs().put(TRAINING_FILE, trainingFile);

        pageRecognitionProducer.setTrainingFile(trainingFile);

        // try {
        // final Optional<IntTemplates> prototypes = loadPrototypes();
        // featureDebugger.setPrototypes(prototypes);
        // } catch (IOException e) {
        // e.printStackTrace();
        // }

        // if the training file has changed, ask to reload the page
        if (!view.getPages().getList().isSelectionEmpty()
                && trainingFile != lastTrainingFile) {
            handlePageSelection();
        }

        lastTrainingFile = trainingFile;
    }

    public void setPageModel(Optional<PageModel> model) {
        final MainComponent active = view.getActiveComponent();

        if (active instanceof PageModelComponent) {
            ((PageModelComponent) active).setPageModel(model);
        }
    }

    // TODO prototype loading?
    // private Optional<IntTemplates> loadPrototypes() throws IOException {
    // final Path tessdir = TrainingFiles.getTessdataDir();
    // final Path base = tmpDir.resolve(TMP_TRAINING_FILE_BASE);
    //
    // TessdataManager.extract(
    // tessdir.resolve(lastTrainingFile + ".traineddata"), base);
    //
    // final Path prototypeFile =
    // tmpDir.resolve(tmpDir.resolve(TMP_TRAINING_FILE_BASE
    // + "inttemp"));
    //
    // final InputStream in = Files.newInputStream(prototypeFile);
    // final InputBuffer buf =
    // InputBuffer.allocate(new BufferedInputStream(in));
    //
    // try {
    // final IntTemplates prototypes = IntTemplates.readFrom(buf);
    //
    // return Optional.of(prototypes);
    // } catch (IOException e) {
    // throw e;
    // } finally {
    // // close input buffer, even if an error occurred
    // buf.close();
    // }
    // }

    @Override
    public void stateChanged(ChangeEvent evt) {
        final Object source = evt.getSource();
        if (source == view.getPages().getList().getParent()) {
            handleThumbnailLoading();
        } else if (source == view.getMainTabs()) {
            handleActiveComponentChange();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == view.getScale()) {
            view.getScaleLabel().setText(o.toString());
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent evt) {
        if (evt.getValueIsAdjusting()) {
            return;
        }

        final Object source = evt.getSource();
        if (source.equals(view.getPages().getList())) {
            handlePageSelection();
        } else if (source.equals(view.getTrainingFiles().getList())) {
            handleTrainingFileSelection();
        } else if (source.equals(view.getSymbolOverview()
                .getSymbolGroupList().getList())) {
            handleSymbolGroupSelection();
        }
    }

    @Override
    public void windowClosed(WindowEvent evt) {
        pageSelectionTimer.cancel();
        thumbnailLoadTimer.cancel();
    }

    public Preprocessor getDefaultPreprocessor() {
        return defaultPreprocessor;
    }

    public Preprocessor getPreprocessor(Path sourceFile) {
        final Optional<Path> selected = getSelectedPage();
        if (selected.isPresent() && selected.get().equals(sourceFile)
                && view.getActiveComponent() == view.getPreprocessingPane()) {
            final Preprocessor preview =
                    view.getPreprocessingPane().getPreprocessor();
            return preview;
        }

        final Preprocessor preprocessor = preprocessors.get(sourceFile);

        if (preprocessor == null) {
            return defaultPreprocessor;
        }

        return preprocessors.get(sourceFile);
    }

    public boolean hasPreprocessorChanged(Path sourceFile) {
        // try to remove it and return true if the set contained the sourceFile
        return changedPreprocessors.contains(sourceFile);
    }

    public void setDefaultPreprocessor(Preprocessor preprocessor) {
        defaultPreprocessor = preprocessor;
    }

    public void setPreprocessor(Path sourceFile, Preprocessor preprocessor) {
        if (preprocessor.equals(defaultPreprocessor))
            preprocessors.remove(sourceFile);
        else
            preprocessors.put(sourceFile, preprocessor);
    }

    public void setPreprocessorChanged(Path sourceFile, boolean changed) {
        if (changed)
            changedPreprocessors.add(sourceFile);
        else
            changedPreprocessors.remove(sourceFile);
    }

    public void setImageModel(Optional<ImageModel> model) {
        view.getProgressBar().setIndeterminate(false);
        final MainComponent active = view.getActiveComponent();

        if (active instanceof PageModelComponent) {
            ((PageModelComponent) active).setPageModel(
                    Optional.<PageModel> absent());

            if (recognitionWorker.isPresent()) {
                recognitionWorker.get().cancel(false);
            }

            final Optional<String> trainingFile = getTrainingFile();

            if (!model.isPresent() || !trainingFile.isPresent()) {
                return;
            }

            final RecognitionWorker rw = new RecognitionWorker(this,
                    model.get(), trainingFile.get());

            rw.execute();

            recognitionWorker = Optional.of(rw);

            return;
        } else if (!(active instanceof ImageModelComponent)) {
            return;
        }

        if (!model.isPresent()) {
            ((ImageModelComponent) active).setImageModel(model);
            return;
        }

        final Path sourceFile = model.get().getSourceFile();
        final Optional<Path> selectedPage = getSelectedPage();

        if (!selectedPage.isPresent()
                || !sourceFile.equals(selectedPage.get())) {
            ((ImageModelComponent) active).setImageModel(
                    Optional.<ImageModel> absent());
            return;
        }

        ((ImageModelComponent) active).setImageModel(model);
    }

    private void handlePreprocessorPreview() {
        final Optional<Path> selectedPage = getSelectedPage();

        // if no page is selected, simply ignore it
        if (!selectedPage.isPresent()) {
            Dialogs.showWarning(view, "No project",
                    "No page has been selected. You need to select a page first.");
            return;
        }

        final JProgressBar progress = view.getProgressBar();
        progress.setIndeterminate(true);

        final Optional<ProjectModel> projectModel = getProjectModel();

        if (!projectModel.isPresent()) {
            Dialogs.showWarning(view, "No project",
                    "No project has been selected. You need to create a project first.");
            return;
        }

        new PreprocessingWorker(this, selectedPage.get(),
                projectModel.get().getProjectDir());
    }

    private void handlePreprocessorChange(boolean allPages) {

    }
}
