package de.vorb.tesseract.gui.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
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
import de.vorb.tesseract.gui.model.PageThumbnail;
import de.vorb.tesseract.gui.model.Scale;
import de.vorb.tesseract.gui.util.Filter;
import de.vorb.tesseract.gui.util.FilterProvider;
import de.vorb.tesseract.gui.util.Resources;
import de.vorb.tesseract.gui.view.i18n.Labels;
import de.vorb.tesseract.gui.view.renderer.PageListCellRenderer;
import de.vorb.tesseract.util.Symbol;

/**
 * Swing component that allows to compare the results of Tesseract.
 */
public class TesseractFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private final FilteredList<PageThumbnail> listPages;
    private final FilteredList<String> listTrainingFiles;
    private final BoxEditor boxEditor;
    private final RecognitionPane recognitionPane;
    private final SymbolOverview glyphOverview;

    private final JLabel lblScaleFactor;
    private final JProgressBar pbLoadPage;
    private final JSplitPane spMain;
    private final JMenuItem mnNewProject;
    private final JTabbedPane tabsMain;

    private final Scale scale;

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

        scale = new Scale();
        boxEditor = new BoxEditor(scale);
        recognitionPane = new RecognitionPane();
        glyphOverview = new SymbolOverview();
        pbLoadPage = new JProgressBar();
        spMain = new JSplitPane();

        listPages = new FilteredList<PageThumbnail>(
                new FilterProvider<PageThumbnail>() {
                    public Optional<Filter<PageThumbnail>> getFilterFor(
                            String query) {
                        final String[] terms =
                                query.toLowerCase().split("\\s+");

                        final Filter<PageThumbnail> filter;
                        if (query.isEmpty()) {
                            filter = null;
                        } else {
                            // item must contain all terms in query
                            filter = new Filter<PageThumbnail>() {
                                @Override
                                public boolean accept(PageThumbnail item) {
                                    String fname =
                                            item.getFile().getFileName().toString().toLowerCase();
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
        listPages.getList().setCellRenderer(new PageListCellRenderer());

        listPages.setMinimumSize(new Dimension(250, 100));
        listPages.getList().setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        listPages.setBorder(BorderFactory.createTitledBorder("Page"));

        // filtered string list
        listTrainingFiles =
                new FilteredList<String>(new FilterProvider<String>() {
                    public Optional<Filter<String>> getFilterFor(String query) {
                        final String[] terms =
                                query.toLowerCase().split("\\s+");

                        final Filter<String> filter;
                        if (query.isEmpty()) {
                            filter = null;
                        } else {
                            // item must contain all terms in query
                            filter = new Filter<String>() {
                                @Override
                                public boolean accept(String item) {
                                    for (String term : terms) {
                                        if (!item.toLowerCase().contains(term)) {
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
        gbl_panel.columnWidths = new int[] { 0, 50, 417,
                0, 0 };
        gbl_panel.rowHeights = new int[] { 14, 0 };
        gbl_panel.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0,
                Double.MIN_VALUE };
        gbl_panel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
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
        tabsMain.addTab(Labels.getLabel(getLocale(), "tab_main_boxeditor"),
                Resources.getIcon("table_edit"), boxEditor);

        tabsMain.addTab(
                Labels.getLabel(getLocale(), "tab_main_symboloverview"),
                Resources.getIcon("application_view_icons"),
                glyphOverview);

        tabsMain.addTab(Labels.getLabel(getLocale(), "tab_main_recognition"),
                Resources.getIcon("application_tile_horizontal"),
                recognitionPane);

        spMain.setRightComponent(tabsMain);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(1.0);
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        spMain.setLeftComponent(splitPane);
        splitPane.setLeftComponent(listPages);
        splitPane.setRightComponent(listTrainingFiles);
    }

    public MainComponent getActiveComponent() {
        final Component main = tabsMain.getSelectedComponent();
        if (main instanceof MainComponent) {
            return (MainComponent) main;
        } else {
            throw new IllegalStateException(
                    "The current main component is not an instance of MainComponent.");
        }
    }

    public FilteredList<PageThumbnail> getPages() {
        return listPages;
    }

    public FilteredList<String> getTrainingFiles() {
        return listTrainingFiles;
    }

    public JLabel getScaleLabel() {
        return lblScaleFactor;
    }

    public Scale getScale() {
        return scale;
    }

    public BoxEditor getBoxEditor() {
        return boxEditor;
    }

    public RecognitionPane getRecognitionPane() {
        return recognitionPane;
    }

    public SymbolOverview getSymbolOverview() {
        return glyphOverview;
    }

    public JProgressBar getProgressBar() {
        return pbLoadPage;
    }

    public void setPageModel(Optional<PageModel> model) {
        getActiveComponent().setPageModel(model);
    }

    public Optional<PageModel> getPageModel() {
        return getActiveComponent().getPageModel();
    }

    public JMenuItem getMenuItemNewProject() {
        return mnNewProject;
    }

    public JTabbedPane getMainTabs() {
        return tabsMain;
    }
}
