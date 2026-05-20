package com.company.system.exceptions;

import javafx.scene.control.Alert;

public class InvalidSalaryException extends RuntimeException {
    public InvalidSalaryException(double salary) {
        super(salary + " is not a valid salary!");
        showInvalidSalaryExceptionAlert(salary);
    }

    public void showInvalidSalaryExceptionAlert(double salary) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText("Punetori nuk u perditesua. " + salary + " is not a valid salary!");
        alert.setTitle("Gabim");
        alert.showAndWait();
    }
}
