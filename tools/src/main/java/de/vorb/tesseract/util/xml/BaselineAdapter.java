package de.vorb.tesseract.util.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.vorb.tesseract.util.Baseline;

public class BaselineAdapter extends XmlAdapter<String, Baseline> {

    @Override
    public String marshal(Baseline baseline) throws Exception {
        return baseline.getYOffset() + " " + baseline.getSlope();
    }

    @Override
    public Baseline unmarshal(String baseline) throws Exception {
        final String[] coeffs = baseline.trim().split("\\s+");
        return new Baseline(Integer.parseInt(coeffs[0]),
                Float.parseFloat(coeffs[1]));
    }

}
