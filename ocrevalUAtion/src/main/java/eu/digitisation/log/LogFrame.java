/*
 * Copyright (C) 2014 Universidad de Alicante
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
package eu.digitisation.log;

import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author R.C.C.
 */
public class LogFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private final Container pane;
    private final JTextArea text;

    public LogFrame() {
        this.setTitle("Operations log");
        setSize(600, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        text = new JTextArea();
        pane = getContentPane();
        pane.add(new JScrollPane(text));
        setVisible(true);

    }

    public void showInfo(String data) {
        text.append(data);
        //this.validate();
        //this.repaint();
    }

    public void close() {
        setVisible(false);
        dispose();
    }
}
