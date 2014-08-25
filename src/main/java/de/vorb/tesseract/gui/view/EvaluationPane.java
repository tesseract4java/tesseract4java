package de.vorb.tesseract.gui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import com.google.common.base.Optional;

import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.gui.model.Scale;
import de.vorb.tesseract.gui.view.renderer.EvaluationPaneRenderer;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Font;

public class EvaluationPane extends JPanel implements PageModelComponent {
    private static final long serialVersionUID = 1L;

    private static final Insets BUTTON_MARGIN = new Insets(2, 4, 2, 4);

    private final Scale scale;
    private final EvaluationPaneRenderer renderer;

    private Optional<PageModel> pageModel;
    private final JLabel lblOriginal;
    private final JTextArea textAreaTranscript;
    private final JButton btnSaveTranscription;
    private final JButton btnGenerateReport;

    private final JButton btnZoomOut;
    private final JButton btnZoomIn;

    /**
     * Create the panel.
     * 
     * @param scale
     */
    public EvaluationPane(final Scale scale) {
        setLayout(new BorderLayout(0, 0));

        this.scale = scale;
        renderer = new EvaluationPaneRenderer(this);

        JSplitPane splitPane = new JSplitPane();
        add(splitPane, BorderLayout.CENTER);
        splitPane.setResizeWeight(0.5);

        JScrollPane scrollPane = new JScrollPane();
        splitPane.setLeftComponent(scrollPane);

        JLabel lblOriginalTitle = new JLabel("Original");
        lblOriginalTitle.setBorder(new EmptyBorder(0, 4, 0, 0));
        scrollPane.setColumnHeaderView(lblOriginalTitle);

        lblOriginal = new JLabel("");
        scrollPane.setViewportView(lblOriginal);

        JScrollPane scrollPane_1 = new JScrollPane();
        splitPane.setRightComponent(scrollPane_1);

        textAreaTranscript = new JTextArea();
        textAreaTranscript.setEnabled(false);
        scrollPane_1.setViewportView(textAreaTranscript);

        JLabel lblReferenceText = new JLabel("Transcription");
        lblReferenceText.setBorder(new EmptyBorder(0, 4, 0, 0));
        scrollPane_1.setColumnHeaderView(lblReferenceText);

        JPanel panel_1 = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        add(panel_1, BorderLayout.SOUTH);

        btnSaveTranscription = new JButton("Save");
        btnSaveTranscription.setEnabled(false);
        btnSaveTranscription.setToolTipText("Save Transcription");
        panel_1.add(btnSaveTranscription);

        btnGenerateReport = new JButton("Generate Report");
        btnGenerateReport.setEnabled(false);
        panel_1.add(btnGenerateReport);

        JPanel panel_2 = new JPanel();
        panel_2.setBorder(new EmptyBorder(2, 2, 2, 2));
        add(panel_2, BorderLayout.NORTH);
        panel_2.setBackground(Color.WHITE);
        GridBagLayout gbl_panel_2 = new GridBagLayout();
        gbl_panel_2.columnWidths = new int[]{67, 29, 0, 0};
        gbl_panel_2.rowHeights = new int[]{25, 0};
        gbl_panel_2.columnWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_panel_2.rowWeights = new double[]{0.0, Double.MIN_VALUE};
        panel_2.setLayout(gbl_panel_2);
                
                JLabel lblOcrevaluation = new JLabel("ocrevalUAtion");
                lblOcrevaluation.setFont(new Font("Tahoma", Font.PLAIN, 12));
                GridBagConstraints gbc_lblOcrevaluation = new GridBagConstraints();
                gbc_lblOcrevaluation.anchor = GridBagConstraints.WEST;
                gbc_lblOcrevaluation.insets = new Insets(0, 0, 0, 5);
                gbc_lblOcrevaluation.gridx = 0;
                gbc_lblOcrevaluation.gridy = 0;
                panel_2.add(lblOcrevaluation, gbc_lblOcrevaluation);
                        
                                btnZoomOut = new JButton();
                                btnZoomOut.setAlignmentX(Component.RIGHT_ALIGNMENT);
                                btnZoomOut.setMargin(BUTTON_MARGIN);
                                btnZoomOut.setBackground(Color.WHITE);
                                btnZoomOut.setIcon(new ImageIcon(
                                        EvaluationPane.class.getResource("/icons/zoom_out.png")));
                                GridBagConstraints gbc_btnZoomOut = new GridBagConstraints();
                                gbc_btnZoomOut.anchor = GridBagConstraints.EAST;
                                gbc_btnZoomOut.insets = new Insets(0, 0, 0, 5);
                                gbc_btnZoomOut.gridx = 1;
                                gbc_btnZoomOut.gridy = 0;
                                panel_2.add(btnZoomOut, gbc_btnZoomOut);
                                
                                        btnZoomOut.addActionListener(new ActionListener() {
                                            public void actionPerformed(ActionEvent evt) {
                                                if (scale.hasPrevious()) {
                                                    renderer.render(getPageModel(), scale.previous());
                                                }
                                
                                                if (!scale.hasPrevious()) {
                                                    btnZoomOut.setEnabled(false);
                                                }
                                
                                                btnZoomIn.setEnabled(true);
                                            }
                                        });
                
                        btnZoomIn = new JButton();
                        btnZoomIn.setAlignmentX(Component.RIGHT_ALIGNMENT);
                        btnZoomIn.setMargin(BUTTON_MARGIN);
                        btnZoomIn.setBackground(Color.WHITE);
                        btnZoomIn.setIcon(new ImageIcon(
                                EvaluationPane.class.getResource("/icons/zoom_in.png")));
                        GridBagConstraints gbc_btnZoomIn = new GridBagConstraints();
                        gbc_btnZoomIn.anchor = GridBagConstraints.EAST;
                        gbc_btnZoomIn.gridx = 2;
                        gbc_btnZoomIn.gridy = 0;
                        panel_2.add(btnZoomIn, gbc_btnZoomIn);
                
                        btnZoomIn.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent evt) {
                                if (scale.hasNext()) {
                                    renderer.render(getPageModel(), scale.next());
                                }
                
                                if (!scale.hasNext()) {
                                    btnZoomIn.setEnabled(false);
                                }
                
                                btnZoomOut.setEnabled(true);
                            }
                        });
    }

    @Override
    public Component asComponent() {
        return this;
    }

    public JButton getGenerateReportButton() {
        return btnGenerateReport;
    }

    public JButton getSaveTranscriptionButton() {
        return btnSaveTranscription;
    }

    public JLabel getOriginal() {
        return lblOriginal;
    }

    public JTextArea getTextAreaTranscript() {
        return textAreaTranscript;
    }

    @Override
    public void setPageModel(Optional<PageModel> model) {
        this.pageModel = model;

        renderer.render(model, scale.current());
    }

    @Override
    public Optional<PageModel> getPageModel() {
        return pageModel;
    }
}
