package de.vorb.tesseract.gui.view;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JSplitPane;
import javax.swing.JButton;
import java.awt.FlowLayout;

public class GlyphExportPane extends JPanel {
    private static final long serialVersionUID = 1L;

    private final GlyphSelectionPane glyphSelection;
    private final GlyphListPane glyphList;

    /**
     * Create the panel.
     */
    public GlyphExportPane() {
        super();
        setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel.getLayout();
        flowLayout.setAlignment(FlowLayout.TRAILING);
        add(panel, BorderLayout.SOUTH);

        JButton btnExport = new JButton("Export ...");
        panel.add(btnExport);

        JSplitPane splitPane = new JSplitPane();
        add(splitPane, BorderLayout.CENTER);

        glyphSelection = new GlyphSelectionPane();
        glyphList = new GlyphListPane();

        splitPane.setLeftComponent(glyphSelection);
        splitPane.setRightComponent(glyphList);
    }

}
