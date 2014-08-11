package de.vorb.tesseract.gui.view;

import javax.swing.JPanel;

import de.vorb.tesseract.gui.model.Scale;
import java.awt.BorderLayout;
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

public class EvaluationPane extends JPanel {
    private static final long serialVersionUID = 1L;

    private JTable table;

    /**
     * Create the panel.
     * 
     * @param scale
     */
    public EvaluationPane(Scale scale) {
        setLayout(new BorderLayout(0, 0));

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        add(tabbedPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        tabbedPane.addTab("Reference", null, panel, null);
        panel.setLayout(new BorderLayout(0, 0));

        JSplitPane splitPane = new JSplitPane();
        panel.add(splitPane);
        splitPane.setResizeWeight(0.5);

        JScrollPane scrollPane = new JScrollPane();
        splitPane.setLeftComponent(scrollPane);

        JLabel lblOriginal = new JLabel("Original");
        scrollPane.setColumnHeaderView(lblOriginal);

        JLabel label = new JLabel("");
        scrollPane.setViewportView(label);

        JScrollPane scrollPane_1 = new JScrollPane();
        splitPane.setRightComponent(scrollPane_1);

        JTextArea textArea = new JTextArea();
        scrollPane_1.setViewportView(textArea);

        JPanel panel_1 = new JPanel();
        panel.add(panel_1, BorderLayout.SOUTH);

        JButton btnGenerateReport = new JButton("Generate Report");
        panel_1.add(btnGenerateReport);

        JPanel panel_2 = new JPanel();
        tabbedPane.addTab("Report", null, panel_2, null);
        panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.Y_AXIS));

        JPanel panel_3 = new JPanel();
        panel_3.setAlignmentY(Component.TOP_ALIGNMENT);
        panel_3.setBorder(new CompoundBorder(new TitledBorder(
                UIManager.getBorder("TitledBorder.border"), "Overview",
                TitledBorder.LEADING, TitledBorder.TOP, null,
                new Color(0, 0, 0)), new EmptyBorder(4, 4, 4, 4)));
        panel_2.add(panel_3);
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
        panel_2.add(splitPane_1);

        JScrollPane scrollPane_2 = new JScrollPane();
        splitPane_1.setLeftComponent(scrollPane_2);

        JScrollPane scrollPane_3 = new JScrollPane();
        splitPane_1.setRightComponent(scrollPane_3);

        JPanel panel_4 = new JPanel();
        panel_4.setBorder(new CompoundBorder(new TitledBorder(
                UIManager.getBorder("TitledBorder.border"), "Details",
                TitledBorder.LEADING, TitledBorder.TOP, null,
                new Color(0, 0, 0)), new EmptyBorder(4, 4, 4, 4)));
        panel_2.add(panel_4);
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
                ) {
                    private static final long serialVersionUID = 1L;

                    Class<?>[] columnTypes = new Class[] {
                            String.class, String.class, Integer.class,
                            Integer.class, Integer.class, Integer.class,
                            Double.class
                    };

                    public Class<?> getColumnClass(int columnIndex) {
                        return columnTypes[columnIndex];
                    }
                });
        table.getColumnModel().getColumn(6).setPreferredWidth(100);
        panel_4.add(table, BorderLayout.CENTER);

    }

}
