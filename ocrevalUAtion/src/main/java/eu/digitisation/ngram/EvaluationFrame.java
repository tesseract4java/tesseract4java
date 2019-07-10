package eu.digitisation.ngram;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class EvaluationFrame extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 4895806099667768081L;

    private JPanel contentPane;
    private JTextField thresholdTextField;
    private JSlider thresholdSlider;
    private JTextPane textPane;

    /**
     * double array containing the perplexity values for every character.
     */
    private double[] perplexityArray;

    /**
     * text style applied when the threshold value is exceeded.
     */
    private static final SimpleAttributeSet thresholdExceededStyle = new SimpleAttributeSet();
    /**
     * default text style.
     */
    private static final SimpleAttributeSet defaultStyle = new SimpleAttributeSet();

    static {
        StyleConstants.setForeground(thresholdExceededStyle, Color.red);
        StyleConstants.setForeground(defaultStyle, Color.black);
    }

    /**
     * Create the frame.
     */
    public EvaluationFrame() {
        init();
    }

    /**
     * GUI initialization.
     */
    private void init() {
        setBounds(100, 100, 473, 347);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        JPanel panel = new JPanel();

        JScrollPane scrollPane = new JScrollPane();
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addComponent(panel, GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE)
                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE));
        gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(
                gl_contentPane
                .createSequentialGroup()
                .addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                        GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)));

        textPane = new JTextPane();
        textPane.setFont(new Font("Tahoma", Font.PLAIN, 16));
        scrollPane.setViewportView(textPane);

        thresholdTextField = new JTextField();
        thresholdTextField.setEditable(false);
        thresholdTextField.setFont(new Font("Tahoma", Font.BOLD, 16));
        thresholdTextField.setText("-1");
        thresholdTextField.setColumns(10);

        thresholdSlider = new JSlider();
        thresholdSlider.setValue(-1);
        thresholdSlider.setMaximum(-1);
        thresholdSlider.setMinimum(-50);
        thresholdSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if (!thresholdSlider.getValueIsAdjusting()) {
                    thresholdTextField.setText((double) thresholdSlider.getValue() + "");
                    update(true);
                }
            }
        });
        GroupLayout gl_panel = new GroupLayout(panel);
        gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.TRAILING).addGroup(
                gl_panel.createSequentialGroup()
                .addComponent(thresholdSlider, GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(thresholdTextField, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
                .addContainerGap()));
        gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.TRAILING).addGroup(
                gl_panel.createSequentialGroup()
                .addContainerGap()
                .addGroup(
                        gl_panel.createParallelGroup(Alignment.TRAILING)
                        .addComponent(thresholdSlider, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 31,
                                Short.MAX_VALUE)
                        .addComponent(thresholdTextField, Alignment.LEADING, GroupLayout.DEFAULT_SIZE,
                                31, Short.MAX_VALUE)).addContainerGap()));
        panel.setLayout(gl_panel);
        contentPane.setLayout(gl_contentPane);

    }

    /**
     * update the evaluation results.
     */
    private void update(boolean thresholdMode) {
        Double threshold = 0.0;
        try {
            threshold = Double.parseDouble(thresholdTextField.getText());
            StyledDocument document = textPane.getStyledDocument();

            document.setCharacterAttributes(0, document.getLength(), defaultStyle, true);
            if (thresholdMode) {
                for (int i = 0; i < perplexityArray.length; i++) {
                    if (perplexityArray[i] < threshold) {
                        document.setCharacterAttributes(i, 1, thresholdExceededStyle, true);
                    }
                }
            } else {
                double max = 0;

                for (int i = 0; i < perplexityArray.length; i++) {
                    double value = perplexityArray[i];
                    if (!Double.isInfinite(value)) {
                        if (Math.abs(value) > Math.abs(max)) {
                            max = value;
                        }
                    }
                }

                List<Color> colors = getColorBands(Color.red, 11);

                for (int i = 0; i < perplexityArray.length; i++) {
                    SimpleAttributeSet style = new SimpleAttributeSet();
                    double value = perplexityArray[i];
                    if (Double.isInfinite(value)) {
                        StyleConstants.setForeground(style, Color.red);
                    } else {
                        int color = 10 - (int) ((value / max) * 10);
                        StyleConstants.setForeground(style, colors.get(color));
                    }
                    document.setCharacterAttributes(i, 1, style, true);
                }
            }

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Unable to parse value '"
                    + thresholdTextField.getText() + "'",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setInput(String textToEvaluate, double[] perplexityArray) {
        this.perplexityArray = perplexityArray;
        textPane.setText(textToEvaluate);
        update(false);
    }

    public List<Color> getColorBands(Color color, int bands) {

        List<Color> colorBands = new ArrayList<Color>(bands);
        for (int index = 0; index < bands; index++) {
            colorBands.add(darken(color, index / (double) bands));
        }
        return colorBands;

    }

    public static Color darken(Color color, double fraction) {

        int red = (int) Math.round(Math.max(0, color.getRed() - 255 * fraction));
        int green = (int) Math.round(Math.max(0, color.getGreen() - 255 * fraction));
        int blue = (int) Math.round(Math.max(0, color.getBlue() - 255 * fraction));

        int alpha = color.getAlpha();

        return new Color(red, green, blue, alpha);

    }

    /**
     * Launch the application.
     *
     * @param args
     */
    public static void main(final String[] args) {

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    File model = new File(args[0]);
                    File input = new File(args[1]);
                    int contextLenght = Integer.parseInt(args[2]);

                    TextPerplexity result
                            = new TextPerplexity(new NgramModel(model),
                                    new FileInputStream(input), contextLenght);

                    EvaluationFrame frame = new EvaluationFrame();
                    frame.setInput(result.getText(), result.getPerplexities());
                    frame.setVisible(true);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
