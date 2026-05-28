package com.company.system.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;

public class DialogUtils {

    private static final String THEME_PATH = "/styles/app-theme.css";

    private DialogUtils() {
    }

    public static void style(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        String stylesheet = DialogUtils.class.getResource(THEME_PATH).toExternalForm();

        if (!dialogPane.getStylesheets().contains(stylesheet)) {
            dialogPane.getStylesheets().add(stylesheet);
        }

        if (!dialogPane.getStyleClass().contains("app-dialog")) {
            dialogPane.getStyleClass().add("app-dialog");
        }
    }
}
