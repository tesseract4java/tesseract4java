package de.vorb.tesseract.gui.model;

import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.table.AbstractTableModel;

import de.vorb.tesseract.util.Symbol;

public class SymbolTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    private final LinkedList<Symbol> symbols;

    public SymbolTableModel(Iterator<Symbol> symbols) {
        this.symbols = new LinkedList<>();

        while (symbols.hasNext()) {
            this.symbols.add(symbols.next());
        }
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public int getRowCount() {
        return symbols.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int colIndex) {
        switch (colIndex) {
        case 0:
            return symbols.get(rowIndex).getText();
        case 1:
            return symbols.get(rowIndex).getBoundingBox().getX();
        case 2:
            return symbols.get(rowIndex).getBoundingBox().getY();
        case 3:
            return symbols.get(rowIndex).getBoundingBox().getWidth();
        case 4:
            return symbols.get(rowIndex).getBoundingBox().getHeight();
        }

        return null;
    }

    public Symbol getSymbol(int index) {
        return symbols.get(index);
    }
}
