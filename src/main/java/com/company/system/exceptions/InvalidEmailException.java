package com.company.system.exceptions;

import com.company.system.utils.DialogUtils;
import javafx.scene.control.Alert;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String email, String message) {
        super(message + " " + email + " is not a valid email!");
        showInvalidEmailExceptionAlert(email, message);
    }

    public void showInvalidEmailExceptionAlert(String email, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        DialogUtils.style(alert);
        alert.setHeaderText(null);
        alert.setContentText(message + " " + email + " is not a valid email!");
        alert.setTitle("Gabim");
        alert.showAndWait();
    }
}
