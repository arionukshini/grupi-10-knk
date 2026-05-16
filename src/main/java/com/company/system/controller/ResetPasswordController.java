package com.company.system.controller;

import com.company.system.i18n.LanguageManager;
import com.company.system.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ResetPasswordController {

    @FXML
    private Label titleLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button resetButton;

    @FXML
    private Button backToLoginButton;

    @FXML
    private Label messageLabel;

    @FXML
    public void initialize() {
        updateTexts();
    }

    private void updateTexts() {
        titleLabel.setText(LanguageManager.get("reset.title"));
        usernameField.setPromptText(LanguageManager.get("reset.username"));
        newPasswordField.setPromptText(LanguageManager.get("reset.newPassword"));
        confirmPasswordField.setPromptText(LanguageManager.get("reset.confirmPassword"));
        resetButton.setText(LanguageManager.get("reset.button"));
        backToLoginButton.setText(LanguageManager.get("reset.backToLogin"));
    }

    @FXML
    public void handleReset() {
        String username = usernameField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText(LanguageManager.get("message.fillAllFields"));
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            messageLabel.setText(LanguageManager.get("message.passwordsDoNotMatch"));
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        String oldPassword = UserService.getPasswordByUsername(username);

        if (oldPassword == null) {
            messageLabel.setText(LanguageManager.get("message.userNotFound"));
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (oldPassword.equals(newPassword)) {
            messageLabel.setText(LanguageManager.get("message.passwordSameAsOld"));
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        boolean success = UserService.resetPassword(username, newPassword);

        if (success) {
            messageLabel.setText(LanguageManager.get("message.resetSuccessful"));
            messageLabel.setStyle("-fx-text-fill: green;");
        } else {
            messageLabel.setText(LanguageManager.get("message.resetFailed"));
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    public void goToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/login-view.fxml")
            );

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.getScene().setRoot(loader.load());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
