package de.vorb.tesseract.gui.view.renderer;

import de.vorb.tesseract.tools.training.Char;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import java.awt.Component;

public class UnicharListRenderer extends DefaultListCellRenderer {
    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {

        // use the char text if value is of type char
        if (value instanceof Char) {
            return super.getListCellRendererComponent(list,
                    ((Char) value).getText(), index, isSelected,
                    cellHasFocus);
        }

        return super.getListCellRendererComponent(list, value, index,
                isSelected,
                cellHasFocus);
    }

}
