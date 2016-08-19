package de.vorb.tesseract.gui.view.dialogs;

import de.vorb.tesseract.gui.model.GlobalPrefs;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;

public class PreferencesDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    public static final String KEY_EXEC_DIR = "exec_dir";
    public static final String KEY_LANGDATA_DIR = "langdata_dir";

    private final JPanel contentPanel = new JPanel();
    private JTextField tfExecutablesDir;
    private JTextField tfLangdataDir;

    private ResultState resultState = ResultState.CANCEL;

    public static enum ResultState {
        APPROVE, CANCEL;
    }

    /**
     * Create the dialog.
     */
    public PreferencesDialog() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(PreferencesDialog.class.getResource("/logos/logo_16.png")));
        final Preferences pref = GlobalPrefs.getPrefs();

        setModalityType(ModalityType.APPLICATION_MODAL);
        setTitle("General Preferences");
        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[]{0, 0, 0, 0};
        gbl_contentPanel.rowHeights = new int[]{0, 0, 0};
        gbl_contentPanel.columnWeights = new double[]{0.0, 1.0, 0.0,
                Double.MIN_VALUE};
        gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        contentPanel.setLayout(gbl_contentPanel);
        {
            JLabel lblTesseractExecutables = new JLabel(
                    "Tesseract executables directory:");
            GridBagConstraints gbc_lblTesseractExecutables = new GridBagConstraints();
            gbc_lblTesseractExecutables.insets = new Insets(0, 0, 5, 5);
            gbc_lblTesseractExecutables.anchor = GridBagConstraints.EAST;
            gbc_lblTesseractExecutables.gridx = 0;
            gbc_lblTesseractExecutables.gridy = 0;
            contentPanel.add(lblTesseractExecutables,
                    gbc_lblTesseractExecutables);
        }
        {
            tfExecutablesDir = new JTextField(pref.get(KEY_EXEC_DIR, ""));
            GridBagConstraints gbc_textField = new GridBagConstraints();
            gbc_textField.insets = new Insets(0, 0, 5, 5);
            gbc_textField.fill = GridBagConstraints.HORIZONTAL;
            gbc_textField.gridx = 1;
            gbc_textField.gridy = 0;
            contentPanel.add(tfExecutablesDir, gbc_textField);
            tfExecutablesDir.setColumns(30);
        }
        {
            JButton btnSelect = new JButton("Select...");
            btnSelect.addActionListener(evt -> {
                final JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                try {
                    final File currentDir = new File(
                            tfExecutablesDir.getText());
                    if (currentDir.isDirectory()) {
                        fc.setCurrentDirectory(currentDir);
                    }
                } catch (Exception e) {
                }

                final int result = fc.showOpenDialog(PreferencesDialog.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    tfExecutablesDir.setText(fc.getSelectedFile().getAbsolutePath());
                }
            });
            GridBagConstraints gbc_btnSelect = new GridBagConstraints();
            gbc_btnSelect.anchor = GridBagConstraints.WEST;
            gbc_btnSelect.insets = new Insets(0, 0, 5, 0);
            gbc_btnSelect.gridx = 2;
            gbc_btnSelect.gridy = 0;
            contentPanel.add(btnSelect, gbc_btnSelect);
        }
        {
            JLabel lbllangdataDirectory = new JLabel("\"Langdata\" directory:");
            GridBagConstraints gbc_lbllangdataDirectory = new GridBagConstraints();
            gbc_lbllangdataDirectory.anchor = GridBagConstraints.EAST;
            gbc_lbllangdataDirectory.insets = new Insets(0, 0, 0, 5);
            gbc_lbllangdataDirectory.gridx = 0;
            gbc_lbllangdataDirectory.gridy = 1;
            contentPanel.add(lbllangdataDirectory, gbc_lbllangdataDirectory);
        }
        {
            tfLangdataDir = new JTextField(pref.get(KEY_LANGDATA_DIR, ""));
            GridBagConstraints gbc_textField_1 = new GridBagConstraints();
            gbc_textField_1.insets = new Insets(0, 0, 0, 5);
            gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
            gbc_textField_1.gridx = 1;
            gbc_textField_1.gridy = 1;
            contentPanel.add(tfLangdataDir, gbc_textField_1);
            tfLangdataDir.setColumns(30);
        }
        {
            JButton btnSelect_1 = new JButton("Select...");
            btnSelect_1.addActionListener(evt -> {
                final JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                try {
                    final File currentDir = new File(
                            tfLangdataDir.getText());
                    if (currentDir.isDirectory()) {
                        fc.setCurrentDirectory(currentDir);
                    }
                } catch (Exception e) {
                }

                final int result = fc.showOpenDialog(PreferencesDialog.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    tfLangdataDir.setText(fc.getSelectedFile().getAbsolutePath());
                }
            });
            GridBagConstraints gbc_btnSelect_1 = new GridBagConstraints();
            gbc_btnSelect_1.anchor = GridBagConstraints.WEST;
            gbc_btnSelect_1.gridx = 2;
            gbc_btnSelect_1.gridy = 1;
            contentPanel.add(btnSelect_1, gbc_btnSelect_1);
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("Save");
                okButton.addActionListener(evt -> {
                    PreferencesDialog.this.setState(ResultState.APPROVE);
                    PreferencesDialog.this.dispose();
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(evt -> {
                    PreferencesDialog.this.setState(ResultState.CANCEL);
                    PreferencesDialog.this.dispose();
                });
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }

        pack();
        setMinimumSize(getSize());
    }

    private void setState(ResultState state) {
        this.resultState = state;
    }

    public JTextField getTfExecutablesDir() {
        return tfExecutablesDir;
    }

    public JTextField getTfLangdataDir() {
        return tfLangdataDir;
    }

    public ResultState showPreferencesDialog(Component parent) {
        setLocationRelativeTo(parent);
        setVisible(true);
        return resultState;
    }
}
