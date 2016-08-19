package de.vorb.tesseract.gui.view;

import de.vorb.tesseract.tools.training.IntClass;
import de.vorb.tesseract.tools.training.IntTemplates;
import de.vorb.tesseract.util.feat.Feature3D;
import de.vorb.tesseract.util.feat.Feature4D;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

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

    private List<Feature3D> features;
    private Optional<IntTemplates> prototypes = Optional.empty();

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

        pack();
    }

    public void setFeatures(List<Feature3D> features) {
        this.features = features;
        redraw();
    }

    public void setPrototypes(Optional<IntTemplates> prototypes) {
        this.prototypes = prototypes;
    }

    private void redraw() {
        final Graphics2D g2d = canvas.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.BLACK);
        g2d.setBackground(Color.WHITE);
        g2d.clearRect(0, 0, WIDTH, HEIGHT);

        final Double line = new Line2D.Double(0d, 0d, 0d, 0d);
        if (prototypes.isPresent()) {
            final List<IntClass> classes = prototypes.get().getClasses();
            for (final Feature4D feat : classes.get(0).getProtoSets().get(0).getProtos()) {
                // transform the angle back to radians
                final double angle = feat.getAngle() / 256d * PI2;
                final double dx = Math.cos(angle) * feat.getC();
                final double dy = Math.sin(angle) * feat.getC();
                final double x1 = feat.getA();
                final double x2 = feat.getA() + dx;
                final double y1 = feat.getB();
                final double y2 = feat.getB() + dy;
                line.setLine(x1, y1, x2, y2);
                g2d.draw(line);
            }
        }

        for (final Feature3D feat : features) {
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
