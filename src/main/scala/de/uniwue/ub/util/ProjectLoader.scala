package de.uniwue.ub.util

import java.nio.file.Path
import akka.actor.Actor
import scala.concurrent.blocking
import java.nio.file.Files
import java.util.ArrayList

object ProjectLoader {
  case class LoadProject(scanDir: Path, hocrDir: Path)
  case class ProjectLoaded(scanDir: Path, hocrDir: Path, files: ArrayList[String])
}

class ProjectLoader extends Actor {
  import ProjectLoader._

  override def receive: Receive = {
    case LoadProject(scanDir, hocrDir) =>
      val pages = blocking {
        val dir = Files.newDirectoryStream(scanDir).iterator()
        val files = new ArrayList[String]()
        while (dir.hasNext()) {
          val file = dir.next()
          if (Files.isRegularFile(file) && !file.getFileName().toString()
            .startsWith(".")) {
            val fname = file.getFileName().toString()
            files.add(fname.substring(0, fname.lastIndexOf('.')))
          }
        }

        files
      }

      sender ! ProjectLoaded(scanDir, hocrDir, pages)
  }
}
