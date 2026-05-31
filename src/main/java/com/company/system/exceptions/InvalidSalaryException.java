package com.company.system.exceptions;

import com.company.system.i18n.LanguageManager;
import com.company.system.utils.DialogUtils;
import javafx.scene.control.Alert;

public class InvalidSalaryException extends RuntimeException {
    private final double salary;
    private final String fieldKey;

    public InvalidSalaryException(double salary, String fieldKey) {
        super(fieldKey);
        this.salary = salary;
        this.fieldKey = fieldKey;
    }

    public void showAlert() {
        showInvalidSalaryExceptionAlert();
    }

    @Override
    public String getMessage() {
        return String.format(LanguageManager.get("exception.invalidSalary"), LanguageManager.get(fieldKey), salary);
    }

    private void showInvalidSalaryExceptionAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        DialogUtils.style(alert);
        alert.setHeaderText(null);
        alert.setContentText(getMessage());
        alert.setTitle(LanguageManager.get("message.error.title"));
        alert.showAndWait();
    }
}
