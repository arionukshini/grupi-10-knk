package com.company.system.exceptions;

import com.company.system.utils.DialogUtils;
import javafx.scene.control.Alert;

public class InvalidSalaryException extends RuntimeException {
    public InvalidSalaryException(double salary, String message) {
        super(message + " " + salary + " is not a valid salary!");
        showInvalidSalaryExceptionAlert(salary, message);
    }

    public void showInvalidSalaryExceptionAlert(double salary, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        DialogUtils.style(alert);
        alert.setHeaderText(null);
        alert.setContentText(message + " " + salary + " is not a valid salary!");
        alert.setTitle("Gabim");
        alert.showAndWait();
    }
}
