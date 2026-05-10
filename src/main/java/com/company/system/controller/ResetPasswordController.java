package com.company.system.controller;

import com.company.system.MainApp;
import com.company.system.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ResetPasswordController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    @FXML
    public void handleReset() {

        String username = usernameField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("Fill all fields!");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match!");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        
        String oldPassword = UserService.getPasswordByUsername(username);

        if (oldPassword == null) {
            messageLabel.setText("User not found!");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (oldPassword.equals(newPassword)) {
            messageLabel.setText("New password cannot be same as old password!");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        boolean success = UserService.resetPassword(username, newPassword);

        if (success) {
            messageLabel.setText("Password reset successful!");
            messageLabel.setStyle("-fx-text-fill: green;");
        } else {
            messageLabel.setText("Reset failed!");
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    public void goToLogin(ActionEvent event) {

        try {
            javafx.fxml.FXMLLoader loader =
                    new javafx.fxml.FXMLLoader(getClass().getResource("/views/login-view.fxml"));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.getScene().setRoot(loader.load());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}