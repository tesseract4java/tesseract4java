package de.vorb.tesseract.gui.view.dialogs;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;

public class BatchExportProgressDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final JPanel contentPanel = new JPanel();

    private final JLabel lblFilename;
    private final JProgressBar progressBar;
    private final JButton btnCancel;

    /**
     * Create the dialog.
     */
    public BatchExportProgressDialog() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(
                BatchExportProgressDialog.class.getResource("/icons/book_next.png")));
        setModalityType(ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        setTitle("Batch Export Progress");
        setBounds(100, 100, 450, 130);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[]{0, 0, 0};
        gbl_contentPanel.rowHeights = new int[]{0, 20, 0, 0};
        gbl_contentPanel.columnWeights = new double[]{0.0, 1.0,
                Double.MIN_VALUE};
        gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0,
                Double.MIN_VALUE};
        contentPanel.setLayout(gbl_contentPanel);
        {
            JLabel lblProcessing = new JLabel("Processing");
            GridBagConstraints gbc_lblCurrentFile = new GridBagConstraints();
            gbc_lblCurrentFile.anchor = GridBagConstraints.WEST;
            gbc_lblCurrentFile.insets = new Insets(0, 0, 5, 5);
            gbc_lblCurrentFile.gridx = 0;
            gbc_lblCurrentFile.gridy = 0;
            contentPanel.add(lblProcessing, gbc_lblCurrentFile);
        }
        {
            lblFilename = new JLabel("Filename");
            GridBagConstraints gbc_lblFilename = new GridBagConstraints();
            gbc_lblFilename.anchor = GridBagConstraints.WEST;
            gbc_lblFilename.insets = new Insets(0, 0, 5, 0);
            gbc_lblFilename.gridx = 1;
            gbc_lblFilename.gridy = 0;
            contentPanel.add(lblFilename, gbc_lblFilename);
        }
        {
            progressBar = new JProgressBar();
            progressBar.setStringPainted(true);
            GridBagConstraints gbc_progressBar = new GridBagConstraints();
            gbc_progressBar.insets = new Insets(0, 0, 5, 0);
            gbc_progressBar.gridwidth = 2;
            gbc_progressBar.fill = GridBagConstraints.BOTH;
            gbc_progressBar.gridx = 0;
            gbc_progressBar.gridy = 1;
            contentPanel.add(progressBar, gbc_progressBar);
        }
        {
            btnCancel = new JButton("Cancel");
            GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
            gbc_btnNewButton.anchor = GridBagConstraints.EAST;
            gbc_btnNewButton.gridx = 1;
            gbc_btnNewButton.gridy = 2;
            contentPanel.add(btnCancel, gbc_btnNewButton);

            // dispose of dialog on cancel
            btnCancel.addActionListener(e -> BatchExportProgressDialog.this.dispose());
        }
    }

    public JLabel getFileNameLabel() {
        return lblFilename;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }
}
