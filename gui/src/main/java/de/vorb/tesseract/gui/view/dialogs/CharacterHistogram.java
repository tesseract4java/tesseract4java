package de.vorb.tesseract.gui.view.dialogs;

import de.vorb.tesseract.gui.view.TesseractFrame;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class CharacterHistogram extends JFrame {
    private static final long serialVersionUID = 1L;

    private JPanel contentPane;
    private JTable tabHistogram;

    /**
     * Create the frame.
     *
     * @param histogram
     */
    public CharacterHistogram(Map<Character, Integer> histogram) {
        final Toolkit t = Toolkit.getDefaultToolkit();
        final List<Image> appIcons = new LinkedList<>();
        appIcons.add(t.getImage(
                TesseractFrame.class.getResource("/logos/logo_16.png")));
        appIcons.add(t.getImage(
                TesseractFrame.class.getResource("/logos/logo_96.png")));
        appIcons.add(t.getImage(
                TesseractFrame.class.getResource("/logos/logo_256.png")));
        setIconImages(appIcons);

        setTitle("Character Histogram");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        JScrollPane scrollPane = new JScrollPane();
        contentPane.add(scrollPane, BorderLayout.CENTER);

        tabHistogram = new JTable();
        tabHistogram.setFillsViewportHeight(true);
        tabHistogram.setRowSelectionAllowed(false);

        final Set<Entry<Character, Integer>> entries = histogram.entrySet();
        final Object[][] tableData = new Object[entries.size()][2];
        int i = 0;

        for (Entry<Character, Integer> entry : entries) {
            tableData[i][0] = entry.getKey();
            tableData[i++][1] = entry.getValue();
        }

        tabHistogram.setModel(new DefaultTableModel(
                tableData,
                new String[]{
                        "Character", "Count"
                }
        ));
        scrollPane.setViewportView(tabHistogram);

        setMinimumSize(getSize());
    }

}
