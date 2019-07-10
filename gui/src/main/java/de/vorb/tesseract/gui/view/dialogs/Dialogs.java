package de.vorb.tesseract.gui.view.dialogs;

import com.drew.lang.annotations.NotNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.swing.JOptionPane;
import java.awt.Component;

public final class Dialogs {

    private Dialogs() {
    }

    public static void showInfo(@Nullable Component parent, @NotNull String title, @NotNull String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showWarning(@Nullable Component parent, @NotNull String title, @NotNull String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public static void showError(@Nullable Component parent, @NotNull String title, @NotNull String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static boolean ask(@Nullable Component parent, @NotNull String title, @NotNull String question) {
        final int result = JOptionPane.showConfirmDialog(parent, question, title, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        return result == JOptionPane.OK_OPTION;
    }
}
