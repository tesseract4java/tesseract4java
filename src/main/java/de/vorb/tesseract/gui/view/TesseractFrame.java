package de.vorb.tesseract.gui.view;

import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.common.base.Optional;

import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.gui.util.Filter;
import de.vorb.tesseract.gui.util.FilterProvider;
import de.vorb.tesseract.gui.view.i18n.Labels;
import de.vorb.tesseract.util.Symbol;

/**
 * Swing component that allows to compare the results of Tesseract.
 */
public class TesseractFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JLabel lbCanvasOCR;
    private JLabel lbCanvasOriginal;
    private final FilteredList<Path> listPages;
    private final FilteredList<String> listTrainingFiles;
    private final BoxEditor boxEditor;
    private final ComparatorPane recognitionPane;
    private final GlyphExportPane exportPane;
    private final OpenProjectDialog openProjectDialog;

    private final JProgressBar pbLoadPage;
    private final ButtonGroup bgrpView = new ButtonGroup();
    private final JSplitPane spMain;

    /**
     * Create the application.
     */
    public TesseractFrame() {
        super();
        final Toolkit t = Toolkit.getDefaultToolkit();

        // load and set multiple icon sizes
        final List<Image> appIcons = new LinkedList<Image>();
        appIcons.add(t.getImage(
                TesseractFrame.class.getResource("/logos/logo_16.png")));
        appIcons.add(t.getImage(
                TesseractFrame.class.getResource("/logos/logo_96.png")));
        appIcons.add(t.getImage(
                TesseractFrame.class.getResource("/logos/logo_256.png")));
        setIconImages(appIcons);

        setLocationByPlatform(true);
        setMinimumSize(new Dimension(1100, 680));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        openProjectDialog = new OpenProjectDialog(this);
        boxEditor = new BoxEditor();
        recognitionPane = new ComparatorPane();
        exportPane = new GlyphExportPane();
        pbLoadPage = new JProgressBar();
        spMain = new JSplitPane();

        listPages = new FilteredList<Path>(new FilterProvider<Path>() {
            public Optional<Filter<Path>> getFilter(String query) {
                final String[] terms = query.split("\\s+");

                final Filter<Path> filter;
                if (query.isEmpty()) {
                    filter = null;
                } else {
                    // item must contain all terms in query
                    filter = new Filter<Path>() {
                        @Override
                        public boolean accept(Path item) {
                            String fname = item.getFileName().toString();
                            for (String term : terms) {
                                if (!fname.contains(term)) {
                                    return false;
                                }
                            }
                            return true;
                        }
                    };
                }
                return Optional.fromNullable(filter);
            }
        });

        listPages.setMinimumSize(new Dimension(250, 100));
        listPages.getList().setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        listPages.setBorder(BorderFactory.createTitledBorder("Page"));

        // filtered string list
        listTrainingFiles =
                new FilteredList<String>(new FilterProvider<String>() {
                    public Optional<Filter<String>> getFilter(String query) {
                        final String[] terms = query.split("\\s+");

                        final Filter<String> filter;
                        if (query.isEmpty()) {
                            filter = null;
                        } else {
                            // item must contain all terms in query
                            filter = new Filter<String>() {
                                @Override
                                public boolean accept(String item) {
                                    for (String term : terms) {
                                        if (!item.contains(term)) {
                                            return false;
                                        }
                                    }
                                    return true;
                                }
                            };
                        }
                        return Optional.fromNullable(filter);
                    }
                });

        listTrainingFiles.setBorder(BorderFactory.createTitledBorder("Training File"));

        exportPane.getGlyphSelectionPane().getList().addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent e) {
                        if (e.getValueIsAdjusting()) {
                            return;
                        }

                        @SuppressWarnings("unchecked")
                        final JList<Entry<String, Set<Symbol>>> selectionList =
                                (JList<Entry<String, Set<Symbol>>>) e.getSource();

                        final Set<Symbol> symbols = selectionList.getModel().getElementAt(
                                selectionList.getSelectedIndex()).getValue();

                        final DefaultListModel<Symbol> model = new DefaultListModel<>();
                        for (final Symbol symbol : symbols) {
                            if (symbol.getBoundingBox().getHeight() > 0) {
                                model.addElement(symbol);
                            }
                        }

                        exportPane.getGlyphListPane().getList().setModel(
                                model);
                    }
                });

        setTitle(Labels.getLabel(getLocale(), "frame_title"));

        // Menu

        final JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        final JMenu mnFile = new JMenu(
                Labels.getLabel(getLocale(), "menu_file"));
        menuBar.add(mnFile);

        openProjectDialog.setModalityType(ModalityType.APPLICATION_MODAL);

        final JMenuItem mnOpenProject = new JMenuItem(Labels.getLabel(
                getLocale(),
                "menu_open_project"));
        mnOpenProject.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                openProjectDialog.setVisible(true);
            }
        });
        mnOpenProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                InputEvent.CTRL_MASK));
        mnFile.add(mnOpenProject);

        final JMenuItem mntmOcrcomparison = new JMenuItem("OCR-Comparison");
        mnFile.add(mntmOcrcomparison);

        final JSeparator separator = new JSeparator();
        mnFile.add(separator);

        final JMenuItem mntmExit = new JMenuItem(
                Labels.getLabel(getLocale(), "menu_exit"));
        mntmExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TesseractFrame.this.dispose();
            }
        });
        mnFile.add(mntmExit);

        final JMenu mnEdit = new JMenu(
                Labels.getLabel(getLocale(), "menu_edit"));
        menuBar.add(mnEdit);

        final JMenu mnView = new JMenu(
                Labels.getLabel(getLocale(), "menu_view"));
        menuBar.add(mnView);

        final JRadioButtonMenuItem rmTraining = new JRadioButtonMenuItem(
                "Training");
        bgrpView.add(rmTraining);
        mnView.add(rmTraining);

        final JRadioButtonMenuItem rmRecognition = new JRadioButtonMenuItem(
                "Recognition");
        bgrpView.add(rmRecognition);
        mnView.add(rmRecognition);

        final JRadioButtonMenuItem rmExport = new JRadioButtonMenuItem(
                "Export");
        bgrpView.add(rmExport);
        mnView.add(rmExport);

        // on a view change, show the other component (export glyphs or compare
        // results)
        final ActionListener viewChangeListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (rmTraining.isSelected()
                        && getMainComponent() != boxEditor) {
                    setMainComponent(boxEditor);
                } else if (rmRecognition.isSelected()
                        && getMainComponent() != recognitionPane) {
                    setMainComponent(recognitionPane);
                } else if (rmExport.isSelected()
                        && getMainComponent() != exportPane) {
                    setMainComponent(exportPane);
                }
            }
        };

        rmTraining.addActionListener(viewChangeListener);
        rmRecognition.addActionListener(viewChangeListener);
        rmExport.addActionListener(viewChangeListener);
        bgrpView.setSelected(rmTraining.getModel(), true);

        final JMenu mnHelp = new JMenu(
                Labels.getLabel(getLocale(), "menu_help"));
        menuBar.add(mnHelp);

        final JMenuItem mntmAbout = new JMenuItem(Labels.getLabel(getLocale(),
                "menu_about"));
        mntmAbout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(TesseractFrame.this,
                        Labels.getLabel(getLocale(), "about_message"),
                        Labels.getLabel(getLocale(), "about_title"),
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        mnHelp.add(mntmAbout);

        // Contents

        final JPanel panel = new JPanel();
        panel.setBackground(SystemColor.menu);
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(panel, BorderLayout.SOUTH);
        final GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[] { 111, 84, 46, 0, 46, 417, 50, 40,
                0, 0 };
        gbl_panel.rowHeights = new int[] { 14, 0 };
        gbl_panel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0,
                0.0,
                0.0, 0.0, Double.MIN_VALUE };
        gbl_panel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        panel.setLayout(gbl_panel);

        final JLabel lblProjectOverview = new JLabel(Labels.getLabel(
                getLocale(),
                "project_overview"));
        lblProjectOverview.setFont(new Font("Tahoma", Font.BOLD, 11));
        GridBagConstraints gbc_lblProjectOverview = new GridBagConstraints();
        gbc_lblProjectOverview.anchor = GridBagConstraints.WEST;
        gbc_lblProjectOverview.insets = new Insets(0, 0, 0, 5);
        gbc_lblProjectOverview.gridx = 0;
        gbc_lblProjectOverview.gridy = 0;
        panel.add(lblProjectOverview, gbc_lblProjectOverview);

        final JLabel lblCorrectWords = new JLabel(Labels.getLabel(getLocale(),
                "correct_words"));
        GridBagConstraints gbc_lblCorrectWords = new GridBagConstraints();
        gbc_lblCorrectWords.fill = GridBagConstraints.VERTICAL;
        gbc_lblCorrectWords.insets = new Insets(0, 0, 0, 5);
        gbc_lblCorrectWords.anchor = GridBagConstraints.EAST;
        gbc_lblCorrectWords.gridx = 1;
        gbc_lblCorrectWords.gridy = 0;
        panel.add(lblCorrectWords, gbc_lblCorrectWords);

        final JLabel label = new JLabel("0");
        label.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gbc_label = new GridBagConstraints();
        gbc_label.insets = new Insets(0, 0, 0, 5);
        gbc_label.anchor = GridBagConstraints.WEST;
        gbc_label.gridx = 2;
        gbc_label.gridy = 0;
        panel.add(label, gbc_label);

        final JLabel lblIncorrectWords = new JLabel(Labels.getLabel(
                getLocale(),
                "incorrect_words"));
        GridBagConstraints gbc_lblIncorrectWords = new GridBagConstraints();
        gbc_lblIncorrectWords.anchor = GridBagConstraints.EAST;
        gbc_lblIncorrectWords.insets = new Insets(0, 0, 0, 5);
        gbc_lblIncorrectWords.gridx = 3;
        gbc_lblIncorrectWords.gridy = 0;
        panel.add(lblIncorrectWords, gbc_lblIncorrectWords);

        final JLabel label_1 = new JLabel("0");
        GridBagConstraints gbc_label_1 = new GridBagConstraints();
        gbc_label_1.insets = new Insets(0, 0, 0, 5);
        gbc_label_1.anchor = GridBagConstraints.WEST;
        gbc_label_1.gridx = 4;
        gbc_label_1.gridy = 0;
        panel.add(label_1, gbc_label_1);

        final JLabel lblTotalWords = new JLabel(Labels.getLabel(getLocale(),
                "total_words"));
        GridBagConstraints gbc_lblTotalWords = new GridBagConstraints();
        gbc_lblTotalWords.anchor = GridBagConstraints.EAST;
        gbc_lblTotalWords.insets = new Insets(0, 0, 0, 5);
        gbc_lblTotalWords.gridx = 6;
        gbc_lblTotalWords.gridy = 0;
        panel.add(lblTotalWords, gbc_lblTotalWords);

        final JLabel label_2 = new JLabel("0");
        GridBagConstraints gbc_label_2 = new GridBagConstraints();
        gbc_label_2.insets = new Insets(0, 0, 0, 5);
        gbc_label_2.anchor = GridBagConstraints.WEST;
        gbc_label_2.gridx = 7;
        gbc_label_2.gridy = 0;
        panel.add(label_2, gbc_label_2);

        GridBagConstraints gbc_pbRegognitionProgress = new GridBagConstraints();
        gbc_pbRegognitionProgress.gridx = 8;
        gbc_pbRegognitionProgress.gridy = 0;
        panel.add(pbLoadPage, gbc_pbRegognitionProgress);
        getContentPane().add(spMain, BorderLayout.CENTER);

        JTabbedPane mainTabs = new JTabbedPane();
        mainTabs.addTab(
                Labels.getLabel(getLocale(), "tab_main_boxeditor"),
                new ImageIcon(
                        getClass().getResource("/icons/table_edit.png")),
                boxEditor);

        spMain.setRightComponent(mainTabs);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(1.0);
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        spMain.setLeftComponent(splitPane);
        splitPane.setLeftComponent(listPages);
        splitPane.setRightComponent(listTrainingFiles);
    }

    private MainComponent getMainComponent() {
        final Component main = spMain.getRightComponent();
        if (main instanceof MainComponent) {
            return (MainComponent) main;
        } else {
            throw new IllegalStateException(
                    "The current main component is not an instance of MainComponent.");
        }
    }

    private void setMainComponent(MainComponent main) {
        final MainComponent old = getMainComponent();
        if (main == old) {
            return;
        }

        main.setModel(old.getModel());
        spMain.setRightComponent(main.asComponent());
    }

    public OpenProjectDialog getLoadProjectDialog() {
        return openProjectDialog;
    }

    public FilteredList<Path> getPageList() {
        return listPages;
    }

    public FilteredList<String> getTrainingFiles() {
        return listTrainingFiles;
    }

    public BoxEditor getBoxEditor() {
        return boxEditor;
    }

    public ComparatorPane getComparatorPane() {
        return recognitionPane;
    }

    public GlyphExportPane getGlyphExportPane() {
        return exportPane;
    }

    public JProgressBar getPageLoadProgressBar() {
        return pbLoadPage;
    }

    public void setModel(Optional<PageModel> model) {
        getMainComponent().setModel(model);
    }

    public Optional<PageModel> getModel() {
        return getMainComponent().getModel();
    }
}
