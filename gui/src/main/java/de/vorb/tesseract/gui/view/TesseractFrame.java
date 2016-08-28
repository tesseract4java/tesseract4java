package de.vorb.tesseract.gui.view;

import de.vorb.tesseract.gui.model.PageThumbnail;
import de.vorb.tesseract.gui.model.PreferencesUtil;
import de.vorb.tesseract.gui.model.Scale;
import de.vorb.tesseract.gui.util.Filter;
import de.vorb.tesseract.gui.util.Resources;
import de.vorb.tesseract.gui.view.dialogs.PreferencesDialog;
import de.vorb.tesseract.gui.view.i18n.Labels;
import de.vorb.tesseract.gui.view.renderer.PageListCellRenderer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.prefs.Preferences;

/**
 * Swing component that allows to compare the results of Tesseract.
 */
public class TesseractFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private final FilteredList<PageThumbnail> listPages;
    private final FilteredList<String> listTrainingFiles;
    private final PreprocessingPane preprocessingPane;
    private final BoxEditor boxEditor;
    private final SymbolOverview glyphOverview;
    private final RecognitionPane recognitionPane;
    private final EvaluationPane evaluationPane;

    private final JLabel lblScaleFactor;
    private final JProgressBar pbLoadPage;
    private final JSplitPane spMain;
    private final JMenuItem mnNewProject;
    private final JTabbedPane tabsMain;
    private final JMenuItem mnPreferences;
    private final JMenuItem mnBatchExport;
    private final JSeparator separator_1;
    private final JMenuItem mnOpenProjectDirectory;
    private final JMenuItem mnOpenBoxFile;
    private final JMenu mnEdit;

    private final JSeparator separator_2;
    private final JMenuItem mnCloseProject;
    private final JMenuItem mnSaveBoxFile;
    private final JMenuItem mnImportTranscriptions;

    private final JMenu mnTools;
    private final JMenuItem mnCharacterHistogram;
    private final JMenuItem mnInspectUnicharset;

    private final JMenuItem mnExit;
    private final JMenuItem mnTesseractTrainer;

    private final Scale scale;

    /**
     * Create the application.
     */
    public TesseractFrame() {
        super();
        final Toolkit t = Toolkit.getDefaultToolkit();

        // load and set multiple icon sizes
        final List<Image> appIcons = new LinkedList<>();
        appIcons.add(t.getImage(
                TesseractFrame.class.getResource("/logos/logo_16.png")));
        appIcons.add(t.getImage(
                TesseractFrame.class.getResource("/logos/logo_96.png")));
        appIcons.add(t.getImage(
                TesseractFrame.class.getResource("/logos/logo_256.png")));
        setIconImages(appIcons);

        setLocationByPlatform(true);
        setMinimumSize(new Dimension(1100, 680));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        scale = new Scale();
        preprocessingPane = new PreprocessingPane();
        boxEditor = new BoxEditor(scale);
        glyphOverview = new SymbolOverview();

        final Preferences prefs = PreferencesUtil.getPreferences();
        final String renderingFont = prefs.get(PreferencesDialog.KEY_RENDERING_FONT, Font.SANS_SERIF);
        final String editorFont = prefs.get(PreferencesDialog.KEY_EDITOR_FONT, Font.MONOSPACED);

        recognitionPane = new RecognitionPane(scale, renderingFont);
        evaluationPane = new EvaluationPane(scale, editorFont);
        evaluationPane.getGenerateReportButton().setIcon(
                new ImageIcon(TesseractFrame.class.getResource("/icons/report.png")));
        pbLoadPage = new JProgressBar();
        spMain = new JSplitPane();

        listPages = new FilteredList<>(
                query -> {
                    final String[] terms =
                            query.toLowerCase().split("\\s+");

                    final Filter<PageThumbnail> filter;
                    if (query.isEmpty()) {
                        filter = null;
                    } else {
                        // item must contain all terms in query
                        filter = item -> {
                            String filename =
                                    item.getFile().getFileName().toString().toLowerCase();
                            for (String term : terms) {
                                if (!filename.contains(term)) {
                                    return false;
                                }
                            }
                            return true;
                        };
                    }
                    return Optional.ofNullable(filter);
                });
        listPages.getList().setCellRenderer(new PageListCellRenderer());

        listPages.setMinimumSize(new Dimension(250, 100));
        listPages.getList().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listPages.setBorder(BorderFactory.createTitledBorder("Image"));

        // filtered string list
        listTrainingFiles =
                new FilteredList<>(query -> {
                    final String[] terms =
                            query.toLowerCase().split("\\s+");

                    final Filter<String> filter;
                    if (query.isEmpty()) {
                        filter = null;
                    } else {
                        // item must contain all terms in query
                        filter = item -> {
                            for (String term : terms) {
                                if (!item.toLowerCase().contains(term)) {
                                    return false;
                                }
                            }
                            return true;
                        };
                    }
                    return Optional.ofNullable(filter);
                });

        listTrainingFiles.setBorder(BorderFactory.createTitledBorder("Traineddata File"));

        setTitle(Labels.getLabel(getLocale(), "frame_title"));

        // Menu

        final JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        final JMenu mnFile = new JMenu(
                Labels.getLabel(getLocale(), "menu_file"));
        menuBar.add(mnFile);

        mnNewProject = new JMenuItem("New Project");
        mnNewProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                InputEvent.CTRL_MASK));
        mnFile.add(mnNewProject);

        mnOpenBoxFile = new JMenuItem("Open Box File...");
        mnOpenBoxFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
        mnFile.add(mnOpenBoxFile);

        separator_2 = new JSeparator();
        mnFile.add(separator_2);

        mnSaveBoxFile = new JMenuItem("Save Box File");
        mnSaveBoxFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
        mnSaveBoxFile.setEnabled(false);
        mnSaveBoxFile.setIcon(new ImageIcon(
                TesseractFrame.class.getResource("/icons/table_save.png")));
        mnFile.add(mnSaveBoxFile);

        mnOpenProjectDirectory = new JMenuItem("Open Project Directory");
        mnOpenProjectDirectory.setEnabled(false);
        mnOpenProjectDirectory.setIcon(new ImageIcon(
                TesseractFrame.class.getResource("/icons/folder_explore.png")));
        mnFile.add(mnOpenProjectDirectory);

        mnCloseProject = new JMenuItem("Close Project");
        mnCloseProject.setEnabled(false);
        mnFile.add(mnCloseProject);

        separator_1 = new JSeparator();
        mnFile.add(separator_1);

        mnImportTranscriptions = new JMenuItem("Import Transcriptions...");
        mnImportTranscriptions.setEnabled(false);
        mnImportTranscriptions.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_I, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
        mnFile.add(mnImportTranscriptions);

        mnBatchExport = new JMenuItem("Batch Export...");
        mnBatchExport.setEnabled(false);
        mnBatchExport.setIcon(new ImageIcon(
                TesseractFrame.class.getResource("/icons/book_next.png")));
        mnBatchExport.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
                InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
        mnFile.add(mnBatchExport);

        final JSeparator separator = new JSeparator();
        mnFile.add(separator);

        mnExit = new JMenuItem("Exit");
        mnExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                InputEvent.CTRL_MASK));
        mnFile.add(mnExit);

        mnEdit = new JMenu("Edit");
        menuBar.add(mnEdit);

        mnPreferences = new JMenuItem("Preferences");
        mnPreferences.setIcon(new ImageIcon(
                TesseractFrame.class.getResource("/icons/cog.png")));
        mnEdit.add(mnPreferences);

        mnTools = new JMenu("Tools");
        menuBar.add(mnTools);

        mnCharacterHistogram = new JMenuItem("Character Histogram...");
        mnTools.add(mnCharacterHistogram);

        mnInspectUnicharset = new JMenuItem("Debug Unicharset...");
        mnTools.add(mnInspectUnicharset);

        mnTesseractTrainer = new JMenuItem("Tesseract Trainer...");
        mnTesseractTrainer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
                InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
        mnTools.add(mnTesseractTrainer);

        final JMenu mnHelp = new JMenu(
                Labels.getLabel(getLocale(), "menu_help"));
        menuBar.add(mnHelp);

        final JMenuItem mntmAbout = new JMenuItem(Labels.getLabel(getLocale(),
                "menu_about"));
        mntmAbout.setIcon(new ImageIcon(
                TesseractFrame.class.getResource("/icons/information.png")));
        mntmAbout.addActionListener(e -> JOptionPane.showMessageDialog(TesseractFrame.this,
                Labels.getLabel(getLocale(), "about_message"),
                Labels.getLabel(getLocale(), "about_title"),
                JOptionPane.INFORMATION_MESSAGE));
        mnHelp.add(mntmAbout);

        // Contents

        final JPanel panel = new JPanel();
        panel.setBackground(SystemColor.menu);
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(panel, BorderLayout.SOUTH);
        final GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[]{0, 50, 417,
                0, 0};
        gbl_panel.rowHeights = new int[]{14, 0};
        gbl_panel.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0,
                Double.MIN_VALUE};
        gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
        panel.setLayout(gbl_panel);

        JLabel lblScale = new JLabel("Scale:");
        GridBagConstraints gbc_lblScale = new GridBagConstraints();
        gbc_lblScale.insets = new Insets(0, 0, 0, 5);
        gbc_lblScale.gridx = 0;
        gbc_lblScale.gridy = 0;
        panel.add(lblScale, gbc_lblScale);

        lblScaleFactor = new JLabel(scale.toString());
        GridBagConstraints gbc_lblScaleFactor = new GridBagConstraints();
        gbc_lblScaleFactor.anchor = GridBagConstraints.WEST;
        gbc_lblScaleFactor.insets = new Insets(0, 0, 0, 5);
        gbc_lblScaleFactor.gridx = 1;
        gbc_lblScaleFactor.gridy = 0;
        panel.add(lblScaleFactor, gbc_lblScaleFactor);

        GridBagConstraints gbc_pbRegognitionProgress = new GridBagConstraints();
        gbc_pbRegognitionProgress.gridx = 3;
        gbc_pbRegognitionProgress.gridy = 0;
        panel.add(pbLoadPage, gbc_pbRegognitionProgress);
        getContentPane().add(spMain, BorderLayout.CENTER);

        tabsMain = new JTabbedPane();
        tabsMain.addTab(Labels.getLabel(getLocale(), "tab_main_preprocessing"),
                Resources.getIcon("contrast"), preprocessingPane);

        tabsMain.addTab(Labels.getLabel(getLocale(), "tab_main_boxeditor"),
                Resources.getIcon("table_edit"), boxEditor);

        tabsMain.addTab(
                Labels.getLabel(getLocale(), "tab_main_symboloverview"),
                Resources.getIcon("application_view_icons"),
                glyphOverview);

        tabsMain.addTab(Labels.getLabel(getLocale(), "tab_main_recognition"),
                Resources.getIcon("application_tile_horizontal"),
                recognitionPane);

        tabsMain.addTab(Labels.getLabel(getLocale(), "tab_main_evaluation"),
                Resources.getIcon("chart_pie"),
                evaluationPane);

        spMain.setRightComponent(tabsMain);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(1.0);
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        spMain.setLeftComponent(splitPane);
        splitPane.setLeftComponent(listPages);
        splitPane.setRightComponent(listTrainingFiles);
    }

    public MainComponent getActiveComponent() {
        return (MainComponent) tabsMain.getSelectedComponent();
    }

    public BoxEditor getBoxEditor() {
        return boxEditor;
    }

    public JTabbedPane getMainTabs() {
        return tabsMain;
    }

    public JMenuItem getMenuItemNewProject() {
        return mnNewProject;
    }

    public JMenuItem getMenuItemOpenBoxFile() {
        return mnOpenBoxFile;
    }

    public JMenuItem getMenuItemSaveBoxFile() {
        return mnSaveBoxFile;
    }

    public JMenuItem getMenuItemCloseProject() {
        return mnCloseProject;
    }

    public JMenuItem getMenuItemOpenProjectDirectory() {
        return mnOpenProjectDirectory;
    }

    public JMenuItem getMenuItemImportTranscriptions() {
        return mnImportTranscriptions;
    }

    public JMenuItem getMenuItemBatchExport() {
        return mnBatchExport;
    }

    public JMenuItem getMenuItemExit() {
        return mnExit;
    }

    public JMenuItem getMenuItemPreferences() {
        return mnPreferences;
    }

    public JMenuItem getMenuItemCharacterHistogram() {
        return mnCharacterHistogram;
    }

    public JMenuItem getMenuItemInspectUnicharset() {
        return mnInspectUnicharset;
    }

    public JMenuItem getMenuItemTesseractTrainer() {
        return mnTesseractTrainer;
    }

    public FilteredList<PageThumbnail> getPages() {
        return listPages;
    }

    public PreprocessingPane getPreprocessingPane() {
        return preprocessingPane;
    }

    public JProgressBar getProgressBar() {
        return pbLoadPage;
    }

    public RecognitionPane getRecognitionPane() {
        return recognitionPane;
    }

    public Scale getScale() {
        return scale;
    }

    public JLabel getScaleLabel() {
        return lblScaleFactor;
    }

    public SymbolOverview getSymbolOverview() {
        return glyphOverview;
    }

    public EvaluationPane getEvaluationPane() {
        return evaluationPane;
    }

    public FilteredList<String> getTraineddataFiles() {
        return listTrainingFiles;
    }
}
