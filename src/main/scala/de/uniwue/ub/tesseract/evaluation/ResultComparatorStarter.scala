package de.uniwue.ub.tesseract.evaluation

import javax.swing.UIManager

object ResultComparatorStarter {
  def main(args: Array[String]): Unit = {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    } catch {
      case e: Exception =>
    }

    val controller = new ResultComparatorController
  }
}
