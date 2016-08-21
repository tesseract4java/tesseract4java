package de.vorb.tesseract.gui.view.dialogs;

import de.vorb.tesseract.gui.view.TesseractFrame;
import de.vorb.tesseract.gui.view.renderer.UnicharListRenderer;
import de.vorb.tesseract.tools.training.Char;
import de.vorb.tesseract.tools.training.CharacterDimensions;
import de.vorb.tesseract.tools.training.Unicharset;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class UnicharsetDebugger extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final int WIDTH = 800;
    private static final int HEIGHT = 256;
    private static final int RIGHT = WIDTH - 1;
    private static final int TOP = HEIGHT - 1;

    private final JPanel contentPanel = new JPanel();

    /**
     * Create the dialog.
     */
    public UnicharsetDebugger(final Unicharset unicharset) {
        final Toolkit t = Toolkit.getDefaultToolkit();
        final List<Image> appIcons = new LinkedList<>();
        appIcons.add(t.getImage(
                TesseractFrame.class.getResource("/logos/logo_16.png")));
        appIcons.add(t.getImage(
                TesseractFrame.class.getResource("/logos/logo_96.png")));
        appIcons.add(t.getImage(
                TesseractFrame.class.getResource("/logos/logo_256.png")));
        setIconImages(appIcons);

        setTitle("Unicharset Debugger");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 300);

        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        contentPanel.add(scrollPane, BorderLayout.WEST);

        final JList<Char> list = new JList<>();
        list.setCellRenderer(new UnicharListRenderer());
        scrollPane.setViewportView(list);

        final BufferedImage canvas = new BufferedImage(WIDTH, HEIGHT,
                BufferedImage.TYPE_INT_RGB);
        final Graphics2D g2d = canvas.createGraphics();

        final Color colorBottom = new Color(0x66FF0000, true);
        final Color colorTop = new Color(0x6600FF00, true);
        final Color colorWidth = new Color(0x660000FF, true);

        g2d.setBackground(Color.WHITE);
        g2d.clearRect(0, 0, WIDTH, HEIGHT);

        final ImageIcon icon = new ImageIcon(canvas);

        JPanel panel = new JPanel();
        contentPanel.add(panel, BorderLayout.CENTER);
        panel.setLayout(new BorderLayout(0, 0));

        final JLabel label = new JLabel("");
        panel.add(label, BorderLayout.CENTER);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setIcon(icon);

        DefaultListModel<Char> listModel = new DefaultListModel<>();
        unicharset.getCharacters().forEach(listModel::addElement);
        list.setModel(listModel);

        list.addListSelectionListener(evt -> {
            if (evt.getValueIsAdjusting()) {
                return;
            }

            g2d.clearRect(0, 0, WIDTH, HEIGHT);

            final int index = list.getSelectedIndex();
            final Char unichar = unicharset.getCharacters().get(index);
            final CharacterDimensions dims = unichar.getDimensions();

            final int minBottom = dims.getMinBottom();
            final int maxBottom = dims.getMaxBottom();

            final int minTop = dims.getMinTop();
            final int maxTop = dims.getMaxTop();

            final int minWidth = dims.getMinWidth();
            final int maxWidth = dims.getMaxWidth();

            g2d.setPaint(colorBottom);
            g2d.fillRect(0, TOP - maxBottom, WIDTH, maxBottom - minBottom);

            g2d.setPaint(colorTop);
            g2d.fillRect(0, TOP - maxTop, WIDTH, maxTop - minTop);

            g2d.setPaint(colorWidth);
            g2d.fillRect(minWidth, 0, maxWidth, HEIGHT);

            // TODO show bearing etc

            icon.setImage(canvas);
            label.repaint();
        });

        scrollPane.setViewportView(list);

        // legend

        JPanel panel_1 = new JPanel();
        panel.add(panel_1, BorderLayout.SOUTH);

        final BufferedImage colorBottomImg = new BufferedImage(16, 16,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D colorG2d = colorBottomImg.createGraphics();
        colorG2d.setBackground(colorBottom);
        colorG2d.clearRect(0, 0, 16, 16);
        colorG2d.dispose();

        JLabel lblBottomRange = new JLabel("Bottom Range");
        lblBottomRange.setIcon(new ImageIcon(colorBottomImg));
        panel_1.add(lblBottomRange);

        final BufferedImage colorTopImage = new BufferedImage(16, 16,
                BufferedImage.TYPE_INT_RGB);
        colorG2d = colorTopImage.createGraphics();
        colorG2d.setBackground(colorTop);
        colorG2d.clearRect(0, 0, 16, 16);
        colorG2d.dispose();

        JLabel lblTopRange = new JLabel("Top Range");
        lblTopRange.setIcon(new ImageIcon(colorTopImage));
        panel_1.add(lblTopRange);

        final BufferedImage colorWidthImage = new BufferedImage(16, 16,
                BufferedImage.TYPE_INT_RGB);
        colorG2d = colorWidthImage.createGraphics();
        colorG2d.setBackground(colorWidth);
        colorG2d.clearRect(0, 0, 16, 16);
        colorG2d.dispose();

        JLabel lblWidthRange = new JLabel("Width Range");
        lblWidthRange.setIcon(new ImageIcon(colorWidthImage));
        panel_1.add(lblWidthRange);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                g2d.dispose();
            }
        });

        pack();
        setResizable(false);
    }
}
