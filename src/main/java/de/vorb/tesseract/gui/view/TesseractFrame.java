package de.vorb.tesseract.gui.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.vorb.tesseract.gui.event.LocaleChangeListener;
import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.gui.view.i18n.Labels;
import de.vorb.tesseract.gui.view.renderer.GlyphListCellRenderer;
import de.vorb.tesseract.util.Line;
import de.vorb.tesseract.util.Page;
import de.vorb.tesseract.util.Symbol;
import de.vorb.tesseract.util.Word;

/**
 * Swing component that allows to compare the results of Tesseract.
 */
public class TesseractFrame extends JFrame implements LocaleChangeListener {
    private static final long serialVersionUID = 1L;
    private JLabel lbCanvasOCR;
    private JLabel lbCanvasOriginal;
    private final PageSelectionPane pageSelectionPane;
    private final BoxFilePane trainingPane;
    private final ComparatorPane recognitionPane;
    private final GlyphExportPane exportPane;
    private final OpenProjectDialog openProjectDialog;

    private final ButtonGroup bgrpLanguage = new ButtonGroup();
    private final JProgressBar pbLoadPage;
    private final ButtonGroup bgrpView = new ButtonGroup();
    private final JSplitPane spMain;

    private static final Comparator<Entry<String, Set<Symbol>>> glyphComparator =
            new Comparator<Entry<String, Set<Symbol>>>() {
                @Override
                public int compare(Entry<String, Set<Symbol>> o1,
                        Entry<String, Set<Symbol>> o2) {
                    return o2.getValue().size() - o1.getValue().size();
                }
            };

    private static final Comparator<Symbol> symbolComparator = new Comparator<Symbol>() {
        @Override
        public int compare(Symbol o1, Symbol o2) {
            if (o2.getConfidence() >= o1.getConfidence())
                return 1;

            return -1;
        }
    };

    /**
     * Create the application.
     */
    public TesseractFrame() {
        super();
        setLocationByPlatform(true);
        setMinimumSize(new Dimension(1024, 680));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        openProjectDialog = new OpenProjectDialog(this);
        pageSelectionPane = new PageSelectionPane();
        trainingPane = new BoxFilePane();
        recognitionPane = new ComparatorPane();
        exportPane = new GlyphExportPane();
        pbLoadPage = new JProgressBar();
        spMain = new JSplitPane();

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

        localeChanged();
    }

    @Override
    public void localeChanged() {
        final boolean wasVisible = isVisible();
        if (wasVisible)
            setVisible(false);

        getContentPane().removeAll();
        pageSelectionPane.localeChanged();
        openProjectDialog.localeChanged();
        // comparatorPane.localeChanged();

        setTitle(Labels.getLabel(getLocale(), "frame_title"));

        // Menu

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu mnFile = new JMenu(Labels.getLabel(getLocale(), "menu_file"));
        menuBar.add(mnFile);

        openProjectDialog.setModalityType(ModalityType.APPLICATION_MODAL);

        JMenuItem mnOpenProject = new JMenuItem(Labels.getLabel(getLocale(),
                "menu_open_project"));
        mnOpenProject.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                openProjectDialog.setVisible(true);
            }
        });
        mnOpenProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                InputEvent.CTRL_MASK));
        mnFile.add(mnOpenProject);

        JMenuItem mntmOcrcomparison = new JMenuItem("OCR-Comparison");
        mnFile.add(mntmOcrcomparison);

        JSeparator separator = new JSeparator();
        mnFile.add(separator);

        JMenuItem mntmExit = new JMenuItem(
                Labels.getLabel(getLocale(), "menu_exit"));
        mntmExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TesseractFrame.this.dispose();
            }
        });
        mnFile.add(mntmExit);

        JMenu mnEdit = new JMenu(Labels.getLabel(getLocale(), "menu_edit"));
        menuBar.add(mnEdit);

        JMenu mnView = new JMenu(Labels.getLabel(getLocale(), "menu_view"));
        menuBar.add(mnView);

        JMenu mnLanguage = new JMenu("Language");
        mnView.add(mnLanguage);

        JRadioButtonMenuItem rbtnEnglish = new JRadioButtonMenuItem("English");
        mnLanguage.add(rbtnEnglish);
        bgrpLanguage.add(rbtnEnglish);
        JRadioButtonMenuItem rbtnGerman = new JRadioButtonMenuItem("Deutsch");
        mnLanguage.add(rbtnGerman);
        bgrpLanguage.add(rbtnGerman);

        // only if the language is really german, select that
        if (getLocale().getLanguage().equals(Locale.GERMAN.getLanguage())) {
            rbtnGerman.getModel().setSelected(true);
        } else {
            rbtnEnglish.getModel().setSelected(true);
        }

        rbtnGerman.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ResourceBundle.clearCache();

                Locale.setDefault(Locale.GERMAN);
                JComponent.setDefaultLocale(Locale.GERMAN);
                TesseractFrame.this.setLocale(Locale.GERMAN);

                TesseractFrame.this.localeChanged();
            }
        });

        rbtnEnglish.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ResourceBundle.clearCache();

                Locale.setDefault(Locale.ENGLISH);
                JComponent.setDefaultLocale(Locale.ENGLISH);
                TesseractFrame.this.setLocale(Locale.ENGLISH);

                TesseractFrame.this.localeChanged();
            }
        });

        final JSeparator separator_1 = new JSeparator();
        mnView.add(separator_1);

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
                        && getMainComponent() != trainingPane) {
                    setMainComponent(trainingPane);
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

        JPanel panel = new JPanel();
        panel.setBackground(SystemColor.menu);
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(panel, BorderLayout.SOUTH);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[] { 111, 84, 46, 0, 46, 417, 50, 40,
                0, 0 };
        gbl_panel.rowHeights = new int[] { 14, 0 };
        gbl_panel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0,
                0.0,
                0.0, 0.0, Double.MIN_VALUE };
        gbl_panel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        panel.setLayout(gbl_panel);

        JLabel lblProjectOverview = new JLabel(Labels.getLabel(getLocale(),
                "project_overview"));
        lblProjectOverview.setFont(new Font("Tahoma", Font.BOLD, 11));
        GridBagConstraints gbc_lblProjectOverview = new GridBagConstraints();
        gbc_lblProjectOverview.anchor = GridBagConstraints.WEST;
        gbc_lblProjectOverview.insets = new Insets(0, 0, 0, 5);
        gbc_lblProjectOverview.gridx = 0;
        gbc_lblProjectOverview.gridy = 0;
        panel.add(lblProjectOverview, gbc_lblProjectOverview);

        JLabel lblCorrectWords = new JLabel(Labels.getLabel(getLocale(),
                "correct_words"));
        GridBagConstraints gbc_lblCorrectWords = new GridBagConstraints();
        gbc_lblCorrectWords.fill = GridBagConstraints.VERTICAL;
        gbc_lblCorrectWords.insets = new Insets(0, 0, 0, 5);
        gbc_lblCorrectWords.anchor = GridBagConstraints.EAST;
        gbc_lblCorrectWords.gridx = 1;
        gbc_lblCorrectWords.gridy = 0;
        panel.add(lblCorrectWords, gbc_lblCorrectWords);

        JLabel label = new JLabel("0");
        label.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gbc_label = new GridBagConstraints();
        gbc_label.insets = new Insets(0, 0, 0, 5);
        gbc_label.anchor = GridBagConstraints.WEST;
        gbc_label.gridx = 2;
        gbc_label.gridy = 0;
        panel.add(label, gbc_label);

        JLabel lblIncorrectWords = new JLabel(Labels.getLabel(getLocale(),
                "incorrect_words"));
        GridBagConstraints gbc_lblIncorrectWords = new GridBagConstraints();
        gbc_lblIncorrectWords.anchor = GridBagConstraints.EAST;
        gbc_lblIncorrectWords.insets = new Insets(0, 0, 0, 5);
        gbc_lblIncorrectWords.gridx = 3;
        gbc_lblIncorrectWords.gridy = 0;
        panel.add(lblIncorrectWords, gbc_lblIncorrectWords);

        JLabel label_1 = new JLabel("0");
        GridBagConstraints gbc_label_1 = new GridBagConstraints();
        gbc_label_1.insets = new Insets(0, 0, 0, 5);
        gbc_label_1.anchor = GridBagConstraints.WEST;
        gbc_label_1.gridx = 4;
        gbc_label_1.gridy = 0;
        panel.add(label_1, gbc_label_1);

        JLabel lblTotalWords = new JLabel(Labels.getLabel(getLocale(),
                "total_words"));
        GridBagConstraints gbc_lblTotalWords = new GridBagConstraints();
        gbc_lblTotalWords.anchor = GridBagConstraints.EAST;
        gbc_lblTotalWords.insets = new Insets(0, 0, 0, 5);
        gbc_lblTotalWords.gridx = 6;
        gbc_lblTotalWords.gridy = 0;
        panel.add(lblTotalWords, gbc_lblTotalWords);

        JLabel label_2 = new JLabel("0");
        GridBagConstraints gbc_label_2 = new GridBagConstraints();
        gbc_label_2.insets = new Insets(0, 0, 0, 5);
        gbc_label_2.anchor = GridBagConstraints.WEST;
        gbc_label_2.gridx = 7;
        gbc_label_2.gridy = 0;
        panel.add(label_2, gbc_label_2);

        GridBagConstraints gbc_pbRegognitionProgress = new GridBagConstraints();
        gbc_pbRegognitionProgress.fill = GridBagConstraints.HORIZONTAL;
        gbc_pbRegognitionProgress.gridx = 8;
        gbc_pbRegognitionProgress.gridy = 0;
        panel.add(pbLoadPage, gbc_pbRegognitionProgress);

        spMain.setResizeWeight(0.0);
        getContentPane().add(spMain, BorderLayout.CENTER);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setMinimumSize(new Dimension(300, 300));
        spMain.setLeftComponent(tabbedPane);

        pageSelectionPane.setBorder(new EmptyBorder(0, 2, 2, 2));
        tabbedPane.add(Labels.getLabel(getLocale(), "tab_project"),
                pageSelectionPane);

        spMain.setRightComponent(trainingPane);

        setVisible(wasVisible);
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
        if (main == getMainComponent()) {
            return;
        }

        spMain.setRightComponent(main.asComponent());
    }

    public OpenProjectDialog getLoadProjectDialog() {
        return openProjectDialog;
    }

    public PageSelectionPane getPageSelectionPane() {
        return pageSelectionPane;
    }

    public JLabel getCanvasOCR() {
        return lbCanvasOCR;
    }

    public JLabel getCanvasOriginal() {
        return lbCanvasOriginal;
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

    public void setModel(PageModel model) {
        getMainComponent().setModel(model);
    }
}
