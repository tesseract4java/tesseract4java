package de.uniwue.ub.tesseract.view;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;

import de.uniwue.ub.tesseract.model.BoxFileModel;

public class BoxFilePane extends JPanel {
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
