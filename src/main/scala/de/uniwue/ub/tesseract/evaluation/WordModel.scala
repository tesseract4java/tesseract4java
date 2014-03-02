package de.uniwue.ub.tesseract.evaluation

class WordModel(val word: String, private var correct: Boolean) {
  private var correction: Option[String] = None

  def isCorrect() = correct

  def setCorrect(isCorrect: Boolean): Unit = {
    correct = isCorrect
  }

  def getCorrection(): String = correction match {
    case None => word
    case Some(correction) => correction
  }

  def setCorrection(word: String): Unit = {
    correction = Some(word)
  }
}
