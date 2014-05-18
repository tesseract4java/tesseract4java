package de.vorb.tesseract.gui.model;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class CharTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    private final ArrayList<Character> chars = new ArrayList<>();

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public int getRowCount() {
        return 0;
    }

    @Override
    public Object getValueAt(int rowIndex, int colIndex) {
        switch (colIndex) {
        case 0:
            return chars.get(rowIndex);
        case 1:
            return Character.getName(Character.getNumericValue(chars.get(
                    rowIndex)));
        case 2:
            final String hex = Integer.toHexString(Character.getNumericValue(
                    chars.get(rowIndex)));
            return "U+" + hex;
        }

        return "";
    }

}
