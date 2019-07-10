/*
 * Copyright (C) 2013 Universidad de Alicante
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package eu.digitisation.input;

import eu.digitisation.log.Messages;
import eu.digitisation.output.Browser;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JOptionPane;

/**
 * Help text or URL for additional information (URL must start with http:)
 *
 * @author R.C.C.
 */
public class Help extends JButton {

    private static final long serialVersionUID = 1L;
    String text;  // help text

    /**
     * Default constructor
     *
     * @param helpText the help text or URL
     * @param forecolor foreground color
     * @param bgcolor background color
     */
    public Help(String helpText, Color forecolor, Color bgcolor) {
        super("?");
        setPreferredSize(new Dimension(10, 10));
        setForeground(forecolor);
        setBackground(bgcolor);
        setContentAreaFilled(false);

        this.text = helpText;

        addActionListener(new ActionListener() {
            Container container = getParent();

            @Override
            public void actionPerformed(ActionEvent e) {
                if (text.startsWith("http:")) {
                    try {
                        Browser.open(new URI(text));
                    } catch (URISyntaxException ex) {
                        Messages.severe(Help.class.getName() + ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(getParent(), text);
                }
            }
        });
    }

    // Artwork
    @Override
    protected void paintComponent(Graphics g) {
        if (getModel().isArmed()) {
            g.setColor(Color.lightGray);
        } else {
            g.setColor(getBackground());
        }
        g.fillOval(7, 0, getSize().width - 16, getSize().height - 1);

        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        g.setColor(getForeground());
        g.drawOval(7, 0, getSize().width - 16, getSize().height - 1);
    }
    Shape shape;

    @Override
    public boolean contains(int x, int y) {
        if (shape == null
                || !shape.getBounds().equals(getBounds())) {
            shape = new Ellipse2D.Float(7, 0, getWidth() - 7, getHeight());
        }
        return shape.contains(x, y);
    }
}
