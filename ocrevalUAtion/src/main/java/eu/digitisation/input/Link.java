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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author R.C.C
 */
public class Link extends JPanel {
    private static final long serialVersionUID = 1L;
    JLabel link;
    /**
     * Basic constructor
     * @param title the text to be shown
     * @param url the linked URL 
     * @param color the color of the link
     */
    public Link(final String title, final String url, Color color) {
        setPreferredSize(new Dimension(600,30));  
        link = new JLabel();
        link.setFont(new Font("Verdana", Font.PLAIN, 12));
        link.setAlignmentX(LEFT_ALIGNMENT);
        link.setText("<html><body>" + title
                + "<a style=\"color:#4c501E\" href=\""
                + url + "\">" + url
                + "</a></body></html>");
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Browser.open(new URI(url));
                } catch (URISyntaxException ex) {
                     Messages.severe(Link.class.getName() + ex);
                }
            }
        });
        add(link);
    }
}
