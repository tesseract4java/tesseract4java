package de.vorb.tesseract.gui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.vorb.tesseract.util.feat.Feature3D;
import de.vorb.tesseract.util.feat.Feature4D;

import javax.swing.JLabel;

import java.awt.Dimension;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.image.BufferedImage;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class FeatureDebugger extends JDialog {
    private static final long serialVersionUID = 1L;

    private static final double PI2 = Math.PI + Math.PI;
    private static final double FEATURE_RADIUS = 3.5d;
    private static final int WIDTH = 256;
    private static final int HEIGHT = 256;

    private final JPanel contentPanel = new JPanel();
    private final BufferedImage canvas = new BufferedImage(WIDTH, HEIGHT,
            BufferedImage.TYPE_INT_RGB);
    private final JLabel lblCanvas;

    /**
     * Create the dialog.
     * 
     * @param parent
     */
    public FeatureDebugger(Window parent) {
        super(parent);
        setLocationByPlatform(true);
        setTitle("Features");
        setModalityType(ModalityType.APPLICATION_MODAL);

        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));
        {
            lblCanvas = new JLabel(new ImageIcon(canvas));
            final Dimension size = new Dimension(WIDTH, HEIGHT);
            lblCanvas.setPreferredSize(size);
            contentPanel.add(lblCanvas);
        }
        {
            JPanel controlPanel = new JPanel();
            FlowLayout flowLayout = (FlowLayout) controlPanel.getLayout();
            flowLayout.setAlignment(FlowLayout.LEADING);
            getContentPane().add(controlPanel, BorderLayout.NORTH);
            {
                JCheckBox chckbxPrototype = new JCheckBox(
                        "Compare to Prototype");
                chckbxPrototype.setSelected(true);
                controlPanel.add(chckbxPrototype);
            }
            {
                JComboBox<String> cbPrototype = new JComboBox<>();
                cbPrototype.setModel(new DefaultComboBoxModel<String>(
                        new String[] { "e", "f" }));
                controlPanel.add(cbPrototype);
            }
        }

        pack();
    }

    public void setFeatures(List<Feature3D> features) {
        final Graphics2D g2d = canvas.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.BLACK);
        g2d.setBackground(Color.WHITE);
        g2d.clearRect(0, 0, WIDTH, HEIGHT);
        final Double line = new Line2D.Double(0, 0, 0, 0);
        for (final Feature3D feat : features) {
            if ((feat.getX() == 0 || feat.getY() == 0) && feat.getTheta() == 0)
                continue;
            // transform the angle back to radians
            final double theta = feat.getTheta() / 256d * PI2;
            final double dx = Math.cos(theta) * FEATURE_RADIUS;
            final double dy = Math.sin(theta) * FEATURE_RADIUS;
            final double x1 = feat.getX() - dx;
            final double x2 = feat.getX() + dx;
            final double y1 = feat.getY() - dy;
            final double y2 = feat.getY() + dy;
            line.setLine(x1, y1, x2, y2);
            g2d.draw(line);
        }
        g2d.dispose();
        lblCanvas.repaint();
    }
}
