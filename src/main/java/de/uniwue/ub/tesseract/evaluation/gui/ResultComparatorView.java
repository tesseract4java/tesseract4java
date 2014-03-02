package de.uniwue.ub.tesseract.evaluation.gui;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.uniwue.ub.tesseract.evaluation.ResultComparatorController;
import de.uniwue.ub.tesseract.evaluation.ResultComparatorModel;
import de.uniwue.ub.tesseract.evaluation.WordModel;

// TODO extend JPanel instead of JFrame
public class ResultComparatorView extends JFrame implements Observer {
  private static final long serialVersionUID = 1L;

  final ResultComparatorController controller;
  private final JSpinner tfPage;
  private JList<String> listPages;
  private JLabel lbCanvasOCR;
  private JLabel lbCanvasOriginal;

  private ResultComparatorModel model = new ResultComparatorModel(
      new File("").toPath(), new File("").toPath(), new ArrayList<String>());

  private final ImageIcon arrowLeft = new ImageIcon(
      "src/main/resources/arrow-left.png", "left arrow");
  private final ImageIcon arrowRight = new ImageIcon(
      "src/main/resources/arrow-right.png", "left right");

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          ResultComparatorView window = new ResultComparatorView(null);
          window.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public ResultComparatorView(final ResultComparatorController controller) {
    super();
    this.controller = controller;

    setTitle("Tesseract Evaluation");
    setBounds(100, 100, 800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JMenuBar menuBar = new JMenuBar();
    setJMenuBar(menuBar);

    JMenu mnFile = new JMenu("File");
    menuBar.add(mnFile);

    JMenuItem mntmOpenFile = new JMenuItem("Open Project");
    mntmOpenFile.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        final LoadProjectDialog loadProj = new LoadProjectDialog(
            ResultComparatorView.this);
        loadProj.setModalityType(ModalityType.APPLICATION_MODAL);
        loadProj.setVisible(true);
      }
    });
    mntmOpenFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
        InputEvent.CTRL_MASK));
    mnFile.add(mntmOpenFile);

    JPanel panel = new JPanel();
    panel.setBackground(SystemColor.menu);
    panel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(panel, BorderLayout.SOUTH);
    GridBagLayout gbl_panel = new GridBagLayout();
    gbl_panel.columnWidths = new int[] { 118, 84, 46, 0, 46, 417, 50, 40, 0 };
    gbl_panel.rowHeights = new int[] { 14, 0 };
    gbl_panel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0,
        0.0, Double.MIN_VALUE };
    gbl_panel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
    panel.setLayout(gbl_panel);

    JLabel lblProjectOverview = new JLabel("Project overview");
    lblProjectOverview.setFont(new Font("Tahoma", Font.BOLD, 11));
    GridBagConstraints gbc_lblProjectOverview = new GridBagConstraints();
    gbc_lblProjectOverview.anchor = GridBagConstraints.WEST;
    gbc_lblProjectOverview.insets = new Insets(0, 0, 0, 5);
    gbc_lblProjectOverview.gridx = 0;
    gbc_lblProjectOverview.gridy = 0;
    panel.add(lblProjectOverview, gbc_lblProjectOverview);

    JLabel lblCorrectWords = new JLabel("Correct words:");
    GridBagConstraints gbc_lblCorrectWords = new GridBagConstraints();
    gbc_lblCorrectWords.fill = GridBagConstraints.VERTICAL;
    gbc_lblCorrectWords.insets = new Insets(0, 0, 0, 5);
    gbc_lblCorrectWords.anchor = GridBagConstraints.WEST;
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

    JLabel lblIncorrectWords = new JLabel("Incorrect words:");
    GridBagConstraints gbc_lblIncorrectWords = new GridBagConstraints();
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

    JLabel lblTotalWords = new JLabel("Total words:");
    GridBagConstraints gbc_lblTotalWords = new GridBagConstraints();
    gbc_lblTotalWords.insets = new Insets(0, 0, 0, 5);
    gbc_lblTotalWords.fill = GridBagConstraints.HORIZONTAL;
    gbc_lblTotalWords.gridx = 6;
    gbc_lblTotalWords.gridy = 0;
    panel.add(lblTotalWords, gbc_lblTotalWords);

    JLabel label_2 = new JLabel("0");
    GridBagConstraints gbc_label_2 = new GridBagConstraints();
    gbc_label_2.anchor = GridBagConstraints.WEST;
    gbc_label_2.gridx = 7;
    gbc_label_2.gridy = 0;
    panel.add(label_2, gbc_label_2);

    JSplitPane splitPane = new JSplitPane();
    splitPane.setResizeWeight(0.1);
    getContentPane().add(splitPane, BorderLayout.CENTER);

    JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    splitPane.setLeftComponent(tabbedPane);

    JPanel panel_1 = new JPanel();
    tabbedPane.addTab("Project", null, panel_1, null);
    panel_1.setLayout(new BorderLayout(0, 0));

    JPanel panel_2 = new JPanel();
    panel_2.setBorder(new EmptyBorder(2, 2, 2, 2));
    panel_1.add(panel_2, BorderLayout.SOUTH);
    panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

    JButton btnPrevious = new JButton(arrowLeft);
    btnPrevious.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        Object prev = tfPage.getPreviousValue();
        if (prev != null)
          tfPage.setValue(prev);
      }
    });

    JLabel lblPage_1 = new JLabel("Page:");
    panel_2.add(lblPage_1);
    panel_2.add(btnPrevious);

    tfPage = new JSpinner();
    tfPage.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent ev) {
        ResultComparatorView.this.getModel().setPageIndex(
            (int) tfPage.getValue());
      }
    });
    tfPage.setModel(new SpinnerNumberModel(1, 1, 9999, 1));
    panel_2.add(tfPage);

    JButton btnNewButton = new JButton(arrowRight);
    btnNewButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        Object next = tfPage.getNextValue();
        if (next != null)
          tfPage.setValue(next);
      }
    });
    panel_2.add(btnNewButton);

    JScrollPane scrollPane_1 = new JScrollPane();
    panel_1.add(scrollPane_1, BorderLayout.CENTER);

    listPages = new JList<String>();
    listPages.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    listPages.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent ev) {
        final int pageIndex = ev.getFirstIndex();
        getModel().setPageIndex(pageIndex);
      }
    });
    scrollPane_1.setViewportView(listPages);

    JPanel panel_3 = new JPanel();
    splitPane.setRightComponent(panel_3);
    panel_3.setLayout(new BorderLayout(0, 0));

    JPanel toolbar = new JPanel();
    panel_3.add(toolbar, BorderLayout.NORTH);

    JButton btnNewButton_1 = new JButton(arrowLeft);
    toolbar.add(btnNewButton_1);

    JCheckBox chckbxNewCheckBox = new JCheckBox("Correct");
    toolbar.add(chckbxNewCheckBox);

    JButton btnNewButton_2 = new JButton(arrowRight);
    toolbar.add(btnNewButton_2);

    JPanel panel_5 = new JPanel();
    panel_3.add(panel_5, BorderLayout.SOUTH);

    JSplitPane splitPane_1 = new JSplitPane();
    splitPane_1.setResizeWeight(0.5);
    panel_3.add(splitPane_1, BorderLayout.CENTER);

    JScrollPane scrollPane = new JScrollPane();
    splitPane_1.setRightComponent(scrollPane);

    lbCanvasOriginal = new JLabel("");
    lbCanvasOriginal.setBackground(SystemColor.window);
    scrollPane.setViewportView(lbCanvasOriginal);

    JScrollPane scrollPane_2 = new JScrollPane();
    splitPane_1.setLeftComponent(scrollPane_2);

    lbCanvasOCR = new JLabel("");
    lbCanvasOCR.setBackground(SystemColor.window);
    scrollPane_2.setViewportView(lbCanvasOCR);
  }

  public ResultComparatorModel getModel() {
    return model;
  }

  public JList<String> getPageList() {
    return listPages;
  }

  public JLabel getCanvasOCR() {
    return lbCanvasOCR;
  }

  public JLabel getCanvasOriginal() {
    return lbCanvasOriginal;
  }

  public void setModel(ResultComparatorModel model) {
    if (model == null)
      throw new IllegalArgumentException("model was null");

    this.model = model;
    this.tfPage.setModel(new SpinnerNumberModel(model.getPageIndex(),
        model.getMinPageIndex(), model.getMaxPageIndex(), 1));

    model.addObserver(this);
    model.notifyObservers();
  }

  @Override
  public void update(Observable o, Object arg) {
    if (o instanceof ResultComparatorModel) {
      ResultComparatorModel model = (ResultComparatorModel) o;

      if (model.hasPageListChanged()) {
        DefaultListModel<String> pages = new DefaultListModel<String>();
        List<String> pageNames = model.pages();

        for (int i = 0; i < pageNames.size(); i++) {
          pages.addElement(pageNames.get(i));
        }

        getPageList().setModel(pages);
      }

      if (model.hasPageIndexChanged()) {
        // FIXME
        getPageList().setSelectedIndex(model.getPageIndex());
        tfPage.getModel().setValue(model.getPageIndex());
        controller.pageIndexChanged(model.getPageIndex());
      }
    }
  }
}
