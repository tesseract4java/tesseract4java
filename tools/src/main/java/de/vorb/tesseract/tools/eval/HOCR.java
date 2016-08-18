package de.vorb.tesseract.tools.eval;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;

public class HOCR {
  public static HOCR parseFile(File hocr) throws ParserConfigurationException,
      SAXException, IOException {
    final SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
    parser.parse(hocr, new DefaultHandler() {
      @Override
      public void startElement(String namespaceURI, String localName,
          String qualName, Attributes attrs) throws SAXException {
        if (!"ocrx_word".equals(attrs.getValue("class")))
          return;

        String title = attrs.getValue("title");
        int conf = Integer.parseInt(title.substring(title.lastIndexOf(' ') + 1));
      }
    });

    return new HOCR();
  }

  public static void main(String[] args) throws ParserConfigurationException,
      SAXException, IOException {
    final File src = new File(
        "C:/Users/Paul/Studium/Masterarbeit/Ressourcen/DE-20__32_AM_49000_L869_G927-1/hocr/DE-20__32_AM_49000_L869_G927-1__0014__R0010.html");

    parseFile(src);
  }
}
