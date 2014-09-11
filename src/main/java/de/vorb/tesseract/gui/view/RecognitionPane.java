package de.vorb.tesseract.gui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.common.base.Optional;

import de.vorb.tesseract.gui.model.BoxFileModel;
import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.gui.model.Scale;
import de.vorb.tesseract.gui.view.renderer.RecognitionRenderer;
import javax.swing.Box;

public class RecognitionPane extends JPanel implements PageModelComponent {
    private static final long serialVersionUID = 1L;

    public static enum FontSelection {
        ANTIQUA("Antiqua"),
        FRAKTUR("Fraktur");

        private final String sel;

        private FontSelection(String sel) {
            this.sel = sel;
        }

        @Override
        public String toString() {
            return sel;
        }
    }

    private static final int SCROLL_UNITS = 12;

    private final RecognitionRenderer renderer;
    private final Scale scale;

    private JLabel lblOriginal_1;
    private final JLabel lblOriginal;
    private JLabel lblRecognition_1;
    private final JLabel lblRecognition;

    private final JCheckBox cbWordBoxes;
    private final JCheckBox cbSymbolBoxes;
    private final JCheckBox cbLineNumbers;
    private final JCheckBox cbBaselines;
    private final JCheckBox cbXLines;
    private final JComboBox<FontSelection> comboFont;

    private Optional<PageModel> model = Optional.absent();

    private final Timer delayer = new Timer(true);
    private JButton btZoomOut;
    private JButton btZoomIn;
    private JCheckBox cbBlocks;
    private JCheckBox cbParagraphs;
    private JLabel lblFont;
    private Component horizontalStrut;

    /**
     * Create the panel.
     * 
     * @param scale
     */
    public RecognitionPane(final Scale scale) {
        setLayout(new BorderLayout(0, 0));

        renderer = new RecognitionRenderer(this);
        this.scale = scale;

        JSplitPane splitPane = new JSplitPane();
        splitPane.setBackground(Color.WHITE);
        splitPane.setOneTouchExpandable(true);
        splitPane.setEnabled(false);
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);

        final JScrollPane spOriginal = new JScrollPane();
        spOriginal.getHorizontalScrollBar().setUnitIncrement(SCROLL_UNITS);
        spOriginal.getVerticalScrollBar().setUnitIncrement(SCROLL_UNITS);
        splitPane.setLeftComponent(spOriginal);

        lblOriginal = new JLabel();
        lblOriginal.setVerticalAlignment(SwingConstants.TOP);
        spOriginal.setViewportView(lblOriginal);

        lblOriginal_1 = new JLabel("Original");
        lblOriginal_1.setBorder(new EmptyBorder(0, 4, 0, 0));
        spOriginal.setColumnHeaderView(lblOriginal_1);

        final JScrollPane spHOCR = new JScrollPane();
        spHOCR.getHorizontalScrollBar().setUnitIncrement(SCROLL_UNITS);
        spHOCR.getVerticalScrollBar().setUnitIncrement(SCROLL_UNITS);
        splitPane.setRightComponent(spHOCR);

        lblRecognition = new JLabel();
        lblRecognition.setVerticalAlignment(SwingConstants.TOP);
        spHOCR.setViewportView(lblRecognition);

        lblRecognition_1 = new JLabel("Recognition Result");
        lblRecognition_1.setBorder(new EmptyBorder(0, 4, 0, 0));
        spHOCR.setColumnHeaderView(lblRecognition_1);

        final ItemListener checkBoxListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent ev) {
                if (cbWordBoxes == ev.getSource() && cbWordBoxes.isSelected()) {
                    cbSymbolBoxes.setSelected(false);
                } else if (cbSymbolBoxes == ev.getSource()
                        && cbSymbolBoxes.isSelected()) {
                    cbWordBoxes.setSelected(false);
                }

                render();
            }
        };

        JPanel panel_1 = new JPanel();
        panel_1.setBorder(new EmptyBorder(0, 4, 4, 4));
        panel_1.setBackground(Color.WHITE);
        add(panel_1, BorderLayout.NORTH);
        GridBagLayout gbl_panel_1 = new GridBagLayout();
        gbl_panel_1.columnWidths = new int[] { 83, 91, 89, 65, 55, 0, 0, 0, 0,
                28, 0, 0, 0,
                0 };
        gbl_panel_1.rowHeights = new int[] { 23, 0 };
        gbl_panel_1.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0,
                0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
        gbl_panel_1.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        panel_1.setLayout(gbl_panel_1);

        cbWordBoxes = new JCheckBox("Word boxes");
        cbWordBoxes.setBackground(Color.WHITE);
        GridBagConstraints gbc_cbWordBoxes = new GridBagConstraints();
        gbc_cbWordBoxes.anchor = GridBagConstraints.NORTHWEST;
        gbc_cbWordBoxes.insets = new Insets(0, 0, 0, 5);
        gbc_cbWordBoxes.gridx = 0;
        gbc_cbWordBoxes.gridy = 0;
        panel_1.add(cbWordBoxes, gbc_cbWordBoxes);
        cbWordBoxes.setSelected(true);
        cbWordBoxes.addItemListener(checkBoxListener);

        cbSymbolBoxes = new JCheckBox("Symbol boxes");
        cbSymbolBoxes.setBackground(Color.WHITE);
        GridBagConstraints gbc_cbSymbolBoxes = new GridBagConstraints();
        gbc_cbSymbolBoxes.anchor = GridBagConstraints.NORTHWEST;
        gbc_cbSymbolBoxes.insets = new Insets(0, 0, 0, 5);
        gbc_cbSymbolBoxes.gridx = 1;
        gbc_cbSymbolBoxes.gridy = 0;
        panel_1.add(cbSymbolBoxes, gbc_cbSymbolBoxes);
        cbSymbolBoxes.setSelected(false);
        cbSymbolBoxes.addItemListener(checkBoxListener);

        comboFont = new JComboBox<FontSelection>();
        comboFont.addItem(FontSelection.ANTIQUA);
        comboFont.addItem(FontSelection.FRAKTUR);

        cbLineNumbers = new JCheckBox("Line numbers");
        cbLineNumbers.setBackground(Color.WHITE);
        GridBagConstraints gbc_cbLineNumbers = new GridBagConstraints();
        gbc_cbLineNumbers.anchor = GridBagConstraints.NORTHWEST;
        gbc_cbLineNumbers.insets = new Insets(0, 0, 0, 5);
        gbc_cbLineNumbers.gridx = 2;
        gbc_cbLineNumbers.gridy = 0;
        panel_1.add(cbLineNumbers, gbc_cbLineNumbers);
        cbLineNumbers.setSelected(true);
        cbLineNumbers.addItemListener(checkBoxListener);

        cbBaselines = new JCheckBox("Baseline");
        cbBaselines.setBackground(Color.WHITE);
        GridBagConstraints gbc_cbBaselines = new GridBagConstraints();
        gbc_cbBaselines.anchor = GridBagConstraints.NORTHWEST;
        gbc_cbBaselines.insets = new Insets(0, 0, 0, 5);
        gbc_cbBaselines.gridx = 3;
        gbc_cbBaselines.gridy = 0;
        panel_1.add(cbBaselines, gbc_cbBaselines);
        cbBaselines.setSelected(false);
        cbBaselines.addItemListener(checkBoxListener);

        cbXLines = new JCheckBox("x-Line");
        cbXLines.setBackground(Color.WHITE);
        GridBagConstraints gbc_cbXLines = new GridBagConstraints();
        gbc_cbXLines.anchor = GridBagConstraints.NORTHWEST;
        gbc_cbXLines.insets = new Insets(0, 0, 0, 5);
        gbc_cbXLines.gridx = 4;
        gbc_cbXLines.gridy = 0;
        panel_1.add(cbXLines, gbc_cbXLines);
        cbXLines.setSelected(false);
        cbXLines.addItemListener(checkBoxListener);

        cbBlocks = new JCheckBox("Blocks");
        cbBlocks.setBackground(Color.WHITE);
        GridBagConstraints gbc_chckbxBlocks = new GridBagConstraints();
        gbc_chckbxBlocks.insets = new Insets(0, 0, 0, 5);
        gbc_chckbxBlocks.gridx = 5;
        gbc_chckbxBlocks.gridy = 0;
        panel_1.add(cbBlocks, gbc_chckbxBlocks);
        cbBlocks.addItemListener(checkBoxListener);

        cbParagraphs = new JCheckBox("Paragraphs");
        cbParagraphs.setBackground(Color.WHITE);
        GridBagConstraints gbc_chckbxParagraphs = new GridBagConstraints();
        gbc_chckbxParagraphs.insets = new Insets(0, 0, 0, 5);
        gbc_chckbxParagraphs.gridx = 6;
        gbc_chckbxParagraphs.gridy = 0;
        panel_1.add(cbParagraphs, gbc_chckbxParagraphs);
        cbParagraphs.addItemListener(checkBoxListener);

        horizontalStrut = Box.createHorizontalStrut(20);
        GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
        gbc_horizontalStrut.insets = new Insets(0, 0, 0, 5);
        gbc_horizontalStrut.gridx = 7;
        gbc_horizontalStrut.gridy = 0;
        panel_1.add(horizontalStrut, gbc_horizontalStrut);

        lblFont = new JLabel("Font:");
        GridBagConstraints gbc_lblFont = new GridBagConstraints();
        gbc_lblFont.insets = new Insets(0, 0, 0, 5);
        gbc_lblFont.anchor = GridBagConstraints.EAST;
        gbc_lblFont.gridx = 8;
        gbc_lblFont.gridy = 0;
        panel_1.add(lblFont, gbc_lblFont);

        comboFont.setBackground(Color.WHITE);
        GridBagConstraints gbc_comboFont = new GridBagConstraints();
        gbc_comboFont.insets = new Insets(0, 0, 0, 5);
        gbc_comboFont.anchor = GridBagConstraints.WEST;
        gbc_comboFont.gridx = 9;
        gbc_comboFont.gridy = 0;
        panel_1.add(comboFont, gbc_comboFont);

        final Insets btnMargin = new Insets(2, 4, 2, 4);

        btZoomOut = new JButton(new ImageIcon(
                RecognitionPane.class.getResource("/icons/zoom_out.png")));
        btZoomOut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                if (scale.hasPrevious()) {
                    renderer.render(getPageModel(), scale.previous());
                }

                if (!scale.hasPrevious()) {
                    btZoomOut.setEnabled(false);
                }

                btZoomIn.setEnabled(true);
            }
        });
        btZoomOut.setMargin(btnMargin);
        btZoomOut.setToolTipText("Zoom out");
        btZoomOut.setBackground(Color.WHITE);
        GridBagConstraints gbc_btZoomOut = new GridBagConstraints();
        gbc_btZoomOut.insets = new Insets(0, 0, 0, 5);
        gbc_btZoomOut.gridx = 11;
        gbc_btZoomOut.gridy = 0;
        panel_1.add(btZoomOut, gbc_btZoomOut);

        btZoomIn = new JButton(new ImageIcon(
                RecognitionPane.class.getResource("/icons/zoom_in.png")));
        btZoomIn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                if (scale.hasNext()) {
                    renderer.render(getPageModel(), scale.next());
                }

                if (!scale.hasPrevious()) {
                    btZoomIn.setEnabled(false);
                }

                btZoomOut.setEnabled(true);
            }
        });
        btZoomIn.setMargin(btnMargin);
        btZoomIn.setToolTipText("Zoom in");
        btZoomIn.setBackground(Color.WHITE);
        GridBagConstraints gbc_btZoomIn = new GridBagConstraints();
        gbc_btZoomIn.gridx = 12;
        gbc_btZoomIn.gridy = 0;
        panel_1.add(btZoomIn, gbc_btZoomIn);
        // comboFont.setModel(new DefaultComboBoxModel<String>(new String[] {
        // "Antiqua", "Fraktur" }));
        comboFont.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                render();
            }
        });

        spOriginal.getViewport().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                spHOCR.getHorizontalScrollBar().setModel(
                        spOriginal.getHorizontalScrollBar().getModel());
                spHOCR.getVerticalScrollBar().setModel(
                        spOriginal.getVerticalScrollBar().getModel());
            }
        });

        spHOCR.getViewport().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                spOriginal.getHorizontalScrollBar().setModel(
                        spHOCR.getHorizontalScrollBar().getModel());
                spOriginal.getVerticalScrollBar().setModel(
                        spHOCR.getVerticalScrollBar().getModel());
            }
        });
    }

    public Optional<PageModel> getPageModel() {
        return model;
    }

    public void setPageModel(Optional<PageModel> page) {
        model = page;

        render();
    }

    private void render() {
        delayer.purge();

        delayer.schedule(new TimerTask() {
            @Override
            public void run() {
                renderer.render(model, scale.current());
            }
        }, 200);
    }

    @Override
    public Component asComponent() {
        return this;
    }

    @Override
    public void freeResources() {
        lblOriginal.setIcon(null);
        lblRecognition.setIcon(null);
    }

    @Override
    public Optional<BoxFileModel> getBoxFileModel() {
        if (model.isPresent()) {
            return Optional.of(model.get().toBoxFileModel());
        } else {
            return Optional.absent();
        }
    }

    public JLabel getCanvasOriginal() {
        return lblOriginal;
    }

    public JLabel getCanvasRecognition() {
        return lblRecognition;
    }

    public JComboBox<FontSelection> getComboFont() {
        return comboFont;
    }

    public JCheckBox getWordBoxes() {
        return cbWordBoxes;
    }

    public JCheckBox getSymbolBoxes() {
        return cbSymbolBoxes;
    }

    public JCheckBox getLineNumbers() {
        return cbLineNumbers;
    }

    public JCheckBox getBaselines() {
        return cbBaselines;
    }

    public JCheckBox getXLines() {
        return cbXLines;
    }

    public JCheckBox getBlocks() {
        return cbBlocks;
    }

    public JCheckBox getParagraphs() {
        return cbParagraphs;
    }
}
