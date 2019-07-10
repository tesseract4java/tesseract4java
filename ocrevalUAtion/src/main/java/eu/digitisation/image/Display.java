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
package eu.digitisation.image;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * Simple class to display an image on screen
 *
 * @author R.C.C.
 */
class ImageComponent extends JComponent {
    private static final long serialVersionUID = 1L;
    int x;
    int y;
    int width;
    int height;
    BufferedImage img = null;

    ImageComponent(BufferedImage img) {
        this.img = img;
        x = 0;
        y = 0;
        width = img.getWidth();
        height = img.getHeight();
    }
    
    ImageComponent(BufferedImage img, int x, int y, int width, int height) {
        this.img = img;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    

    @Override
    public void paint(Graphics g) {
        g.drawImage(img, x, y, width, height, this);
        // g.finalize();
    }
}

/**
 * Plot an image on screen
 * @author R.C.C:
 */
public class Display {

    JFrame window = null;

    public static void draw(BufferedImage img) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBounds(0, 0, img.getWidth(), img.getHeight());
        window.getContentPane().add(new ImageComponent(img));
        window.setVisible(true);
    }

    public static void draw(BufferedImage img, int width, int height) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBounds(0, 0, width, height);
        window.getContentPane().add(new ImageComponent(img, 0, 0,width, height));
        window.setVisible(true);
    }
    
}
