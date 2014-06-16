package de.vorb.tesseract.gui.util;

import java.util.Hashtable;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Resource;

public class ColorPalette extends Resource {
    private static Map<Device, ColorPalette> colorPalettes =
            new Hashtable<Device, ColorPalette>();

    private final Color[] colors;
    private final Device device;

    public final Color normal = getColor(0x0000FF);
    public final Color selection = getColor(0xFF0000);

    public final Color correct = getColor(0x66CC00);
    public final Color incorrect = getColor(0xFF0000);
    public final Color baseline = getColor(0x0000FF);
    public final Color text = getColor(0x000000);
    public final Color lineNumber = getColor(0x555555);

    private ColorPalette(Device device) {
        this.device = device;
        colors = new Color[0xFFFFFF + 1];
        for (int i = 0; i < colors.length; ++i) {
            colors[i] = new Color(device, (i >> 16) & 0xFF, (i >> 8) & 0xFF,
                    i & 0xFF);
        }
    }

    public static ColorPalette getInstance(Device device) {
        if (!colorPalettes.containsKey(device)) {
            colorPalettes.put(device, new ColorPalette(device));
        }

        return colorPalettes.get(device);
    }

    public Color getColor(int rgb) {
        return colors[rgb];
    }

    @Override
    public boolean isDisposed() {
        return colors[0].isDisposed();
    }

    @Override
    public void dispose() {
        if (device.isDisposed())
            return;
        for (int i = 0; i <= colors.length; ++i) {
            colors[i].dispose();
        }
    }
}
