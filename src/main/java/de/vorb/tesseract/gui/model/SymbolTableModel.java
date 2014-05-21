package de.vorb.tesseract.gui.model;

import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.table.AbstractTableModel;

import de.vorb.tesseract.util.Symbol;

public class SymbolTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    private final LinkedList<Symbol> symbols;

    public SymbolTableModel() {
        symbols = new LinkedList<>();
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public int getRowCount() {
        return symbols.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int colIndex) {
        switch (colIndex) {
        case 0:
            return rowIndex + 1;
        case 1:
            return symbols.get(rowIndex).getText();
        case 2:
            return symbols.get(rowIndex).getBoundingBox().getX();
        case 3:
            return symbols.get(rowIndex).getBoundingBox().getY();
        case 4:
            return symbols.get(rowIndex).getBoundingBox().getWidth();
        case 5:
            return symbols.get(rowIndex).getBoundingBox().getHeight();
        }

        return null;
    }

    @Override
    public String getColumnName(int colIndex) {
        switch (colIndex) {
        case 0:
            return "No.";
        case 1:
            return "Symbol";
        case 2:
            return "X";
        case 3:
            return "Y";
        case 4:
            return "Width";
        case 5:
            return "Height";
        }

        return "";
    }

    @Override
    public Class<?> getColumnClass(int colIndex) {
        switch (colIndex) {
        case 0:
            return Integer.class;
        case 1:
            return String.class;
        case 2:
            return Integer.class;
        case 3:
            return Integer.class;
        case 4:
            return Integer.class;
        case 5:
            return Integer.class;
        }

        return Object.class;
    }

    public Symbol getSymbol(int index) {
        return symbols.get(index);
    }

    public void replaceAllSymbols(Iterator<Symbol> newSymbols) {
        symbols.clear();

        while (newSymbols.hasNext()) {
            symbols.add(newSymbols.next());
        }

        fireTableDataChanged();
    }
}
