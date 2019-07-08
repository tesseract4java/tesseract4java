package de.vorb.tesseract.gui.view.dialogs;

import javax.swing.JOptionPane;
import java.awt.Component;

public final class Dialogs {

    private Dialogs() {}

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

    public static boolean ask(Component parent, String title, String question) {
        int result = JOptionPane.showConfirmDialog(parent, question, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        return result == JOptionPane.OK_OPTION;
    }
}
