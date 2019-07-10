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
package eu.digitisation.output;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * File chooser with confirmation dialog to avoid accidental overwrite
 *
 * @author R.C.C.
 */
public class OutputFileSelector extends JFileChooser {

    private static final long serialVersionUID = 1L;
    private static File dir; // default directory

    /**
     * Default constructor
     */
    public OutputFileSelector() {
        super();
    }

    /**
     *
     * @param dir the default directory
     * @param file the preselected selection
     * @return the selected selection
     */
    public File choose(File dir, File file) {
        // Use last choice
        if (OutputFileSelector.dir == null) {
            OutputFileSelector.dir = dir;
        }
        setCurrentDirectory(OutputFileSelector.dir);
        setSelectedFile(file);

        int returnVal = showOpenDialog(OutputFileSelector.this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selection = getSelectedFile();
            OutputFileSelector.dir = selection.getParentFile();

            if (selection != null && selection.exists()) {
                int response = JOptionPane.showConfirmDialog(new JFrame().getContentPane(),
                        "The file " + selection.getName()
                        + " already exists.\n"
                        + "Do you want to replace the existing file?",
                        "Overwrite file", JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                return (response == JOptionPane.YES_NO_OPTION) ? selection : null;
            }
            return selection;
        }
        return null;
    }
}
