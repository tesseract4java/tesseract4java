package de.vorb.tesseract.gui.view.renderer;

import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.gui.view.Colors;
import de.vorb.tesseract.gui.view.RecognitionPane;
import de.vorb.tesseract.util.Baseline;
import de.vorb.tesseract.util.Block;
import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.FontAttributes;
import de.vorb.tesseract.util.Line;
import de.vorb.tesseract.util.Page;
import de.vorb.tesseract.util.Paragraph;
import de.vorb.tesseract.util.Symbol;
import de.vorb.tesseract.util.Word;

import javax.swing.ImageIcon;
import javax.swing.SwingWorker;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static de.vorb.tesseract.gui.model.Scale.scaled;

public class RecognitionRenderer implements PageRenderer {

    private static final int DEFAULT_FONT_SIZE = 12;

    private static final Font FONT_LINE_NUMBERS = new Font("Dialog", Font.PLAIN, DEFAULT_FONT_SIZE);

    private final RecognitionPane recognitionPane;
    private SwingWorker<Void, Void> renderWorker;

    private PageModel lastPageModel = null;
    private BufferedImage original = null;
    private BufferedImage recognition = null;
    private float minimumConfidence = 0;
    private float lastScale;

    private final AtomicReference<Font> fontNormal = new AtomicReference<>();
    private final AtomicReference<Font> fontBold = new AtomicReference<>();
    private final AtomicReference<Font> fontItalic = new AtomicReference<>();
    private final AtomicReference<Font> fontBoldItalic = new AtomicReference<>();

    public RecognitionRenderer(RecognitionPane pane, String renderingFont) {
        recognitionPane = pane;

        setRenderingFont(renderingFont);
    }

    public void setRenderingFont(String renderingFont) {
        final String selectedFontFamily = renderingFont;

        fontNormal.set(new Font(selectedFontFamily, Font.PLAIN, DEFAULT_FONT_SIZE));
        fontBold.set(new Font(selectedFontFamily, Font.BOLD, DEFAULT_FONT_SIZE));
        fontItalic.set(new Font(selectedFontFamily, Font.ITALIC, DEFAULT_FONT_SIZE));
        fontBoldItalic.set(new Font(selectedFontFamily, Font.BOLD | Font.ITALIC, DEFAULT_FONT_SIZE));
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

            recognitionPane.getCanvasOriginal().setIcon(null);
            recognitionPane.getCanvasRecognition().setIcon(null);

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

        // set the base fonts
        final Font baseFontNormal;
        final Font baseFontItalic;
        final Font baseFontBold;
        final Font baseFontBoldItalic;

        final boolean showWordBoxes = recognitionPane.getWordBoxes().isSelected();
        final boolean showSymbolBoxes = recognitionPane.getSymbolBoxes().isSelected();
        final boolean showLineNumbers = recognitionPane.getLineNumbers().isSelected();
        final boolean showBaselines = recognitionPane.getBaselines().isSelected();
        // final boolean showXLines = recognitionPane.getXLines().isSelected();
        final boolean showBlocks = recognitionPane.getBlocks().isSelected();
        final boolean showParagraphs = recognitionPane.getParagraphs().isSelected();

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
                final int ty = scaled(bY + bH - word.getBaseline().getYOffset(), scale);

                // set font
                final Font font;
                if (!italic && !bold) {
                    font = fontNormal.get().deriveFont(scFontSize);
                } else if (italic && !bold) {
                    font = fontItalic.get().deriveFont(scFontSize);
                } else if (bold && !italic) {
                    font = fontBold.get().deriveFont(scFontSize);
                } else {
                    font = fontBoldItalic.get().deriveFont(scFontSize);
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

                            final Box symbolBoundingBox = sym.getBoundingBox();
                            final String symbolText = sym.getText();

                            // coordinates
                            final int sbX = symbolBoundingBox.getX();
                            final int sbY = symbolBoundingBox.getY();
                            final int sbW = symbolBoundingBox.getWidth();
                            final int sbH = symbolBoundingBox.getHeight();

                            // scaled coordinates
                            final int ssbX = scaled(sbX, scale);
                            final int ssbY = scaled(sbY, scale);
                            final int ssbW = scaled(sbW, scale);
                            final int ssbH = scaled(sbH, scale);

                            recogGfx.setPaint(Colors.NORMAL);

                            origGfx.drawRect(ssbX, ssbY, ssbW, ssbH);
                            recogGfx.drawRect(ssbX, ssbY, ssbW, ssbH);

                            recogGfx.setPaint(Colors.TEXT);

                            recogGfx.drawString(symbolText, ssbX,
                                    scaled(box.getY() + box.getHeight() - word.getBaseline().getYOffset(), scale));
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
                        final Box boundingBox = block.getBoundingBox();
                        origGfx.drawRect(scaled(boundingBox.getX(), scale),
                                scaled(boundingBox.getY(), scale),
                                scaled(boundingBox.getWidth(), scale),
                                scaled(boundingBox.getHeight(), scale));
                        recogGfx.drawRect(scaled(boundingBox.getX(), scale),
                                scaled(boundingBox.getY(), scale),
                                scaled(boundingBox.getWidth(), scale),
                                scaled(boundingBox.getHeight(), scale));
                    }
                }

                if (showParagraphs) {
                    origGfx.setPaint(Colors.PARAGRAPH);
                    recogGfx.setPaint(Colors.PARAGRAPH);
                    final Iterator<Paragraph> paragraphs = page.paragraphIterator();
                    while (paragraphs.hasNext()) {
                        final Paragraph paragraph = paragraphs.next();
                        final Box boundingBox = paragraph.getBoundingBox();
                        origGfx.drawRect(scaled(boundingBox.getX(), scale),
                                scaled(boundingBox.getY(), scale),
                                scaled(boundingBox.getWidth(), scale),
                                scaled(boundingBox.getHeight(), scale));
                        recogGfx.drawRect(scaled(boundingBox.getX(), scale),
                                scaled(boundingBox.getY(), scale),
                                scaled(boundingBox.getWidth(), scale),
                                scaled(boundingBox.getHeight(), scale));
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
                    recognitionPane.getCanvasOriginal().setIcon(new ImageIcon(original));
                    recognitionPane.getCanvasRecognition().setIcon(
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
