package de.vorb.tesseract.gui.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import de.vorb.tesseract.gui.model.BoxFileModel;
import de.vorb.tesseract.util.Page;

public class BoxFilePane extends JPanel implements MainComponent {
    private static final long serialVersionUID = 1L;

    private Page model = null;
    private JTextField textField;
    private JTable table;

    private final JSpinner spinLeft;
    private final JSpinner spinTop;
    private final JSpinner spinRight;
    private final JSpinner spinBottom;

    private final JLabel lblImage;

    private static final Dimension DEFAULT_SPINNER_DIMENSION = new Dimension(
            50, 20);

    /**
     * Create the panel.
     */
    public BoxFilePane() {
        setLayout(new BorderLayout(0, 0));

        JPanel toolbar = new JPanel();
        FlowLayout flowLayout = (FlowLayout) toolbar.getLayout();
        flowLayout.setAlignment(FlowLayout.LEADING);
        add(toolbar, BorderLayout.NORTH);

        JSplitPane spMain = new JSplitPane();
        add(spMain, BorderLayout.CENTER);

        JLabel lblExample = new JLabel("Symbol");
        toolbar.add(lblExample);

        textField = new JTextField();
        toolbar.add(textField);
        textField.setColumns(5);

        Component horizontalStrut = Box.createHorizontalStrut(10);
        toolbar.add(horizontalStrut);

        JLabel lblLeft = new JLabel("Left");
        toolbar.add(lblLeft);

        spinLeft = new JSpinner();
        spinLeft.setPreferredSize(DEFAULT_SPINNER_DIMENSION);
        spinLeft.setModel(new SpinnerNumberModel(0, 0, null, 1));
        toolbar.add(spinLeft);

        Component horizontalStrut_1 = Box.createHorizontalStrut(5);
        toolbar.add(horizontalStrut_1);

        JLabel lblTop = new JLabel("Top");
        toolbar.add(lblTop);

        spinTop = new JSpinner();
        spinTop.setPreferredSize(DEFAULT_SPINNER_DIMENSION);
        spinTop.setModel(new SpinnerNumberModel(0, 0, null, 1));
        toolbar.add(spinTop);

        Component horizontalStrut_2 = Box.createHorizontalStrut(5);
        toolbar.add(horizontalStrut_2);

        JLabel lblRight = new JLabel("Right");
        toolbar.add(lblRight);

        spinRight = new JSpinner();
        spinRight.setPreferredSize(DEFAULT_SPINNER_DIMENSION);
        spinRight.setModel(new SpinnerNumberModel(0, 0, null, 1));
        toolbar.add(spinRight);

        Component horizontalStrut_3 = Box.createHorizontalStrut(5);
        toolbar.add(horizontalStrut_3);

        JLabel lblBottom = new JLabel("Bottom");
        toolbar.add(lblBottom);

        spinBottom = new JSpinner();
        spinBottom.setPreferredSize(DEFAULT_SPINNER_DIMENSION);
        spinBottom.setModel(new SpinnerNumberModel(0, 0, null, 1));
        toolbar.add(spinBottom);

        JPanel sidebar = new JPanel();
        spMain.setLeftComponent(sidebar);
        sidebar.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane_1 = new JScrollPane();
        sidebar.add(scrollPane_1, BorderLayout.CENTER);

        table = new JTable();

        table.setModel(new DefaultTableModel(
                new Object[][] {
                },
                new String[] {
                        "Symbol", "Left", "Top", "Right", "Bottom"
                }
                ) {
                    private static final long serialVersionUID = 1L;

                    final Class<?>[] columnTypes = new Class[] {
                            String.class, Integer.class, Integer.class,
                            Integer.class, Integer.class
                    };

                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        return columnTypes[columnIndex];
                    }

                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                });

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(0).setMaxWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setMaxWidth(60);
        table.getColumnModel().getColumn(2).setPreferredWidth(40);
        table.getColumnModel().getColumn(2).setMaxWidth(60);
        table.getColumnModel().getColumn(3).setPreferredWidth(40);
        table.getColumnModel().getColumn(3).setMaxWidth(60);
        table.getColumnModel().getColumn(4).setPreferredWidth(40);
        table.getColumnModel().getColumn(4).setMaxWidth(60);

        scrollPane_1.setViewportView(table);

        scrollPane_1.setMinimumSize(new Dimension(200, 100));
        scrollPane_1.setPreferredSize(new Dimension(260, 10000));
        scrollPane_1.setMaximumSize(new Dimension(310, 10000));

        JScrollPane scrollPane = new JScrollPane();
        spMain.setRightComponent(scrollPane);

        lblImage = new JLabel("");
        scrollPane.setViewportView(lblImage);

    }

    @Override
    public void setModel(Page page) {
        this.model = page;

    }

    @Override
    public Page getModel() {
        return model;
    }

    @Override
    public Component asComponent() {
        return this;
    }
}
