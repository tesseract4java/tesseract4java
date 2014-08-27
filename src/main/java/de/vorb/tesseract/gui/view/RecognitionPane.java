package de.vorb.tesseract.gui.view;

import static de.vorb.tesseract.gui.model.Scale.scaled;
import static de.vorb.tesseract.gui.model.Scale.unscaled;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.common.base.Optional;

import de.vorb.tesseract.gui.event.ComparatorSettingsChangeListener;
import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.gui.model.Scale;
import de.vorb.tesseract.util.Baseline;
import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.FontAttributes;
import de.vorb.tesseract.util.Line;
import de.vorb.tesseract.util.Page;
import de.vorb.tesseract.util.Point;
import de.vorb.tesseract.util.Symbol;
import de.vorb.tesseract.util.Word;

public class RecognitionPane extends JPanel implements
        ComparatorSettingsChangeListener,
        PageModelComponent {
    private static final long serialVersionUID = 1L;

    private static final int SCROLL_UNITS = 12;

    // Fallback fonts
    private static final Font FONT_FALLBACK_NORMAL = new Font("SansSerif",
            Font.PLAIN, 12);
    private static final Font FONT_FALLBACK_ITALIC = new Font("SansSerif",
            Font.ITALIC, 12);
    private static final Font FONT_FALLBACK_BOLD = new Font("SansSerif",
            Font.BOLD, 12);
    private static final Font FONT_FALLBACK_BOLD_ITALIC = new Font("SansSerif",
            Font.BOLD | Font.ITALIC, 12);

    private static final Font FONT_ANTIQUA_NORMAL;
    private static final Font FONT_ANTIQUA_ITALIC;
    private static final Font FONT_ANTIQUA_BOLD;
    private static final Font FONT_ANTIQUA_BOLD_ITALIC;

    private static final Font FONT_FRAKTUR_NORMAL;
    private static final Font FONT_FRAKTUR_BOLD;

    static {
        // load fonts

        // ---------------------------------------------------------------------
        // ANTIQUA:
        // ---------------------------------------------------------------------

        // normal
        Font loaded = FONT_FALLBACK_NORMAL;
        try {
            loaded = Font.createFont(
                    Font.TRUETYPE_FONT,
                    RecognitionPane.class.getResourceAsStream("/fonts/RobotoCondensed-Regular.ttf"));
        } catch (FontFormatException | IOException e) {
            System.err.println("Could not load normal font.");
            e.printStackTrace();
        }
        FONT_ANTIQUA_NORMAL = loaded;

        // bold
        loaded = FONT_FALLBACK_ITALIC;
        try {
            loaded = Font.createFont(
                    Font.TRUETYPE_FONT,
                    RecognitionPane.class.getResourceAsStream("/fonts/RobotoCondensed-Italic.ttf"));
        } catch (FontFormatException | IOException e) {
            System.err.println("Could not load italic font.");
        }
        FONT_ANTIQUA_ITALIC = loaded;

        // bold
        loaded = FONT_FALLBACK_BOLD;
        try {
            loaded = Font.createFont(
                    Font.TRUETYPE_FONT,
                    RecognitionPane.class.getResourceAsStream("/fonts/RobotoCondensed-Bold.ttf"));
        } catch (FontFormatException | IOException e) {
            System.err.println("Could not load bold font.");
        }
        FONT_ANTIQUA_BOLD = loaded;

        // bold & italic
        loaded = FONT_FALLBACK_BOLD_ITALIC;
        try {
            loaded = Font.createFont(
                    Font.TRUETYPE_FONT,
                    RecognitionPane.class.getResourceAsStream("/fonts/RobotoCondensed-BoldItalic.ttf"));
        } catch (FontFormatException | IOException e) {
            System.err.println("Could not load bold italic font.");
        }
        FONT_ANTIQUA_BOLD_ITALIC = loaded;

        // ---------------------------------------------------------------------
        // FRAKTUR:
        // ---------------------------------------------------------------------

        // normal
        loaded = FONT_FALLBACK_NORMAL;
        try {
            loaded = Font.createFont(
                    Font.TRUETYPE_FONT,
                    RecognitionPane.class.getResourceAsStream("/fonts/NeueFraktur.ttf"));
        } catch (FontFormatException | IOException e) {
            System.err.println("Could not load Fraktur font.");
        }
        FONT_FRAKTUR_NORMAL = loaded;

        // bold
        FONT_FRAKTUR_BOLD = loaded;

        // currently there is no bold Fraktur font
    }

    private final JTextField tfSelection;
    private final JTextField tfConfidence;
    private final JCheckBox cbCorrect;

    private final JLabel lblOriginal;
    private final JLabel lblHOCR;
    private final JSlider zoomSlider;

    private final JCheckBox cbWordBoxes;
    private final JCheckBox cbSymbolBoxes;
    private final JCheckBox cbLineNumbers;
    private final JCheckBox cbBaseline;
    private final JCheckBox cbXLine;
    private final JComboBox<String> comboBox;

    private final LinkedList<ComparatorSettingsChangeListener> zoomChangeListeners = new LinkedList<ComparatorSettingsChangeListener>();

    private Optional<PageModel> model = Optional.absent();

    // private PageModel model = new PageModel(new Page(Paths.get(""), 1, 1,
    // 300,
    // new LinkedList<Line>()), new BufferedImage(1, 1,
    // BufferedImage.TYPE_BYTE_GRAY), new BufferedImage(1, 1,
    // BufferedImage.TYPE_BYTE_BINARY));

    /**
     * Create the panel.
     * 
     * @param scale
     */
    public RecognitionPane(Scale scale) {
        setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        add(panel, BorderLayout.SOUTH);

        JLabel lblSelectedWord = new JLabel("Selected word:");
        panel.add(lblSelectedWord);

        tfSelection = new JTextField();
        tfSelection.setBackground(Color.WHITE);
        tfSelection.setEditable(false);
        panel.add(tfSelection);
        tfSelection.setColumns(20);

        JLabel lblConfidence = new JLabel("Confidence:");
        panel.add(lblConfidence);

        tfConfidence = new JTextField();
        tfConfidence.setBackground(Color.WHITE);
        tfConfidence.setEditable(false);
        panel.add(tfConfidence);
        tfConfidence.setColumns(8);

        cbCorrect = new JCheckBox("Correct?");
        cbCorrect.setBackground(Color.WHITE);
        cbCorrect.setToolTipText("Is the selected word correct?");
        panel.add(cbCorrect);

        horizontalStrut = javax.swing.Box.createHorizontalStrut(20);
        panel.add(horizontalStrut);

        lblFontAttributes = new JLabel("Font attributes:");
        panel.add(lblFontAttributes);

        tfFontAttributes = new JTextField();
        tfFontAttributes.setBackground(Color.WHITE);
        tfFontAttributes.setEditable(false);
        panel.add(tfFontAttributes);
        tfFontAttributes.setColumns(20);

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

        MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!getPageModel().isPresent()) {
                    // ignore clicks if no model is present
                    return;
                }

                final PageModel model = getPageModel().get();
                final Page page = model.getPage();

                final float factor = getScaleFactor();

                final Point scaled = new Point(e.getPoint());
                final Point unscaled = new Point(
                        unscaled(scaled.getX(), factor),
                        unscaled(scaled.getY(), factor));

                int lineIndex = 0;
                int wordIndex = 0;

                // true if clicked a box (word)
                boolean hit = false;

                for (Line line : page.getLines()) {
                    for (Word word : line.getWords()) {

                        // word.setSelected(false);

                        if (word.getBoundingBox().contains(unscaled)) {
                            hit = true;

                            if (e.getClickCount() == 2 || e.isControlDown()) {
                                word.setCorrect(!word.isCorrect());
                            }

                            tfSelection.setText(word.getText());
                            final String text = word.getText();
                            final StringBuilder tooltip = new StringBuilder();
                            tooltip.append("[ ");
                            for (char c : text.toCharArray()) {
                                tooltip.append((int) c);
                                tooltip.append(' ');
                            }
                            tooltip.append(']');
                            tfSelection.setToolTipText(tooltip.toString());
                            tfConfidence.setText(String.valueOf(word.getConfidence()));
                            cbCorrect.setSelected(word.isCorrect());

                            final FontAttributes fa = word.getFontAttributes();
                            tfFontAttributes.setText(fa.toString());

                            // word.setSelected(true);

                            // model.setSelectedLineIndex(lineIndex);
                            // model.setSelectedWordIndex(wordIndex);
                        }

                        wordIndex++;
                    }

                    wordIndex = 0;
                    lineIndex++;
                }

                if (!hit) {
                    tfSelection.setText("");
                    tfConfidence.setText("");
                    cbCorrect.setSelected(false);

                    // if (model.hasSelected()) {
                    // model.getSelected().setSelected(false);
                    // model.setSelectedLineIndex(-1);
                    // model.setSelectedWordIndex(-1);
                    // }
                }

                render();
            }
        };

        lblOriginal = new JLabel();
        lblOriginal.addMouseListener(mouseListener);
        lblOriginal.setVerticalAlignment(SwingConstants.TOP);
        spOriginal.setViewportView(lblOriginal);

        lblOriginal_1 = new JLabel("Original");
        spOriginal.setColumnHeaderView(lblOriginal_1);

        final JScrollPane spHOCR = new JScrollPane();
        spHOCR.getHorizontalScrollBar().setUnitIncrement(SCROLL_UNITS);
        spHOCR.getVerticalScrollBar().setUnitIncrement(SCROLL_UNITS);
        splitPane.setRightComponent(spHOCR);

        lblHOCR = new JLabel();
        lblHOCR.addMouseListener(mouseListener);
        lblHOCR.setVerticalAlignment(SwingConstants.TOP);
        spHOCR.setViewportView(lblHOCR);

        lblRecognized = new JLabel("Recognized");
        spHOCR.setColumnHeaderView(lblRecognized);

        final ChangeListener checkBoxListener = new ChangeListener() {
            public void stateChanged(ChangeEvent ev) {
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
        panel_1.setBackground(Color.WHITE);
        add(panel_1, BorderLayout.NORTH);
        panel_1.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 5));

        cbWordBoxes = new JCheckBox("Word boxes");
        cbWordBoxes.setBackground(Color.WHITE);
        panel_1.add(cbWordBoxes);
        cbWordBoxes.setSelected(true);
        cbWordBoxes.addChangeListener(checkBoxListener);

        cbSymbolBoxes = new JCheckBox("Symbol boxes");
        cbSymbolBoxes.setBackground(Color.WHITE);
        panel_1.add(cbSymbolBoxes);
        cbSymbolBoxes.setSelected(false);
        cbSymbolBoxes.addChangeListener(checkBoxListener);

        cbLineNumbers = new JCheckBox("Line numbers");
        cbLineNumbers.setBackground(Color.WHITE);
        panel_1.add(cbLineNumbers);
        cbLineNumbers.setSelected(true);
        cbLineNumbers.addChangeListener(checkBoxListener);

        cbBaseline = new JCheckBox("Baseline");
        cbBaseline.setBackground(Color.WHITE);
        panel_1.add(cbBaseline);
        cbBaseline.setSelected(false);
        cbBaseline.addChangeListener(checkBoxListener);

        cbXLine = new JCheckBox("x-Line");
        cbXLine.setBackground(Color.WHITE);
        panel_1.add(cbXLine);
        cbXLine.setSelected(false);
        cbXLine.addChangeListener(checkBoxListener);

        comboBox = new JComboBox<String>();
        comboBox.setBackground(Color.WHITE);
        panel_1.add(comboBox);
        comboBox.setModel(new DefaultComboBoxModel<String>(new String[] {
                "Antiqua", "Fraktur" }));
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                render();
            }
        });

        zoomSlider = new JSlider();
        zoomSlider.setBackground(Color.WHITE);
        panel_1.add(zoomSlider);
        zoomSlider.setMinimum(1);
        zoomSlider.setPreferredSize(new Dimension(160, 20));
        zoomSlider.setSnapToTicks(true);
        zoomSlider.setMajorTickSpacing(1);
        zoomSlider.setValue(5);
        zoomSlider.setMaximum(9);
        zoomSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ev) {
                zoomChanged();
            }
        });

        addZoomChangeListener(this);

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

        settingsChanged();
    }

    public void addZoomChangeListener(ComparatorSettingsChangeListener listener) {
        zoomChangeListeners.add(listener);
    }

    public void removeZoomChangeListener(
            ComparatorSettingsChangeListener listener) {
        zoomChangeListeners.remove(listener);
    }

    private void zoomChanged() {
        for (ComparatorSettingsChangeListener l : zoomChangeListeners) {
            l.settingsChanged();
        }
    }

    private static class ImagePair {
        final BufferedImage a;
        final BufferedImage b;

        ImagePair(BufferedImage a, BufferedImage b) {
            this.a = a;
            this.b = b;
        }
    }

    private SwingWorker<ImagePair, Void> renderer = null;
    private Component horizontalStrut;
    private JLabel lblFontAttributes;
    private JTextField tfFontAttributes;
    private JLabel lblOriginal_1;
    private JLabel lblRecognized;

    public void settingsChanged() {
        render();
    }

    private void render() {
        if (renderer != null && !renderer.isDone()) {
            renderer.cancel(true);
        }

        if (!getPageModel().isPresent()) {
            // don't render anything if no model is present
            return;
        }

        final int zoom = zoomSlider.getValue();
        final float factor = getScaleFactor();

        final Page page = getPageModel().get().getPage();
        final List<Line> lines = page.getLines();
        final BufferedImage normal = getPageModel().get().getImageModel()
                .getPreprocessedImage();

        // font for line numbers
        final Font lineNumberFont = new Font("Dialog", Font.PLAIN, 12);

        // is Fraktur selected?
        final boolean useFraktur = "Fraktur".equals(comboBox.getSelectedItem());

        // set the base fonts
        final Font baseFontNormal;
        final Font baseFontItalic;
        final Font baseFontBold;
        final Font baseFontBoldItalic;
        if (useFraktur) {
            baseFontNormal = FONT_FRAKTUR_NORMAL;
            baseFontItalic = FONT_FRAKTUR_NORMAL;
            baseFontBold = FONT_FRAKTUR_BOLD;
            baseFontBoldItalic = FONT_FRAKTUR_BOLD;
        } else {
            baseFontNormal = FONT_ANTIQUA_NORMAL;
            baseFontItalic = FONT_ANTIQUA_ITALIC;
            baseFontBold = FONT_ANTIQUA_BOLD;
            baseFontBoldItalic = FONT_ANTIQUA_BOLD_ITALIC;
        }

        final int width = page.getWidth();
        final int height = page.getHeight();

        // calc scaled width and height
        final int scaledWidth = scaled(width, factor);
        final int scaledHeight = scaled(height, factor);

        final boolean showWordBoxes = cbWordBoxes.isSelected();
        final boolean showSymbolBoxes = cbSymbolBoxes.isSelected();
        final boolean showLineNumbers = cbLineNumbers.isSelected();
        final boolean showBaselines = cbBaseline.isSelected();
        final boolean showXLines = cbXLine.isSelected();

        renderer = new SwingWorker<ImagePair, Void>() {
            private BufferedImage scanImg, hocrImg;
            private Graphics2D scanGfx, hocrGfx;

            private void drawLineNumber(final Line line, final int lineNumber,
                    final Color color) {

                final Box box = line.getBoundingBox();

                final String num = String.valueOf(lineNumber);
                final int x = scaled(20, factor);
                final int y = scaled(box.getY() + box.getHeight()
                        - line.getBaseline().getYOffset(), factor);

                scanGfx.setFont(lineNumberFont);
                scanGfx.setColor(color);
                scanGfx.drawString(num, x, y);

                hocrGfx.setFont(lineNumberFont);
                hocrGfx.setColor(color);
                hocrGfx.drawString(num, x, y);
            }

            private void drawWord(final Line line, final Word word) {
                // bounding box
                final Box box = word.getBoundingBox();

                // baseline
                final Baseline bl = word.getBaseline();

                // font attributes
                final FontAttributes fa = word.getFontAttributes();

                // scaled font size
                final float scFontSize = scaled(fa.getSize(), factor);

                // bold?
                final boolean bold = fa.isBold();
                // italic?
                final boolean italic = fa.isItalic();

                // selected?
                // final boolean isSelected = word.isSelected();

                // coordinates
                final int bX = box.getX(), bY = box.getY();
                final int bW = box.getWidth(), bH = box.getHeight();

                // scaled coordinates
                final int scX = scaled(bX, factor);
                final int scY = scaled(bY, factor);
                final int scW = scaled(bW, factor);
                final int scH = scaled(bH, factor);

                // text coordinates
                final int tx = scX;
                final int ty = scaled(
                        bY + bH - word.getBaseline().getYOffset(),
                        factor);

                // set font
                final Font font;
                if (italic && bold) {
                    font = baseFontBoldItalic.deriveFont(scFontSize);
                } else if (italic) {
                    font = baseFontItalic.deriveFont(scFontSize);
                } else if (bold) {
                    font = baseFontBold.deriveFont(scFontSize);
                } else {
                    font = baseFontNormal.deriveFont(scFontSize);
                }

                hocrGfx.setFont(font);

                if (showWordBoxes || showSymbolBoxes) {
                    // if (isSelected) {
                    // scanGfx.setStroke(STROKE_SELECTION);
                    // hocrGfx.setStroke(STROKE_SELECTION);
                    // }

                    if (showWordBoxes) {
                        if (word.isCorrect()) {
                            scanGfx.setColor(Colors.CORRECT);
                            hocrGfx.setColor(Colors.CORRECT);
                        } else {
                            scanGfx.setColor(Colors.INCORRECT);
                            hocrGfx.setColor(Colors.INCORRECT);
                        }

                        scanGfx.drawRect(scX, scY, scW, scH);
                        hocrGfx.drawRect(scX, scY, scW, scH);
                    } else if (showSymbolBoxes) {
                        for (final Symbol sym : word.getSymbols()) {
                            if (sym.getConfidence() < 0.6)
                                continue;

                            // symbol bounding box
                            final Box sbox = sym.getBoundingBox();

                            // symbol text
                            final String stext = sym.getText();

                            // coordinates
                            final int sbX = sbox.getX();
                            final int sbY = sbox.getY();
                            final int sbW = sbox.getWidth();
                            final int sbH = sbox.getHeight();

                            // scaled coordinates
                            final int ssbX = scaled(sbX, factor);
                            final int ssbY = scaled(sbY, factor);
                            final int ssbW = scaled(sbW, factor);
                            final int ssbH = scaled(sbH, factor);

                            if (word.isCorrect()) {
                                scanGfx.setColor(Colors.CORRECT);
                                hocrGfx.setColor(Colors.CORRECT);
                            } else {
                                scanGfx.setColor(Colors.INCORRECT);
                                hocrGfx.setColor(Colors.INCORRECT);
                            }

                            scanGfx.drawRect(ssbX, ssbY, ssbW, ssbH);
                            hocrGfx.drawRect(ssbX, ssbY, ssbW, ssbH);

                            hocrGfx.setColor(Colors.TEXT);

                            hocrGfx.drawString(
                                    stext,
                                    ssbX,
                                    scaled(box.getY() + box.getHeight()
                                            - word.getBaseline().getYOffset(),
                                            factor));
                        }
                    }

                    // if (isSelected) {
                    // scanGfx.setStroke(STROKE_NORMAL);
                    // hocrGfx.setStroke(STROKE_NORMAL);
                    // }
                }

                if (!showSymbolBoxes) {
                    hocrGfx.setColor(Colors.TEXT);

                    // only draw the string
                    hocrGfx.drawString(word.getText(), tx, ty);
                }

                if (showBaselines) {
                    final int x2 = bX + bW;
                    final int y1 = bY + bH - bl.getYOffset();
                    final int y2 = Math.round(y1 + bW * bl.getSlope());

                    scanGfx.setColor(Colors.BASELINE);
                    scanGfx.drawLine(scaled(bX, factor), scaled(y1, factor),
                            scaled(x2, factor), scaled(y2, factor));
                    hocrGfx.setColor(Colors.BASELINE);
                    hocrGfx.drawLine(scaled(bX, factor), scaled(y1, factor),
                            scaled(x2, factor), scaled(y2, factor));
                }
            }

            @Override
            protected ImagePair doInBackground() throws Exception {
                // init attributes
                scanImg = new BufferedImage(scaledWidth, scaledHeight,
                        BufferedImage.TYPE_INT_RGB);
                scanGfx = scanImg.createGraphics();

                hocrImg = new BufferedImage(scaledWidth, scaledHeight,
                        BufferedImage.TYPE_INT_RGB);
                hocrGfx = hocrImg.createGraphics();

                scanGfx.drawImage(normal, 0, 0, scaledWidth, scaledHeight, 0,
                        0, width - 1, height - 1, null);

                hocrGfx.setColor(Color.WHITE);
                hocrGfx.fillRect(0, 0, scaledWidth, scaledHeight);

                // stays the same for all lines
                scanGfx.setFont(lineNumberFont);

                int lineNumber = 1;
                for (Line line : lines) {
                    if (zoom >= 1 && showLineNumbers) {
                        drawLineNumber(line, lineNumber, Colors.LINE_NUMBER);
                    }

                    hocrGfx.setRenderingHint(
                            RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

                    for (final Word word : line.getWords()) {
                        drawWord(line, word);
                    }

                    lineNumber++;
                }

                return new ImagePair(scanImg, hocrImg);
            }

            @Override
            protected void done() {
                try {
                    final ImagePair pair = get();

                    lblOriginal.setIcon(new ImageIcon(pair.a));
                    lblHOCR.setIcon(new ImageIcon(pair.b));
                } catch (Exception e) {
                }
            }
        };

        renderer.execute();
    }

    private float getScaleFactor() {
        return (zoomSlider.getValue() + 1) * 0.1f;
    }

    private void setAscendersEnabled(boolean enabled) {
        if (!enabled) {
            cbLineNumbers.setSelected(false);
            cbBaseline.setSelected(false);
            cbXLine.setSelected(false);
        }

        cbLineNumbers.setEnabled(enabled);
        cbBaseline.setEnabled(enabled);
        cbXLine.setEnabled(enabled);
    }

    @Override
    public Component asComponent() {
        return this;
    }

    @Override
    public void freeResources() {
        lblOriginal.setIcon(null);
        lblHOCR.setIcon(null);
    }
}
