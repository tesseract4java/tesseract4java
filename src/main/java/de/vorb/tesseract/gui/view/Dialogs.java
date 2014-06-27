package de.vorb.tesseract.gui.view;

import java.awt.Component;

import javax.swing.JOptionPane;

public class Dialogs {
    public static void showInfo(Component parent, String title,
            String message) {
        JOptionPane.showMessageDialog(parent, message, title,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showWarning(Component parent, String title,
            String message) {
        JOptionPane.showMessageDialog(parent, message, title,
                JOptionPane.WARNING_MESSAGE);
    }

    public static void showError(Component parent, String title,
            String message) {
        JOptionPane.showMessageDialog(parent, message, title,
                JOptionPane.ERROR_MESSAGE);
    }
}
