package de.vorb.tesseract.gui.view.dialogs;

import de.vorb.tesseract.gui.model.PreferencesUtil;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.File;
import java.util.Arrays;
import java.util.prefs.Preferences;

public class PreferencesDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private static final String[] PSM_MODES = {
            "0 - PSM_OSD_ONLY",
            "1 - PSM_AUTO_OSD",
            "2 - PSM_AUTO_ONLY",
            "3 - (DEFAULT) PSM_AUTO",
            "4 - PSM_SINGLE_COLUMN",
            "5 - PSM_SINGLE_BLOCK_VERT_TEXT",
            "6 - PSM_SINGLE_BLOCK",
            "7 - PSM_SINGLE_LINE",
            "8 - PSM_SINGLE_WORD",
            "9 - PSM_CIRCLE_WORD",
            "10 - PSM_SINGLE_CHAR",
            "11 - PSM_SPARSE_TEXT",
            "12 - PSM_SPARSE_TEXT_OSD",
            "13 - PSM_RAW_LINE",
    };
    public static final int DEFAULT_PSM_MODE = 3;

    public static final String KEY_LANGDATA_DIR = "langdata_dir";
    public static final String KEY_RENDERING_FONT = "rendering_font";
    public static final String KEY_EDITOR_FONT = "editor_font";
    public static final String KEY_PAGE_SEG_MODE = "page_seg_mode";

    private final JPanel contentPanel = new JPanel();
    private JTextField tfLangdataDir;

    private final JComboBox<String> comboRenderingFont;
    private final JComboBox<String> comboEditorFont;
    private final JComboBox<String> comboPageSegMode;

    private ResultState resultState = ResultState.CANCEL;

    public enum ResultState {
        APPROVE, CANCEL
    }

    /**
     * Create the dialog.
     */
    public PreferencesDialog() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(PreferencesDialog.class.getResource("/logos/logo_16.png")));
        final Preferences pref = PreferencesUtil.getPreferences();

        setModalityType(ModalityType.APPLICATION_MODAL);
        setTitle("General Preferences");
        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[]{0, 0, 0, 0};
        gbl_contentPanel.rowHeights = new int[]{0, 0, 0};
        gbl_contentPanel.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
        gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        contentPanel.setLayout(gbl_contentPanel);
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

        final GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final String[] availableFontFamilyNames = graphicsEnvironment.getAvailableFontFamilyNames();

        // Rendering font
        addGridLabel(0, 2, "Rendering font:");
        final String initialFontFamilyName = pref.get(PreferencesDialog.KEY_EDITOR_FONT, Font.SANS_SERIF);
        comboRenderingFont = createGridComboBox(1, 2, availableFontFamilyNames, initialFontFamilyName);

        // Editor font
        addGridLabel(0, 3, "Editor font:");
        final String initialEditorFontFamilyName = pref.get(PreferencesDialog.KEY_RENDERING_FONT, Font.MONOSPACED);
        comboEditorFont = createGridComboBox(1, 3, availableFontFamilyNames, initialEditorFontFamilyName);

        // Page Segmentation Modes
        addGridLabel(0, 4, "Page Segmentation Mode:");
        final int pageSegMode = pref.getInt(PreferencesDialog.KEY_PAGE_SEG_MODE, DEFAULT_PSM_MODE);
        comboPageSegMode = createGridComboBox(1, 4, PSM_MODES, PSM_MODES[pageSegMode]);

        pack();
        setMinimumSize(getSize());
    }

    private void setState(ResultState state) {
        this.resultState = state;
    }

    public JTextField getTfLangdataDir() {
        return tfLangdataDir;
    }

    public JComboBox<String> getComboRenderingFont() {
        return comboRenderingFont;
    }

    public JComboBox<String> getComboEditorFont() {
        return comboEditorFont;
    }

    public int getPageSegmentationMode() {
        return comboPageSegMode.getSelectedIndex();
    }

    public ResultState showPreferencesDialog(Component parent) {
        setLocationRelativeTo(parent);
        setVisible(true);
        return resultState;
    }

    private Component addGridLabel(int gridX, int gridY, String label) {
        JLabel jLabel = new JLabel(label);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(0, 0, 0, 5);
        constraints.gridx = gridX;
        constraints.gridy = gridY;
        contentPanel.add(jLabel, constraints);
        return jLabel;
    }

    private <T> JComboBox<T> createGridComboBox(int gridX, int gridY, T[] options, T selectedItem) {
        JComboBox<T> jComboBox = new JComboBox<>();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(0, 0, 0, 5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = gridX;
        constraints.gridy = gridY;
        contentPanel.add(jComboBox, constraints);

        Arrays.stream(options).forEach(jComboBox::addItem);
        jComboBox.setSelectedItem(selectedItem);
        return jComboBox;
    }

}
