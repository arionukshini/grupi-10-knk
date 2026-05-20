package com.company.system.exceptions;

import javafx.scene.control.Alert;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String email) {
        super(email + " is not a valid email!");
    }

    public void showInvalidEmailExceptionAlert(double salary) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(null);
        alert.setTitle("Gabim");
        alert.showAndWait();
    }
}
