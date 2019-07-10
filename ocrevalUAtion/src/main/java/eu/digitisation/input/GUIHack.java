/*
 * Copyright (C) 2014 Universidad de Alicante
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package eu.digitisation.input;

import eu.digitisation.log.Messages;
import eu.digitisation.ngram.NgramModel;
import eu.digitisation.ngram.NgramPerplexityEvaluator;
import eu.digitisation.output.Browser;
import eu.digitisation.output.OutputFileSelector;
import eu.digitisation.output.Report;
import eu.digitisation.text.Text;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author R.C.C
 */
public class GUIHack extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final Color green = Color.decode("#4C501E");
    private static final Color white = Color.decode("#FAFAFA");
    private static final Color gray = Color.decode("#EEEEEE");
    // Frame components
    FileSelector gtselector;
    FileSelector ocrselector;
    FileSelector lmselector;
    JPanel advanced;
    Link info;
    JPanel actions;

    /**
     * Show a warning message
     *
     * @param text the text to be displayed
     */

    public void warn(String message) {
        JOptionPane.showMessageDialog(super.getRootPane(), message, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Ask for confirmation
     */
    public boolean confirm(String message) {
        return JOptionPane.showConfirmDialog(super.getRootPane(),
                message, message, JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }

    // The unique constructor
    public GUIHack() {
        init();
    }

    /**
     * Build advanced options panel
     *
     * @param ignoreCase
     * @param ignoreDiacritics
     * @param ignorePunctuation
     * @param compatibilty
     * @param eqfile
     * @return
     */
    private JPanel advancedOptionsPanel(Parameters pars) {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        JPanel subpanel = new JPanel(new GridLayout(0, 2));
        Color fg = getForeground();
        Color bg = getBackground();

        subpanel.setForeground(fg);
        subpanel.setBackground(bg);
        subpanel.add(new BooleanSelector(pars.ignoreCase, fg, bg));
        subpanel.add(new BooleanSelector(pars.ignoreDiacritics, fg, bg));
        subpanel.add(new BooleanSelector(pars.ignorePunctuation, fg, bg));
        subpanel.add(new BooleanSelector(pars.compatibility, fg, bg));

        panel.setForeground(fg);
        panel.setBackground(bg);
        panel.setVisible(false);
        panel.add(subpanel);
        panel.add(new FileSelector(pars.swfile, fg, bg));
        panel.add(new FileSelector(pars.eqfile, fg, bg));
        return panel;
    }

    /**
     * Creates a subpanel with two actions: "show advanced options" & "generate
     * report"
     *
     * @param gui
     * @return
     */
    private JPanel actionsPanel(final GUIHack gui, final Parameters pars) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        final JCheckBox more = new JCheckBox("Show advanced options");
        more.setForeground(getForeground());
        more.setBackground(Color.LIGHT_GRAY);
        more.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Dimension dframe = gui.getSize();
                Dimension dadvanced = gui.advanced.getPreferredSize();
                if (more.isSelected()) {
                    gui.setSize(new Dimension(dframe.width, dframe.height + dadvanced.height));
                } else {
                    gui.setSize(new Dimension(dframe.width, dframe.height - dadvanced.height));
                }
                gui.advanced.setVisible(more.isSelected());
            }
        });

        JButton reset = new JButton("Reset");
        reset.setForeground(getForeground());
        reset.setBackground(getBackground());
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pars.clear();
                gui.remove(gtselector);
                gui.remove(ocrselector);
                gui.remove(info);
                gui.remove(advanced);
                gui.remove(actions);
                gui.repaint();
                gui.setVisible(true);
                gui.init();
            }
        });

        // Go for it! button with inverted colors
        JButton trigger = new JButton("Generate report");
        trigger.setForeground(getBackground());
        trigger.setBackground(getForeground());
        trigger.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    launch(pars);
                } catch (SchemaLocationException ex) {
                    Messages.severe(this.getClass() + ": "+ ex.getMessage());
                }
            }
        });

        panel.add(more, BorderLayout.WEST);
        panel.add(Box.createHorizontalGlue());
        panel.add(reset, BorderLayout.CENTER);
        panel.add(Box.createHorizontalGlue());
        panel.add(trigger, BorderLayout.EAST);
        return panel;
    }

    public void launch(Parameters pars) throws SchemaLocationException {
        try {
            if (ocrselector.ready() && (gtselector.ready() || lmselector.ready())) {
                File ocrfile = pars.ocrfile.getValue();
                if (gtselector.ready()) {
                    String name = ocrfile.getName().replaceAll("\\.\\w+", "") + "_report.html";
                    File dir = ocrfile.getParentFile();
                    File preselected = new File(name);
                    OutputFileSelector selector = new OutputFileSelector();
                    File outfile = selector.choose(dir, preselected);
                    pars.outfile.setValue(outfile);

                    if (outfile != null) {
                        try {
                            Batch batch = new Batch(pars.gtfile.value, pars.ocrfile.value);
                            Report report = new Report(batch, pars);
                            report.write(outfile);
                            Messages.info("Report dumped to " + outfile);
                            Browser.open(outfile.toURI());
                        } catch (InvalidObjectException ex) {
                            warn(ex.getMessage());
                        } catch (IOException ex) {
                            warn("Input/Output Error");
                        }
                    }
                }
                if (lmselector.ready()) {
                    Object[] possibilities = {"2", "3", "4", "5"};
                    String value
                            = (String) JOptionPane.showInputDialog(null, "Select contect length", "",
                                    JOptionPane.QUESTION_MESSAGE, null, possibilities, "2");
                    if (value != null) {
                        int contextLength = Integer.parseInt(value);

                        NgramPerplexityEvaluator lpc = new NgramPerplexityEvaluator(pars.lmfile.value);

                        Text ocr = new Text(ocrfile);
                        double[] perplexityArray = lpc.calculatePerplexity(ocr.toString(), contextLength);

                        LanguageModelEvaluationFrame frame = new LanguageModelEvaluationFrame();
                        frame.setInput(ocr.toString(), perplexityArray);
                        frame.setVisible(true);
                    }
                }
            } else {
                gtselector.checkout();
                ocrselector.checkout();
                lmselector.checkout();
            }
        } catch (WarningException ex) {
            warn(ex.getMessage());
        }
    }

    public final void init() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Main container
        Container pane = getContentPane();
        // Initialization settings
        setForeground(green);
        setBackground(gray);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        setLocationRelativeTo(null);

        // Define program parameters: input files
        Parameters pars = new Parameters();

        // Define content
        gtselector = new FileSelector(pars.gtfile, getForeground(), white);
        ocrselector = new FileSelector(pars.ocrfile, getForeground(), white);
        lmselector = new FileSelector(pars.lmfile, getForeground(), white);
        advanced = advancedOptionsPanel(pars);
        info = new Link("Info:", "https://sites.google.com/site/textdigitisation/ocrevaluation", getForeground());
        actions = actionsPanel(this, pars);

        // Put all content together
        pane.add(gtselector);
        pane.add(ocrselector);
        pane.add(lmselector);
        pane.add(advanced);
        pane.add(info);
        pane.add(actions);

        // menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu mainMenu = new JMenu("Main");
        JMenuItem createLanguageModelMenuItem = new JMenuItem("Create Language Model...", KeyEvent.VK_C);
        createLanguageModelMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                File inputFile = choose("Choose file to create language model", "sample.txt");
                if (inputFile != null) {
                    File outputFile = choose("Choose output file", "model.lm");
                    if (outputFile != null) {
                        Object[] possibilities = {"2", "3", "4", "5"};
                        String value
                                = (String) JOptionPane.showInputDialog(null, "Select value vor 'n'", "",
                                        JOptionPane.QUESTION_MESSAGE, null, possibilities, "2");
                        if (value != null) {
                            int n = Integer.parseInt(value);

                            NgramModel ngramModel = new NgramModel(n);
                            Charset encoding = Charset.forName(System.getProperty("file.encoding"));
                            if (inputFile.isDirectory()) {
                                File[] files = inputFile.listFiles();
                                for (File file : files) {
                                    ngramModel.addWords(file, encoding, false);
                                }
                            } else {
                                ngramModel.addWords(inputFile, encoding, false);
                            }
                            ngramModel.save(outputFile);
                        }
                    }
                }
            }
        });
        JMenuItem exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        exitMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        mainMenu.add(createLanguageModelMenuItem);
        mainMenu.addSeparator();
        mainMenu.add(exitMenuItem);

        menuBar.add(mainMenu);

        this.setJMenuBar(menuBar);

        // Show
        pack();
        setVisible(true);
    }

    private File choose(String title, String defaultName) {
        JFileChooser chooser = new JFileChooser();

        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setDialogTitle(title);
        chooser.setSelectedFile(new File(defaultName));
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        } else {
            return null;
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GUI();
            }
        });
    }
}
