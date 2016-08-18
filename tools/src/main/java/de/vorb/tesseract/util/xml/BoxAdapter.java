package de.vorb.tesseract.util.xml;

import de.vorb.tesseract.util.Box;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class BoxAdapter extends XmlAdapter<String, Box> {
    @Override
    public String marshal(Box box) throws Exception {
        return box.getX() + " " + box.getY() + " " + box.getWidth() + " "
                + box.getHeight();
    }

    @Override
    public Box unmarshal(String box) throws Exception {
        final String[] coords = box.trim().split(" ");
        return new Box(Integer.parseInt(coords[0]),
                Integer.parseInt(coords[1]),
                Integer.parseInt(coords[2]),
                Integer.parseInt(coords[3]));
    }
}
