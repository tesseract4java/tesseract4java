package de.vorb.tesseract.gui.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.google.common.base.Optional;

public class NewProjectDialog extends JDialog implements ActionListener,
        DocumentListener {
    public static class Result {
        public final Path directory;
        public final boolean tiff;
        public final boolean png;
        public final boolean jpeg;

        private Result(Path directory, boolean tiff, boolean png, boolean jpeg) {
            this.directory = directory;
            this.tiff = tiff;
            this.png = png;
            this.jpeg = jpeg;
        }
    }

    private static final long serialVersionUID = 1L;

    private final JTextField tfPath;
    private final JButton btnPathSelect;

    private final JCheckBox cbTiff;
    private final JCheckBox cbPng;
    private final JCheckBox cbJpeg;

    private final JButton btnCreate;
    private final JButton btnCancel;

    private Optional<Result> result = Optional.absent();

    /**
     * Create the dialog.
     * 
     * @param owner
     */
    private NewProjectDialog(final Window owner) {
        super(owner);

        setModalityType(ModalityType.APPLICATION_MODAL);

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

        btnCreate = new JButton("Create");
        btnCreate.setEnabled(false);
        bottom.add(btnCreate);
        btnCreate.addActionListener(this);

        btnCancel = new JButton("Cancel");
        bottom.add(btnCancel);
        btnCancel.addActionListener(this);

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
        tfPath.getDocument().addDocumentListener(this);

        btnPathSelect = new JButton("...");
        GridBagConstraints gbc_tfPathSelect = new GridBagConstraints();
        gbc_tfPathSelect.anchor = GridBagConstraints.NORTHWEST;
        gbc_tfPathSelect.gridx = 2;
        gbc_tfPathSelect.gridy = 0;
        directory.add(btnPathSelect, gbc_tfPathSelect);
        btnPathSelect.addActionListener(this);

        JPanel options = new JPanel();
        options.setBorder(new TitledBorder(null, "Options",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        main.add(options);
        options.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 5));

        JLabel lblFileFilter = new JLabel("File types:");
        options.add(lblFileFilter);

        cbTiff = new JCheckBox("TIFF");
        cbTiff.setSelected(true);
        options.add(cbTiff);
        cbTiff.addActionListener(this);

        cbPng = new JCheckBox("PNG");
        cbPng.setSelected(true);
        options.add(cbPng);
        cbPng.addActionListener(this);

        cbJpeg = new JCheckBox("JPEG");
        options.add(cbJpeg);
        cbJpeg.addActionListener(this);

        pack();

        setLocationRelativeTo(owner);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == btnCancel) {
            this.dispose();
        } else if (evt.getSource() == btnCreate) {
            // set result if settings are valid
            if (isStateValid()) {
                Path dir = Paths.get(tfPath.getText());
                this.result = Optional.of(new Result(dir, cbTiff.isSelected(),
                        cbPng.isSelected(), cbJpeg.isSelected()));
            }

            this.dispose();
        } else {
            if (evt.getSource() == btnPathSelect) {
                // show directory chooser
                final JFileChooser jfc = new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = jfc.showOpenDialog(this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    tfPath.setText(jfc.getSelectedFile().getAbsolutePath());
                }
            }

            // validate state
            btnCreate.setEnabled(isStateValid());
        }
    }

    private boolean isStateValid() {
        // validate the dialog
        if (tfPath.getText().isEmpty())
            return false;

        final Path directory = Paths.get(tfPath.getText());

        return Files.isDirectory(directory) && Files.isReadable(directory)
                && (cbTiff.isSelected() || cbPng.isSelected()
                || cbJpeg.isSelected());
    }

    public Path getDirectory() {
        return Paths.get(tfPath.getText());
    }

    public boolean isTiffChecked() {
        return cbTiff.isSelected();
    }

    public boolean isPngChecked() {
        return cbPng.isSelected();
    }

    public boolean isJpegChecked() {
        return cbJpeg.isSelected();
    }

    public static Optional<Result> showDialog(Window parent) {
        final NewProjectDialog dialog = new NewProjectDialog(parent);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        dialog.setVisible(true);

        return dialog.result;
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        btnCreate.setEnabled(isStateValid());
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        btnCreate.setEnabled(isStateValid());
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        btnCreate.setEnabled(isStateValid());
    }
}
