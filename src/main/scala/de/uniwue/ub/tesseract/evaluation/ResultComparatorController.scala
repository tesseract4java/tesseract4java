package de.uniwue.ub.tesseract.evaluation

import java.awt.EventQueue
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.blocking
import scala.util.Failure
import scala.util.Success
import de.uniwue.ub.swing.PageChangeListener
import de.uniwue.ub.tesseract.evaluation.gui.ResultComparator
import de.uniwue.ub.util.Implicits.makeRunnable
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.JOptionPane
import de.uniwue.ub.swing.ProjectChangeListener

class ResultComparatorController
  extends ProjectChangeListener
  with PageChangeListener {

  val view = new ResultComparator

  // make view visible
  EventQueue invokeLater {
    view.setVisible(true)
  }

  def pageChanged(): Unit = {
    val model = view.getModel()
    val page = model.pages.get(model.getPageIndex)
    val image = model.scanDir.resolve(page + ".png")
    val hocr = model.hocrDir.resolve(page + ".png")

    // load the page
    Future {
      blocking {
        ImageIO.read(image.toFile())
      }
    } onComplete {
      case Success(image) =>
        view.getCanvasOriginal().setIcon(new ImageIcon(image))
      case Failure(err) =>
        JOptionPane.showMessageDialog(view, "Could not load the page", "Error",
          JOptionPane.ERROR_MESSAGE)
    }
  }

  def projectChanged(): Unit = {
    Future {
      blocking {
        val dir = Files.newDirectoryStream(scanDir).iterator()
        val pages = new ArrayList[String]()

        while (dir.hasNext()) {
          val file = dir.next()
          if (Files.isRegularFile(file) && !file.getFileName().toString()
            .startsWith(".")) {
            val fname = file.getFileName().toString()
            pages.add(fname.substring(0, fname.lastIndexOf('.')))
          }
        }

        pages
      }
    } onComplete {
      case Success(pages) =>
        view.setModel(new ResultComparatorModel(scanDir, hocrDir, pages))
      case Failure(err) =>
        JOptionPane.showMessageDialog(view, "Could not load the project",
          "Error", JOptionPane.ERROR_MESSAGE)
    }
  }
}
