package de.vorb.tesseract.gui.view;

import javax.swing.JPanel;

import de.vorb.tesseract.gui.model.ImageModel;
import de.vorb.tesseract.gui.model.Scale;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;

import java.awt.Color;

import javax.swing.border.EmptyBorder;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Component;

import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;

import com.google.common.base.Optional;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

public class EvaluationPane extends JPanel implements ImageModelComponent {
    private static final long serialVersionUID = 1L;

    private final JTable table;
    private final JLabel lblOriginal;

    private Optional<ImageModel> imageModel;

    /**
     * Create the panel.
     * 
     * @param scale
     */
    public EvaluationPane(Scale scale) {
        setLayout(new BorderLayout(0, 0));

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBackground(Color.WHITE);
        add(tabbedPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        tabbedPane.addTab("Reference", null, panel, null);
        panel.setLayout(new BorderLayout(0, 0));

        JSplitPane splitPane = new JSplitPane();
        panel.add(splitPane);
        splitPane.setResizeWeight(0.5);

        JScrollPane scrollPane = new JScrollPane();
        splitPane.setLeftComponent(scrollPane);

        JLabel lblOriginalTitle = new JLabel("Original");
        lblOriginalTitle.setBorder(new EmptyBorder(0, 4, 0, 0));
        scrollPane.setColumnHeaderView(lblOriginalTitle);

        lblOriginal = new JLabel("");
        scrollPane.setViewportView(lblOriginal);

        JScrollPane scrollPane_1 = new JScrollPane();
        splitPane.setRightComponent(scrollPane_1);

        JTextArea textArea = new JTextArea();
        scrollPane_1.setViewportView(textArea);

        JLabel lblReferenceText = new JLabel("Reference Text");
        lblReferenceText.setBorder(new EmptyBorder(0, 4, 0, 0));
        scrollPane_1.setColumnHeaderView(lblReferenceText);

        JPanel panel_1 = new JPanel();
        panel.add(panel_1, BorderLayout.SOUTH);

        JButton btnGenerateReport = new JButton("Generate Report");
        panel_1.add(btnGenerateReport);

        JPanel panel_2 = new JPanel();
        tabbedPane.addTab("Report", null, panel_2, null);
        GridBagLayout gbl_panel_2 = new GridBagLayout();
        gbl_panel_2.columnWidths = new int[] { 445, 0 };
        gbl_panel_2.rowHeights = new int[] { 68, 68, 68, 0 };
        gbl_panel_2.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gbl_panel_2.rowWeights = new double[] { 0.0, 1.0, 1.0, Double.MIN_VALUE };
        panel_2.setLayout(gbl_panel_2);

        JPanel panel_3 = new JPanel();
        panel_3.setAlignmentY(Component.TOP_ALIGNMENT);
        panel_3.setBorder(new CompoundBorder(new TitledBorder(
                UIManager.getBorder("TitledBorder.border"), "Overview",
                TitledBorder.LEADING, TitledBorder.TOP, null,
                new Color(0, 0, 0)), new EmptyBorder(4, 4, 4, 4)));
        GridBagConstraints gbc_panel_3 = new GridBagConstraints();
        gbc_panel_3.anchor = GridBagConstraints.NORTH;
        gbc_panel_3.fill = GridBagConstraints.HORIZONTAL;
        gbc_panel_3.insets = new Insets(0, 0, 5, 0);
        gbc_panel_3.gridx = 0;
        gbc_panel_3.gridy = 0;
        panel_2.add(panel_3, gbc_panel_3);
        GridBagLayout gbl_panel_3 = new GridBagLayout();
        gbl_panel_3.columnWidths = new int[] { 0, 0, 0 };
        gbl_panel_3.rowHeights = new int[] { 0, 0, 0 };
        gbl_panel_3.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
        gbl_panel_3.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
        panel_3.setLayout(gbl_panel_3);

        JLabel lblWordErrorRate = new JLabel("Word Error Rate:");
        GridBagConstraints gbc_lblWordErrorRate = new GridBagConstraints();
        gbc_lblWordErrorRate.anchor = GridBagConstraints.EAST;
        gbc_lblWordErrorRate.insets = new Insets(0, 0, 5, 5);
        gbc_lblWordErrorRate.gridx = 0;
        gbc_lblWordErrorRate.gridy = 0;
        panel_3.add(lblWordErrorRate, gbc_lblWordErrorRate);

        JLabel label_1 = new JLabel("0.0%");
        GridBagConstraints gbc_label_1 = new GridBagConstraints();
        gbc_label_1.anchor = GridBagConstraints.EAST;
        gbc_label_1.insets = new Insets(0, 0, 5, 0);
        gbc_label_1.gridx = 1;
        gbc_label_1.gridy = 0;
        panel_3.add(label_1, gbc_label_1);

        JLabel lblCharacterErrorRate = new JLabel("Character Error Rate:");
        GridBagConstraints gbc_lblCharacterErrorRate = new GridBagConstraints();
        gbc_lblCharacterErrorRate.anchor = GridBagConstraints.EAST;
        gbc_lblCharacterErrorRate.insets = new Insets(0, 0, 0, 5);
        gbc_lblCharacterErrorRate.gridx = 0;
        gbc_lblCharacterErrorRate.gridy = 1;
        panel_3.add(lblCharacterErrorRate, gbc_lblCharacterErrorRate);

        JLabel label_2 = new JLabel("0.0%");
        GridBagConstraints gbc_label_2 = new GridBagConstraints();
        gbc_label_2.anchor = GridBagConstraints.EAST;
        gbc_label_2.gridx = 1;
        gbc_label_2.gridy = 1;
        panel_3.add(label_2, gbc_label_2);

        JSplitPane splitPane_1 = new JSplitPane();
        splitPane_1.setAlignmentY(Component.CENTER_ALIGNMENT);
        splitPane_1.setResizeWeight(0.5);
        splitPane_1.setAlignmentX(Component.CENTER_ALIGNMENT);
        GridBagConstraints gbc_splitPane_1 = new GridBagConstraints();
        gbc_splitPane_1.fill = GridBagConstraints.BOTH;
        gbc_splitPane_1.insets = new Insets(0, 0, 5, 0);
        gbc_splitPane_1.gridx = 0;
        gbc_splitPane_1.gridy = 1;
        panel_2.add(splitPane_1, gbc_splitPane_1);

        JScrollPane scrollPane_2 = new JScrollPane();
        splitPane_1.setLeftComponent(scrollPane_2);

        JLabel lblOriginalDiff = new JLabel("");
        scrollPane_2.setViewportView(lblOriginalDiff);

        JLabel lblOriginal_1 = new JLabel("Original");
        scrollPane_2.setColumnHeaderView(lblOriginal_1);

        JScrollPane scrollPane_3 = new JScrollPane();
        splitPane_1.setRightComponent(scrollPane_3);

        JLabel lblLblreferencediff = new JLabel((String) null);
        scrollPane_3.setViewportView(lblLblreferencediff);

        JLabel lblReference_1 = new JLabel("Reference");
        scrollPane_3.setColumnHeaderView(lblReference_1);

        JPanel panel_4 = new JPanel();
        panel_4.setBorder(new CompoundBorder(new TitledBorder(
                UIManager.getBorder("TitledBorder.border"), "Details",
                TitledBorder.LEADING, TitledBorder.TOP, null,
                new Color(0, 0, 0)), new EmptyBorder(4, 4, 4, 4)));
        GridBagConstraints gbc_panel_4 = new GridBagConstraints();
        gbc_panel_4.fill = GridBagConstraints.BOTH;
        gbc_panel_4.gridx = 0;
        gbc_panel_4.gridy = 2;
        panel_2.add(panel_4, gbc_panel_4);
        panel_4.setLayout(new BorderLayout(0, 0));

        table = new JTable();
        table.setModel(new DefaultTableModel(
                new Object[][] {
                        { null, null, null, null, null, null, null },
                },
                new String[] {
                        "Character", "Hex code", "Total", "Spurious",
                        "Confused", "Lost", "Error Rate (%)"
                }
                ));
        table.getColumnModel().getColumn(6).setPreferredWidth(100);
        panel_4.add(table, BorderLayout.CENTER);
    }

    @Override
    public Component asComponent() {
        return this;
    }

    @Override
    public void setImageModel(Optional<ImageModel> model) {
        imageModel = model;

        if (model.isPresent()) {
            lblOriginal.setIcon(new ImageIcon(
                    model.get().getPreprocessedImage()));
        } else {
            lblOriginal.setIcon(null);
        }
    }

    @Override
    public Optional<ImageModel> getImageModel() {
        return imageModel;
    }
}
