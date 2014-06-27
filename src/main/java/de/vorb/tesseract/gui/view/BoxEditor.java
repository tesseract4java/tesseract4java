package de.vorb.tesseract.gui.view;

import static de.vorb.tesseract.gui.view.Coordinates.unscaled;
import static javax.swing.Box.createHorizontalStrut;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumnModel;

import com.google.common.base.Optional;

import de.vorb.tesseract.gui.event.SelectionListener;
import de.vorb.tesseract.gui.model.FilteredListModel;
import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.gui.model.SingleSelectionModel;
import de.vorb.tesseract.gui.model.SymbolTableModel;
import de.vorb.tesseract.gui.util.Filter;
import de.vorb.tesseract.gui.util.FilterProvider;
import de.vorb.tesseract.gui.view.renderer.BoxFileRenderer;
import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.Iterators;
import de.vorb.tesseract.util.Point;
import de.vorb.tesseract.util.Symbol;

public class BoxEditor extends JPanel implements MainComponent {
    private static final long serialVersionUID = 1L;

    private Optional<PageModel> model = Optional.absent();
    private final SingleSelectionModel selectionModel =
            new SingleSelectionModel();

    private final JTextField tfSymbol;
    private final FilteredTable<Symbol> tabSymbols;
    private final JSpinner spinX;
    private final JSpinner spinY;
    private final JSpinner spinWidth;
    private final JSpinner spinHeight;
    private final JLabel lblImage;

    private static final Dimension DEFAULT_SPINNER_DIMENSION =
            new Dimension(50, 20);

    private final BoxFileRenderer renderer;

    private final JPopupMenu contextMenu;

    // FIXME concurrency issues
    private final PropertyChangeListener boxChangeListener =
            new PropertyChangeListener() {
                @Override
                public void propertyChange(final PropertyChangeEvent e) {
                    if (!e.getPropertyName().startsWith("SPIN")) {
                        return;
                    }

                    // don't do anything if no symbol is selected
                    if (getCurrentSymbol() == null) {
                        return;
                    }

                    final Object source = e.getSource();
                    final Symbol currentSymbol = getCurrentSymbol();

                    // if the source is one of the JSpinners for x, y, width and
                    // height, update the bounding box
                    if (source instanceof JSpinner) {
                        // get coords
                        final int x = (int) spinX.getValue();
                        final int y = (int) spinY.getValue();
                        final int width = (int) spinWidth.getValue();
                        final int height = (int) spinHeight.getValue();

                        // create new box
                        final Box newBBox = new Box(x, y, width, height);

                        // replace current box with new one
                        currentSymbol.setBoundingBox(newBBox);

                        // re-render the whole model
                        // renderer.render(getModel().getPage(),
                        // getModel().getImageFile(), 1f);
                    }

                    final JTable table = tabSymbols.getTable();
                    table.tableChanged(new TableModelEvent(table.getModel(),
                            table.getSelectedRow()));
                }
            };

    /**
     * Create the panel.
     */
    public BoxEditor() {
        setLayout(new BorderLayout(0, 0));

        final FilteredListModel<Symbol> filteredListModel =
                new FilteredListModel<Symbol>(new DefaultListModel<Symbol>());
        // create table first, so it can be used by the property change listener
        tabSymbols = new FilteredTable<Symbol>(new FilterProvider<Symbol>() {
            @Override
            public Optional<Filter<Symbol>> getFilter(String filterText) {
                final Filter<Symbol> filter;

                if (filterText.isEmpty()) {
                    filter = null;
                } else {
                    // split filter text into terms
                    final String[] terms = filterText.split("\\s+");

                    filter = new Filter<Symbol>() {
                        @Override
                        public boolean accept(Symbol item) {
                            // accept if at least one term is contained
                            final String symbolText = item.getText();
                            for (String term : terms) {
                                if (symbolText.contains(term))
                                    return true;
                            }
                            return false;
                        }
                    };
                }

                return Optional.fromNullable(filter);
            }
        });

        final JTable table = tabSymbols.getTable();
        table.setFillsViewportHeight(true);
        table.setModel(new SymbolTableModel(filteredListModel));

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
                        selectionModel.setSelectedIndex(table.getSelectedRow());

                        // renderer.render(model.getPage(),
                        // model.getImageFile(), 1f);
                    }
                });

        JPanel toolbar = new JPanel();
        toolbar.setBackground(UIManager.getColor("window"));
        add(toolbar, BorderLayout.NORTH);

        JSplitPane spMain = new JSplitPane();
        add(spMain, BorderLayout.CENTER);
        toolbar.setLayout(new BorderLayout(0, 0));

        JPanel panel_1 = new JPanel();
        panel_1.setBackground(UIManager.getColor("window"));
        toolbar.add(panel_1, BorderLayout.WEST);

        JLabel lblExample = new JLabel("Symbol");
        panel_1.add(lblExample);

        tfSymbol = new JTextField();

        // listen for document changes
        tfSymbol.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                change();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                change();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                change();
            }

            private void change() {
                final Symbol current = getCurrentSymbol();
                if (current == null) {
                    return;
                }

                current.setText(tfSymbol.getText());
                table.tableChanged(new TableModelEvent(
                        table.getModel(), table.getSelectedRow(), 1));
            }
        });
        panel_1.add(tfSymbol);
        tfSymbol.setColumns(5);

        Component horizontalStrut = createHorizontalStrut(10);
        panel_1.add(horizontalStrut);

        JLabel lblLeft = new JLabel("X");
        panel_1.add(lblLeft);

        spinX = new JSpinner();
        spinX.addPropertyChangeListener(boxChangeListener);
        panel_1.add(spinX);
        spinX.setPreferredSize(DEFAULT_SPINNER_DIMENSION);
        spinX.setModel(new SpinnerNumberModel(0, 0, null, 1));

        Component horizontalStrut_1 = createHorizontalStrut(5);
        panel_1.add(horizontalStrut_1);

        JLabel lblTop = new JLabel("Y");
        panel_1.add(lblTop);

        spinY = new JSpinner();
        spinY.addPropertyChangeListener(boxChangeListener);
        panel_1.add(spinY);
        spinY.setPreferredSize(DEFAULT_SPINNER_DIMENSION);
        spinY.setModel(new SpinnerNumberModel(0, 0, null, 1));

        Component horizontalStrut_2 = createHorizontalStrut(5);
        panel_1.add(horizontalStrut_2);

        JLabel lblRight = new JLabel("Width");
        panel_1.add(lblRight);

        spinWidth = new JSpinner();
        spinWidth.addPropertyChangeListener(boxChangeListener);
        panel_1.add(spinWidth);
        spinWidth.setPreferredSize(DEFAULT_SPINNER_DIMENSION);
        spinWidth.setModel(new SpinnerNumberModel(0, 0, null, 1));

        Component horizontalStrut_3 = createHorizontalStrut(5);
        panel_1.add(horizontalStrut_3);

        JLabel lblBottom = new JLabel("Height");
        panel_1.add(lblBottom);

        spinHeight = new JSpinner();
        spinHeight.addPropertyChangeListener(boxChangeListener);
        panel_1.add(spinHeight);
        spinHeight.setPreferredSize(DEFAULT_SPINNER_DIMENSION);
        spinHeight.setModel(new SpinnerNumberModel(0, 0, null, 1));

        JPanel panel = new JPanel();
        panel.setBackground(UIManager.getColor("window"));
        toolbar.add(panel, BorderLayout.EAST);

        JLabel lblZoom = new JLabel("Zoom");
        panel.add(lblZoom);

        JSlider zoomSlider = new JSlider();
        zoomSlider.setBackground(UIManager.getColor("window"));
        zoomSlider.setMinorTickSpacing(1);
        zoomSlider.setPreferredSize(new Dimension(160, 20));
        zoomSlider.setValue(5);
        zoomSlider.setMajorTickSpacing(1);
        zoomSlider.setMaximum(9);
        zoomSlider.setMinimum(1);
        panel.add(zoomSlider);

        Dimension tabSize = new Dimension(260, 0);
        tabSymbols.setMinimumSize(tabSize);
        tabSymbols.setPreferredSize(tabSize);
        tabSymbols.setMaximumSize(tabSize);
        spMain.setLeftComponent(tabSymbols);

        JScrollPane scrollPane = new JScrollPane();
        spMain.setRightComponent(scrollPane);

        lblImage = new JLabel("");
        scrollPane.setViewportView(lblImage);

        contextMenu = new JPopupMenu("Box operations");
        contextMenu.add(new JMenuItem("Split box"));
        contextMenu.add(new JSeparator());
        contextMenu.add(new JMenuItem("Merge with previous box"));
        contextMenu.add(new JMenuItem("Merge with next box"));

        lblImage.addMouseListener(new MouseAdapter() {
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

                final float scale = 1f;

                final Point p = new Point(unscaled(e.getX(), scale), unscaled(
                        e.getY(), scale));

                final Iterator<Symbol> it =
                        Iterators.symbolIterator(getModel().get().getPage());

                final ListSelectionModel sel =
                        tabSymbols.getTable().getSelectionModel();

                boolean selection = false;
                for (int i = 0; it.hasNext(); i++) {
                    final Box bbox = it.next().getBoundingBox();

                    if (bbox.contains(p)) {
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

                // renderer.render(model.getPage(),
                // model.getBlackAndWhiteImage(),
                // scale);
            }
        });

        selectionModel.addSelectionListener(new SelectionListener() {
            @Override
            public void selectionChanged(int index) {
                if (index < 0) {
                    return;
                }

                final SymbolTableModel tabModel =
                        (SymbolTableModel) tabSymbols.getTable().getModel();
                final Symbol symbol = tabModel.getSymbol(index);

                final String symbolText = symbol.getText();
                tfSymbol.setText(symbolText);

                // tooltip with codepoints
                final StringBuilder tooltip = new StringBuilder("[ ");
                for (int i = 0; i < symbolText.length(); i++) {
                    tooltip.append(Integer.toHexString(symbolText.codePointAt(
                            i))).append(' ');
                }
                tfSymbol.setToolTipText(tooltip.append(']').toString());

                final Box bbox = symbol.getBoundingBox();
                spinX.setValue(bbox.getX());
                spinY.setValue(bbox.getY());
                spinWidth.setValue(bbox.getWidth());
                spinHeight.setValue(bbox.getHeight());
            }
        });

        renderer = new BoxFileRenderer(lblImage, selectionModel);
    }

    @Override
    public void setModel(Optional<PageModel> model) {
        this.model = model;

        final SymbolTableModel tabModel =
                (SymbolTableModel) tabSymbols.getTable().getModel();

        final DefaultListModel<Symbol> source =
                (DefaultListModel<Symbol>) tabModel.getSource().getSource();

        source.clear();

        if (!model.isPresent()) {
            return;
        } else {
            // fill table model and render the page

            // renderer.render(model.getPage(), model.getBlackAndWhiteImage(),
            // 1f);
            final Iterator<Symbol> it =
                    Iterators.symbolIterator(model.get().getPage());

            while (it.hasNext()) {
                source.addElement(it.next());
            }
        }
    }

    @Override
    public Optional<PageModel> getModel() {
        return model;
    }

    @Override
    public Component asComponent() {
        return this;
    }

    private Symbol getCurrentSymbol() {
        final int index = tabSymbols.getTable().getSelectedRow();

        if (index < 0) {
            return null;
        }

        return ((SymbolTableModel) tabSymbols.getTable().getModel()).getSymbol(
                index);
    }
}
