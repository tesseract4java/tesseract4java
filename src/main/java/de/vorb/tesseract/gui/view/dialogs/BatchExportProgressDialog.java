package de.vorb.tesseract.gui.view.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import java.awt.Dialog.ModalityType;
import javax.swing.JProgressBar;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import java.awt.Insets;
import javax.swing.SwingConstants;

public class BatchExportProgressDialog extends JDialog {

    private final JPanel contentPanel = new JPanel();

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            BatchExportProgressDialog dialog = new BatchExportProgressDialog();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     */
    public BatchExportProgressDialog() {
        setModalityType(ModalityType.APPLICATION_MODAL);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setTitle("Batch Export Progress");
        setBounds(100, 100, 450, 114);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[] { 0, 0, 0 };
        gbl_contentPanel.rowHeights = new int[] { 0, 20, 0 };
        gbl_contentPanel.columnWeights = new double[] { 0.0, 1.0,
                Double.MIN_VALUE };
        gbl_contentPanel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
        contentPanel.setLayout(gbl_contentPanel);
        {
            JLabel lblCurrentFile = new JLabel("Processing");
            GridBagConstraints gbc_lblCurrentFile = new GridBagConstraints();
            gbc_lblCurrentFile.anchor = GridBagConstraints.WEST;
            gbc_lblCurrentFile.insets = new Insets(0, 0, 5, 5);
            gbc_lblCurrentFile.gridx = 0;
            gbc_lblCurrentFile.gridy = 0;
            contentPanel.add(lblCurrentFile, gbc_lblCurrentFile);
        }
        {
            JLabel lblFilename = new JLabel("Filename");
            GridBagConstraints gbc_lblFilename = new GridBagConstraints();
            gbc_lblFilename.anchor = GridBagConstraints.WEST;
            gbc_lblFilename.insets = new Insets(0, 0, 5, 0);
            gbc_lblFilename.gridx = 1;
            gbc_lblFilename.gridy = 0;
            contentPanel.add(lblFilename, gbc_lblFilename);
        }
        {
            JProgressBar progressBar = new JProgressBar();
            progressBar.setStringPainted(true);
            GridBagConstraints gbc_progressBar = new GridBagConstraints();
            gbc_progressBar.gridwidth = 2;
            gbc_progressBar.fill = GridBagConstraints.BOTH;
            gbc_progressBar.gridx = 0;
            gbc_progressBar.gridy = 1;
            contentPanel.add(progressBar, gbc_progressBar);
        }
    }

}
