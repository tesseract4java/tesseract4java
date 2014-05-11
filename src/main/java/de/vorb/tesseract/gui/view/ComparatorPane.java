package de.vorb.tesseract.gui.view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.vorb.tesseract.gui.event.DefaultMouseListener;
import de.vorb.tesseract.gui.event.ZoomChangeListener;
import de.vorb.tesseract.util.Baseline;
import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.FontAttributes;
import de.vorb.tesseract.util.Line;
import de.vorb.tesseract.util.Page;
import de.vorb.tesseract.util.Point;
import de.vorb.tesseract.util.Symbol;
import de.vorb.tesseract.util.Word;

public class ComparatorPane extends JPanel implements ZoomChangeListener {
    private static final long serialVersionUID = 1L;

    private static final int SCROLL_UNITS = 12;

    private static final Color COLOR_CORRECT = new Color(0xFF66CC00);
    private static final Color COLOR_INCORRECT = Color.RED;
    private static final Color COLOR_BASELINE = Color.BLUE;
    private static final Color COLOR_TEXT = Color.BLACK;
    private static final Color COLOR_LINE_NUMBER = Color.GRAY;

    private static final Stroke STROKE_NORMAL = new BasicStroke(1);
    private static final Stroke STROKE_SELECTION = new BasicStroke(3);

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
                    ComparatorPane.class.getResourceAsStream("/RobotoCondensed-Regular.ttf"));
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
                    ComparatorPane.class.getResourceAsStream("/RobotoCondensed-Italic.ttf"));
        } catch (FontFormatException | IOException e) {
            System.err.println("Could not load italic font.");
        }
        FONT_ANTIQUA_ITALIC = loaded;

        // bold
        loaded = FONT_FALLBACK_BOLD;
        try {
            loaded = Font.createFont(
                    Font.TRUETYPE_FONT,
                    ComparatorPane.class.getResourceAsStream("/RobotoCondensed-Bold.ttf"));
        } catch (FontFormatException | IOException e) {
            System.err.println("Could not load bold font.");
        }
        FONT_ANTIQUA_BOLD = loaded;

        // bold & italic
        loaded = FONT_FALLBACK_BOLD_ITALIC;
        try {
            loaded = Font.createFont(
                    Font.TRUETYPE_FONT,
                    ComparatorPane.class.getResourceAsStream("/RobotoCondensed-BoldItalic.ttf"));
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
                    ComparatorPane.class.getResourceAsStream("/UnifrakturMaguntia.ttf"));
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

    private final LinkedList<ZoomChangeListener> zoomChangeListeners = new LinkedList<ZoomChangeListener>();

    private Page model = new Page(Paths.get(""), new BufferedImage(1, 1,
            BufferedImage.TYPE_BYTE_BINARY), new BufferedImage(1, 1,
            BufferedImage.TYPE_BYTE_BINARY), new LinkedList<Line>());

    /**
     * Create the panel.
     */
    public ComparatorPane() {
        setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        add(panel, BorderLayout.SOUTH);

        JLabel lblSelectedWord = new JLabel("Selected word:");
        panel.add(lblSelectedWord);

        tfSelection = new JTextField();
        tfSelection.setEditable(false);
        panel.add(tfSelection);
        tfSelection.setColumns(20);

        JLabel lblConfidence = new JLabel("Confidence:");
        panel.add(lblConfidence);

        tfConfidence = new JTextField();
        tfConfidence.setEditable(false);
        panel.add(tfConfidence);
        tfConfidence.setColumns(8);

        cbCorrect = new JCheckBox("Correct?");
        cbCorrect.setEnabled(false);
        cbCorrect.setToolTipText("Is the selected word correct?");
        panel.add(cbCorrect);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setEnabled(false);
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);

        final JScrollPane spOriginal = new JScrollPane();
        spOriginal.getHorizontalScrollBar().setUnitIncrement(SCROLL_UNITS);
        spOriginal.getVerticalScrollBar().setUnitIncrement(SCROLL_UNITS);
        splitPane.setLeftComponent(spOriginal);

        MouseListener mouseListener = new DefaultMouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                final Page model = getModel();

                final float factor = getScaleFactor();

                final Point scaled = new Point(e.getPoint());
                final Point unscaled = new Point(
                        unscaled(scaled.getX(), factor),
                        unscaled(scaled.getY(), factor));

                int lineIndex = 0;
                int wordIndex = 0;

                // true if clicked a box (word)
                boolean hit = false;

                for (Line line : model.getLines()) {
                    for (Word word : line.getWords()) {

                        word.setSelected(false);

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

                            word.setSelected(true);

                            model.setSelectedLineIndex(lineIndex);
                            model.setSelectedWordIndex(wordIndex);
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

                    if (model.hasSelected()) {
                        model.getSelected().setSelected(false);
                        model.setSelectedLineIndex(-1);
                        model.setSelectedWordIndex(-1);
                    }
                }

                render();
            }
        };

        lblOriginal = new JLabel();
        lblOriginal.addMouseListener(mouseListener);
        lblOriginal.setVerticalAlignment(SwingConstants.TOP);
        spOriginal.setViewportView(lblOriginal);

        final JScrollPane spHOCR = new JScrollPane();
        spHOCR.getHorizontalScrollBar().setUnitIncrement(SCROLL_UNITS);
        spHOCR.getVerticalScrollBar().setUnitIncrement(SCROLL_UNITS);
        splitPane.setRightComponent(spHOCR);

        lblHOCR = new JLabel();
        lblHOCR.addMouseListener(mouseListener);
        lblHOCR.setVerticalAlignment(SwingConstants.TOP);
        spHOCR.setViewportView(lblHOCR);

        JPanel panel_1 = new JPanel();
        add(panel_1, BorderLayout.NORTH);
        panel_1.setLayout(new BorderLayout(0, 0));

        JPanel panel_2 = new JPanel();
        panel_1.add(panel_2, BorderLayout.EAST);
        panel_2.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

        JLabel lblZoom = new JLabel("Zoom:");
        panel_2.add(lblZoom);

        zoomSlider = new JSlider();
        panel_2.add(zoomSlider);
        zoomSlider.setPreferredSize(new Dimension(160, 20));
        zoomSlider.setSnapToTicks(true);
        zoomSlider.setMajorTickSpacing(1);
        zoomSlider.setValue(4);
        zoomSlider.setMaximum(9);

        JPanel panel_3 = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel_3.getLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        panel_1.add(panel_3, BorderLayout.WEST);

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

        cbWordBoxes = new JCheckBox("Word boxes");
        cbWordBoxes.setSelected(true);
        cbWordBoxes.addChangeListener(checkBoxListener);
        panel_3.add(cbWordBoxes);

        cbSymbolBoxes = new JCheckBox("Symbol boxes");
        cbSymbolBoxes.setSelected(false);
        cbSymbolBoxes.addChangeListener(checkBoxListener);
        panel_3.add(cbSymbolBoxes);

        cbLineNumbers = new JCheckBox("Line numbers");
        cbLineNumbers.setSelected(true);
        cbLineNumbers.addChangeListener(checkBoxListener);
        panel_3.add(cbLineNumbers);

        cbBaseline = new JCheckBox("Baseline");
        cbBaseline.setSelected(false);
        cbBaseline.addChangeListener(checkBoxListener);
        panel_3.add(cbBaseline);

        cbXLine = new JCheckBox("x-Line");
        cbXLine.setSelected(false);
        cbXLine.addChangeListener(checkBoxListener);
        panel_3.add(cbXLine);

        comboBox = new JComboBox<String>();
        comboBox.setModel(new DefaultComboBoxModel<String>(new String[] {
                "Antiqua", "Fraktur" }));
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                render();
            }
        });

        panel_3.add(comboBox);
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

    public Page getModel() {
        return model;
    }

    public void setModel(Page page) {
        model = page;

        setAscendersEnabled(page.isAscendersEnabled());

        zoomChanged(zoomSlider.getValue());
    }

    public void addZoomChangeListener(ZoomChangeListener listener) {
        zoomChangeListeners.add(listener);
    }

    public void removeZoomChangeListener(ZoomChangeListener listener) {
        zoomChangeListeners.remove(listener);
    }

    private void zoomChanged() {
        final int zoom = zoomSlider.getValue();
        for (ZoomChangeListener l : zoomChangeListeners) {
            l.zoomChanged(zoom);
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

    public void zoomChanged(final int zoom) {
        render();
    }

    private void render() {
        if (renderer != null && !renderer.isDone()) {
            renderer.cancel(true);
        }

        final int zoom = zoomSlider.getValue();
        final float factor = getScaleFactor();

        final List<Line> lines = getModel().getLines();

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

        final BufferedImage original = getModel().getOriginalImage();

        final int width = original.getWidth();
        final int height = original.getHeight();

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
                final boolean isSelected = word.isSelected();

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
                    if (isSelected) {
                        scanGfx.setStroke(STROKE_SELECTION);
                        hocrGfx.setStroke(STROKE_SELECTION);
                    }

                    if (showWordBoxes) {
                        if (word.isCorrect()) {
                            scanGfx.setColor(COLOR_CORRECT);
                            hocrGfx.setColor(COLOR_CORRECT);
                        } else {
                            scanGfx.setColor(COLOR_INCORRECT);
                            hocrGfx.setColor(COLOR_INCORRECT);
                        }

                        scanGfx.drawRect(scX, scY, scW, scH);
                        hocrGfx.drawRect(scX, scY, scW, scH);
                    } else if (showSymbolBoxes) {
                        for (final Symbol sym : word.getSymbols()) {
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
                                scanGfx.setColor(COLOR_CORRECT);
                                hocrGfx.setColor(COLOR_CORRECT);
                            } else {
                                scanGfx.setColor(COLOR_INCORRECT);
                                hocrGfx.setColor(COLOR_INCORRECT);
                            }

                            scanGfx.drawRect(ssbX, ssbY, ssbW, ssbH);
                            hocrGfx.drawRect(ssbX, ssbY, ssbW, ssbH);

                            hocrGfx.setColor(COLOR_TEXT);

                            hocrGfx.drawString(
                                    stext,
                                    ssbX,
                                    scaled(box.getY() + box.getHeight()
                                            - word.getBaseline().getYOffset(),
                                            factor));
                        }
                    }

                    if (isSelected) {
                        scanGfx.setStroke(STROKE_NORMAL);
                        hocrGfx.setStroke(STROKE_NORMAL);
                    }
                }

                if (!showSymbolBoxes) {
                    hocrGfx.setColor(COLOR_TEXT);

                    // only draw the string
                    hocrGfx.drawString(
                            word.getText(), tx, ty);
                }

                if (showBaselines) {
                    final int x2 = bX + bW;
                    final int y1 = bY + bH - bl.getYOffset();
                    final int y2 = Math.round(y1 + bW * bl.getSlope());

                    scanGfx.setColor(COLOR_BASELINE);
                    scanGfx.drawLine(scaled(bX, factor), scaled(y1, factor),
                            scaled(x2, factor), scaled(y2, factor));
                    hocrGfx.setColor(COLOR_BASELINE);
                    hocrGfx.drawLine(scaled(bX, factor), scaled(y1, factor),
                            scaled(x2, factor), scaled(y2, factor));
                }
            }

            @Override
            protected ImagePair doInBackground() throws Exception {
                // init attributes
                scanImg = new BufferedImage(scaledWidth, scaledHeight,
                        BufferedImage.TYPE_INT_RGB);
                scanGfx = (Graphics2D) scanImg.getGraphics();

                hocrImg = new BufferedImage(scaledWidth, scaledHeight,
                        BufferedImage.TYPE_INT_RGB);
                hocrGfx = (Graphics2D) hocrImg.getGraphics();

                scanGfx.drawImage(original, 0, 0, scaledWidth, scaledHeight, 0,
                        0,
                        width - 1, height - 1, null);

                hocrGfx.setColor(Color.WHITE);
                hocrGfx.fillRect(0, 0, scaledWidth, scaledHeight);

                // stays the same for all lines
                scanGfx.setFont(lineNumberFont);

                int lineNumber = 1;
                for (Line line : lines) {
                    if (zoom >= 1 && showLineNumbers) {
                        drawLineNumber(line, lineNumber, COLOR_LINE_NUMBER);
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

    private static int scaled(float value, float factor) {
        return Math.round(value * factor);
    }

    private static int unscaled(int value, float factor) {
        return Math.round(value / factor);
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
}
