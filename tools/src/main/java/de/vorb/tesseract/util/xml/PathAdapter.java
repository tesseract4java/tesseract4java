package de.vorb.tesseract.util.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathAdapter extends XmlAdapter<String, Path> {
    @Override
    public String marshal(Path path) throws Exception {
        return path.toAbsolutePath().toString();
    }

    @Override
    public Path unmarshal(String path) throws Exception {
        return Paths.get(path.trim());
    }
}
