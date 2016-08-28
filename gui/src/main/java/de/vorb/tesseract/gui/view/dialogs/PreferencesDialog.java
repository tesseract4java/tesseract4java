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

    public static final String KEY_LANGDATA_DIR = "langdata_dir";
    public static final String KEY_RENDERING_FONT = "rendering_font";
    public static final String KEY_EDITOR_FONT = "editor_font";

    private final JPanel contentPanel = new JPanel();
    private JTextField tfLangdataDir;

    private final JComboBox<String> comboRenderingFont;
    private final JComboBox<String> comboEditorFont;

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

        {
            JLabel lblRenderingFont = new JLabel("Rendering font:");
            GridBagConstraints gbc_lblRenderingFont = new GridBagConstraints();
            gbc_lblRenderingFont.anchor = GridBagConstraints.EAST;
            gbc_lblRenderingFont.insets = new Insets(0, 0, 0, 5);
            gbc_lblRenderingFont.gridx = 0;
            gbc_lblRenderingFont.gridy = 2;
            contentPanel.add(lblRenderingFont, gbc_lblRenderingFont);
        }
        {
            comboRenderingFont = new JComboBox<>();
            GridBagConstraints gbc_comboRenderingFont = new GridBagConstraints();
            gbc_comboRenderingFont.insets = new Insets(0, 0, 0, 5);
            gbc_comboRenderingFont.fill = GridBagConstraints.HORIZONTAL;
            gbc_comboRenderingFont.gridx = 1;
            gbc_comboRenderingFont.gridy = 2;
            contentPanel.add(comboRenderingFont, gbc_comboRenderingFont);

            Arrays.stream(availableFontFamilyNames).forEach(comboRenderingFont::addItem);
            comboRenderingFont.setSelectedItem(pref.get(PreferencesDialog.KEY_RENDERING_FONT, Font.SANS_SERIF));
        }
        {
            JLabel lblEditorFont = new JLabel("Editor font:");
            GridBagConstraints gbc_lblEditorFont = new GridBagConstraints();
            gbc_lblEditorFont.anchor = GridBagConstraints.EAST;
            gbc_lblEditorFont.insets = new Insets(0, 0, 0, 5);
            gbc_lblEditorFont.gridx = 0;
            gbc_lblEditorFont.gridy = 3;
            contentPanel.add(lblEditorFont, gbc_lblEditorFont);
        }
        {
            comboEditorFont = new JComboBox<>();
            GridBagConstraints gbc_comboEditorFont = new GridBagConstraints();
            gbc_comboEditorFont.insets = new Insets(0, 0, 0, 5);
            gbc_comboEditorFont.fill = GridBagConstraints.HORIZONTAL;
            gbc_comboEditorFont.gridx = 1;
            gbc_comboEditorFont.gridy = 3;
            contentPanel.add(comboEditorFont, gbc_comboEditorFont);

            Arrays.stream(availableFontFamilyNames).forEach(comboEditorFont::addItem);
            comboEditorFont.setSelectedItem(pref.get(PreferencesDialog.KEY_EDITOR_FONT, Font.MONOSPACED));
        }

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

    public ResultState showPreferencesDialog(Component parent) {
        setLocationRelativeTo(parent);
        setVisible(true);
        return resultState;
    }
}
