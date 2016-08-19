package de.vorb.tesseract.gui.view;

import de.vorb.tesseract.gui.model.SymbolOrder;
import de.vorb.tesseract.util.Symbol;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SymbolVariantList extends JPanel {
    private static final long serialVersionUID = 1L;
    private final JList<Symbol> glyphList;
    private final JComboBox<SymbolOrder> cbOrdering;
    private final JMenuItem showInBoxEditor;
    private final JMenuItem compareToPrototype;

    /**
     * Create the panel.
     */
    public SymbolVariantList() {
        super();
        setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        FlowLayout fl_panel = (FlowLayout) panel.getLayout();
        fl_panel.setAlignment(FlowLayout.LEADING);
        add(panel, BorderLayout.NORTH);

        JLabel lblVariants = new JLabel("Variants");
        panel.add(lblVariants);

        Component horizontalStrut = Box.createHorizontalStrut(20);
        panel.add(horizontalStrut);

        JLabel lblOrder = new JLabel("Order by");
        panel.add(lblOrder);

        cbOrdering = new JComboBox<>();
        cbOrdering.setBackground(Color.WHITE);
        cbOrdering.setModel(new DefaultComboBoxModel<>(
                SymbolOrder.values()));
        panel.add(cbOrdering);

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, BorderLayout.CENTER);

        glyphList = new JList<>();
        glyphList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        glyphList.setVisibleRowCount(-1);
        scrollPane.setViewportView(glyphList);

        final JPopupMenu popupMenu = new JPopupMenu();
        showInBoxEditor = new JMenuItem("Show in Box Editor");
        compareToPrototype = new JMenuItem("Show features");
        popupMenu.add(showInBoxEditor);
        popupMenu.add(compareToPrototype);
        glyphList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }

            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    final int selection =
                            glyphList.locationToIndex(e.getPoint());

                    glyphList.setSelectedIndex(selection);
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    public JList<Symbol> getList() {
        return glyphList;
    }

    public JComboBox<SymbolOrder> getOrderingComboBox() {
        return cbOrdering;
    }

    public JMenuItem getShowInBoxEditor() {
        return showInBoxEditor;
    }

    public JMenuItem getCompareToPrototype() {
        return compareToPrototype;
    }
}
