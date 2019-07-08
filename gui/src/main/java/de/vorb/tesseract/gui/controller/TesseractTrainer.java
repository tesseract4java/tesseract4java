package de.vorb.tesseract.gui.controller;

import de.vorb.tesseract.gui.model.PreferencesUtil;
import de.vorb.tesseract.gui.view.TesseractFrame;
import de.vorb.tesseract.gui.view.dialogs.Dialogs;
import de.vorb.tesseract.gui.view.dialogs.PreferencesDialog;
import de.vorb.util.FileNames;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;

public class TesseractTrainer extends JDialog {
    private static final long serialVersionUID = 1L;

    private static final String KEY_TRAINING_DIR = "training_dir";

    private JPanel contentPane;
    private JTextField tfTrainingDir;
    private JTextField tfLangdataDir;
    private JTextField tfExecutablesDir;
    private JCheckBox checkUseLangdata;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e1) {
            // fail silently
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e2) {
                // fail silently
            }

            // If the system LaF is not available, use whatever LaF is already
            // being used.
        }

        final TesseractTrainer trainer = new TesseractTrainer();
        trainer.setVisible(true);
    }

    /**
     * Create the frame.
     */
    public TesseractTrainer() {
        final Toolkit t = Toolkit.getDefaultToolkit();

        // load and set multiple icon sizes
        final List<Image> appIcons = new LinkedList<>();
        appIcons.add(t.getImage(TesseractFrame.class.getResource("/logos/logo_16.png")));
        appIcons.add(t.getImage(TesseractFrame.class.getResource("/logos/logo_96.png")));
        appIcons.add(t.getImage(TesseractFrame.class.getResource("/logos/logo_256.png")));
        setIconImages(appIcons);

        setTitle("Tesseract Trainer");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setModalityType(ModalityType.APPLICATION_MODAL);

        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        GridBagLayout gbl_contentPane = new GridBagLayout();
        gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0};
        gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        gbl_contentPane.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
        gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
        contentPane.setLayout(gbl_contentPane);

        JLabel lblExecutablesDirectory = new JLabel("Executables Directory:");
        GridBagConstraints gbc_lblExecutablesDirectory = new GridBagConstraints();
        gbc_lblExecutablesDirectory.anchor = GridBagConstraints.EAST;
        gbc_lblExecutablesDirectory.insets = new Insets(0, 0, 5, 5);
        gbc_lblExecutablesDirectory.gridx = 0;
        gbc_lblExecutablesDirectory.gridy = 0;
        contentPane.add(lblExecutablesDirectory, gbc_lblExecutablesDirectory);

        tfExecutablesDir = new JTextField();
        GridBagConstraints gbc_tfExecutablesDir = new GridBagConstraints();
        gbc_tfExecutablesDir.insets = new Insets(0, 0, 5, 5);
        gbc_tfExecutablesDir.fill = GridBagConstraints.HORIZONTAL;
        gbc_tfExecutablesDir.gridx = 1;
        gbc_tfExecutablesDir.gridy = 0;
        contentPane.add(tfExecutablesDir, gbc_tfExecutablesDir);
        tfExecutablesDir.setColumns(10);

        JButton btnSelectExecutablesDir = new JButton("Select...");
        btnSelectExecutablesDir.addActionListener(evt -> {
            final JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setCurrentDirectory(new File(tfExecutablesDir.getText()));
            final int result = fc.showOpenDialog(TesseractTrainer.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                final File dir = fc.getSelectedFile();
                tfExecutablesDir.setText(dir.getAbsolutePath());
            }
        });
        GridBagConstraints gbc_btnSelectExecutablesDir = new GridBagConstraints();
        gbc_btnSelectExecutablesDir.anchor = GridBagConstraints.WEST;
        gbc_btnSelectExecutablesDir.insets = new Insets(0, 0, 5, 0);
        gbc_btnSelectExecutablesDir.gridx = 2;
        gbc_btnSelectExecutablesDir.gridy = 0;
        contentPane.add(btnSelectExecutablesDir, gbc_btnSelectExecutablesDir);

        JLabel lblTrainingDirectory = new JLabel("Training Directory:");
        GridBagConstraints gbc_lblTrainingDirectory = new GridBagConstraints();
        gbc_lblTrainingDirectory.insets = new Insets(0, 0, 5, 5);
        gbc_lblTrainingDirectory.anchor = GridBagConstraints.EAST;
        gbc_lblTrainingDirectory.gridx = 0;
        gbc_lblTrainingDirectory.gridy = 1;
        contentPane.add(lblTrainingDirectory, gbc_lblTrainingDirectory);

        tfTrainingDir = new JTextField(PreferencesUtil.getPreferences().get(KEY_TRAINING_DIR, ""));
        GridBagConstraints gbc_textField = new GridBagConstraints();
        gbc_textField.insets = new Insets(0, 0, 5, 5);
        gbc_textField.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField.gridx = 1;
        gbc_textField.gridy = 1;
        contentPane.add(tfTrainingDir, gbc_textField);
        tfTrainingDir.setColumns(30);

        JButton btnSelectTrainingDir = new JButton("Select...");
        btnSelectTrainingDir.addActionListener(evt -> {
            final JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setCurrentDirectory(new File(tfTrainingDir.getText()));
            final int result = fc.showOpenDialog(TesseractTrainer.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                final File dir = fc.getSelectedFile();
                tfTrainingDir.setText(dir.getAbsolutePath());
                PreferencesUtil.getPreferences().put(KEY_TRAINING_DIR, dir.getAbsolutePath());
            }
        });
        GridBagConstraints gbc_btnSelectTrainingDir = new GridBagConstraints();
        gbc_btnSelectTrainingDir.anchor = GridBagConstraints.WEST;
        gbc_btnSelectTrainingDir.insets = new Insets(0, 0, 5, 0);
        gbc_btnSelectTrainingDir.gridx = 2;
        gbc_btnSelectTrainingDir.gridy = 1;
        contentPane.add(btnSelectTrainingDir, gbc_btnSelectTrainingDir);

        checkUseLangdata = new JCheckBox("Set unicharset properties");
        checkUseLangdata.setToolTipText("Requires 3.03+ training tools");
        GridBagConstraints gbc_chckbxUseLangdata = new GridBagConstraints();
        gbc_chckbxUseLangdata.anchor = GridBagConstraints.WEST;
        gbc_chckbxUseLangdata.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxUseLangdata.gridx = 1;
        gbc_chckbxUseLangdata.gridy = 2;
        contentPane.add(checkUseLangdata, gbc_chckbxUseLangdata);

        JLabel lblLangdataDirectory = new JLabel("Langdata Directory:");
        GridBagConstraints gbc_lblLangdataDirectory = new GridBagConstraints();
        gbc_lblLangdataDirectory.anchor = GridBagConstraints.EAST;
        gbc_lblLangdataDirectory.insets = new Insets(0, 0, 5, 5);
        gbc_lblLangdataDirectory.gridx = 0;
        gbc_lblLangdataDirectory.gridy = 3;
        contentPane.add(lblLangdataDirectory, gbc_lblLangdataDirectory);

        tfLangdataDir = new JTextField(PreferencesUtil.getPreferences().get(PreferencesDialog.KEY_LANGDATA_DIR, ""));
        tfLangdataDir.setEnabled(false);
        GridBagConstraints gbc_tfLangdataDir = new GridBagConstraints();
        gbc_tfLangdataDir.insets = new Insets(0, 0, 5, 5);
        gbc_tfLangdataDir.fill = GridBagConstraints.HORIZONTAL;
        gbc_tfLangdataDir.gridx = 1;
        gbc_tfLangdataDir.gridy = 3;
        contentPane.add(tfLangdataDir, gbc_tfLangdataDir);
        tfLangdataDir.setColumns(30);

        final JButton btnSelectLangdataDir = new JButton("Select...");
        btnSelectLangdataDir.addActionListener(evt -> {
            final JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setCurrentDirectory(new File(tfLangdataDir.getText()));
            final int result = fc.showOpenDialog(TesseractTrainer.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                final File dir = fc.getSelectedFile();
                tfLangdataDir.setText(dir.getAbsolutePath());
                PreferencesUtil.getPreferences().put(PreferencesDialog.KEY_LANGDATA_DIR, dir.getAbsolutePath());
            }
        });
        btnSelectLangdataDir.setEnabled(false);
        GridBagConstraints gbc_btnSelectLangdataDir = new GridBagConstraints();
        gbc_btnSelectLangdataDir.anchor = GridBagConstraints.WEST;
        gbc_btnSelectLangdataDir.insets = new Insets(0, 0, 5, 0);
        gbc_btnSelectLangdataDir.gridx = 2;
        gbc_btnSelectLangdataDir.gridy = 3;

        contentPane.add(btnSelectLangdataDir, gbc_btnSelectLangdataDir);
        checkUseLangdata.addActionListener(evt -> {
            tfLangdataDir.setEnabled(checkUseLangdata.isSelected());
            btnSelectLangdataDir.setEnabled(checkUseLangdata.isSelected());
        });

        JButton btnTrain = new JButton("Train");
        btnTrain.setIcon(new ImageIcon(
                TesseractTrainer.class.getResource("/icons/wand.png")));

        GridBagConstraints gbc_btnTrain = new GridBagConstraints();
        gbc_btnTrain.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnTrain.insets = new Insets(0, 0, 0, 5);
        gbc_btnTrain.gridx = 1;
        gbc_btnTrain.gridy = 6;
        contentPane.add(btnTrain, gbc_btnTrain);

        pack();
        setMinimumSize(getSize());

        btnTrain.addActionListener(new Trainer());
    }

    private class Trainer implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            final Path execDir = Paths.get(tfExecutablesDir.getText());
            if (!Files.isDirectory(execDir)) {
                Dialogs.showError(TesseractTrainer.this, "Error", "Invalid executables directory.");
                return;
            }
            final String cmdDir = execDir + File.separator;

            final Path trainingDir = Paths.get(tfTrainingDir.getText());
            if (!Files.isDirectory(trainingDir)
                    || !Files.isWritable(trainingDir)) {
                Dialogs.showError(TesseractTrainer.this, "Error", "Invalid training directory.");
                return;
            }

            final Path langdataDir = Paths.get(tfLangdataDir.getText());
            if (checkUseLangdata.isSelected()
                    && !Files.isDirectory(langdataDir)) {
                Dialogs.showError(TesseractTrainer.this, "Error", "Invalid langdata directory.");
                return;
            }

            // indeterminate
            TesseractTrainer.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                Files.deleteIfExists(trainingDir.resolve("training.log"));

                // create log stream

                try (final PrintStream log = new PrintStream(Files.newOutputStream(
                        trainingDir.resolve("training.log")), true, "UTF-8")) {

                    final DirectoryStream<Path> ds = Files.newDirectoryStream(trainingDir, new TrainingFileFilter());

                    ProcessBuilder pb;
                    InputStream err;
                    int c;

                    final LinkedList<String> boxFiles = new LinkedList<>();
                    final LinkedList<String> trFiles = new LinkedList<>();

                    String base = null;

                    // train
                    for (Path file : ds) {
                        final String sample = file.toString();
                        final String sampleBase = sample.replaceFirst("\\.[^.]+$", "");

                        if (base == null) {
                            final String fname = file.getFileName().toString();
                            base = file.getParent()
                                    .resolve(fname.replaceFirst("\\..+", "") + ".")
                                    .toString();
                        }

                        boxFiles.add(sampleBase + ".box");
                        trFiles.add(sampleBase + ".tr");

                        pb = new ProcessBuilder(cmdDir + "tesseract", sample, sampleBase, "box.train")
                                .directory(trainingDir.toFile());

                        log.println("tesseract " + sample + " box.train:\n");
                        final Process train = pb.start();
                        err = train.getErrorStream();

                        while ((c = err.read()) != -1) {
                            log.print((char) c);
                        }

                        log.println();

                        if (train.waitFor() != 0) {
                            throw new Exception("Unable to train '" + sample + "'.");
                        }
                    }

                    final String lang = Paths.get(base).getFileName().toString();

                    // delete old unicharset
                    Files.deleteIfExists(trainingDir.resolve("unicharset"));

                    // extract unicharset
                    final List<String> uniExtractor = new LinkedList<>();
                    uniExtractor.add(cmdDir + "unicharset_extractor");
                    uniExtractor.addAll(boxFiles);

                    pb = new ProcessBuilder(uniExtractor).directory(trainingDir.toFile());

                    log.println("\nunicharset_extractor:\n");
                    final Process unicharset = pb.start();
                    err = unicharset.getInputStream();

                    while ((c = err.read()) != -1) {
                        log.print((char) c);
                    }

                    if (unicharset.waitFor() != 0) {
                        throw new Exception("Unable to extract unicharset.");
                    }

                    // set unicharset properties
                    if (checkUseLangdata.isSelected()) {
                        pb = new ProcessBuilder(cmdDir + "set_unicharset_properties",
                                "-U", "unicharset", "-O", "out.unicharset",
                                "--script_dir=" + langdataDir).directory(
                                trainingDir.toFile());

                        log.println("\nset_unicharset_properties:\n");
                        final Process uniProps = pb.start();
                        err = uniProps.getErrorStream();

                        while ((c = err.read()) != -1) {
                            log.print((char) c);
                        }

                        if (uniProps.waitFor() != 0) {
                            throw new Exception("Unable to set unicharset properties.");
                        }
                    } else {
                        Files.copy(trainingDir.resolve("unicharset"),
                                trainingDir.resolve("out.unicharset"),
                                StandardCopyOption.REPLACE_EXISTING);
                    }

                    // mftraining
                    final List<String> mfTraining = new LinkedList<>();
                    mfTraining.add(cmdDir + "mftraining");
                    mfTraining.add("-F");
                    mfTraining.add(lang + "font_properties");
                    mfTraining.add("-U");
                    mfTraining.add("out.unicharset");
                    mfTraining.addAll(trFiles);
                    pb = new ProcessBuilder(mfTraining).directory(trainingDir.toFile());

                    log.println("\nmftraining:\n");
                    final Process mfTrain = pb.start();
                    err = mfTrain.getErrorStream();

                    while ((c = err.read()) != -1) {
                        log.print((char) c);
                    }

                    if (mfTrain.waitFor() != 0) {
                        throw new Exception("Unable to do mftraining.");
                    }

                    // cntraining
                    final List<String> cnTrainingParams = new LinkedList<>();
                    cnTrainingParams.add(cmdDir + "cntraining");
                    cnTrainingParams.addAll(trFiles);

                    pb = new ProcessBuilder(cnTrainingParams).directory(trainingDir.toFile());

                    log.println("\ncntraining:\n");
                    final Process cnTraining = pb.start();
                    err = cnTraining.getErrorStream();

                    while ((c = err.read()) != -1) {
                        log.print((char) c);
                    }

                    if (cnTraining.waitFor() != 0) {
                        throw new Exception("Unable to do cntraining.");
                    }

                    // rename files
                    Files.move(trainingDir.resolve("inttemp"),
                            trainingDir.resolve(lang + "inttemp"),
                            StandardCopyOption.REPLACE_EXISTING);
                    Files.move(trainingDir.resolve("normproto"),
                            trainingDir.resolve(lang + "normproto"),
                            StandardCopyOption.REPLACE_EXISTING);
                    Files.move(trainingDir.resolve("out.unicharset"),
                            trainingDir.resolve(lang + "unicharset"),
                            StandardCopyOption.REPLACE_EXISTING);
                    Files.move(trainingDir.resolve("pffmtable"),
                            trainingDir.resolve(lang + "pffmtable"),
                            StandardCopyOption.REPLACE_EXISTING);
                    Files.move(trainingDir.resolve("shapetable"),
                            trainingDir.resolve(lang + "shapetable"),
                            StandardCopyOption.REPLACE_EXISTING);

                    // combine
                    pb = new ProcessBuilder(cmdDir + "combine_tessdata", lang).directory(trainingDir.toFile());

                    log.println("\ncombine_tessdata:\n");
                    final Process combine = pb.start();
                    err = combine.getErrorStream();

                    while ((c = err.read()) != -1) {
                        log.print((char) c);
                    }

                    if (combine.waitFor() != 0) {
                        throw new Exception("Unable to combine the traineddata files.");
                    }

                    Dialogs.showInfo(TesseractTrainer.this,
                            "Training Complete",
                            "Training completed successfully.");
                } catch (Exception e) {
                    Dialogs.showError(TesseractTrainer.this, "Error",
                            "Training failed. " + e.getMessage());
                } finally {
                    TesseractTrainer.this.setCursor(Cursor.getDefaultCursor());

                    try {
                        Desktop.getDesktop().open(
                                trainingDir.resolve("training.log").toFile());
                    } catch (IOException e) {
                        Dialogs.showWarning(TesseractTrainer.this, "Warning",
                                "Could not open training log file.");
                    }
                }
            } catch (IOException e) {
                Dialogs.showError(TesseractTrainer.this, "Error",
                        "Training failed. " + e.getMessage());
            }
        }
    }

    private static class TrainingFileFilter implements
            DirectoryStream.Filter<Path> {
        @Override
        public boolean accept(Path entry)
                throws IOException {
            final String filename = entry.getFileName().toString();
            final boolean isImage = filename.endsWith(".png")
                    || filename.endsWith(".tif")
                    || filename.endsWith(".tiff")
                    || filename.endsWith(".jpg")
                    || filename.endsWith(".jpeg");
            if (!isImage) {
                return false;
            }

            final Path boxFile = FileNames.replaceExtension(entry, "box");
            return Files.isRegularFile(boxFile);
        }
    }
}
