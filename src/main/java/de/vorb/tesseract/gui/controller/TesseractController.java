package de.vorb.tesseract.gui.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JViewport;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.bridj.BridJ;

import com.google.common.base.Optional;

import de.vorb.tesseract.gui.model.FilteredListModel;
import de.vorb.tesseract.gui.model.GlobalPrefs;
import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.gui.model.PageThumbnail;
import de.vorb.tesseract.gui.model.SymbolListModel;
import de.vorb.tesseract.gui.model.SymbolOrder;
import de.vorb.tesseract.gui.util.PageListLoader;
import de.vorb.tesseract.gui.util.PageModelLoader;
import de.vorb.tesseract.gui.util.PageRecognitionProducer;
import de.vorb.tesseract.gui.util.ThumbnailLoader;
import de.vorb.tesseract.gui.util.ThumbnailLoader.Task;
import de.vorb.tesseract.gui.view.Dialogs;
import de.vorb.tesseract.gui.view.FeatureDebugger;
import de.vorb.tesseract.gui.view.MainComponent;
import de.vorb.tesseract.gui.view.NewProjectDialog;
import de.vorb.tesseract.gui.view.NewProjectDialog.Result;
import de.vorb.tesseract.gui.view.RecognitionParametersDialog;
import de.vorb.tesseract.gui.view.TesseractFrame;
import de.vorb.tesseract.tools.recognition.RecognitionProducer;
import de.vorb.tesseract.tools.training.InputBuffer;
import de.vorb.tesseract.tools.training.IntTemplates;
import de.vorb.tesseract.tools.training.TessdataManager;
import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.Symbol;
import de.vorb.tesseract.util.TrainingFiles;
import de.vorb.tesseract.util.feat.Feature3D;
import de.vorb.util.FileIO;

public class TesseractController extends WindowAdapter implements
        ActionListener, ListSelectionListener, Observer, ChangeListener {

    public static void main(String[] args) {
        BridJ.setNativeLibraryFile("leptonica", new File("liblept170.dll"));
        BridJ.setNativeLibraryFile("tesseract", new File("libtesseract303.dll"));

        new TesseractController();
    }

    private static final String TRAINING_FILE = "training_file";

    private static final String TMP_TRAINING_FILE_BASE = "unspecified.";
    private final TesseractFrame view;

    private final FeatureDebugger featureDebugger;

    private MainComponent activeComponent;
    private final PageRecognitionProducer pageRecognitionProducer;
    private Optional<PageModelLoader> pageModelLoader = Optional.absent();

    private Optional<ThumbnailLoader> thumbnailLoader = Optional.absent();
    private final Timer pageSelectionTimer = new Timer("PageSelectionTimer");

    private Optional<TimerTask> lastPageSelection = Optional.absent();
    private final Timer thumbnailLoadTimer = new Timer("ThumbnailLoadTimer");

    private Optional<TimerTask> lastThumbnailLoad = Optional.absent();

    private String lastTrainingFile;

    private final Path tmpDir;

    private final List<Task> tasks = new LinkedList<Task>();

    public TesseractController() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // fail silently

            // If the system LaF is not available, use whatever LaF is already
            // being used.
        }

        // create new tesseract frame
        view = new TesseractFrame();
        featureDebugger = new FeatureDebugger(view);

        activeComponent = view.getActiveComponent();

        view.setVisible(true);

        Path tmpDir = null;
        try {
            tmpDir = Files.createTempDirectory("tess");
        } catch (IOException e) {
            Dialogs.showError(view, "Error",
                    "Training files could not be found.");
            System.exit(1);
        } finally {
            this.tmpDir = tmpDir;
        }

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
        view.getMenuItemNewProject().addActionListener(this);
        view.getPages().getList().addListSelectionListener(this);
        final JViewport pagesViewport =
                (JViewport) view.getPages().getList().getParent();
        pagesViewport.addChangeListener(this);
        view.getTrainingFiles().getList().addListSelectionListener(this);
        view.getScale().addObserver(this);

        // glyph overview pane
        view.getSymbolOverview().getSymbolGroupList().getList()
                .addListSelectionListener(this);
        view.getSymbolOverview().getSymbolVariantList().getList()
                .addListSelectionListener(this);
        view.getSymbolOverview().getSymbolVariantList()
                .getCompareToPrototype().addActionListener(this);
        view.getSymbolOverview().getSymbolVariantList().getShowInBoxEditor()
                .addActionListener(this);
        view.getSymbolOverview().getSymbolVariantList().getOrderingCheckBox()
                .addActionListener(this);

        // recognition pane
        view.getRecognitionPane().getParametersButton().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        final Object source = evt.getSource();
        if (source.equals(view.getMenuItemNewProject())) {
            handleNewProject();
        } else if (source.equals(view.getSymbolOverview().getSymbolVariantList()
                .getCompareToPrototype())) {
            handleCompareSymbolToPrototype();
        } else if (source.equals(view.getSymbolOverview().getSymbolVariantList()
                .getShowInBoxEditor())) {
            handleShowSymbolInBoxEditor();
        } else if (source.equals(view.getRecognitionPane().getParametersButton())) {
            handleParametersButtonClick();
        } else if (source.equals(view.getSymbolOverview().getSymbolVariantList().getOrderingCheckBox())) {
            handleSymbolReordering();
        }
    }

    public PageRecognitionProducer getPageRecognitionProducer() {
        return pageRecognitionProducer;
    }

    public TesseractFrame getView() {
        return view;
    }

    private void handleCompareSymbolToPrototype() {
        final Symbol selected = view.getSymbolOverview().getSymbolVariantList()
                .getList().getSelectedValue();

        final Optional<PageModel> pm = view.getPageModel();
        if (pm.isPresent()) {
            final BufferedImage pageImg = pm.get().getImage();
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

    private void handleMainComponentChange() {
        final MainComponent newActiveComponent = view.getActiveComponent();
        if (activeComponent == newActiveComponent) {
            return;
        }

        newActiveComponent.setPageModel(activeComponent.getPageModel());

        activeComponent = newActiveComponent;
    }

    private void handleNewProject() {
        final Optional<NewProjectDialog.Result> result =
                NewProjectDialog.showDialog(view);

        if (!result.isPresent())
            return;

        final Result projectConfig = result.get();

        final DefaultListModel<PageThumbnail> pages =
                view.getPages().getListModel();

        final ThumbnailLoader thumbnailLoader =
                new ThumbnailLoader(projectConfig, pages);
        thumbnailLoader.execute();
        this.thumbnailLoader = Optional.of(thumbnailLoader);

        final PageListLoader pageListLoader =
                new PageListLoader(projectConfig, pages);

        pageListLoader.execute();
    }

    private void handlePageSelection() {
        final PageThumbnail pt =
                view.getPages().getList().getSelectedValue();
        final String trainingFile =
                view.getTrainingFiles().getList().getSelectedValue();

        // cancel the last page load if it is present
        if (lastPageSelection.isPresent()) {
            lastPageSelection.get().cancel();
        }

        // new task
        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // cancel last task
                if (pageModelLoader.isPresent()) {
                    pageModelLoader.get().cancel(false);
                }

                // create swingworker to load model
                final PageModelLoader pml = new PageModelLoader(
                        TesseractController.this, pt.getFile(), trainingFile);

                // save reference
                pageModelLoader = Optional.of(pml);

                // execute it
                pml.execute();
            }
        };

        // run the page loader with a delay of 1 second
        pageSelectionTimer.schedule(task, 1000);

        // set as new timer task
        lastPageSelection = Optional.of(task);
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

        final List<Symbol> symbols = selectionList.getModel().getElementAt(
                selectionList.getSelectedIndex()).getValue();

        final SymbolListModel model = new SymbolListModel();
        for (final Symbol symbol : symbols) {
            model.addElement(symbol);
        }

        final JComboBox<SymbolOrder> ordering = view.getSymbolOverview()
                .getSymbolVariantList().getOrderingCheckBox();
        model.sortBy(ordering.getItemAt(ordering.getSelectedIndex()));

        view.getSymbolOverview().getSymbolVariantList().getList().setModel(
                model);
    }

    private void handleSymbolReordering() {

    }

    private void handleThumbnailLoading() {
        if (!thumbnailLoader.isPresent())
            return;

        final ThumbnailLoader thumbnailLoader = this.thumbnailLoader.get();

        for (Task t : tasks) {
            t.cancel();
        }

        tasks.clear();

        if (lastThumbnailLoad.isPresent()) {
            lastThumbnailLoad.get().cancel();
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

                            if (pt.getThumbnail().isPresent())
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

        try {
            final Optional<IntTemplates> prototypes = loadPrototypes();
            featureDebugger.setPrototypes(prototypes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // if the training file has changed, ask to reload the page
        if (!view.getPages().getList().isSelectionEmpty()
                && trainingFile != lastTrainingFile) {
            handlePageSelection();
        }

        lastTrainingFile = trainingFile;
    }

    private Optional<IntTemplates> loadPrototypes() throws IOException {
        final Path tessdir = TrainingFiles.getTessdataDir();
        final Path base = tmpDir.resolve(TMP_TRAINING_FILE_BASE);

        TessdataManager.extract(
                tessdir.resolve(lastTrainingFile + ".traineddata"), base);

        final Path prototypeFile =
                tmpDir.resolve(tmpDir.resolve(TMP_TRAINING_FILE_BASE
                        + "inttemp"));

        final InputStream in = Files.newInputStream(prototypeFile);
        final InputBuffer buf = InputBuffer.allocate(in, 4096);

        final IntTemplates prototypes = IntTemplates.readFrom(buf);

        return Optional.of(prototypes);
    }

    @Override
    public void stateChanged(ChangeEvent evt) {
        final Object source = evt.getSource();
        if (source == view.getPages().getList().getParent()) {
            handleThumbnailLoading();
        } else if (source == view.getMainTabs()) {
            handleMainComponentChange();
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
        // delete temporary directory
        try {
            if (Files.exists(tmpDir)) {
                FileIO.deleteDirectory(tmpDir);
            }
        } catch (IOException e) {
            Dialogs.showError(view, "Error",
                    "Temporary directory could not be removed completely.");
        }

        pageSelectionTimer.cancel();
        thumbnailLoadTimer.cancel();
    }
}
