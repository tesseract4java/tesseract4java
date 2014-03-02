package de.uniwue.ub.tesseract.evaluation.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class LoadProjectDialog extends JDialog {
  private static final long serialVersionUID = 1L;

  private final JPanel contentPanel = new JPanel();
  private final JTextField tfScanDir;
  private final JTextField tfHocrDir;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    try {
      LoadProjectDialog dialog = new LoadProjectDialog(null);
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
  public LoadProjectDialog(final ResultComparatorView owner) {
    super(owner);

    setTitle("Open Project");
    setBounds(100, 100, 450, 130);
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
      JLabel lblScans = new JLabel("Scan directory:");
      GridBagConstraints gbc_lblScans = new GridBagConstraints();
      gbc_lblScans.insets = new Insets(0, 0, 5, 5);
      gbc_lblScans.anchor = GridBagConstraints.EAST;
      gbc_lblScans.gridx = 0;
      gbc_lblScans.gridy = 0;
      contentPanel.add(lblScans, gbc_lblScans);
    }
    {
      tfScanDir = new JTextField();
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

      JLabel lblHocrDirectory = new JLabel("HOCR directory:");
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
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            owner.controller.loadProject(Paths.get(tfScanDir.getText()),
                Paths.get(tfHocrDir.getText()));
            LoadProjectDialog.this.dispose();
          }
        });
        okButton.setActionCommand("OK");
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);
      }
      {
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            LoadProjectDialog.this.dispose();
          }
        });
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);
      }
    }
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

        int state = dirChooser.showOpenDialog(LoadProjectDialog.this);
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

  public JTextField getTfHocrDir() {
    return tfHocrDir;
  }

  public JTextField getTfScanDir() {
    return tfScanDir;
  }
}
