package com.company.system.controller;

import com.company.system.i18n.LanguageManager;
import com.company.system.models.User;
import com.company.system.service.UserService;
import com.company.system.service.WizardUserService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditUserController {

    @FXML private Label titleLabel;
    @FXML private Label employeeIdLabel;
    @FXML private Label employeeIdHintLabel;
    @FXML private Label usernameLabel;
    @FXML private Label newPasswordLabel;
    @FXML private Label confirmPasswordLabel;
    @FXML private TextField employeeIdField;
    @FXML private TextField usernameField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;
    @FXML private Label errorLabel;

    private User user;
    private Runnable onSuccess;

    @FXML
    public void initialize() {
        loadTexts();
        errorLabel.setVisible(false);
    }

    public void setUser(User user) {
        this.user = user;
        titleLabel.setText(String.format(LanguageManager.get("users.edit.dialog.title"), user.getUsername()));
        usernameField.setText(user.getUsername());
        if (user.getEmployeeId() != null) {
            employeeIdField.setText(String.valueOf(user.getEmployeeId()));
        }
    }

    public void setOnSuccess(Runnable callback) {
        this.onSuccess = callback;
    }

    @FXML
    private void onSave() {
        errorLabel.setVisible(false);

        String username = usernameField.getText().trim();
        String empIdText = employeeIdField.getText().trim();
        String newPass = newPasswordField.getText();
        String confirmPass = confirmPasswordField.getText();

        if (username.isBlank()) {
            showError(LanguageManager.get("users.edit.validation.username"));
            return;
        }

        if (!username.equals(user.getUsername()) && UserService.userExists(username)) {
            showError(LanguageManager.get("users.edit.validation.usernameExists"));
            return;
        }

        Integer employeeId = null;
        if (!empIdText.isBlank()) {
            try {
                employeeId = Integer.parseInt(empIdText);
                if (!WizardUserService.isEmployeeIdUnique(employeeId, user.getId())) {
                    showError(LanguageManager.get("users.edit.validation.employeeLinked"));
                    return;
                }
            } catch (NumberFormatException e) {
                showError(LanguageManager.get("users.edit.validation.employeeNumber"));
                return;
            }
        }

        if (!newPass.isBlank()) {
            if (!newPass.equals(confirmPass)) {
                showError(LanguageManager.get("message.passwordsDoNotMatch"));
                return;
            }
            if (newPass.length() < 6) {
                showError(LanguageManager.get("users.edit.validation.passwordLength"));
                return;
            }
        }

        boolean success = WizardUserService.updateUser(
                user.getId(),
                employeeId,
                username,
                newPass.isBlank() ? null : newPass
        );

        if (success) {
            if (onSuccess != null) {
                onSuccess.run();
            }
            closeWindow();
            return;
        }

        showError(LanguageManager.get("users.edit.save.error"));
    }

    @FXML
    private void onCancel() {
        closeWindow();
    }

    private void loadTexts() {
        titleLabel.setText(LanguageManager.get("users.edit.title"));
        employeeIdLabel.setText(LanguageManager.get("users.edit.employeeId"));
        employeeIdField.setPromptText(LanguageManager.get("users.edit.employeeIdPrompt"));
        employeeIdHintLabel.setText(LanguageManager.get("users.edit.employeeIdHint"));
        usernameLabel.setText(LanguageManager.get("users.edit.username"));
        newPasswordLabel.setText(LanguageManager.get("users.edit.newPassword"));
        newPasswordField.setPromptText(LanguageManager.get("users.edit.newPasswordPrompt"));
        confirmPasswordLabel.setText(LanguageManager.get("users.edit.confirmPassword"));
        confirmPasswordField.setPromptText(LanguageManager.get("users.edit.confirmPasswordPrompt"));
        cancelBtn.setText(LanguageManager.get("button.cancel"));
        saveBtn.setText(LanguageManager.get("users.edit.saveChanges"));
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }
}
