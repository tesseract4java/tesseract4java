package de.vorb.tesseract.gui.view;

import static de.vorb.tesseract.gui.view.Coordinates.unscaled;
import static javax.swing.Box.createHorizontalStrut;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;

import de.vorb.tesseract.gui.event.SelectionListener;
import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.gui.model.SingleSelectionModel;
import de.vorb.tesseract.gui.model.SymbolTableModel;
import de.vorb.tesseract.gui.view.renderer.BoxFileRenderer;
import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.Iterators;
import de.vorb.tesseract.util.Point;
import de.vorb.tesseract.util.Symbol;

public class BoxFilePane extends JPanel implements MainComponent {
    private static final long serialVersionUID = 1L;

    private PageModel model = null;
    private final SingleSelectionModel selectionModel =
            new SingleSelectionModel();

    private final JTextField tfSymbol;
    private final JTable tabSymbols;
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
    private final ChangeListener boxChangeListener = new ChangeListener() {
        @Override
        public void stateChanged(final ChangeEvent e) {
            // don't do anything if no symbol is selected
            if (getCurrentSymbol() == null) {
                return;
            }

            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    final Object source = e.getSource();
                    final Symbol currentSymbol = getCurrentSymbol();

                    // update the text
                    if (source == tfSymbol) {
                        currentSymbol.setText(tfSymbol.getText());
                    }
                    // if the source is one of the JSpinners for x, y, width and
                    // height, update the bounding box
                    else if (source instanceof JSpinner) {
                        final int x = (int) spinX.getValue();
                        final int y = (int) spinY.getValue();
                        final int width = (int) spinWidth.getValue();
                        final int height = (int) spinHeight.getValue();

                        final Box newBbox = new Box(x, y, width, height);

                        currentSymbol.setBoundingBox(newBbox);

                        renderer.render(getModel().getPage(),
                                getModel().getImage(), 1f);
                    }
                }
            });
        }
    };

    /**
     * Create the panel.
     */
    public BoxFilePane() {
        setLayout(new BorderLayout(0, 0));

        JPanel toolbar = new JPanel();
        add(toolbar, BorderLayout.NORTH);

        JSplitPane spMain = new JSplitPane();
        add(spMain, BorderLayout.CENTER);
        toolbar.setLayout(new BorderLayout(0, 0));

        JPanel panel_1 = new JPanel();
        toolbar.add(panel_1, BorderLayout.WEST);

        JLabel lblExample = new JLabel("Symbol");
        panel_1.add(lblExample);

        tfSymbol = new JTextField();
        panel_1.add(tfSymbol);
        tfSymbol.setColumns(5);

        Component horizontalStrut = createHorizontalStrut(10);
        panel_1.add(horizontalStrut);

        JLabel lblLeft = new JLabel("X");
        panel_1.add(lblLeft);

        spinX = new JSpinner();
        spinX.addChangeListener(boxChangeListener);
        panel_1.add(spinX);
        spinX.setPreferredSize(DEFAULT_SPINNER_DIMENSION);
        spinX.setModel(new SpinnerNumberModel(0, 0, null, 1));

        Component horizontalStrut_1 = createHorizontalStrut(5);
        panel_1.add(horizontalStrut_1);

        JLabel lblTop = new JLabel("Y");
        panel_1.add(lblTop);

        spinY = new JSpinner();
        panel_1.add(spinY);
        spinY.setPreferredSize(DEFAULT_SPINNER_DIMENSION);
        spinY.setModel(new SpinnerNumberModel(0, 0, null, 1));

        Component horizontalStrut_2 = createHorizontalStrut(5);
        panel_1.add(horizontalStrut_2);

        JLabel lblRight = new JLabel("Width");
        panel_1.add(lblRight);

        spinWidth = new JSpinner();
        panel_1.add(spinWidth);
        spinWidth.setPreferredSize(DEFAULT_SPINNER_DIMENSION);
        spinWidth.setModel(new SpinnerNumberModel(0, 0, null, 1));

        Component horizontalStrut_3 = createHorizontalStrut(5);
        panel_1.add(horizontalStrut_3);

        JLabel lblBottom = new JLabel("Height");
        panel_1.add(lblBottom);

        spinHeight = new JSpinner();
        panel_1.add(spinHeight);
        spinHeight.setPreferredSize(DEFAULT_SPINNER_DIMENSION);
        spinHeight.setModel(new SpinnerNumberModel(0, 0, null, 1));

        JPanel panel = new JPanel();
        toolbar.add(panel, BorderLayout.EAST);

        JLabel lblZoom = new JLabel("Zoom");
        panel.add(lblZoom);

        JSlider zoomSlider = new JSlider();
        zoomSlider.setMinorTickSpacing(1);
        zoomSlider.setPreferredSize(new Dimension(160, 20));
        zoomSlider.setValue(5);
        zoomSlider.setMajorTickSpacing(1);
        zoomSlider.setMaximum(9);
        zoomSlider.setMinimum(1);
        panel.add(zoomSlider);

        JPanel sidebar = new JPanel();
        spMain.setLeftComponent(sidebar);
        sidebar.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane_1 = new JScrollPane();
        sidebar.add(scrollPane_1, BorderLayout.CENTER);

        tabSymbols = new JTable();
        tabSymbols.setModel(new SymbolTableModel());

        {
            // set column widths
            final TableColumnModel colModel = tabSymbols.getColumnModel();
            colModel.getColumn(0).setPreferredWidth(50);
            colModel.getColumn(0).setMaxWidth(70);
            colModel.getColumn(1).setPreferredWidth(40);
            colModel.getColumn(1).setMaxWidth(60);
            colModel.getColumn(2).setPreferredWidth(40);
            colModel.getColumn(2).setMaxWidth(60);
            colModel.getColumn(3).setPreferredWidth(40);
            colModel.getColumn(3).setMaxWidth(60);
            colModel.getColumn(4).setPreferredWidth(40);
            colModel.getColumn(4).setMaxWidth(60);
        }

        tabSymbols.getSelectionModel().setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        tabSymbols.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        selectionModel.setSelectedIndex(e.getLastIndex());

                        renderer.render(model.getPage(), model.getImage(), 1f);
                    }
                });

        scrollPane_1.setViewportView(tabSymbols);

        scrollPane_1.setMinimumSize(new Dimension(200, 100));
        scrollPane_1.setPreferredSize(new Dimension(260, 10000));
        scrollPane_1.setMaximumSize(new Dimension(310, 10000));

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
                final float scale = 1f;

                final Point p = new Point(unscaled(e.getX(), scale), unscaled(
                        e.getY(), scale));

                final Iterator<Symbol> it =
                        Iterators.symbolIterator(getModel().getPage());

                final ListSelectionModel sel = tabSymbols.getSelectionModel();

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

                renderer.render(model.getPage(), model.getBlackAndWhiteImage(),
                        scale);
            }
        });

        selectionModel.addSelectionListener(new SelectionListener() {
            @Override
            public void selectionChanged(int index) {
                if (index < 0) {
                    return;
                }

                final SymbolTableModel tabModel =
                        (SymbolTableModel) tabSymbols.getModel();
                final Symbol symbol = tabModel.getSymbol(index);

                tfSymbol.setText(symbol.getText());
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
    public void setModel(PageModel model) {
        renderer.render(model.getPage(), model.getBlackAndWhiteImage(), 1f);

        final SymbolTableModel tabModel =
                (SymbolTableModel) tabSymbols.getModel();

        tabModel.replaceAllSymbols(Iterators.symbolIterator(model.getPage()));

        this.model = model;
    }

    @Override
    public PageModel getModel() {
        return model;
    }

    @Override
    public Component asComponent() {
        return this;
    }

    private Symbol getCurrentSymbol() {
        final int index = tabSymbols.getSelectedRow();

        if (index < 0) {
            return null;
        }

        return ((SymbolTableModel) tabSymbols.getModel()).getSymbol(index);
    }
}
