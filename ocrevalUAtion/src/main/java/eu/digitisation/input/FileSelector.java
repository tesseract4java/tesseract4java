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
package eu.digitisation.input;

import eu.digitisation.log.Messages;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextPane;

/**
 *
 * @author R.C.C
 */
public class FileSelector extends ParameterSelector<File> {

    static final long serialVersionUID = 1L;
    static File dir; // directory opened by default
    JTextPane area;  // The area to display the filename
    JButton choose;  // Optional file chooser

    public FileSelector(Parameter<File> op, Color forecolor, Color bgcolor) {
        super(op, forecolor, bgcolor);

        setPreferredSize(new Dimension(600, 70));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(BorderFactory.createLineBorder(forecolor, 2));
        
        // Drop area
        area = new JTextPane();
        area.setFont(new Font("Verdana", Font.PLAIN, 12));
        area.setText("Drop here your " + param.name);
        area.setForeground(forecolor);
        area.setBackground(bgcolor);
        enableDragAndDrop(area);

        // File chooser
        choose = new JButton("Or select the file");
        choose.setFont(new Font("Verdana", Font.PLAIN, 10));
        choose.setForeground(forecolor);
        choose.setBackground(bgcolor);
        choose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = choose("input_file");
                if (file != null) {
                    param.setValue(file);
                    area.setText(file.getName());
                    dir = file.getParentFile();
                    shade(Color.decode("#B5CC9E")); // green
                }
            }
        });
        
        // Put everything togetehr
        add(area);
        add(choose, BorderLayout.EAST);
    }

    /**
     * Set the background of panel (excluding choose JButton)
     *
     * @param color the background color
     */
    public void shade(Color color) {
        setBackground(color);
        area.setBackground(color);
        area.setForeground(Color.DARK_GRAY);
    }

    /**
     * Highlight if not ready
     */
    public void checkout() {
        if (!ready()) {
            shade(Color.decode("#fffacd")); // yellow
        }
    }

    /**
     * Change descriptive text
     *
     * @param text the text to be displayed
     */
    public void setText(String text) {
        area.setText(text);
    }

    /**
     *
     * @return the selected input file
     */
    public File getFile() {
        return param.getValue();
    }

    /**
     *
     * @return true if a file has been selected and the file exists
     */
    public boolean ready() {
        File file = getFile();
        return file != null && file.exists();
    }

    /**
     * The drag&drop function
     * @param area 
     */
    private void enableDragAndDrop(final JTextPane area) {
        DropTarget target;
        target = new DropTarget(area, new DropTargetListener() {

            @Override
            public void dragEnter(DropTargetDragEvent e) {
            }

            @Override
            public void dragExit(DropTargetEvent e) {
            }

            @Override
            public void dragOver(DropTargetDragEvent e) {
            }

            @Override
            public void dropActionChanged(DropTargetDragEvent e) {
            }

            @Override
            @SuppressWarnings("unchecked")
            public void drop(DropTargetDropEvent e) {
                try {
                    // Accept the drop first!
                    e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    if (e.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        java.util.List<File> list;
                        list = (java.util.List<File>) e.getTransferable()
                                .getTransferData(DataFlavor.javaFileListFlavor);
                        param.setValue(list.get(0));
                    } else if (e.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                        String name = (String) e.getTransferable()
                                .getTransferData(DataFlavor.stringFlavor);
                        param.setValue(new File(new URI(name.trim())));
                    }
                    area.setText(getFile().getName());
                    dir = getFile().getParentFile();
                    shade(Color.decode("#B5CC9E"));
                } catch (URISyntaxException ex) {
                    Messages.info(FileSelector.class.getName() + ": " + ex);
                } catch (IOException ex) {
                    Messages.info(FileSelector.class.getName() + ": " + ex);
                } catch (UnsupportedFlavorException ex) {
                    Messages.info(FileSelector.class.getName() + ": " + ex);
                }
            }
        });
    }
    
    /**
     * Select file with a file selector (menu)
     *
     * @param defaultName the default name for the chosen file
     * @return
     */
    private File choose(String defaultName) {
        JFileChooser chooser = new JFileChooser();

        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setDialogTitle("Select input file");
        chooser.setCurrentDirectory(dir);
        chooser.setSelectedFile(new File(defaultName));
        int returnVal = chooser.showOpenDialog(FileSelector.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        } else {
            return null;
        }
    }

}
