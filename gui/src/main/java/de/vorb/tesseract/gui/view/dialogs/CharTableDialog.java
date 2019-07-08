package de.vorb.tesseract.gui.view.dialogs;

import de.vorb.tesseract.gui.model.CharTableModel;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;

public class CharTableDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private JTable table;
    private JFormattedTextField textField;

    /**
     * Create the panel.
     */
    public CharTableDialog() {
        super();

        setTitle("Special characters");
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        getContentPane().setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();

        getContentPane().add(panel, BorderLayout.SOUTH);
        panel.setLayout(new BorderLayout(0, 0));

        JPanel panel_1 = new JPanel();
        panel.add(panel_1, BorderLayout.WEST);

        JLabel label = new JLabel("Code point");
        panel_1.add(label);

        textField = new JFormattedTextField();
        panel_1.add(textField);
        textField.setColumns(4);

        JButton btnAdd = new JButton("Add");
        panel_1.add(btnAdd);

        JPanel panel_2 = new JPanel();
        panel.add(panel_2, BorderLayout.EAST);

        JButton btnInsertCharacter = new JButton("Insert character");
        panel_2.add(btnInsertCharacter);

        JScrollPane scrollPane = new JScrollPane();
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        table = new JTable();
        table.setModel(new CharTableModel());
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        scrollPane.setViewportView(table);
    }
}
