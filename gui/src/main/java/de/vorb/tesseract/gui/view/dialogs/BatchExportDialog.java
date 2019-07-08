package de.vorb.tesseract.gui.view.dialogs;

import de.vorb.tesseract.gui.controller.TesseractController;
import de.vorb.tesseract.gui.model.BatchExportModel;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class BatchExportDialog extends JDialog implements ActionListener {
    private static final long serialVersionUID = 1L;

    private static final int MAX_THREADS = Runtime.getRuntime().availableProcessors();
    private static final int MIN_THREADS = 1;
    private static final int DEFAULT_THREADS = MAX_THREADS - 1;

    private final JButton btnExport;
    private final JCheckBox chckbxTxt;
    private final JCheckBox chckbxHtml;
    private final JSpinner spinnerWorkerThreads;
    private final JPanel panel_2;
    private final JPanel panel_3;
    private final JTextField tfDestinationDir;
    private final JButton btnDestinationDir;
    private final JLabel lblDestinationDir;
    private final JLabel lblFileFormats;
    private final JButton btnCancel;
    private final JCheckBox chckbxOpenDestination;

    private final TesseractController controller;

    private BatchExportModel exportModel = null;
    private JCheckBox chckbxExportImages;
    private JLabel lblExport;
    private JCheckBox chckbxXml;
    private JCheckBox chckbxEvaluationReports;

    /**
     * Create the panel.
     */
    public BatchExportDialog(TesseractController controller) {
        setIconImage(Toolkit.getDefaultToolkit().getImage(
                BatchExportDialog.class.getResource("/icons/book_next.png")));
        setLocationRelativeTo(controller.getView());

        this.controller = controller;

        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        setTitle("Batch Export");
        getContentPane().setLayout(new BorderLayout(0, 0));

        setModalityType(ModalityType.APPLICATION_MODAL);

        JPanel panel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel.getLayout();
        flowLayout.setAlignment(FlowLayout.TRAILING);
        getContentPane().add(panel, BorderLayout.SOUTH);

        btnExport = new JButton("Export");
        btnExport.addActionListener(this);

        chckbxOpenDestination = new JCheckBox("Open Destination");
        panel.add(chckbxOpenDestination);
        panel.add(btnExport);

        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(this);
        panel.add(btnCancel);

        JPanel panel_1 = new JPanel();
        panel_1.setBorder(new EmptyBorder(4, 4, 4, 4));
        getContentPane().add(panel_1, BorderLayout.CENTER);
        panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));

        panel_3 = new JPanel();
        panel_3.setBorder(new CompoundBorder(new TitledBorder(
                UIManager.getBorder("TitledBorder.border"), "Export Options",
                TitledBorder.LEADING, TitledBorder.TOP, null,
                new Color(0, 0, 0)), new EmptyBorder(4, 4, 4, 4)));
        panel_1.add(panel_3);
        GridBagLayout gbl_panel_3 = new GridBagLayout();
        gbl_panel_3.columnWidths = new int[]{0, 0, 0, 0};
        gbl_panel_3.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
        gbl_panel_3.columnWeights = new double[]{0.0, 1.0, 0.0,
                Double.MIN_VALUE};
        gbl_panel_3.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0,
                0.0,
                Double.MIN_VALUE};
        panel_3.setLayout(gbl_panel_3);

        lblDestinationDir = new JLabel("Destination Directory");
        GridBagConstraints gbc_lblDestinationDir = new GridBagConstraints();
        gbc_lblDestinationDir.insets = new Insets(0, 0, 5, 5);
        gbc_lblDestinationDir.anchor = GridBagConstraints.EAST;
        gbc_lblDestinationDir.gridx = 0;
        gbc_lblDestinationDir.gridy = 0;
        panel_3.add(lblDestinationDir, gbc_lblDestinationDir);

        tfDestinationDir = new JTextField();
        GridBagConstraints gbc_tfDestinationDir = new GridBagConstraints();
        gbc_tfDestinationDir.insets = new Insets(0, 0, 5, 5);
        gbc_tfDestinationDir.fill = GridBagConstraints.HORIZONTAL;
        gbc_tfDestinationDir.gridx = 1;
        gbc_tfDestinationDir.gridy = 0;
        panel_3.add(tfDestinationDir, gbc_tfDestinationDir);
        tfDestinationDir.setColumns(10);

        btnDestinationDir = new JButton("...");
        GridBagConstraints gbc_btnDestinationDir = new GridBagConstraints();
        gbc_btnDestinationDir.insets = new Insets(0, 0, 5, 0);
        gbc_btnDestinationDir.gridx = 2;
        gbc_btnDestinationDir.gridy = 0;
        panel_3.add(btnDestinationDir, gbc_btnDestinationDir);
        btnDestinationDir.addActionListener(this);

        lblFileFormats = new JLabel("File Formats");
        GridBagConstraints gbc_lblFileFormats = new GridBagConstraints();
        gbc_lblFileFormats.anchor = GridBagConstraints.EAST;
        gbc_lblFileFormats.insets = new Insets(0, 0, 5, 5);
        gbc_lblFileFormats.gridx = 0;
        gbc_lblFileFormats.gridy = 1;
        panel_3.add(lblFileFormats, gbc_lblFileFormats);

        chckbxTxt = new JCheckBox("TXT");
        GridBagConstraints gbc_chckbxText = new GridBagConstraints();
        gbc_chckbxText.anchor = GridBagConstraints.WEST;
        gbc_chckbxText.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxText.gridx = 1;
        gbc_chckbxText.gridy = 1;
        panel_3.add(chckbxTxt, gbc_chckbxText);
        chckbxTxt.setSelected(true);

        chckbxXml = new JCheckBox("XML");
        GridBagConstraints gbc_chckbxXml = new GridBagConstraints();
        gbc_chckbxXml.anchor = GridBagConstraints.WEST;
        gbc_chckbxXml.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxXml.gridx = 1;
        gbc_chckbxXml.gridy = 2;
        panel_3.add(chckbxXml, gbc_chckbxXml);

        chckbxHtml = new JCheckBox("HTML");
        chckbxHtml.setVisible(false);
        GridBagConstraints gbc_chckbxHtml = new GridBagConstraints();
        gbc_chckbxHtml.anchor = GridBagConstraints.WEST;
        gbc_chckbxHtml.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxHtml.gridx = 1;
        gbc_chckbxHtml.gridy = 3;
        panel_3.add(chckbxHtml, gbc_chckbxHtml);

        lblExport = new JLabel("Export");
        GridBagConstraints gbc_lblExport = new GridBagConstraints();
        gbc_lblExport.anchor = GridBagConstraints.EAST;
        gbc_lblExport.insets = new Insets(0, 0, 5, 5);
        gbc_lblExport.gridx = 0;
        gbc_lblExport.gridy = 4;
        panel_3.add(lblExport, gbc_lblExport);

        chckbxExportImages = new JCheckBox("Preprocessed Images");
        chckbxExportImages.setSelected(true);
        GridBagConstraints gbc_chckbxExportImages = new GridBagConstraints();
        gbc_chckbxExportImages.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxExportImages.gridx = 1;
        gbc_chckbxExportImages.gridy = 4;
        panel_3.add(chckbxExportImages, gbc_chckbxExportImages);

        chckbxEvaluationReports = new JCheckBox("Evaluation Reports");
        chckbxEvaluationReports.setSelected(true);
        GridBagConstraints gbc_chckbxEvaluationReports = new GridBagConstraints();
        gbc_chckbxEvaluationReports.anchor = GridBagConstraints.WEST;
        gbc_chckbxEvaluationReports.insets = new Insets(0, 0, 0, 5);
        gbc_chckbxEvaluationReports.gridx = 1;
        gbc_chckbxEvaluationReports.gridy = 5;
        panel_3.add(chckbxEvaluationReports, gbc_chckbxEvaluationReports);

        panel_2 = new JPanel();
        panel_2.setBorder(new CompoundBorder(new TitledBorder(
                UIManager.getBorder("TitledBorder.border"),
                "Advanced Settings", TitledBorder.LEADING, TitledBorder.TOP,
                null, new Color(0, 0, 0)), new EmptyBorder(4, 4, 4, 4)));
        panel_1.add(panel_2);
        GridBagLayout gbl_panel_2 = new GridBagLayout();
        gbl_panel_2.columnWidths = new int[]{67, 44, 0};
        gbl_panel_2.rowHeights = new int[]{20, 0};
        gbl_panel_2.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        gbl_panel_2.rowWeights = new double[]{0.0, Double.MIN_VALUE};
        panel_2.setLayout(gbl_panel_2);

        JLabel lblWorkerThreads = new JLabel("Number of Worker Threads");
        GridBagConstraints gbc_lblWorkerThreads = new GridBagConstraints();
        gbc_lblWorkerThreads.anchor = GridBagConstraints.WEST;
        gbc_lblWorkerThreads.insets = new Insets(0, 0, 0, 5);
        gbc_lblWorkerThreads.gridx = 0;
        gbc_lblWorkerThreads.gridy = 0;
        panel_2.add(lblWorkerThreads, gbc_lblWorkerThreads);

        spinnerWorkerThreads = new JSpinner();
        GridBagConstraints gbc_spinnerWorkerThreads = new GridBagConstraints();
        gbc_spinnerWorkerThreads.fill = GridBagConstraints.HORIZONTAL;
        gbc_spinnerWorkerThreads.anchor = GridBagConstraints.NORTH;
        gbc_spinnerWorkerThreads.gridx = 1;
        gbc_spinnerWorkerThreads.gridy = 0;
        panel_2.add(spinnerWorkerThreads, gbc_spinnerWorkerThreads);
        spinnerWorkerThreads.setModel(new SpinnerNumberModel(DEFAULT_THREADS,
                MIN_THREADS, MAX_THREADS, 1));

        pack();
        setMinimumSize(getSize());
        setSize(new Dimension(276, 280));

        setLocationRelativeTo(controller.getView());
    }

    public JButton getExportButton() {
        return btnExport;
    }

    public JCheckBox getTextCheckBox() {
        return chckbxTxt;
    }

    public JCheckBox getHTMLCheckBox() {
        return chckbxHtml;
    }

    public JSpinner getWorkerThreadsSpinner() {
        return spinnerWorkerThreads;
    }

    public JTextField getDestinationDirectoryTextField() {
        return tfDestinationDir;
    }

    public static Optional<BatchExportModel> showBatchExportDialog(
            TesseractController controller) {
        final BatchExportDialog dialog = new BatchExportDialog(controller);
        dialog.setVisible(true);

        return Optional.ofNullable(dialog.exportModel);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == btnCancel) {
            this.dispose();
        } else if (evt.getSource() == btnDestinationDir) {
            final JFileChooser dirChooser = new JFileChooser(
                    controller.getProjectModel().get().getProjectDir().toFile());
            dirChooser.setDialogTitle("Choose Destination Directory");
            dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            final int result = dirChooser.showOpenDialog(controller.getView());
            if (result == JFileChooser.APPROVE_OPTION) {
                final File destinationDir = dirChooser.getSelectedFile();
                tfDestinationDir.setText(destinationDir.toString());
            }
        } else if (evt.getSource() == btnExport) {
            try {
                if (tfDestinationDir.getText().equals("")) {
                    throw new InvalidPathException("", "empty");
                }

                final Path destinationDir =
                        Paths.get(tfDestinationDir.getText());

                if (!Files.exists(destinationDir)) {
                    throw new InvalidPathException(destinationDir.toString(),
                            "doesn't exist");
                }

                final boolean exportTXT = chckbxTxt.isSelected();
                final boolean exportHTML = chckbxHtml.isSelected();
                final boolean exportXML = chckbxXml.isSelected();
                final boolean exportImages = chckbxExportImages.isSelected();
                final boolean exportReports = chckbxEvaluationReports.isSelected();
                final boolean openDestination = chckbxOpenDestination.isSelected();
                final int numThreads = (Integer) spinnerWorkerThreads.getValue();

                exportModel = new BatchExportModel(
                        destinationDir, exportTXT, exportHTML, exportXML,
                        exportImages, exportReports, numThreads,
                        openDestination);

                this.setVisible(false);
            } catch (InvalidPathException e) {
                Dialogs.showError(this, "Invalid Destination Directory",
                        "The given destination directory doesn't exist.");
            }
        }
    }
}
