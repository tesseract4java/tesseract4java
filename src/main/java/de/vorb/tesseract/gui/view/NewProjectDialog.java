package de.vorb.tesseract.gui.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import de.vorb.tesseract.gui.event.ProjectChangeListener;
import javax.swing.border.TitledBorder;
import javax.swing.JCheckBox;

public class NewProjectDialog extends JDialog {
    public static enum ResultState {
        APPROVE, CANCEL, ERROR;
    }

    private static final long serialVersionUID = 1L;

    protected ResultState resultState;

    private final List<ProjectChangeListener> listeners = new LinkedList<ProjectChangeListener>();
    private JTextField tfPath;

    /**
     * Create the dialog.
     * 
     * @param owner
     */
    private NewProjectDialog(final Window owner) {
        super(owner);
        setResizable(false);
        setIconImage(Toolkit.getDefaultToolkit().getImage(
                NewProjectDialog.class.getResource("/logos/logo_16.png")));
        setTitle("New Project");

        JPanel panel = new JPanel();
        getContentPane().add(panel);
        panel.setLayout(new BorderLayout(0, 0));

        JPanel top = new JPanel();
        top.setBorder(new EmptyBorder(10, 10, 10, 10));
        top.setBackground(SystemColor.window);
        panel.add(top, BorderLayout.NORTH);
        FlowLayout fl_top = new FlowLayout(FlowLayout.LEADING, 5, 5);
        top.setLayout(fl_top);

        JLabel lblCreateNewProject = new JLabel("Create a new Project");
        lblCreateNewProject.setFont(new Font("Tahoma", Font.BOLD, 13));
        top.add(lblCreateNewProject);

        JPanel bottom = new JPanel();
        bottom.setBorder(new EmptyBorder(10, 10, 10, 10));
        FlowLayout fl_bottom = (FlowLayout) bottom.getLayout();
        fl_bottom.setAlignment(FlowLayout.TRAILING);
        panel.add(bottom, BorderLayout.SOUTH);

        JButton btnCreate = new JButton("Create");
        btnCreate.setEnabled(false);
        bottom.add(btnCreate);

        JButton btnCancel = new JButton("Cancel");
        bottom.add(btnCancel);

        JPanel main = new JPanel();
        main.setBorder(new CompoundBorder(new MatteBorder(1, 0, 1, 0,
                (Color) new Color(180, 180, 180)), new EmptyBorder(16, 16, 16,
                16)));
        panel.add(main, BorderLayout.CENTER);
        main.setLayout(new BorderLayout(0, 0));

        JPanel directory = new JPanel();
        directory.setBorder(new EmptyBorder(0, 0, 16, 0));
        main.add(directory, BorderLayout.NORTH);
        GridBagLayout gbl_directory = new GridBagLayout();
        gbl_directory.columnWidths = new int[] { 0, 0, 0, 0 };
        gbl_directory.rowHeights = new int[] { 23, 0 };
        gbl_directory.columnWeights = new double[] { 0.0, 1.0, 0.0,
                Double.MIN_VALUE };
        gbl_directory.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        directory.setLayout(gbl_directory);

        JLabel lblDDirectory = new JLabel("Directory:");
        GridBagConstraints gbc_lblDDirectory = new GridBagConstraints();
        gbc_lblDDirectory.anchor = GridBagConstraints.WEST;
        gbc_lblDDirectory.insets = new Insets(0, 0, 0, 5);
        gbc_lblDDirectory.gridx = 0;
        gbc_lblDDirectory.gridy = 0;
        directory.add(lblDDirectory, gbc_lblDDirectory);

        tfPath = new JTextField();
        GridBagConstraints gbc_tfPath = new GridBagConstraints();
        gbc_tfPath.fill = GridBagConstraints.HORIZONTAL;
        gbc_tfPath.insets = new Insets(0, 0, 0, 5);
        gbc_tfPath.gridx = 1;
        gbc_tfPath.gridy = 0;
        directory.add(tfPath, gbc_tfPath);
        tfPath.setColumns(10);

        JButton tfPathSelect = new JButton("...");
        GridBagConstraints gbc_tfPathSelect = new GridBagConstraints();
        gbc_tfPathSelect.anchor = GridBagConstraints.NORTHWEST;
        gbc_tfPathSelect.gridx = 2;
        gbc_tfPathSelect.gridy = 0;
        directory.add(tfPathSelect, gbc_tfPathSelect);
        tfPathSelect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                final JFileChooser jfc = new JFileChooser();
                int result = jfc.showOpenDialog(NewProjectDialog.this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    tfPath.setText(jfc.getSelectedFile().getAbsolutePath());
                }
            }
        });

        JPanel options = new JPanel();
        options.setBorder(new TitledBorder(null, "Options",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        main.add(options);
        options.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 5));

        JLabel lblFileFilter = new JLabel("File types:");
        options.add(lblFileFilter);

        JCheckBox chckbxTiff = new JCheckBox("TIFF");
        options.add(chckbxTiff);

        JCheckBox chckbxPng = new JCheckBox("PNG");
        options.add(chckbxPng);

        JCheckBox chckbxJpeg = new JCheckBox("JPEG");
        options.add(chckbxJpeg);
    }

    public static ResultState showDialog(Window parent) {
        final NewProjectDialog dialog = new NewProjectDialog(parent);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dialog.resultState = ResultState.CANCEL;
            }
        });

        return dialog.resultState;
    }
}
