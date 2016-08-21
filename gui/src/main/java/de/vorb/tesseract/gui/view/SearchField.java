package de.vorb.tesseract.gui.view;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SearchField extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JLabel lblMagnifier;
    private final JTextField tfSearch;

    /**
     * Create the panel.
     */
    public SearchField() {
        setBackground(SystemColor.window);
        setBorder(BorderFactory.createEtchedBorder());
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0, 0, 0, 0};
        gridBagLayout.rowHeights = new int[]{0, 0};
        gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0,
                Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, Double.MIN_VALUE};
        setLayout(gridBagLayout);

        lblMagnifier = new JLabel();
        lblMagnifier.setIcon(new ImageIcon(SearchField.class.getResource("/icons/find.png")));
        GridBagConstraints gbc_label = new GridBagConstraints();
        gbc_label.insets = new Insets(0, 0, 0, 5);
        gbc_label.anchor = GridBagConstraints.WEST;
        gbc_label.gridx = 0;
        gbc_label.gridy = 0;
        add(lblMagnifier, gbc_label);

        tfSearch = new JTextField();
        tfSearch.setBorder(BorderFactory.createEmptyBorder());
        GridBagConstraints gbc_textField = new GridBagConstraints();
        gbc_textField.insets = new Insets(0, 0, 0, 5);
        gbc_textField.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField.gridx = 1;
        gbc_textField.gridy = 0;
        add(tfSearch, gbc_textField);

        final JLabel lblCross = new JLabel();
        lblCross.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                tfSearch.setText("");
            }
        });
        lblCross.setVisible(false);
        lblCross.setHorizontalAlignment(SwingConstants.TRAILING);
        lblCross.setIcon(new ImageIcon(
                SearchField.class.getResource("/icons/cross.png")));
        GridBagConstraints gbc_label_1 = new GridBagConstraints();
        gbc_label_1.anchor = GridBagConstraints.EAST;
        gbc_label_1.gridx = 2;
        gbc_label_1.gridy = 0;
        add(lblCross, gbc_label_1);

        tfSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent evt) {
                change();
            }

            @Override
            public void insertUpdate(DocumentEvent evt) {
                change();
            }

            @Override
            public void changedUpdate(DocumentEvent evt) {
                change();
            }

            private void change() {
                lblCross.setVisible(!tfSearch.getText().isEmpty());
            }
        });

        setPreferredSize(getPreferredSize());
    }

    public void setIcon(Icon icon) {
        lblMagnifier.setIcon(icon);
    }

    public JTextField getTextField() {
        return tfSearch;
    }
}
