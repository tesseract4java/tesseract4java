package de.vorb.tesseract.gui.view;

import java.awt.Component;

import javax.swing.JOptionPane;

public class Dialogs {
    public static void showInfo(Component parent, String title,
            String message) {
        JOptionPane.showMessageDialog(parent, title, message,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showWarning(Component parent, String title,
            String message) {
        JOptionPane.showMessageDialog(parent, title, message,
                JOptionPane.WARNING_MESSAGE);
    }

    public static void showError(Component parent, String title,
            String message) {
        JOptionPane.showMessageDialog(parent, title, message,
                JOptionPane.ERROR_MESSAGE);
    }
}
