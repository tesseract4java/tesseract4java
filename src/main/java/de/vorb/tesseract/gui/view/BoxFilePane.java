package de.vorb.tesseract.gui.view;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.vorb.tesseract.gui.model.BoxFileModel;

public class BoxFilePane extends JPanel {
  private static final long serialVersionUID = 1L;

  private BoxFileModel model = null;

  /**
   * Create the panel.
   */
  public BoxFilePane() {
    setLayout(new BorderLayout(0, 0));

    JScrollPane scrollPane = new JScrollPane();
    add(scrollPane, BorderLayout.CENTER);

    JLabel label = new JLabel("");
    scrollPane.setViewportView(label);

  }

  public void setModel(BoxFileModel model) {

    // TODO
  }

}
