package de.vorb.tesseract.gui.view;

import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField;
import javax.swing.JButton;
import javax.swing.JLabel;

public class CharTable extends JDialog {
    private static final long serialVersionUID = 1L;

    private JTable table;
    private JFormattedTextField textField;

    /**
     * Create the panel.
     */
    public CharTable() {
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

        JLabel label = new JLabel("Codepoint");
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
        table.setModel(new DefaultTableModel(
                new Object[][] {
                },
                new String[] {
                        "Character", "Description", "Codepoint"
                }
                ));
        scrollPane.setViewportView(table);

    }

}
