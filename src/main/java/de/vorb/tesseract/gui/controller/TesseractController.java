package de.vorb.tesseract.gui.controller;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import java.util.prefs.Preferences;
import java.util.Timer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.xml.transform.TransformerException;

import org.bridj.BridJ;

import com.google.common.base.Optional;

import de.vorb.tesseract.gui.model.BatchExportModel;
import de.vorb.tesseract.gui.model.FilteredListModel;
import de.vorb.tesseract.gui.model.GlobalPrefs;
import de.vorb.tesseract.gui.model.ImageModel;
import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.gui.model.PageThumbnail;
import de.vorb.tesseract.gui.model.ProjectModel;
import de.vorb.tesseract.gui.model.SymbolListModel;
import de.vorb.tesseract.gui.model.SymbolOrder;
import de.vorb.tesseract.gui.util.DocumentWriter;
import de.vorb.tesseract.gui.view.EvaluationPane;
import de.vorb.tesseract.gui.view.FeatureDebugger;
import de.vorb.tesseract.gui.view.FilteredTable;
import de.vorb.tesseract.gui.view.ImageModelComponent;
import de.vorb.tesseract.gui.view.MainComponent;
import de.vorb.tesseract.gui.view.PageModelComponent;
import de.vorb.tesseract.gui.view.PreprocessingPane;
import de.vorb.tesseract.gui.view.SymbolOverview;
import de.vorb.tesseract.gui.view.TesseractFrame;
import de.vorb.tesseract.gui.view.dialogs.BatchExportDialog;
import de.vorb.tesseract.gui.view.dialogs.CharacterHistogram;
import de.vorb.tesseract.gui.view.dialogs.Dialogs;
import de.vorb.tesseract.gui.view.dialogs.ImportTranscriptionDialog;
import de.vorb.tesseract.gui.view.dialogs.NewProjectDialog;
import de.vorb.tesseract.gui.view.dialogs.PreferencesDialog;
import de.vorb.tesseract.gui.view.dialogs.PreferencesDialog.ResultState;
import de.vorb.tesseract.gui.view.dialogs.UnicharsetDebugger;
import de.vorb.tesseract.gui.work.BatchExecutor;
import de.vorb.tesseract.gui.work.PageListWorker;
import de.vorb.tesseract.gui.work.PageRecognitionProducer;
import de.vorb.tesseract.gui.work.PlainTextWriter;
import de.vorb.tesseract.gui.work.PreprocessingWorker;
import de.vorb.tesseract.gui.work.RecognitionWorker;
import de.vorb.tesseract.gui.work.ThumbnailWorker;
import de.vorb.tesseract.gui.work.ThumbnailWorker.Task;
import de.vorb.tesseract.tools.preprocessing.DefaultPreprocessor;
import de.vorb.tesseract.tools.preprocessing.Preprocessor;
import de.vorb.tesseract.tools.recognition.RecognitionProducer;
import de.vorb.tesseract.tools.training.Unicharset;
import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.Symbol;
import de.vorb.tesseract.util.TrainingFiles;
import de.vorb.tesseract.util.feat.Feature3D;
import de.vorb.util.FileNames;

import eu.digitisation.input.Batch;
import eu.digitisation.input.Parameters;
import eu.digitisation.input.WarningException;
import eu.digitisation.output.Report;

public class TesseractController extends WindowAdapter implements
        ActionListener, ListSelectionListener, Observer, ChangeListener {

    public static void main(String[] args) {
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

        File leptonica = new File("liblept170.dll");
        File tesseract = new File("libtesseract303.dll");

        try {
            // Windows?
            if (leptonica.exists() && tesseract.exists()) {
                BridJ.setNativeLibraryFile("leptonica", leptonica);
                BridJ.setNativeLibraryFile("tesseract", tesseract);
            } else { // Linux?
                leptonica = new File("liblept.so");
                tesseract = new File("libtesseract.so");

                if (leptonica.exists() && tesseract.exists()) {
                    BridJ.setNativeLibraryFile("leptonica", leptonica);
                    BridJ.setNativeLibraryFile("tesseract", tesseract);
                } else { // Mac?
                    leptonica = new File("liblept.dylib");
                    tesseract = new File("libtesseract.dylib");

                    if (leptonica.exists() && tesseract.exists()) {
                        BridJ.setNativeLibraryFile("leptonica", leptonica);
                        BridJ.setNativeLibraryFile("tesseract", tesseract);
                    }
                }
            }

            new TesseractController();
        } catch (UnsatisfiedLinkError e) {
            Dialogs.showError(null, "Fatal error",
                    "The necessary libraries could not be loaded.");
            System.exit(1);
        }
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
    private Preprocessor defaultPreprocessor = new DefaultPreprocessor();
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
            view.getMenuItemOpenProjectDirectory().addActionListener(this);
            view.getMenuItemImportTranscriptions().addActionListener(this);
            view.getMenuItemBatchExport().addActionListener(this);
            view.getMenuItemPreferences().addActionListener(this);
            view.getMenuItemCharacterHistogram().addActionListener(this);
            view.getMenuItemInspectUnicharset().addActionListener(this);
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

        {
            // evaluation pane
            final EvaluationPane evalPane = view.getEvaluationPane();
            evalPane.getSaveTranscriptionButton().addActionListener(this);
            evalPane.getGenerateReportButton().addActionListener(this);
            evalPane.getUseOCRResultButton().addActionListener(this);
        }

        view.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        final Object source = evt.getSource();
        final SymbolOverview symbolOverview = view.getSymbolOverview();
        final PreprocessingPane preprocPane = view.getPreprocessingPane();
        final EvaluationPane evalPane = view.getEvaluationPane();

        if (source.equals(view.getMenuItemNewProject())) {
            handleNewProject();
        } else if (source.equals(view.getMenuItemOpenProject())) {
            handleOpenProject();
        } else if (source.equals(view.getMenuItemSaveProject())) {
            handleSaveProject();
        } else if (source.equals(view.getMenuItemCloseProject())) {
            handleCloseProject();
        } else if (source.equals(view.getMenuItemOpenProjectDirectory())) {
            handleOpenProjectDirectory();
        } else if (source.equals(view.getMenuItemImportTranscriptions())) {
            handleImportTranscriptions();
        } else if (source.equals(view.getMenuItemBatchExport())) {
            handleBatchExport();
        } else if (source.equals(view.getMenuItemPreferences())) {
            handlePreferences();
        } else if (source.equals(view.getMenuItemCharacterHistogram())) {
            handleCharacterHistogram();
        } else if (source.equals(view.getMenuItemInspectUnicharset())) {
            handleInspectUnicharset();
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
        } else if (source.equals(evalPane.getSaveTranscriptionButton())) {
            handleTranscriptionSave();
        } else if (source.equals(evalPane.getGenerateReportButton())) {
            handleGenerateReport();
        } else if (source.equals(evalPane.getUseOCRResultButton())) {
            handleUseOCRResult();
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

    private void handleOpenProjectDirectory() {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(
                        projectModel.get().getProjectDir().toUri());
            } catch (IOException e) {
                Dialogs.showError(view, "Exception",
                        "Project directory could not be opened.");
            }
        }
    }

    private void handleUseOCRResult() {
        if (getPageModel().isPresent()) {
            final StringWriter ocrResult = new StringWriter();
            try {
                new PlainTextWriter().write(getPageModel().get().getPage(),
                        ocrResult);

                view.getEvaluationPane().getTextAreaTranscript().setText(
                        ocrResult.toString());
            } catch (IOException e) {
                Dialogs.showWarning(view, "Error",
                        "Could not use the OCR result.");
            }
        }
    }

    private void handleImportTranscriptions() {
        final ImportTranscriptionDialog importDialog =
                new ImportTranscriptionDialog();
        importDialog.setVisible(true);

        if (importDialog.isApproved() && projectModel.isPresent()) {
            final Path file = importDialog.getTranscriptionFile();
            final String sep = importDialog.getPageSeparator();

            try {
                Files.createDirectories(projectModel.get().getTranscriptionDir());

                view.getProgressBar().setIndeterminate(true);

                final BufferedReader reader =
                        Files.newBufferedReader(file, StandardCharsets.UTF_8);

                // for every file
                for (final Path imgFile : projectModel.get().getImageFiles()) {
                    final Path fname = FileNames.replaceExtension(
                            imgFile.getFileName(), "txt");
                    final Path transcription =
                            projectModel.get().getTranscriptionDir().resolve(
                                    fname);

                    final BufferedWriter writer = Files.newBufferedWriter(
                            transcription, StandardCharsets.UTF_8);

                    int lines = 0;

                    // read file line by line
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        // if the line equals the separator, create the next
                        // file
                        if (line.equals(sep)) {
                            break;
                        }

                        lines++;
                        // otherwise write the line to the current file
                        writer.write(line);
                        writer.write('\n');
                    }

                    // if a transcription file is empty, delete it
                    if (lines == 0) {
                        Files.delete(transcription);
                    }

                    writer.write('\n');
                    writer.close();
                }

                reader.close();

                Dialogs.showInfo(view, "Import Transcriptions",
                        "Transcription file successfully imported.");
            } catch (IOException e) {
                Dialogs.showError(view, "Import Exception",
                        "Could not import the transcription file.");
            } finally {
                view.getProgressBar().setIndeterminate(false);
            }
        }
    }

    private void handleBatchExport() {
        final Optional<BatchExportModel> export =
                BatchExportDialog.showBatchExportDialog(this);

        if (export.isPresent()) {
            final BatchExecutor batchExec = new BatchExecutor(this,
                    this.getProjectModel().get(), export.get());

            try {
                final int totalFiles;
                {
                    // count number of files to process
                    int count = 0;
                    for (@SuppressWarnings("unused")
                    final Path f : this.getProjectModel().get()
                            .getImageFiles()) {
                        count++;
                    }
                    totalFiles = count;
                }

                final ProgressMonitor progressMonitor = new ProgressMonitor(
                        view, "Processing:", "", 0, totalFiles + 1);
                progressMonitor.setProgress(0);

                final BufferedWriter errorLog = Files.newBufferedWriter(
                        export.get().getDestinationDir().resolve("errors.log"),
                        StandardCharsets.UTF_8);

                batchExec.start(progressMonitor, errorLog);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
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

    private void handleGenerateReport() {
        final Optional<Path> transcriptionFile = handleTranscriptionSave();

        if (!transcriptionFile.isPresent()) {
            Dialogs.showWarning(view, "Report",
                    "The report could not be generated.");
            return;
        }

        final Path sourceFile =
                getPageModel().get().getImageModel().getSourceFile();
        final Path fname =
                FileNames.replaceExtension(sourceFile.getFileName(), "txt");
        final Path repName = FileNames.replaceExtension(fname, "html");
        final Path plain =
                projectModel.get().getOCRDir().resolve(fname);
        final Path report =
                projectModel.get().getEvaluationDir().resolve(repName);

        try {
            final Path equivalencesFile = prepareReports();

            // generate report
            final Batch reportBatch = new Batch(
                    transcriptionFile.get().toFile(), plain.toFile());
            final Parameters pars = new Parameters();
            pars.eqfile.setValue(equivalencesFile.toFile());
            final Report rep = new Report(reportBatch, pars);

            // write to file
            DocumentWriter.writeToFile(rep.document(), report);

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(report.toFile());
            }
        } catch (WarningException | IOException | TransformerException e) {
            e.printStackTrace();
        }
    }

    public Path prepareReports() throws IOException {
        Files.createDirectories(projectModel.get().getEvaluationDir());

        final Path equivalencesFile = projectModel.get().getProjectDir().resolve(
                "character_equivalences.csv");

        if (!Files.exists(equivalencesFile)) {
            // copy the default character equivalences to the equivalences
            // file
            final BufferedInputStream defaultEq = new BufferedInputStream(
                    getClass().getResourceAsStream(
                            "/default_character_equivalences.csv"));
            final BufferedOutputStream eq = new BufferedOutputStream(
                    new FileOutputStream(equivalencesFile.toFile()));

            int c = -1;
            while ((c = defaultEq.read()) != -1) {
                eq.write(c);
            }

            defaultEq.close();
            eq.close();
        }

        return equivalencesFile;
    }

    private Optional<Path> handleTranscriptionSave() {
        try {
            if (projectModel.isPresent() && getPageModel().isPresent()) {
                Files.createDirectories(
                        projectModel.get().getTranscriptionDir());

                final Path sourceFile =
                        getPageModel().get().getImageModel().getSourceFile();
                final Path fileName =
                        FileNames.replaceExtension(sourceFile.getFileName(),
                                "txt");

                final Path transcriptionFile =
                        projectModel.get().getTranscriptionDir().resolve(
                                fileName);

                try (final Writer writer = Files.newBufferedWriter(
                        transcriptionFile, StandardCharsets.UTF_8)) {

                    final String transcription =
                            view.getEvaluationPane().getTextAreaTranscript()
                                    .getText();

                    writer.write(transcription);

                    return Optional.of(transcriptionFile);
                }
            }
        } catch (IOException e) {
            Dialogs.showError(view, "Exception",
                    "Transcription could not be saved.");
        }

        return Optional.absent();
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
        view.getMenuItemImportTranscriptions().setEnabled(b);
        view.getMenuItemOpenProjectDirectory().setEnabled(b);
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
        // TODO fix me
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

        // don't do anything, if no page is selected
        if (pt == null) {
            return;
        }

        final Preprocessor preprocessor = getPreprocessor(pt.getFile());
        view.getPreprocessingPane().setPreprocessor(preprocessor);

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
        } else if (view.getActiveComponent() == view.getSymbolOverview()) {
            view.getSymbolOverview().freeResources();
        }

        pageThumbnail = Optional.fromNullable(pt);

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
                        TesseractController.this,
                        getPreprocessor(pt.getFile()), pt.getFile(),
                        getProjectModel().get().getPreprocessedDir());

                // save reference
                preprocessingWorker = Optional.of(pw);

                view.getProgressBar().setIndeterminate(true);
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

    private void handleShowSymbolInBoxEditor() {
        final Symbol selected = view.getSymbolOverview().getSymbolVariantList()
                .getList().getSelectedValue();

        if (selected == null) {
            return;
        }

        view.getMainTabs().setSelectedComponent(view.getBoxEditor());

        final FilteredTable<Symbol> symbols = view.getBoxEditor().getSymbols();
        final ListModel<Symbol> model = symbols.getListModel();
        final int size = model.getSize();

        // find the selected symbol in
        for (int i = 0; i < size; i++) {
            if (selected == model.getElementAt(i)) {
                symbols.getTable().setRowSelectionInterval(i, i);
            }
        }
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
        model.sortBy((SymbolOrder) ordering.getSelectedItem());

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
        model.sortBy((SymbolOrder) ordering.getSelectedItem());
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

    private void handlePreprocessorPreview() {
        final Optional<Path> selectedPage = getSelectedPage();

        // if no page is selected, simply ignore it
        if (!selectedPage.isPresent()) {
            Dialogs.showWarning(view, "No page selection",
                    "No page has been selected. You need to select a page first.");
            return;
        }

        final Optional<ProjectModel> projectModel = getProjectModel();

        if (!projectModel.isPresent()) {
            Dialogs.showWarning(view, "No project",
                    "No project has been selected. You need to create a project first.");
            return;
        }

        final Preprocessor preprocessor =
                view.getPreprocessingPane().getPreprocessor();

        if (preprocessingWorker.isPresent()) {
            preprocessingWorker.get().cancel(false);
        }

        final PreprocessingWorker pw = new PreprocessingWorker(this,
                preprocessor, selectedPage.get(),
                projectModel.get().getProjectDir());

        preprocessingWorker = Optional.of(pw);

        view.getProgressBar().setIndeterminate(true);
        pw.execute();
    }

    private void handlePreprocessorChange(boolean allPages) {
        final Preprocessor preprocessor =
                view.getPreprocessingPane().getPreprocessor();

        if (allPages
                && Dialogs.ask(view, "Confirmation",
                        "Do you really want to apply the current preprocessing methods to all pages?")) {
            defaultPreprocessor = preprocessor;
            preprocessors.clear();

            if (getSelectedPage().isPresent()) {
                handlePreprocessorPreview();
            }
        } else if (getSelectedPage().isPresent()) {
            setPreprocessor(getSelectedPage().get(), preprocessor);

            handlePreprocessorPreview();
        }
    }

    private void handlePreferences() {
        final PreferencesDialog prefDialog = new PreferencesDialog();
        final ResultState result = prefDialog.showPreferencesDialog(view);
        if (result == ResultState.APPROVE) {
            final Preferences globalPrefs = GlobalPrefs.getPrefs();
            try {
                final Path execDir = Paths.get(prefDialog.getTfExecutablesDir().getText());
                if (Files.isDirectory(execDir)
                        && (Files.isExecutable(execDir.resolve("tesseract")) || Files.isExecutable(execDir.resolve("tesseract.exe")))) {
                    globalPrefs.put(PreferencesDialog.KEY_EXEC_DIR,
                            execDir.toString());
                }

                final Path langdataDir = Paths.get(prefDialog.getTfLangdataDir().getText());
                if (Files.isDirectory(langdataDir)) {
                    globalPrefs.put(PreferencesDialog.KEY_LANGDATA_DIR,
                            langdataDir.toString());
                }
            } catch (Exception e) {
                Dialogs.showWarning(view, "Error",
                        "Could not save the preferences.");
            }
        }
    }

    private void handleTraining() {

    }

    private void handleCharacterHistogram() {
        final JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setFileFilter(new FileFilter() {
            @Override
            public String getDescription() {
                return "Text files";
            }

            @Override
            public boolean accept(File f) {
                return f.canRead();
            }
        });

        final int approved = fc.showOpenDialog(view);
        if (approved == JFileChooser.APPROVE_OPTION) {
            final Path textFile = fc.getSelectedFile().toPath();

            try {
                final BufferedReader reader = Files.newBufferedReader(
                        textFile, StandardCharsets.UTF_8);

                final Map<Character, Integer> histogram =
                        new TreeMap<Character, Integer>();

                // build up a histogram
                int c = -1;
                while ((c = reader.read()) != -1) {
                    final char character = (char) c;

                    Integer val = histogram.get(character);

                    if (val == null) {
                        val = 0;
                    }

                    histogram.put(character, val + 1);
                }

                final CharacterHistogram ch = new CharacterHistogram(histogram);
                ch.setLocationRelativeTo(view);
                ch.setVisible(true);
            } catch (IOException e) {
                Dialogs.showError(view, "Invalid text file",
                        "Could not read the text file.");
            }
        }
    }

    private void handleInspectUnicharset() {
        final JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setFileFilter(new FileFilter() {
            @Override
            public String getDescription() {
                return "Unicharset files";
            }

            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith("unicharset");
            }
        });

        final int approved = fc.showOpenDialog(view);
        if (approved == JFileChooser.APPROVE_OPTION) {
            final Path unicharsetFile = fc.getSelectedFile().toPath();
            try {
                final Unicharset unicharset =
                        Unicharset.readFrom(Files.newBufferedReader(
                                unicharsetFile, StandardCharsets.UTF_8));

                // show the unicharset dialog
                final UnicharsetDebugger uniDebugger =
                        new UnicharsetDebugger(unicharset);
                uniDebugger.setLocationRelativeTo(view);
                uniDebugger.setVisible(true);
            } catch (IOException e) {
                Dialogs.showError(view, "Invalid Unicharset",
                        "Could not read the unicharset file. It may have an incompatible version.");
            }
        }
    }

    public void setPageModel(Optional<PageModel> model) {
        if (projectModel.isPresent() && model.isPresent()) {
            try {
                // plain text file name
                final Path fname =
                        FileNames.replaceExtension(model.get().getImageModel()
                                .getSourceFile().getFileName(), "txt");

                // create ocr directory
                Files.createDirectories(projectModel.get().getOCRDir());

                // write the plain text ocr file
                final Path plain =
                        projectModel.get().getOCRDir().resolve(fname);

                final Writer writer = Files.newBufferedWriter(plain,
                        StandardCharsets.UTF_8);
                new PlainTextWriter().write(model.get().getPage(), writer);
                writer.close();

                // read the transcription file
                final Path transcr =
                        projectModel.get().getTranscriptionDir().resolve(fname);

                if (Files.isRegularFile(transcr)) {
                    final byte[] bytes = Files.readAllBytes(transcr);
                    final String transcription = new String(bytes,
                            StandardCharsets.UTF_8);

                    model = Optional.of(model.get().withTranscription(
                            transcription));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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

        if (preprocessingWorker.isPresent()) {
            preprocessingWorker.get().cancel(true);
        }

        if (recognitionWorker.isPresent()) {
            recognitionWorker.get().cancel(true);
        }

        // forcefully shut down the application after 2 seconds
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        } finally {
            System.exit(0);
        }
    }

    public Preprocessor getDefaultPreprocessor() {
        return defaultPreprocessor;
    }

    public Preprocessor getPreprocessor(Path sourceFile) {
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
}
