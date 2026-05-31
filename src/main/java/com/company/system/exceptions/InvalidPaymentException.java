package com.company.system.exceptions;

import com.company.system.i18n.LanguageManager;
import com.company.system.utils.DialogUtils;
import javafx.scene.control.Alert;

public class InvalidPaymentException extends RuntimeException {
    private final String messageKey;

    public InvalidPaymentException(String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
    }

    @Override
    public String getMessage() {
        return LanguageManager.get(messageKey);
    }

    public void showAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        DialogUtils.style(alert);
        alert.setTitle(LanguageManager.get("message.error.title"));
        alert.setHeaderText(null);
        alert.setContentText(getMessage());
        alert.showAndWait();
    }
}
