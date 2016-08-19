package de.vorb.tesseract.gui.view;

import de.vorb.tesseract.gui.model.ImageModel;
import de.vorb.tesseract.tools.preprocessing.DefaultPreprocessor;
import de.vorb.tesseract.tools.preprocessing.Preprocessor;
import de.vorb.tesseract.tools.preprocessing.binarization.Binarization;
import de.vorb.tesseract.tools.preprocessing.binarization.BinarizationMethod;
import de.vorb.tesseract.tools.preprocessing.binarization.Otsu;
import de.vorb.tesseract.tools.preprocessing.binarization.Sauvola;
import de.vorb.tesseract.tools.preprocessing.filter.BlobSizeFilter;
import de.vorb.tesseract.tools.preprocessing.filter.ImageFilter;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class PreprocessingPane extends JPanel implements ImageModelComponent {
    private static final long serialVersionUID = 1L;

    private final JComboBox<BinarizationMethod> comboBinarization;
    private final JSpinner spinnerWindowRadius;

    private final JSpinner spinnerBlobMinSize;
    private final JSpinner spinnerBlobMaxSize;

    private final JButton btnPreview;
    private final JLabel lblPreview;

    private final JButton btnApplyToPage;
    private final JButton btnApplyToAllPages;

    private Optional<ImageModel> imageModel = Optional.empty();

    /**
     * Create the panel.
     */
    public PreprocessingPane() {
        setBackground(Color.WHITE);
        setLayout(new BorderLayout(0, 0));

        JSplitPane splitPane = new JSplitPane();
        add(splitPane);

        JPanel panel_3 = new JPanel();
        splitPane.setLeftComponent(panel_3);
        panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.Y_AXIS));

        JPanel panel = new JPanel();
        panel_3.add(panel);
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(new TitledBorder(
                UIManager.getBorder("TitledBorder.border"), "Binarization",
                TitledBorder.LEADING, TitledBorder.TOP, null,
                new Color(0, 0, 0)), new EmptyBorder(4, 4, 4, 4)));
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[]{0, 0, 0};
        gbl_panel.rowHeights = new int[]{0, 0, 0};
        gbl_panel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
        gbl_panel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        panel.setLayout(gbl_panel);

        JLabel lblMethod = new JLabel("Method");
        GridBagConstraints gbc_lblMethod = new GridBagConstraints();
        gbc_lblMethod.insets = new Insets(0, 0, 5, 5);
        gbc_lblMethod.anchor = GridBagConstraints.EAST;
        gbc_lblMethod.gridx = 0;
        gbc_lblMethod.gridy = 0;
        panel.add(lblMethod, gbc_lblMethod);

        comboBinarization = new JComboBox<>();
        comboBinarization.setBackground(Color.WHITE);
        comboBinarization.setModel(new DefaultComboBoxModel<>(
                BinarizationMethod.values()));
        comboBinarization.setSelectedIndex(0);
        GridBagConstraints gbc_cbBinarization = new GridBagConstraints();
        gbc_cbBinarization.insets = new Insets(0, 0, 5, 0);
        gbc_cbBinarization.fill = GridBagConstraints.HORIZONTAL;
        gbc_cbBinarization.gridx = 1;
        gbc_cbBinarization.gridy = 0;
        panel.add(comboBinarization, gbc_cbBinarization);

        comboBinarization.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (comboBinarization.getSelectedItem() == BinarizationMethod.SAUVOLA) {
                    spinnerWindowRadius.setEnabled(true);
                } else {
                    spinnerWindowRadius.setEnabled(false);
                }
            }
        });

        JLabel lblWindowRadius = new JLabel("Window Radius");
        GridBagConstraints gbc_lblWindowSize = new GridBagConstraints();
        gbc_lblWindowSize.insets = new Insets(0, 0, 0, 5);
        gbc_lblWindowSize.gridx = 0;
        gbc_lblWindowSize.gridy = 1;
        panel.add(lblWindowRadius, gbc_lblWindowSize);

        spinnerWindowRadius = new JSpinner();
        spinnerWindowRadius.setEnabled(false);
        spinnerWindowRadius.setModel(new SpinnerNumberModel(15, 5, 50, 1));
        GridBagConstraints gbc_spinner = new GridBagConstraints();
        gbc_spinner.fill = GridBagConstraints.HORIZONTAL;
        gbc_spinner.gridx = 1;
        gbc_spinner.gridy = 1;
        panel.add(spinnerWindowRadius, gbc_spinner);

        JPanel panel_1 = new JPanel();
        panel_3.add(panel_1);
        panel_1.setBackground(Color.WHITE);
        panel_1.setBorder(new CompoundBorder(new TitledBorder(
                UIManager.getBorder("TitledBorder.border"), "Filters",
                TitledBorder.LEADING, TitledBorder.TOP, null,
                new Color(0, 0, 0)), new EmptyBorder(4, 4, 4, 4)));
        GridBagLayout gbl_panel_1 = new GridBagLayout();
        gbl_panel_1.columnWidths = new int[]{0, 0, 0};
        gbl_panel_1.rowHeights = new int[]{0, 0, 0};
        gbl_panel_1.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        gbl_panel_1.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        panel_1.setLayout(gbl_panel_1);

        JLabel lblBlobMinSizeFilter = new JLabel("Blob size filter (min)");
        lblBlobMinSizeFilter.setBackground(Color.WHITE);
        GridBagConstraints gbc_lblBlobMinSizeFilter = new GridBagConstraints();
        gbc_lblBlobMinSizeFilter.anchor = GridBagConstraints.EAST;
        gbc_lblBlobMinSizeFilter.insets = new Insets(0, 0, 5, 5);
        gbc_lblBlobMinSizeFilter.gridx = 0;
        gbc_lblBlobMinSizeFilter.gridy = 0;
        panel_1.add(lblBlobMinSizeFilter, gbc_lblBlobMinSizeFilter);

        spinnerBlobMinSize = new JSpinner();
        spinnerBlobMinSize.setToolTipText("If this value is 0, it is ignored");
        spinnerBlobMinSize.setModel(new SpinnerNumberModel(0, 0, 300, 1));
        GridBagConstraints gbc_spinnerBlobMinSize = new GridBagConstraints();
        gbc_spinnerBlobMinSize.fill = GridBagConstraints.HORIZONTAL;
        gbc_spinnerBlobMinSize.insets = new Insets(0, 0, 5, 0);
        gbc_spinnerBlobMinSize.gridx = 1;
        gbc_spinnerBlobMinSize.gridy = 0;
        panel_1.add(spinnerBlobMinSize, gbc_spinnerBlobMinSize);

        JLabel lblBlobsizefiltermax = new JLabel("BlobSizeFilter (max)");
        GridBagConstraints gbc_lblBlobsizefiltermax = new GridBagConstraints();
        gbc_lblBlobsizefiltermax.anchor = GridBagConstraints.EAST;
        gbc_lblBlobsizefiltermax.insets = new Insets(0, 0, 0, 5);
        gbc_lblBlobsizefiltermax.gridx = 0;
        gbc_lblBlobsizefiltermax.gridy = 1;
        panel_1.add(lblBlobsizefiltermax, gbc_lblBlobsizefiltermax);

        spinnerBlobMaxSize = new JSpinner();
        spinnerBlobMaxSize.setToolTipText("If this value is 0, it is ignored");
        GridBagConstraints gbc_spinnerBlobMaxSize = new GridBagConstraints();
        gbc_spinnerBlobMaxSize.fill = GridBagConstraints.HORIZONTAL;
        gbc_spinnerBlobMaxSize.gridx = 1;
        gbc_spinnerBlobMaxSize.gridy = 1;
        panel_1.add(spinnerBlobMaxSize, gbc_spinnerBlobMaxSize);

        JPanel panel_4 = new JPanel();
        panel_4.setBackground(Color.WHITE);
        panel_3.add(panel_4);
        panel_4.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 2));

        btnPreview = new JButton("Preview");
        btnPreview.setBackground(Color.WHITE);
        panel_4.add(btnPreview);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        splitPane.setRightComponent(scrollPane);

        lblPreview = new JLabel();
        scrollPane.setViewportView(lblPreview);

        JLabel lblPreviewHeading = new JLabel("Preview");
        lblPreviewHeading.setBorder(new EmptyBorder(0, 4, 0, 0));
        scrollPane.setColumnHeaderView(lblPreviewHeading);

        JPanel panel_2 = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
        flowLayout.setAlignment(FlowLayout.TRAILING);
        add(panel_2, BorderLayout.SOUTH);

        btnApplyToPage = new JButton("Apply to current page");
        btnApplyToPage.setIcon(new ImageIcon(
                PreprocessingPane.class.getResource("/icons/page_white.png")));
        panel_2.add(btnApplyToPage);

        btnApplyToAllPages = new JButton("Apply to all pages");
        btnApplyToAllPages.setIcon(new ImageIcon(
                PreprocessingPane.class.getResource("/icons/page_white_stack.png")));
        panel_2.add(btnApplyToAllPages);

    }

    public JButton getPreviewButton() {
        return btnPreview;
    }

    public JButton getApplyAllPagesButton() {
        return btnApplyToAllPages;
    }

    public JButton getApplyPageButton() {
        return btnApplyToPage;
    }

    public JLabel getPreviewLabel() {
        return lblPreview;
    }

    public Binarization getBinarization() {
        final BinarizationMethod method =
                (BinarizationMethod) comboBinarization.getSelectedItem();

        final Binarization binarization;
        switch (method) {
            case SAUVOLA:
                binarization = new Sauvola((int) spinnerWindowRadius.getValue());
                break;
            case OTSU:
                binarization = new Otsu();
                break;
            default:
                binarization = new Sauvola();
        }

        return binarization;
    }

    public List<ImageFilter> getFilters() {
        int min = (int) spinnerBlobMinSize.getModel().getValue();
        int max = (int) spinnerBlobMaxSize.getModel().getValue();

        if (min == 0 && max == 0) {
            return Collections.emptyList();
        }

        final ImageFilter blobSizeFilter = new BlobSizeFilter(min, max);

        final LinkedList<ImageFilter> result = new LinkedList<>();
        result.add(blobSizeFilter);
        return result;
    }

    public Preprocessor getPreprocessor() {
        return new DefaultPreprocessor(getBinarization(), getFilters());
    }

    public void setPreprocessor(Preprocessor preprocessor) {
        if (preprocessor instanceof DefaultPreprocessor) {
            final DefaultPreprocessor p = (DefaultPreprocessor) preprocessor;
            final Binarization b = p.getBinarization();
            if (b instanceof Sauvola) {
                comboBinarization.setSelectedItem(BinarizationMethod.SAUVOLA);
                spinnerWindowRadius.setValue(((Sauvola) b).getRadius());
            } else if (b instanceof Otsu) {
                comboBinarization.setSelectedItem(BinarizationMethod.OTSU);
            }

            for (ImageFilter f : p.getFilters()) {
                if (f instanceof BlobSizeFilter) {
                    final BlobSizeFilter bsf = (BlobSizeFilter) f;
                    spinnerBlobMinSize.setValue(bsf.getMinArea());
                    spinnerBlobMaxSize.setValue(bsf.getMaxArea());
                    return;
                }
            }

            spinnerBlobMinSize.setValue(0);
            spinnerBlobMaxSize.setValue(0);
        }
    }

    @Override
    public Component asComponent() {
        return this;
    }

    @Override
    public void setImageModel(Optional<ImageModel> model) {
        imageModel = model;

        if (model.isPresent()) {
            lblPreview.setIcon(new ImageIcon(model.get().getPreprocessedImage()));
        } else {
            lblPreview.setIcon(null);
        }
    }

    @Override
    public Optional<ImageModel> getImageModel() {
        return imageModel;
    }

    @Override
    public void freeResources() {
        lblPreview.setIcon(null);
    }
}
