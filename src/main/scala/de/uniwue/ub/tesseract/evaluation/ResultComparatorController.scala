package de.uniwue.ub.tesseract.evaluation

import java.awt.EventQueue
import java.nio.file.Path
import akka.actor.Actor
import akka.actor.Props
import akka.actor.actorRef2Scala
import de.uniwue.ub.tesseract.evaluation.gui.ResultComparatorView
import de.uniwue.ub.util.ProjectLoader
import de.uniwue.ub.util.ProjectLoader.LoadProject
import de.uniwue.ub.util.ProjectLoader.ProjectLoaded
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.UIManager
import javax.swing.ImageIcon
import scala.concurrent.Future
import scala.concurrent.blocking
import javax.imageio.ImageIO
import scala.util.Success
import scala.util.Failure
import javax.swing.JOptionPane

object ResultComparatorController {
  case class SetFrame(frame: JFrame)
  case class LoadImage(img: Path)
}

class ResultComparatorController extends Actor {
  import ResultComparatorController._
  import ProjectLoader._

  import context.dispatcher

  lazy val view = new ResultComparatorView(this)

  override def preStart(): Unit = {
    EventQueue.invokeLater(new Runnable() {
      override def run(): Unit = {
        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        } catch {
          case e: Exception =>
        }

        // init view
        view.setVisible(true)
      }
    })
  }

  override def receive: Receive = initialized

  def initialized: Receive = {
    case ProjectLoaded(scanDir, hocrDir, pages) =>
      SwingUtilities.invokeLater(new Runnable {
        override def run(): Unit = {
          println("model")
          view.setModel(new ResultComparatorModel(scanDir, hocrDir, pages))
        }
      })
  }

  def pageIndexChanged(id: Int): Unit = {
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

  def loadProject(scanDir: Path, hocrDir: Path): Unit = {
    context.actorOf(Props[ProjectLoader]) ! LoadProject(scanDir, hocrDir)
  }
}
