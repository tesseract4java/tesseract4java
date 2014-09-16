package de.vorb.tesseract.gui.view.renderer;

import static de.vorb.tesseract.gui.model.Scale.scaled;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

import com.google.common.base.Optional;

import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.gui.view.Colors;
import de.vorb.tesseract.gui.view.RecognitionPane;
import de.vorb.tesseract.gui.view.RecognitionPane.FontSelection;
import de.vorb.tesseract.util.*;

public class RecognitionRenderer implements PageRenderer {
    // Fallback fonts
    private static final Font FONT_FALLBACK_NORMAL = new Font("SansSerif",
            Font.PLAIN, 12);
    private static final Font FONT_FALLBACK_ITALIC = new Font("SansSerif",
            Font.ITALIC, 12);
    private static final Font FONT_FALLBACK_BOLD = new Font("SansSerif",
            Font.BOLD, 12);
    private static final Font FONT_FALLBACK_BOLD_ITALIC = new Font("SansSerif",
            Font.BOLD | Font.ITALIC, 12);

    private final static Font FONT_LINE_NUMBERS = new Font("Dialog",
            Font.PLAIN, 12);

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

    private final RecognitionPane rp;
    private SwingWorker<Void, Void> renderWorker;

    private PageModel lastPageModel = null;
    private BufferedImage original = null;
    private BufferedImage recognition = null;
    private float minimumConfidence = 0;
    private float lastScale;

    public RecognitionRenderer(RecognitionPane pane) {
        this.rp = pane;
    }

    public void setMinimumConfidence(float min) {
        this.minimumConfidence = min;
    }

    @Override
    public void render(Optional<PageModel> pageModel, final float scale) {
        if (renderWorker != null && !renderWorker.isCancelled()
                && !renderWorker.isDone()) {
            renderWorker.cancel(true);
        }

        // if no page model is present, remove the images and render worker
        if (!pageModel.isPresent()) {
            renderWorker = null;

            rp.getCanvasOriginal().setIcon(null);
            rp.getCanvasRecognition().setIcon(null);

            original = null;
            recognition = null;

            return;
        }

        final Page page = pageModel.get().getPage();
        final BufferedImage preprocessed =
                pageModel.get().getImageModel().getPreprocessedImage();
        final int width = preprocessed.getWidth();
        final int height = preprocessed.getHeight();

        final int scaledWidth;
        final int scaledHeight;
        if (lastPageModel != pageModel.get() || lastScale != scale) {
            // prepare the images if the model has changed

            lastPageModel = pageModel.get();
            lastScale = scale;

            // calculate the width and height of the scene
            scaledWidth = scaled(width, scale);
            scaledHeight = scaled(height, scale);

            // create empty images
            original = new BufferedImage(scaledWidth, scaledHeight,
                    BufferedImage.TYPE_INT_RGB);
            recognition = new BufferedImage(scaledWidth, scaledHeight,
                    BufferedImage.TYPE_INT_RGB);
        } else {
            // otherwise
            scaledWidth = original.getWidth();
            scaledHeight = original.getHeight();
        }

        final boolean useFraktur =
                rp.getComboFont().getSelectedItem() == FontSelection.FRAKTUR;

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

        final boolean showWordBoxes = rp.getWordBoxes().isSelected();
        final boolean showSymbolBoxes = rp.getSymbolBoxes().isSelected();
        final boolean showLineNumbers = rp.getLineNumbers().isSelected();
        final boolean showBaselines = rp.getBaselines().isSelected();
        final boolean showXLines = rp.getXLines().isSelected();
        final boolean showBlocks = rp.getBlocks().isSelected();
        final boolean showParagraphs = rp.getParagraphs().isSelected();

        renderWorker = new SwingWorker<Void, Void>() {
            private Graphics2D origGfx, recogGfx;

            private void drawLineNumber(final Line line, final int lineNumber) {

                final Box box = line.getBoundingBox();

                final String num = String.valueOf(lineNumber);
                final int x = scaled(20, scale);
                final int y = scaled(box.getY() + box.getHeight()
                        - line.getBaseline().getYOffset(), scale);

                origGfx.setFont(FONT_LINE_NUMBERS);
                origGfx.setPaint(Colors.LINE_NUMBER);
                origGfx.drawString(num, x, y);

                recogGfx.setFont(FONT_LINE_NUMBERS);
                recogGfx.setPaint(Colors.LINE_NUMBER);
                recogGfx.drawString(num, x, y);
            }

            private void drawWord(final Line line, final Word word) {
                // bounding box
                final Box box = word.getBoundingBox();

                // baseline
                final Baseline bl = word.getBaseline();

                // font attributes
                final FontAttributes fa = word.getFontAttributes();

                // scaled font size
                final float scFontSize = scaled(fa.getSize(), scale);

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
                final int scX = scaled(bX, scale);
                final int scY = scaled(bY, scale);
                final int scW = scaled(bW, scale);
                final int scH = scaled(bH, scale);

                // text coordinates
                final int tx = scX;
                final int ty = scaled(
                        bY + bH - word.getBaseline().getYOffset(),
                        scale);

                // set font
                final Font font;
                if (!italic && !bold) {
                    font = baseFontNormal.deriveFont(scFontSize);
                } else if (italic && !bold) {
                    font = baseFontItalic.deriveFont(scFontSize);
                } else if (bold && !italic) {
                    font = baseFontBold.deriveFont(scFontSize);
                } else {
                    font = baseFontBoldItalic.deriveFont(scFontSize);
                }

                recogGfx.setFont(font);

                if (showWordBoxes || showSymbolBoxes) {
                    origGfx.setPaint(Colors.NORMAL);
                    recogGfx.setPaint(Colors.NORMAL);

                    if (showWordBoxes) {
                        origGfx.drawRect(scX, scY, scW, scH);
                        recogGfx.drawRect(scX, scY, scW, scH);
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
                            final int ssbX = scaled(sbX, scale);
                            final int ssbY = scaled(sbY, scale);
                            final int ssbW = scaled(sbW, scale);
                            final int ssbH = scaled(sbH, scale);

                            recogGfx.setPaint(Colors.NORMAL);

                            origGfx.drawRect(ssbX, ssbY, ssbW, ssbH);
                            recogGfx.drawRect(ssbX, ssbY, ssbW, ssbH);

                            recogGfx.setPaint(Colors.TEXT);

                            recogGfx.drawString(stext, ssbX,
                                    scaled(box.getY() + box.getHeight()
                                            - word.getBaseline().getYOffset(),
                                            scale));
                        }
                    }
                }

                if (!showSymbolBoxes) {
                    recogGfx.setPaint(Colors.TEXT);

                    // only draw the string
                    recogGfx.drawString(word.getText(), tx, ty);
                }

                if (showBaselines) {
                    final int x2 = bX + bW;
                    final int y1 = bY + bH - bl.getYOffset();
                    final int y2 = Math.round(y1 + bW * bl.getSlope());

                    origGfx.setPaint(Colors.BASELINE);
                    origGfx.drawLine(scaled(bX, scale), scaled(y1, scale),
                            scaled(x2, scale), scaled(y2, scale));
                    recogGfx.setPaint(Colors.BASELINE);
                    recogGfx.drawLine(scaled(bX, scale), scaled(y1, scale),
                            scaled(x2, scale), scaled(y2, scale));
                }
            }

            @Override
            protected Void doInBackground() throws Exception {
                // init graphics contexts
                origGfx = original.createGraphics();
                recogGfx = recognition.createGraphics();

                // draw the preprocessed image on original
                origGfx.drawImage(preprocessed, 0, 0, scaledWidth,
                        scaledHeight, 0, 0, width - 1, height - 1, null);

                // clear recognition
                recogGfx.setColor(Color.WHITE);
                recogGfx.fillRect(0, 0, scaledWidth, scaledHeight);

                // stays the same for all lines
                origGfx.setFont(FONT_LINE_NUMBERS);

                if (showBlocks) {
                    origGfx.setPaint(Colors.BLOCK);
                    recogGfx.setPaint(Colors.BLOCK);
                    final Iterator<Block> blocks = page.blockIterator();
                    while (blocks.hasNext()) {
                        final Block block = blocks.next();
                        final Box bbox = block.getBoundingBox();
                        origGfx.drawRect(scaled(bbox.getX(), scale),
                                scaled(bbox.getY(), scale),
                                scaled(bbox.getWidth(), scale),
                                scaled(bbox.getHeight(), scale));
                        recogGfx.drawRect(scaled(bbox.getX(), scale),
                                scaled(bbox.getY(), scale),
                                scaled(bbox.getWidth(), scale),
                                scaled(bbox.getHeight(), scale));
                    }
                }

                if (showParagraphs) {
                    origGfx.setPaint(Colors.PARAGRAPH);
                    recogGfx.setPaint(Colors.PARAGRAPH);
                    final Iterator<Paragraph> paragraphs = page.paragraphIterator();
                    while (paragraphs.hasNext()) {
                        final Paragraph paragraph = paragraphs.next();
                        final Box bbox = paragraph.getBoundingBox();
                        origGfx.drawRect(scaled(bbox.getX(), scale),
                                scaled(bbox.getY(), scale),
                                scaled(bbox.getWidth(), scale),
                                scaled(bbox.getHeight(), scale));
                        recogGfx.drawRect(scaled(bbox.getX(), scale),
                                scaled(bbox.getY(), scale),
                                scaled(bbox.getWidth(), scale),
                                scaled(bbox.getHeight(), scale));
                    }
                }

                int lineNumber = 1;
                final Iterator<Line> lines = page.lineIterator();
                while (lines.hasNext()) {
                    final Line line = lines.next();
                    if (scale >= 0.5f && showLineNumbers) {
                        drawLineNumber(line, lineNumber);
                    }

                    recogGfx.setRenderingHint(
                            RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

                    for (final Word word : line.getWords()) {
                        drawWord(line, word);
                    }

                    lineNumber++;
                }

                return null;
            }

            @Override
            protected void done() {
                try {
                    rp.getCanvasOriginal().setIcon(new ImageIcon(original));
                    rp.getCanvasRecognition().setIcon(
                            new ImageIcon(recognition));
                } catch (Exception e) {
                } finally {
                    System.gc();
                }
            }
        };

        renderWorker.execute();
    }

    public void freeResources() {
        lastPageModel = null;
    }
}
