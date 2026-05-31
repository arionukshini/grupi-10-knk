package com.company.system.exceptions;

import com.company.system.i18n.LanguageManager;
import com.company.system.utils.DialogUtils;
import javafx.scene.control.Alert;

public class InvalidEmailException extends RuntimeException {
    private final String email;
    private final String contextKey;

    public InvalidEmailException(String email, String contextKey) {
        super(contextKey);
        this.email = email;
        this.contextKey = contextKey;
    }

    public void showAlert() {
        showInvalidEmailExceptionAlert();
    }

    @Override
    public String getMessage() {
        return String.format(LanguageManager.get("exception.invalidEmail"), LanguageManager.get(contextKey), email);
    }

    private void showInvalidEmailExceptionAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        DialogUtils.style(alert);
        alert.setHeaderText(null);
        alert.setContentText(getMessage());
        alert.setTitle(LanguageManager.get("message.error.title"));
        alert.showAndWait();
    }
}
