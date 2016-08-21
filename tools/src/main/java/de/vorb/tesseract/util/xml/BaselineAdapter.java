package de.vorb.tesseract.util.xml;

import de.vorb.tesseract.util.Baseline;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class BaselineAdapter extends XmlAdapter<String, Baseline> {

    @Override
    public String marshal(Baseline baseline) throws Exception {
        return baseline.getYOffset() + " " + baseline.getSlope();
    }

    @Override
    public Baseline unmarshal(String baseline) throws Exception {
        final String[] coefficients = baseline.trim().split("\\s+");
        return new Baseline(Integer.parseInt(coefficients[0]),
                Float.parseFloat(coefficients[1]));
    }

}
