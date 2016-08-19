package de.vorb.tesseract.gui.view.dialogs;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImportTranscriptionDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final JPanel contentPanel = new JPanel();
    private JTextField tfFile;
    private JTextField tfPageSeparator;

    private boolean approved = false;

    /**
     * Create the dialog.
     */
    public ImportTranscriptionDialog() {
        setTitle("Import Transcription");
        setBounds(100, 100, 450, 176);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[]{0, 0, 0, 0};
        gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0};
        gbl_contentPanel.columnWeights = new double[]{0.0, 1.0, 0.0,
                Double.MIN_VALUE};
        gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0,
                Double.MIN_VALUE};
        contentPanel.setLayout(gbl_contentPanel);
        {
            JLabel lblTranscriptionFile = new JLabel("Transcription File");
            GridBagConstraints gbc_lblTranscriptionFile = new GridBagConstraints();
            gbc_lblTranscriptionFile.insets = new Insets(0, 0, 5, 5);
            gbc_lblTranscriptionFile.anchor = GridBagConstraints.EAST;
            gbc_lblTranscriptionFile.gridx = 0;
            gbc_lblTranscriptionFile.gridy = 0;
            contentPanel.add(lblTranscriptionFile, gbc_lblTranscriptionFile);
        }
        {
            tfFile = new JTextField();
            GridBagConstraints gbc_tfFile = new GridBagConstraints();
            gbc_tfFile.insets = new Insets(0, 0, 5, 5);
            gbc_tfFile.fill = GridBagConstraints.HORIZONTAL;
            gbc_tfFile.gridx = 1;
            gbc_tfFile.gridy = 0;
            contentPanel.add(tfFile, gbc_tfFile);
            tfFile.setColumns(10);
        }
        {
            JButton btnSelect = new JButton("Select...");
            btnSelect.addActionListener(evt -> {
                final JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fc.setFileFilter(new FileFilter() {
                    @Override
                    public String getDescription() {
                        return "Plain text (*.txt)";
                    }

                    @Override
                    public boolean accept(File f) {
                        return f.canRead() && (f.isDirectory() ||
                                f.getName().endsWith(".txt"));
                    }
                });

                final int result = fc.showOpenDialog(
                        ImportTranscriptionDialog.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    tfFile.setText(fc.getSelectedFile().toString());
                }
            });
            GridBagConstraints gbc_btnSelect = new GridBagConstraints();
            gbc_btnSelect.insets = new Insets(0, 0, 5, 0);
            gbc_btnSelect.gridx = 2;
            gbc_btnSelect.gridy = 0;
            contentPanel.add(btnSelect, gbc_btnSelect);
        }
        {
            JLabel lblPageSeparator = new JLabel("Page Separator");
            GridBagConstraints gbc_lblPageSeparator = new GridBagConstraints();
            gbc_lblPageSeparator.insets = new Insets(0, 0, 5, 5);
            gbc_lblPageSeparator.anchor = GridBagConstraints.EAST;
            gbc_lblPageSeparator.gridx = 0;
            gbc_lblPageSeparator.gridy = 1;
            contentPanel.add(lblPageSeparator, gbc_lblPageSeparator);
        }
        {
            tfPageSeparator = new JTextField("~~~~~");
            GridBagConstraints gbc_textField_1 = new GridBagConstraints();
            gbc_textField_1.insets = new Insets(0, 0, 5, 5);
            gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
            gbc_textField_1.gridx = 1;
            gbc_textField_1.gridy = 1;
            contentPanel.add(tfPageSeparator, gbc_textField_1);
            tfPageSeparator.setColumns(10);
        }
        {
            JLabel lblWarning = new JLabel("Warning:");
            lblWarning.setFont(new Font("Tahoma", Font.BOLD, 11));
            GridBagConstraints gbc_lblWarning = new GridBagConstraints();
            gbc_lblWarning.anchor = GridBagConstraints.EAST;
            gbc_lblWarning.insets = new Insets(0, 0, 0, 5);
            gbc_lblWarning.gridx = 0;
            gbc_lblWarning.gridy = 2;
            contentPanel.add(lblWarning, gbc_lblWarning);
        }
        {
            JLabel lblExistingTranscriptionFiles = new JLabel(
                    "Existing transcription files will be lost!");
            GridBagConstraints gbc_lblExistingTranscriptionFiles = new GridBagConstraints();
            gbc_lblExistingTranscriptionFiles.anchor = GridBagConstraints.WEST;
            gbc_lblExistingTranscriptionFiles.insets = new Insets(0, 0, 0, 5);
            gbc_lblExistingTranscriptionFiles.gridx = 1;
            gbc_lblExistingTranscriptionFiles.gridy = 2;
            contentPanel.add(lblExistingTranscriptionFiles,
                    gbc_lblExistingTranscriptionFiles);
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("Import");
                okButton.addActionListener(evt -> {
                    if (getTranscriptionFile() != null) {
                        if (getPageSeparator().isEmpty()) {
                            Dialogs.showWarning(
                                    ImportTranscriptionDialog.this,
                                    "Empty Separator",
                                    "The separator must not be empty.");
                            return;
                        }

                        approved = true;

                        ImportTranscriptionDialog.this.dispose();
                    } else {
                        Dialogs.showWarning(ImportTranscriptionDialog.this,
                                "Invalid File",
                                "The given transcription file is invalid.");
                    }
                });

                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(evt -> {
                    approved = false;

                    ImportTranscriptionDialog.this.dispose();
                });
                buttonPane.add(cancelButton);
            }
        }

    }

    public boolean isApproved() {
        return approved;
    }

    public Path getTranscriptionFile() {
        try {
            final Path file = Paths.get(tfFile.getText());
            if (Files.isRegularFile(file)) {
                return file;
            }
        } catch (InvalidPathException e) {
        }

        return null;
    }

    public String getPageSeparator() {
        return tfPageSeparator.getText();
    }
}
