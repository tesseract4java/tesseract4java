package de.vorb.tesseract.gui.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.JCheckBox;

public class BatchProcessor extends JPanel implements MainComponent {
    private static final long serialVersionUID = 1L;

    private static final int MAX_THREADS = Runtime.getRuntime().availableProcessors();
    private static final int MIN_THREADS = 1;
    private static final int DEFAULT_THREADS = MAX_THREADS - 1;

    private final JTextArea txtLog;
    private JButton btnExport;
    private JCheckBox chckbxText;
    private JCheckBox chckbxHtml;
    private JSpinner spinnerWorkerThreads;

    /**
     * Create the panel.
     */
    public BatchProcessor() {
        setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel.getLayout();
        flowLayout.setAlignment(FlowLayout.TRAILING);
        add(panel, BorderLayout.SOUTH);

        chckbxText = new JCheckBox("Text");
        chckbxText.setSelected(true);
        panel.add(chckbxText);

        chckbxHtml = new JCheckBox("HTML");
        panel.add(chckbxHtml);

        btnExport = new JButton("Export");
        panel.add(btnExport);

        JPanel panel_1 = new JPanel();
        panel_1.setBorder(new EmptyBorder(4, 4, 4, 4));
        add(panel_1, BorderLayout.NORTH);
        GridBagLayout gbl_panel_1 = new GridBagLayout();
        gbl_panel_1.columnWidths = new int[] { 100, 45, 0 };
        gbl_panel_1.rowHeights = new int[] { 14, 0 };
        gbl_panel_1.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
        gbl_panel_1.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        panel_1.setLayout(gbl_panel_1);

        JLabel lblWorkerThreads = new JLabel("Number of Worker Threads");
        GridBagConstraints gbc_lblWorkerThreads = new GridBagConstraints();
        gbc_lblWorkerThreads.insets = new Insets(0, 0, 0, 5);
        gbc_lblWorkerThreads.anchor = GridBagConstraints.WEST;
        gbc_lblWorkerThreads.gridx = 0;
        gbc_lblWorkerThreads.gridy = 0;
        panel_1.add(lblWorkerThreads, gbc_lblWorkerThreads);

        spinnerWorkerThreads = new JSpinner();
        spinnerWorkerThreads.setToolTipText("Using at most one thread less than the maximum is recommended in order to keep your system responsive");
        spinnerWorkerThreads.setModel(new SpinnerNumberModel(DEFAULT_THREADS,
                MIN_THREADS, MAX_THREADS, 1));
        GridBagConstraints gbc_spinnerWorkerThreads = new GridBagConstraints();
        gbc_spinnerWorkerThreads.fill = GridBagConstraints.HORIZONTAL;
        gbc_spinnerWorkerThreads.gridx = 1;
        gbc_spinnerWorkerThreads.gridy = 0;
        panel_1.add(spinnerWorkerThreads, gbc_spinnerWorkerThreads);

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, BorderLayout.CENTER);

        txtLog = new JTextArea();
        txtLog.setEditable(false);
        scrollPane.setViewportView(txtLog);
    }

    @Override
    public Component asComponent() {
        return this;
    }

    public JButton getExportButton() {
        return btnExport;
    }

    public JCheckBox getTextCheckBox() {
        return chckbxText;
    }

    public JCheckBox getHTMLCheckBox() {
        return chckbxHtml;
    }

    public JTextArea getLog() {
        return txtLog;
    }

    public JSpinner getWorkerThreadsSpinner() {
        return spinnerWorkerThreads;
    }
}
