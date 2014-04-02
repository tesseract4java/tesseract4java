package de.uniwue.ub.tesseract.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import de.uniwue.ub.tesseract.event.LocaleChangeListener;
import de.uniwue.ub.tesseract.event.ProjectChangeListener;
import de.uniwue.ub.tesseract.view.i18n.Labels;

public class OpenProjectDialog extends JDialog implements LocaleChangeListener {
  private static final long serialVersionUID = 1L;

  private final JPanel contentPanel = new JPanel();
  private final JTextField tfScanDir;
  private final JTextField tfHocrDir;

  private final List<ProjectChangeListener> listeners = new LinkedList<ProjectChangeListener>();

  private final JButton btCancel;
  private final JButton btOK;
  private final JLabel lblHocrDirectory;
  private final JLabel lblScanDirectory;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    try {
      OpenProjectDialog dialog = new OpenProjectDialog(null);
      dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
      dialog.setVisible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Create the dialog.
   * 
   * @param owner
   */
  public OpenProjectDialog(final Window owner) {
    super(owner);

    setMinimumSize(new Dimension(500, 130));

    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    GridBagLayout gbl_contentPanel = new GridBagLayout();
    gbl_contentPanel.columnWidths = new int[] { 0, 0, 0, 0 };
    gbl_contentPanel.rowHeights = new int[] { 0, 0, 0 };
    gbl_contentPanel.columnWeights = new double[] { 0.0, 1.0, 0.0,
        Double.MIN_VALUE };
    gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
    contentPanel.setLayout(gbl_contentPanel);
    {
      lblScanDirectory = new JLabel("Scan directory:");
      GridBagConstraints gbc_lblScans = new GridBagConstraints();
      gbc_lblScans.insets = new Insets(0, 0, 5, 5);
      gbc_lblScans.anchor = GridBagConstraints.EAST;
      gbc_lblScans.gridx = 0;
      gbc_lblScans.gridy = 0;
      contentPanel.add(lblScanDirectory, gbc_lblScans);
    }
    {
      tfScanDir = new JTextField();

      // TODO remove
      tfScanDir.setText("E:\\Masterarbeit\\Ressourcen\\DE-20__32_AM_49000_L869_G927-1\\sauvola");

      GridBagConstraints gbc_tfScanDir = new GridBagConstraints();
      gbc_tfScanDir.insets = new Insets(0, 0, 5, 5);
      gbc_tfScanDir.fill = GridBagConstraints.HORIZONTAL;
      gbc_tfScanDir.gridx = 1;
      gbc_tfScanDir.gridy = 0;
      contentPanel.add(tfScanDir, gbc_tfScanDir);
      tfScanDir.setColumns(10);
    }
    {
      JButton button = new JButton("...");
      GridBagConstraints gbc_button = new GridBagConstraints();
      gbc_button.insets = new Insets(0, 0, 5, 0);
      gbc_button.gridx = 2;
      gbc_button.gridy = 0;
      contentPanel.add(button, gbc_button);

      lblHocrDirectory = new JLabel("HOCR directory:");
      GridBagConstraints gbc_lblHocrDirectory = new GridBagConstraints();
      gbc_lblHocrDirectory.insets = new Insets(0, 0, 0, 5);
      gbc_lblHocrDirectory.anchor = GridBagConstraints.EAST;
      gbc_lblHocrDirectory.gridx = 0;
      gbc_lblHocrDirectory.gridy = 1;
      contentPanel.add(lblHocrDirectory, gbc_lblHocrDirectory);

      makePathChooser(tfScanDir, button);
    }
    {
      tfHocrDir = new JTextField();

      // TODO remove
      tfHocrDir.setText("E:\\Masterarbeit\\Ressourcen\\DE-20__32_AM_49000_L869_G927-1\\deu-frak-2010\\hocr");

      GridBagConstraints gbc_tfHocrDir = new GridBagConstraints();
      gbc_tfHocrDir.insets = new Insets(0, 0, 0, 5);
      gbc_tfHocrDir.fill = GridBagConstraints.HORIZONTAL;
      gbc_tfHocrDir.gridx = 1;
      gbc_tfHocrDir.gridy = 1;
      contentPanel.add(tfHocrDir, gbc_tfHocrDir);
      tfHocrDir.setColumns(10);

      JButton button = new JButton("...");
      GridBagConstraints gbc_button = new GridBagConstraints();
      gbc_button.gridx = 2;
      gbc_button.gridy = 1;
      contentPanel.add(button, gbc_button);

      makePathChooser(tfHocrDir, button);
    }
    {
      JPanel buttonPane = new JPanel();
      buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
      getContentPane().add(buttonPane, BorderLayout.SOUTH);
      {
        btOK = new JButton();
        btOK.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            projectChanged();
            OpenProjectDialog.this.dispose();
          }
        });
        btOK.setActionCommand("OK");
        buttonPane.add(btOK);
        getRootPane().setDefaultButton(btOK);
      }
      {
        btCancel = new JButton();
        btCancel.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            OpenProjectDialog.this.dispose();
          }
        });
        btCancel.setActionCommand("Cancel");
        buttonPane.add(btCancel);
      }
    }

    localeChanged();

    this.setResizable(false);
  }

  @Override
  public void localeChanged() {
    setLocale(Locale.getDefault());

    setTitle(Labels.getLabel(getLocale(), "open_dialog_title"));

    lblScanDirectory.setText(Labels.getLabel(getLocale(), "scan_dir"));
    lblHocrDirectory.setText(Labels.getLabel(getLocale(), "hocr_dir"));
    btCancel.setText(Labels.getLabel(getLocale(), "btn_cancel"));
    btOK.setText(Labels.getLabel(getLocale(), "btn_ok"));
  }

  private void makePathChooser(final JTextField tfPath,
      final JButton btnChoosePath) {

    File dir = new File("E:\\Masterarbeit\\Ressourcen");
    if (!dir.isDirectory())
      dir = null;
    final File startDir = dir;

    btnChoosePath.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        final JFileChooser dirChooser = new JFileChooser(startDir);
        dirChooser.setMultiSelectionEnabled(false);
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int state = dirChooser.showOpenDialog(OpenProjectDialog.this);
        if (state == JFileChooser.APPROVE_OPTION) {
          final File selection = dirChooser.getSelectedFile();
          tfPath.setText(selection.getAbsolutePath());
        } else if (state == JFileChooser.ERROR_OPTION) {
          JOptionPane.showMessageDialog(dirChooser,
              "Please select a directory", "Invalid selection",
              JOptionPane.ERROR_MESSAGE);
        }
      }
    });
  }

  public void addProjectChangeListener(ProjectChangeListener listener) {
    listeners.add(listener);
  }

  public void removeProjectChangeListener(ProjectChangeListener listener) {
    listeners.remove(listener);
  }

  private void projectChanged() {
    final Path scanDir = Paths.get(tfScanDir.getText());
    final Path hocrDir = Paths.get(tfHocrDir.getText());

    for (ProjectChangeListener l : listeners) {
      l.projectChanged(scanDir, hocrDir);
    }
  }

  @Override
  public void setVisible(boolean visible) {
    setLocationRelativeTo(getParent());
    super.setVisible(visible);
  }
}
