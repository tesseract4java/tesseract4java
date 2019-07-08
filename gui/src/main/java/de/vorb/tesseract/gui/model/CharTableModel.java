package de.vorb.tesseract.gui.model;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class CharTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    private final ArrayList<Character> chars = new ArrayList<>();

    public CharTableModel() {
        // TODO remove sample characters
        chars.add('c');
        chars.add('ü');
        chars.add('ß');
        chars.add('ſ');
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public int getRowCount() {
        return chars.size();
    }

    @Override
    public String getColumnName(int colIndex) {
        switch (colIndex) {
            case 0:
                return "Character";
            case 1:
                return "Description";
            case 2:
                return "Code point";
        }

        return "";
    }

    @Override
    public Object getValueAt(int rowIndex, int colIndex) {
        switch (colIndex) {
            case 0:
                return chars.get(rowIndex);
            case 1:
                return Character.getName(chars.get(rowIndex));
            case 2:
                return hexPad(chars.get(rowIndex), 4);
        }

        return "";
    }

    private static String hexPad(int hex, int length) {
        final String hexString = Integer.toHexString(hex).toUpperCase();

        int missing = Math.max(length - hexString.length(), 0);

        if (missing == 0) {
            return "U+" + hexString;
        }

        final StringBuilder result = new StringBuilder("U+");
        for (; missing > 0; missing--) {
            result.append('0');
        }
        result.append(hexString);

        return result.toString();
    }
}
