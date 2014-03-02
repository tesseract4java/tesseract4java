package de.uniwue.ub.tesseract.evaluation

import java.nio.file.Path
import java.util.ArrayList
import java.util.Observable
import java.util.HashMap
import java.util.Collections
import java.util.List

object ResultComparatorModel {
  // TODO
  val pageIndexChanged = -1
  val correctionChanged = 0
  val otherChanged = 1
}

class ResultComparatorModel(
  val scanDir: Path,
  val hocrDir: Path,
  val pages: ArrayList[String]) extends Observable {

  // a new model always changes sth.
  setChanged()

  private var pageIndexChanged = true
  private var pageListChanged = true
  private var pageIndex = 0
  private val errors: ArrayList[ArrayList[WordModel]] = new ArrayList(pages.size)

  // initialize `errors`
  for (i <- 0 until pages.size)
    errors.add(new ArrayList())

  def getPageIndex(): Int = pageIndex
  def getMinPageIndex(): Int = 0
  def getMaxPageIndex(): Int = pages.size() - 1

  def setPageIndex(i: Int): Unit = {
    require(i >= 0 && i < pages.size, "pageIndex out of bounds")

    pageIndex = i

    setPageIndexChanged()
    notifyObservers()
  }

  def getPageModel(): List[WordModel] = {
    Collections.unmodifiableList(errors.get(pageIndex))
  }

  protected def setPageIndexChanged(): Unit = {
    pageIndexChanged = true
    setChanged()
  }

  def hasPageIndexChanged(): Boolean = {
    pageIndexChanged
  }

  def hasPageListChanged(): Boolean =
    if (pageListChanged) {
      pageListChanged = false
      true
    } else {
      false
    }
}
