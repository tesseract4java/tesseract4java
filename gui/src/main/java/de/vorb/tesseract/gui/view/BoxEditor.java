package de.vorb.tesseract.gui.view;

import de.vorb.tesseract.gui.model.BoxFileModel;
import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.gui.model.Scale;
import de.vorb.tesseract.gui.model.SingleSelectionModel;
import de.vorb.tesseract.gui.model.SymbolTableModel;
import de.vorb.tesseract.gui.util.Filter;
import de.vorb.tesseract.gui.view.renderer.BoxFileRenderer;
import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.Point;
import de.vorb.tesseract.util.Symbol;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumnModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static de.vorb.tesseract.gui.model.Scale.scaled;
import static de.vorb.tesseract.gui.model.Scale.unscaled;

public class BoxEditor extends JPanel implements BoxFileModelComponent {
    private static final long serialVersionUID = 1L;

    private static final Dimension DEFAULT_SPINNER_DIMENSION =
            new Dimension(50, 20);

    private final BoxFileRenderer renderer;

    // state
    private final Scale scale;
    private boolean changed = false;

    private Optional<BoxFileModel> model = Optional.empty();
    private Optional<PageModel> pageModel = Optional.empty();

    private final SingleSelectionModel selectionModel =
            new SingleSelectionModel();

    private final FilteredTable<Symbol> tabSymbols;
    private final JLabel lblCanvas;
    private final JPopupMenu contextMenu;
    private final JTextField tfSymbol;
    private final JSpinner spinnerX;
    private final JSpinner spinnerY;
    private final JSpinner spinnerWidth;
    private final JSpinner spinnerHeight;

    // events
    private final List<ChangeListener> changeListeners = new ArrayList<>();

    private final PropertyChangeListener spinnerListener =
            new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (!evt.getPropertyName().startsWith("SPIN")) {
                        return;
                    }

                    // don't do anything if no symbol is selected
                    final Optional<Symbol> currentSymbol = getSelectedSymbol();
                    if (!currentSymbol.isPresent()) {
                        return;
                    }

                    final Object source = evt.getSource();

                    // if the source is one of the JSpinners for x, y, width and
                    // height, update the bounding box
                    if (source instanceof JSpinner) {
                        // get coordinates
                        final int x = (int) spinnerX.getValue();
                        final int y = (int) spinnerY.getValue();
                        final int width = (int) spinnerWidth.getValue();
                        final int height = (int) spinnerHeight.getValue();

                        // update bounding box
                        final Box boundingBox = currentSymbol.get().getBoundingBox();
                        boundingBox.setX(x);
                        boundingBox.setY(y);
                        boundingBox.setWidth(width);
                        boundingBox.setHeight(height);

                        // re-render the whole model
                        renderer.render(getBoxFileModel(), scale.current());
                    }

                    // propagate table change
                    final JTable table = tabSymbols.getTable();
                    table.tableChanged(new TableModelEvent(table.getModel(),
                            table.getSelectedRow()));

                    changed = true;
                }
            };

    /**
     * Create the panel.
     */
    public BoxEditor(final Scale scale) {
        setLayout(new BorderLayout(0, 0));

        renderer = new BoxFileRenderer(this);

        this.scale = scale;

        // create table first, so it can be used by the property change listener
        tabSymbols = new FilteredTable<>(new SymbolTableModel(),
                filterText -> {
                    final Filter<Symbol> filter;

                    if (filterText.isEmpty()) {
                        filter = null;
                    } else {
                        // split filter text into terms
                        final String[] terms =
                                filterText.toLowerCase().split("\\s+");

                        filter = item -> {
                            // accept if at least one term is contained
                            final String symbolText =
                                    item.getText().toLowerCase();

                            for (String term : terms) {
                                if (symbolText.contains(term)) {
                                    return true;
                                }
                            }
                            return false;
                        };
                    }

                    return Optional.ofNullable(filter);
                });

        tabSymbols.getListModel().addListDataListener(new ListDataListener() {
            private long last = 0L;

            @Override
            public void intervalRemoved(ListDataEvent evt) {
                update();
            }

            @Override
            public void intervalAdded(ListDataEvent evt) {
                update();
            }

            @Override
            public void contentsChanged(ListDataEvent evt) {
                update();
            }

            private void update() {
                long now = System.currentTimeMillis();
                if (now - last > 1000) {
                    renderer.render(model, scale.current());
                }
                last = now;
            }
        });

        final JTable table = tabSymbols.getTable();
        table.setFillsViewportHeight(true);

        {
            // set column widths
            final TableColumnModel colModel = table.getColumnModel();
            colModel.getColumn(0).setPreferredWidth(30);
            colModel.getColumn(0).setMaxWidth(40);
            colModel.getColumn(1).setPreferredWidth(50);
            colModel.getColumn(1).setMaxWidth(70);
            colModel.getColumn(2).setPreferredWidth(40);
            colModel.getColumn(2).setMaxWidth(60);
            colModel.getColumn(3).setPreferredWidth(40);
            colModel.getColumn(3).setMaxWidth(60);
            colModel.getColumn(4).setPreferredWidth(40);
            colModel.getColumn(4).setMaxWidth(60);
            colModel.getColumn(5).setPreferredWidth(40);
            colModel.getColumn(5).setMaxWidth(60);
        }

        table.getSelectionModel().setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        table.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        final int selectedRow = table.getSelectedRow();
                        selectionModel.setSelectedIndex(selectedRow);

                        if (!getSelectedSymbol().isPresent()) {
                            return;
                        }

                        final Box boundingBox = getSelectedSymbol().get().getBoundingBox();

                        final Rectangle scaled = new Rectangle(
                                scaled(boundingBox.getX() - 10, scale.current()),
                                scaled(boundingBox.getY() - 10, scale.current()),
                                scaled(boundingBox.getWidth() + 10, scale.current()),
                                scaled(boundingBox.getHeight() + 10, scale.current()));

                        lblCanvas.scrollRectToVisible(scaled);

                        Rectangle cell = tabSymbols.getTable().getCellRect(
                                selectedRow, 0, true);
                        tabSymbols.getTable().scrollRectToVisible(cell);

                        renderer.render(model, scale.current());
                    }
                });

        JPanel toolbar = new JPanel();
        toolbar.setBorder(new EmptyBorder(0, 4, 4, 4));
        toolbar.setBackground(UIManager.getColor("window"));
        add(toolbar, BorderLayout.NORTH);

        JSplitPane splitMain = new JSplitPane();
        add(splitMain, BorderLayout.CENTER);
        GridBagLayout gbl_toolbar = new GridBagLayout();
        gbl_toolbar.columnWidths = new int[]{0, 56, 15, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 36, 0, 0};
        gbl_toolbar.rowHeights = new int[]{0, 0};
        gbl_toolbar.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_toolbar.rowWeights = new double[]{0.0, Double.MIN_VALUE};
        toolbar.setLayout(gbl_toolbar);

        JLabel lblSymbol = new JLabel("Symbol");
        GridBagConstraints gbc_lblSymbol = new GridBagConstraints();
        gbc_lblSymbol.insets = new Insets(0, 0, 0, 5);
        gbc_lblSymbol.anchor = GridBagConstraints.EAST;
        gbc_lblSymbol.gridx = 0;
        gbc_lblSymbol.gridy = 0;
        toolbar.add(lblSymbol, gbc_lblSymbol);

        tfSymbol = new JTextField();
        tfSymbol.addActionListener(e -> {
            final Optional<Symbol> symbol = getSelectedSymbol();

            if (!symbol.isPresent()) {
                return;
            }

            symbol.get().setText(tfSymbol.getText());
            table.tableChanged(new TableModelEvent(table.getModel(),
                    table.getSelectedRow()));

            int newSel = table.getSelectedRow() + 1;
            if (newSel < table.getModel().getRowCount()) {
                table.getSelectionModel().setSelectionInterval(newSel,
                        newSel);
            }
        });

        GridBagConstraints gbc_tfSymbol = new GridBagConstraints();
        gbc_tfSymbol.insets = new Insets(0, 0, 0, 5);
        gbc_tfSymbol.fill = GridBagConstraints.HORIZONTAL;
        gbc_tfSymbol.gridx = 1;
        gbc_tfSymbol.gridy = 0;
        toolbar.add(tfSymbol, gbc_tfSymbol);
        tfSymbol.setColumns(6);

        Component hsDiv1 = javax.swing.Box.createHorizontalStrut(10);
        GridBagConstraints gbc_hsDiv1 = new GridBagConstraints();
        gbc_hsDiv1.insets = new Insets(0, 0, 0, 5);
        gbc_hsDiv1.gridx = 2;
        gbc_hsDiv1.gridy = 0;
        toolbar.add(hsDiv1, gbc_hsDiv1);

        JLabel lblX = new JLabel("X");
        GridBagConstraints gbc_lblX = new GridBagConstraints();
        gbc_lblX.insets = new Insets(0, 0, 0, 5);
        gbc_lblX.gridx = 3;
        gbc_lblX.gridy = 0;
        toolbar.add(lblX, gbc_lblX);

        spinnerX = new JSpinner();
        spinnerX.setToolTipText("x coordinate");
        spinnerX.setPreferredSize(DEFAULT_SPINNER_DIMENSION);
        GridBagConstraints gbc_spX = new GridBagConstraints();
        gbc_spX.insets = new Insets(0, 0, 0, 5);
        gbc_spX.gridx = 4;
        gbc_spX.gridy = 0;
        toolbar.add(spinnerX, gbc_spX);

        JLabel lblY = new JLabel("Y");
        GridBagConstraints gbc_lblY = new GridBagConstraints();
        gbc_lblY.insets = new Insets(0, 0, 0, 5);
        gbc_lblY.gridx = 5;
        gbc_lblY.gridy = 0;
        toolbar.add(lblY, gbc_lblY);

        spinnerY = new JSpinner();
        spinnerY.setPreferredSize(DEFAULT_SPINNER_DIMENSION);
        spinnerY.setToolTipText("y coordinate");
        GridBagConstraints gbc_spY = new GridBagConstraints();
        gbc_spY.insets = new Insets(0, 0, 0, 5);
        gbc_spY.gridx = 6;
        gbc_spY.gridy = 0;
        toolbar.add(spinnerY, gbc_spY);

        JLabel lblWidth = new JLabel("W");
        GridBagConstraints gbc_lblWidth = new GridBagConstraints();
        gbc_lblWidth.insets = new Insets(0, 0, 0, 5);
        gbc_lblWidth.gridx = 7;
        gbc_lblWidth.gridy = 0;
        toolbar.add(lblWidth, gbc_lblWidth);

        spinnerWidth = new JSpinner();
        spinnerWidth.setToolTipText("Width");
        spinnerWidth.setPreferredSize(DEFAULT_SPINNER_DIMENSION);
        GridBagConstraints gbc_spWidth = new GridBagConstraints();
        gbc_spWidth.insets = new Insets(0, 0, 0, 5);
        gbc_spWidth.gridx = 8;
        gbc_spWidth.gridy = 0;
        toolbar.add(spinnerWidth, gbc_spWidth);

        JLabel lblHeight = new JLabel("H");
        GridBagConstraints gbc_lblHeight = new GridBagConstraints();
        gbc_lblHeight.insets = new Insets(0, 0, 0, 5);
        gbc_lblHeight.gridx = 9;
        gbc_lblHeight.gridy = 0;
        toolbar.add(lblHeight, gbc_lblHeight);

        spinnerHeight = new JSpinner();
        spinnerHeight.setToolTipText("Height");
        spinnerHeight.setPreferredSize(DEFAULT_SPINNER_DIMENSION);
        GridBagConstraints gbc_spHeight = new GridBagConstraints();
        gbc_spHeight.insets = new Insets(0, 0, 0, 5);
        gbc_spHeight.gridx = 10;
        gbc_spHeight.gridy = 0;
        toolbar.add(spinnerHeight, gbc_spHeight);

        Component horizontalStrut = javax.swing.Box.createHorizontalStrut(10);
        GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
        gbc_horizontalStrut.insets = new Insets(0, 0, 0, 5);
        gbc_horizontalStrut.gridx = 11;
        gbc_horizontalStrut.gridy = 0;
        toolbar.add(horizontalStrut, gbc_horizontalStrut);

        final Insets btnMargin = new Insets(2, 4, 2, 4);

        final JButton btnZoomOut = new JButton();
        btnZoomOut.setMargin(btnMargin);
        btnZoomOut.setToolTipText("Zoom out");
        btnZoomOut.setBackground(Color.WHITE);
        btnZoomOut.setIcon(new ImageIcon(BoxEditor.class.getResource("/icons/magnifier_zoom_out.png")));
        GridBagConstraints gbc_btnZoomOut = new GridBagConstraints();
        gbc_btnZoomOut.insets = new Insets(0, 0, 0, 5);
        gbc_btnZoomOut.gridx = 12;
        gbc_btnZoomOut.gridy = 0;
        toolbar.add(btnZoomOut, gbc_btnZoomOut);

        final JButton btnZoomIn = new JButton();
        btnZoomIn.setMargin(btnMargin);
        btnZoomIn.setToolTipText("Zoom in");
        btnZoomIn.setBackground(Color.WHITE);
        btnZoomIn.setIcon(new ImageIcon(BoxEditor.class.getResource("/icons/magnifier_zoom_in.png")));
        GridBagConstraints gbc_btnZoomIn = new GridBagConstraints();
        gbc_btnZoomIn.gridx = 13;
        gbc_btnZoomIn.gridy = 0;
        toolbar.add(btnZoomIn, gbc_btnZoomIn);

        btnZoomOut.addActionListener(evt -> {
            if (scale.hasPrevious()) {
                renderer.render(getBoxFileModel(), scale.previous());
            }

            if (!scale.hasPrevious()) {
                btnZoomOut.setEnabled(false);
            }

            btnZoomIn.setEnabled(true);
        });

        btnZoomIn.addActionListener(evt -> {
            if (scale.hasNext()) {
                renderer.render(getBoxFileModel(), scale.next());
            }

            if (!scale.hasNext()) {
                btnZoomIn.setEnabled(false);
            }

            btnZoomOut.setEnabled(true);
        });

        Dimension tabSize = new Dimension(260, 0);
        tabSymbols.setMinimumSize(tabSize);
        tabSymbols.setPreferredSize(tabSize);
        tabSymbols.setMaximumSize(tabSize);
        splitMain.setLeftComponent(tabSymbols);

        JScrollPane scrollPane = new JScrollPane();
        splitMain.setRightComponent(scrollPane);

        lblCanvas = new JLabel("");
        scrollPane.setViewportView(lblCanvas);

        contextMenu = new JPopupMenu("Box operations");
        contextMenu.add(new JMenuItem("Split box"));
        contextMenu.add(new JSeparator());
        contextMenu.add(new JMenuItem("Merge with previous box"));
        contextMenu.add(new JMenuItem("Merge with next box"));

        lblCanvas.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                clicked(e);
            }

            public void mouseReleased(MouseEvent e) {
                clicked(e);
            }

            private void clicked(MouseEvent e) {
                if (!model.isPresent()) {
                    // ignore clicks if no model is present
                    return;
                }

                final Point p = new Point(unscaled(e.getX(), scale.current()),
                        unscaled(e.getY(), scale.current()));

                final Iterator<Symbol> it =
                        model.get().getBoxes().iterator();

                final ListSelectionModel sel =
                        tabSymbols.getTable().getSelectionModel();

                boolean selection = false;
                for (int i = 0; it.hasNext(); i++) {
                    final Box boundingBox = it.next().getBoundingBox();

                    if (boundingBox.contains(p)) {
                        selection = true;
                        selectionModel.setSelectedIndex(i);
                        sel.setSelectionInterval(i, i);
                        break;
                    }
                }

                if (!selection) {
                    selectionModel.setSelectedIndex(-1);
                    sel.setSelectionInterval(-1, -1);
                } else if (e.isPopupTrigger()) {
                    contextMenu.show(e.getComponent(), e.getX(), e.getY());
                }

                renderer.render(model, scale.current());
            }
        });

        selectionModel.addSelectionListener(index -> {
            if (index < 0) {
                return;
            }

            final SymbolTableModel tabModel =
                    (SymbolTableModel) tabSymbols.getTable().getModel();
            final Symbol symbol = tabModel.getSymbol(index);

            final String symbolText = symbol.getText();
            tfSymbol.setText(symbolText);

            // tooltip with codePoints
            final StringBuilder tooltip = new StringBuilder("[ ");
            for (int i = 0; i < symbolText.length(); i++) {
                tooltip.append(Integer.toHexString(symbolText.codePointAt(i)))
                        .append(' ');
            }
            tfSymbol.setToolTipText(tooltip.append(']').toString());

            final Box boundingBox = symbol.getBoundingBox();
            spinnerX.setValue(boundingBox.getX());
            spinnerY.setValue(boundingBox.getY());
            spinnerWidth.setValue(boundingBox.getWidth());
            spinnerHeight.setValue(boundingBox.getHeight());

            lblCanvas.scrollRectToVisible(boundingBox.toRectangle());
        });

        spinnerX.addPropertyChangeListener(spinnerListener);
        spinnerY.addPropertyChangeListener(spinnerListener);
        spinnerWidth.addPropertyChangeListener(spinnerListener);
        spinnerHeight.addPropertyChangeListener(spinnerListener);
    }

    @Override
    public void setBoxFileModel(Optional<BoxFileModel> model) {
        this.model = model;

        final SymbolTableModel tabModel =
                (SymbolTableModel) tabSymbols.getTable().getModel();

        final DefaultListModel<Symbol> source =
                (DefaultListModel<Symbol>) tabModel.getSource().getSource();

        source.clear();

        if (model.isPresent()) {
            // fill table model and render the page
            model.get().getBoxes().forEach(source::addElement);
        }

        renderer.render(model, scale.current());
    }

    @Override
    public void setPageModel(Optional<PageModel> model) {
        if (model.isPresent()) {
            setBoxFileModel(Optional.of(model.get().toBoxFileModel()));
            pageModel = model;
        } else {
            setBoxFileModel(Optional.empty());
            pageModel = model;
        }
    }

    @Override
    public Optional<BoxFileModel> getBoxFileModel() {
        return model;
    }

    @Override
    public Optional<PageModel> getPageModel() {
        return pageModel;
    }

    public JLabel getCanvas() {
        return lblCanvas;
    }

    public FilteredTable<Symbol> getSymbols() {
        return tabSymbols;
    }

    @Override
    public Component asComponent() {
        return this;
    }

    public Optional<Symbol> getSelectedSymbol() {
        final int index = tabSymbols.getTable().getSelectedRow();

        if (index < 0) {
            return Optional.empty();
        }

        return Optional.of(((SymbolTableModel) tabSymbols.getTable().getModel())
                .getSymbol(index));
    }

    public JTextField getSymbolTextField() {
        return tfSymbol;
    }

    public JSpinner getXSpinner() {
        return spinnerX;
    }

    public JSpinner getYSpinner() {
        return spinnerY;
    }

    public JSpinner getWidthSpinner() {
        return spinnerWidth;
    }

    public JSpinner getHeightSpinner() {
        return spinnerHeight;
    }

    public void addChangeListener(ChangeListener listener) {
        changeListeners.add(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeListeners.remove(listener);
    }

    public boolean hasChanged() {
        return changed;
    }

    public void setChanged(boolean b) {
        changed = b;

        final ChangeEvent evt = new ChangeEvent(this);
        for (ChangeListener l : changeListeners) {
            l.stateChanged(evt);
        }
    }

    @Override
    public void freeResources() {
        lblCanvas.setIcon(null);
    }
}
