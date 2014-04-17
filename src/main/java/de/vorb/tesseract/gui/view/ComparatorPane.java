package de.vorb.tesseract.gui.view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
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
import de.vorb.tesseract.util.Line;
import de.vorb.tesseract.util.Page;
import de.vorb.tesseract.util.Point;
import de.vorb.tesseract.util.Symbol;
import de.vorb.tesseract.util.Word;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class ComparatorPane extends JPanel implements ZoomChangeListener {
  private static final long serialVersionUID = 1L;

  private static final int SCROLL_UNITS = 12;

  private static final Color COLOR_CORRECT = new Color(0xFF66CC00);
  private static final Color COLOR_INCORRECT = Color.RED;
  private static final Color COLOR_BASELINE = Color.BLUE;

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
    tfSelection.setColumns(30);

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
        final Point unscaled = new Point(calcUnscaled(scaled.getX(), factor),
            calcUnscaled(scaled.getY(), factor));

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
  private JCheckBox chckbxSymbolBoxes;

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

    final Font lineNumberFont = new Font("Dialog", Font.PLAIN, 12);
    final String textFontName;
    if ("Fraktur".equals(comboBox.getSelectedItem())) {
      textFontName = "UnifrakturMaguntia";
    } else {
      textFontName = "Arial Narrow";
    }

    final BufferedImage original = getModel().getOriginalImage();

    final int width = original.getWidth();
    final int height = original.getHeight();

    // calc scaled width and height
    final int scaledWidth = calcScaled(width, factor);
    final int scaledHeight = calcScaled(height, factor);

    final Stroke normalStroke = new BasicStroke(1);
    final Stroke selectionStroke = new BasicStroke(3);

    final boolean showWordBoxes = cbWordBoxes.isSelected();
    final boolean showSymbolBoxes = cbSymbolBoxes.isSelected();
    final boolean showLineNumbers = cbLineNumbers.isSelected();
    final boolean showBaselines = cbBaseline.isSelected();
    final boolean showXLines = cbXLine.isSelected();

    renderer = new SwingWorker<ImagePair, Void>() {
      private BufferedImage scanImg, hocrImg;
      private Graphics2D scanGfx, hocrGfx;

      private void drawLineNumber(Line line, int lineNumber, Color color) {
        final Box box = line.getBoundingBox();

        scanGfx.setFont(lineNumberFont);
        scanGfx.setColor(color);
        scanGfx.drawString(String.valueOf(lineNumber), calcScaled(20, factor),
            calcScaled(box.getY() + box.getHeight()
                - line.getBaseline().getYOffset(), factor));

        hocrGfx.setFont(lineNumberFont);
        hocrGfx.setColor(color);
        hocrGfx.drawString(String.valueOf(lineNumber), calcScaled(20, factor),
            calcScaled(box.getY() + box.getHeight()
                - line.getBaseline().getYOffset(), factor));
      }

      private void drawWord(Line line, Word word) {
        final Box box = word.getBoundingBox();
        final Baseline bl = word.getBaseline();

        final boolean isSelected = word.isSelected();

        hocrGfx.setFont(new Font(textFontName, Font.PLAIN, calcScaled(
            Math.max(word.getFontAttributes().getSize(), 40), factor)));

        if (showWordBoxes || showSymbolBoxes) {
          if (isSelected) {
            scanGfx.setStroke(selectionStroke);
            hocrGfx.setStroke(selectionStroke);
          }

          if (showWordBoxes) {
            if (word.isCorrect()) {
              scanGfx.setColor(COLOR_CORRECT);
              hocrGfx.setColor(COLOR_CORRECT);
            } else {
              scanGfx.setColor(COLOR_INCORRECT);
              hocrGfx.setColor(COLOR_INCORRECT);
            }

            scanGfx.drawRect(calcScaled(box.getX(), factor),
                calcScaled(box.getY(), factor),
                calcScaled(box.getWidth(), factor),
                calcScaled(box.getHeight(), factor));

            hocrGfx.drawRect(calcScaled(box.getX(), factor),
                calcScaled(box.getY(), factor),
                calcScaled(box.getWidth(), factor),
                calcScaled(box.getHeight(), factor));

            hocrGfx.setColor(Color.BLACK);

            hocrGfx.drawString(
                word.getText(),
                calcScaled(box.getX(), factor),
                calcScaled(box.getY() + box.getHeight()
                    - line.getBaseline().getYOffset(), factor));
          } else if (showSymbolBoxes) {
            for (Symbol sym : word.getSymbols()) {
              if (word.isCorrect()) {
                scanGfx.setColor(COLOR_CORRECT);
                hocrGfx.setColor(COLOR_CORRECT);
              } else {
                scanGfx.setColor(COLOR_INCORRECT);
                hocrGfx.setColor(COLOR_INCORRECT);
              }

              final Box symBox = sym.getBoundingBox();

              scanGfx.drawRect(calcScaled(symBox.getX(), factor),
                  calcScaled(symBox.getY(), factor),
                  calcScaled(symBox.getWidth(), factor),
                  calcScaled(symBox.getHeight(), factor));

              hocrGfx.drawRect(calcScaled(symBox.getX(), factor),
                  calcScaled(symBox.getY(), factor),
                  calcScaled(symBox.getWidth(), factor),
                  calcScaled(symBox.getHeight(), factor));

              hocrGfx.setColor(Color.BLACK);

              hocrGfx.drawString(
                  sym.getText(),
                  calcScaled(symBox.getX(), factor),
                  calcScaled(box.getY() + box.getHeight()
                      - word.getBaseline().getYOffset(), factor));
            }
          }

          if (isSelected) {
            scanGfx.setStroke(normalStroke);
            hocrGfx.setStroke(normalStroke);
          }
        }

        if (showBaselines) {
          final int x1 = box.getX();
          final int x2 = x1 + box.getWidth();
          final int y1 = box.getY() + box.getHeight() - bl.getYOffset();
          final int y2 = Math.round(y1 + box.getWidth() * bl.getSlope());

          scanGfx.setColor(COLOR_BASELINE);
          scanGfx.drawLine(calcScaled(x1, factor), calcScaled(y1, factor),
              calcScaled(x2, factor), calcScaled(y2, factor));
          hocrGfx.setColor(COLOR_BASELINE);
          hocrGfx.drawLine(calcScaled(x1, factor), calcScaled(y1, factor),
              calcScaled(x2, factor), calcScaled(y2, factor));
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

        scanGfx.drawImage(original, 0, 0, scaledWidth, scaledHeight, 0, 0,
            width - 1, height - 1, null);

        hocrGfx.setColor(Color.WHITE);
        hocrGfx.fillRect(0, 0, scaledWidth, scaledHeight);

        // stays the same for all lines
        scanGfx.setFont(lineNumberFont);

        int lineNumber = 1;
        for (Line line : lines) {
          if (zoom >= 1 && showLineNumbers) {
            drawLineNumber(line, lineNumber, Color.GRAY);
          }

          hocrGfx.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
              RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

          for (Word word : line.getWords()) {
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
    switch (zoomSlider.getValue()) {
    case 0:
      return 0.1f;
    case 1:
      return 0.2f;
    case 2:
      return 0.3f;
    case 3:
      return 0.4f;
    case 4:
      return 0.5f;
    case 5:
      return 0.6f;
    case 6:
      return 0.7f;
    case 7:
      return 0.8f;
    case 8:
      return 0.9f;
    default:
      return 1.0f;
    }
  }

  private static int calcScaled(float value, float factor) {
    return Math.round(value * factor);
  }

  private static int calcUnscaled(int value, float factor) {
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
