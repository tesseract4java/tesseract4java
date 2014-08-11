package de.vorb.tesseract.gui.view.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.google.common.base.Optional;

public class RecognitionParametersDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private final JPanel contentPanel = new JPanel();
    private final JTextField tfMaxNoiseHeightFraction;

    private boolean applied = false;

    /**
     * Create the dialog.
     * 
     * @param parent
     */
    private RecognitionParametersDialog(Window parent) {
        super(parent);

        setBounds(100, 100, 450, 300);
        setModalityType(ModalityType.APPLICATION_MODAL);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[] { 0, 0, 0 };
        gbl_contentPanel.rowHeights = new int[] { 0, 0 };
        gbl_contentPanel.columnWeights = new double[] { 0.0, 1.0,
                Double.MIN_VALUE };
        gbl_contentPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        contentPanel.setLayout(gbl_contentPanel);
        {
            JLabel lblMaxNoiseHeight = new JLabel("Max. noise height fraction:");
            GridBagConstraints gbc_lblMaxNoiseHeight = new GridBagConstraints();
            gbc_lblMaxNoiseHeight.insets = new Insets(0, 0, 0, 5);
            gbc_lblMaxNoiseHeight.anchor = GridBagConstraints.EAST;
            gbc_lblMaxNoiseHeight.gridx = 0;
            gbc_lblMaxNoiseHeight.gridy = 0;
            contentPanel.add(lblMaxNoiseHeight, gbc_lblMaxNoiseHeight);
        }
        {
            tfMaxNoiseHeightFraction = new JTextField();
            GridBagConstraints gbc_tfMaxNoiseHeightFraction = new GridBagConstraints();
            gbc_tfMaxNoiseHeightFraction.anchor = GridBagConstraints.WEST;
            gbc_tfMaxNoiseHeightFraction.gridx = 1;
            gbc_tfMaxNoiseHeightFraction.gridy = 0;
            contentPanel.add(tfMaxNoiseHeightFraction,
                    gbc_tfMaxNoiseHeightFraction);
            tfMaxNoiseHeightFraction.setColumns(10);
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton applyButton = new JButton("Apply");
                applyButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        applied = true;
                        RecognitionParametersDialog.this.setVisible(false);
                    }
                });
                buttonPane.add(applyButton);
                getRootPane().setDefaultButton(applyButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        RecognitionParametersDialog.this.setVisible(false);
                    }
                });
                buttonPane.add(cancelButton);
            }
        }
    }

    public static Optional<Float> showDialog(Window parent) {
        final RecognitionParametersDialog dialog =
                new RecognitionParametersDialog(parent);
        dialog.setVisible(true);
        if (dialog.applied) {
            try {
                return Optional.of(Float.parseFloat(dialog.tfMaxNoiseHeightFraction.getText()));
            } catch (NumberFormatException e) {
                return Optional.absent();
            }
        }

        return Optional.absent();
    }
}
