package com.company.system.exceptions;

import javafx.scene.control.Alert;

public class InvalidSalaryException extends RuntimeException {
    public InvalidSalaryException(double salary, String message) {
        super(salary + " is not a valid salary!");
        showInvalidSalaryExceptionAlert(salary, message);
    }

    public void showInvalidSalaryExceptionAlert(double salary, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message + " " + salary + " is not a valid salary!");
        alert.setTitle("Gabim");
        alert.showAndWait();
    }
}
