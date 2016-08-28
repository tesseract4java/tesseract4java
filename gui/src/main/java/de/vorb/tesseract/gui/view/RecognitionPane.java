package de.vorb.tesseract.gui.view;

import de.vorb.tesseract.gui.model.BoxFileModel;
import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.gui.model.Scale;
import de.vorb.tesseract.gui.view.renderer.RecognitionRenderer;
import de.vorb.tesseract.util.AlternativeChoice;
import de.vorb.tesseract.util.FontAttributes;
import de.vorb.tesseract.util.Symbol;
import de.vorb.tesseract.util.Word;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class RecognitionPane extends JPanel implements PageModelComponent {
    private static final long serialVersionUID = 1L;

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

    private Optional<PageModel> model = Optional.empty();

    private final Timer delayer = new Timer(true);
    private JButton btZoomOut;
    private JButton btZoomIn;
    private JCheckBox cbBlocks;
    private JCheckBox cbParagraphs;
    private JLabel lblFont;
    private Component horizontalStrut;
    private JPopupMenu popupMenu;

    /**
     * Create the panel.
     *
     * @param scale
     */
    public RecognitionPane(final Scale scale, final String renderingFont) {
        setLayout(new BorderLayout(0, 0));

        renderer = new RecognitionRenderer(this, renderingFont);
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

        final JPopupMenu popupMenu = new JPopupMenu();
        final JMenuItem glyph = new JMenuItem("Show in Box Editor");
        popupMenu.add(glyph);

        final MouseInputAdapter adapter = new MouseInputAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }

            private void showPopup(MouseEvent e) {
                if (!e.isPopupTrigger()) {
                    return;
                }

                if (cbSymbolBoxes.isSelected()) {
                    final Optional<Symbol> symbol = findSymbolAt(e.getX(),
                            e.getY());

                    if (symbol.isPresent()) {
                        final Symbol s = symbol.get();
                        popupMenu.removeAll();
                        popupMenu.add(String.format(
                                "Alternative choices for symbol \"%s\" (confidence = %.2f%%):",
                                s.getText(), s.getConfidence()));
                        popupMenu.add(new JSeparator());

                        for (AlternativeChoice alt : s.getAlternatives()) {
                            popupMenu.add(String.format(
                                    "- \"%s\" (confidence = %.2f%%)",
                                    alt.getText(),
                                    alt.getConfidence()));
                        }

                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                } else if (cbWordBoxes.isSelected()) {
                    final Optional<Word> word = findWordAt(e.getX(), e.getY());

                    if (word.isPresent()) {
                        final Word w = word.get();

                        popupMenu.removeAll();
                        popupMenu.add(String.format("Word confidence = %.2f%%",
                                w.getConfidence()));
                        popupMenu.add(new JSeparator());
                        final FontAttributes fa = w.getFontAttributes();
                        popupMenu.add(String.format("Font ID = %d",
                                fa.getFontID()));
                        popupMenu.add(String.format("Font size = %dpx",
                                fa.getSize()));

                        if (fa.isBold()) {
                            popupMenu.add("Bold");
                        }
                        if (fa.isItalic()) {
                            popupMenu.add("Italic");
                        }
                        if (fa.isSerif()) {
                            popupMenu.add("Serif");
                        }
                        if (fa.isMonospace()) {
                            popupMenu.add("Monospace");
                        }
                        if (fa.isSmallCaps()) {
                            popupMenu.add("Small Caps");
                        }
                        if (fa.isUnderlined()) {
                            popupMenu.add("Underlined");
                        }

                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        };

        lblRecognition.addMouseListener(adapter);
        lblOriginal.addMouseListener(adapter);

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
        gbl_panel_1.columnWidths = new int[]{83, 91, 89, 65, 0, 0, 0, 0,
                28, 0, 0, 0,
                0};
        gbl_panel_1.rowHeights = new int[]{23, 0};
        gbl_panel_1.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0,
                0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_panel_1.rowWeights = new double[]{0.0, Double.MIN_VALUE};
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

        cbBlocks = new JCheckBox("Blocks");
        cbBlocks.setBackground(Color.WHITE);
        GridBagConstraints gbc_chckbxBlocks = new GridBagConstraints();
        gbc_chckbxBlocks.insets = new Insets(0, 0, 0, 5);
        gbc_chckbxBlocks.gridx = 4;
        gbc_chckbxBlocks.gridy = 0;
        panel_1.add(cbBlocks, gbc_chckbxBlocks);
        cbBlocks.addItemListener(checkBoxListener);

        cbParagraphs = new JCheckBox("Paragraphs");
        cbParagraphs.setBackground(Color.WHITE);
        GridBagConstraints gbc_chckbxParagraphs = new GridBagConstraints();
        gbc_chckbxParagraphs.insets = new Insets(0, 0, 0, 5);
        gbc_chckbxParagraphs.gridx = 5;
        gbc_chckbxParagraphs.gridy = 0;
        panel_1.add(cbParagraphs, gbc_chckbxParagraphs);
        cbParagraphs.addItemListener(checkBoxListener);

        horizontalStrut = Box.createHorizontalStrut(20);
        GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
        gbc_horizontalStrut.insets = new Insets(0, 0, 0, 5);
        gbc_horizontalStrut.gridx = 6;
        gbc_horizontalStrut.gridy = 0;
        panel_1.add(horizontalStrut, gbc_horizontalStrut);

        final Insets btnMargin = new Insets(2, 4, 2, 4);

        btZoomOut = new JButton(new ImageIcon(
                RecognitionPane.class.getResource("/icons/zoom_out.png")));
        btZoomOut.addActionListener(ev -> {
            if (scale.hasPrevious()) {
                renderer.render(getPageModel(), scale.previous());
            }

            if (!scale.hasPrevious()) {
                btZoomOut.setEnabled(false);
            }

            btZoomIn.setEnabled(true);
        });
        btZoomOut.setMargin(btnMargin);
        btZoomOut.setToolTipText("Zoom out");
        btZoomOut.setBackground(Color.WHITE);
        GridBagConstraints gbc_btZoomOut = new GridBagConstraints();
        gbc_btZoomOut.insets = new Insets(0, 0, 0, 5);
        gbc_btZoomOut.gridx = 10;
        gbc_btZoomOut.gridy = 0;
        panel_1.add(btZoomOut, gbc_btZoomOut);

        btZoomIn = new JButton(new ImageIcon(
                RecognitionPane.class.getResource("/icons/zoom_in.png")));
        btZoomIn.addActionListener(ev -> {
            if (scale.hasNext()) {
                renderer.render(getPageModel(), scale.next());
            }

            if (!scale.hasPrevious()) {
                btZoomIn.setEnabled(false);
            }

            btZoomOut.setEnabled(true);
        });
        btZoomIn.setMargin(btnMargin);
        btZoomIn.setToolTipText("Zoom in");
        btZoomIn.setBackground(Color.WHITE);
        GridBagConstraints gbc_btZoomIn = new GridBagConstraints();
        gbc_btZoomIn.gridx = 11;
        gbc_btZoomIn.gridy = 0;
        panel_1.add(btZoomIn, gbc_btZoomIn);
        // comboFont.setModel(new DefaultComboBoxModel<String>(new String[] {
        // "Antiqua", "Fraktur" }));

        spOriginal.getViewport().addChangeListener(e -> {
            spHOCR.getHorizontalScrollBar().setModel(
                    spOriginal.getHorizontalScrollBar().getModel());
            spHOCR.getVerticalScrollBar().setModel(
                    spOriginal.getVerticalScrollBar().getModel());
        });

        spHOCR.getViewport().addChangeListener(e -> {
            spOriginal.getHorizontalScrollBar().setModel(
                    spHOCR.getHorizontalScrollBar().getModel());
            spOriginal.getVerticalScrollBar().setModel(
                    spHOCR.getVerticalScrollBar().getModel());
        });
    }

    private Optional<Symbol> findSymbolAt(int x, int y) {
        if (!model.isPresent()) {
            return Optional.empty();
        }

        final Iterator<Symbol> symbolIt =
                model.get().getPage().symbolIterator();
        while (symbolIt.hasNext()) {
            final Symbol symb = symbolIt.next();
            final de.vorb.tesseract.util.Box bbox = symb.getBoundingBox();

            final int scaledX0 = Scale.scaled(bbox.getX(), scale.current());
            final int scaledY0 = Scale.scaled(bbox.getY(), scale.current());
            final int scaledX1 = scaledX0
                    + Scale.scaled(bbox.getWidth(), scale.current());
            final int scaledY1 = scaledY0
                    + Scale.scaled(bbox.getHeight(), scale.current());

            if (x >= scaledX0 && x <= scaledX1 && y >= scaledY0
                    && y <= scaledY1) {
                return Optional.of(symb);
            }
        }

        return Optional.empty();
    }

    private Optional<Word> findWordAt(int x, int y) {
        if (!model.isPresent()) {
            return Optional.empty();
        }

        final Iterator<Word> wordIt =
                model.get().getPage().wordIterator();

        while (wordIt.hasNext()) {
            final Word word = wordIt.next();
            final de.vorb.tesseract.util.Box bbox = word.getBoundingBox();

            final int scaledX0 = Scale.scaled(bbox.getX(), scale.current());
            final int scaledY0 = Scale.scaled(bbox.getY(), scale.current());
            final int scaledX1 = scaledX0
                    + Scale.scaled(bbox.getWidth(), scale.current());
            final int scaledY1 = scaledY0
                    + Scale.scaled(bbox.getHeight(), scale.current());

            if (x >= scaledX0 && x <= scaledX1 && y >= scaledY0
                    && y <= scaledY1) {
                return Optional.of(word);
            }
        }

        return Optional.empty();
    }

    public Optional<PageModel> getPageModel() {
        return model;
    }

    public void setPageModel(Optional<PageModel> page) {
        model = page;

        render();
    }

    public void render() {
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
            return Optional.empty();
        }
    }

    public JLabel getCanvasOriginal() {
        return lblOriginal;
    }

    public JLabel getCanvasRecognition() {
        return lblRecognition;
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

    public JCheckBox getBlocks() {
        return cbBlocks;
    }

    public JCheckBox getParagraphs() {
        return cbParagraphs;
    }

    public void setRenderingFont(String renderingFont) {
        renderer.setRenderingFont(renderingFont);
    }
}
