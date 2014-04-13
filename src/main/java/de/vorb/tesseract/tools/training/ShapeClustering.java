package de.vorb.tesseract.tools.training;

public class ShapeClustering {
  private static ShapeClustering instance = null;

  private ShapeClustering() {
  }

  /**
   * @return singleton instance of this class.
   */
  public static ShapeClustering getInstance() {
    if (instance == null)
      instance = new ShapeClustering();

    return instance;
  }
  
  
}
