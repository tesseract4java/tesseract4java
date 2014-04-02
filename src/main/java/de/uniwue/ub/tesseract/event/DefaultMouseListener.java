package de.uniwue.ub.tesseract.event;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * MouseListener that does nothing. It's purpose is to only have to override the
 * methods one needs.
 * 
 * @author Paul Vorbach
 */
public abstract class DefaultMouseListener implements MouseListener {
  @Override
  public void mouseClicked(MouseEvent e) {
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }

  @Override
  public void mousePressed(MouseEvent e) {
  }

  @Override
  public void mouseReleased(MouseEvent e) {
  }
}
