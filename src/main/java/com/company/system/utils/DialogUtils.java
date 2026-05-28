package com.company.system.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

public class DialogUtils {

    private static final String THEME_PATH = "/styles/app-theme.css";

    private DialogUtils() {
    }

    public static void style(Alert alert) {
        style(alert.getDialogPane());
    }

    public static void style(Dialog<?> dialog) {
        style(dialog.getDialogPane());
    }

    public static void style(DialogPane dialogPane) {
        String stylesheet = DialogUtils.class.getResource(THEME_PATH).toExternalForm();

        if (!dialogPane.getStylesheets().contains(stylesheet)) {
            dialogPane.getStylesheets().add(stylesheet);
        }

        if (!dialogPane.getStyleClass().contains("app-dialog")) {
            dialogPane.getStyleClass().add("app-dialog");
        }
    }
}
