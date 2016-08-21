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
        final String[] coordinates = box.trim().split(" ");
        return new Box(Integer.parseInt(coordinates[0]),
                Integer.parseInt(coordinates[1]),
                Integer.parseInt(coordinates[2]),
                Integer.parseInt(coordinates[3]));
    }
}
