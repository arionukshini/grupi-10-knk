package com.company.system.controller;

import com.company.system.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    public void handleRegister(ActionEvent event) {

        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Fill all fields");
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        boolean success = UserService.register(username, password);

        if (success) {
            messageLabel.setText("Registered successfully");
            messageLabel.setStyle("-fx-text-fill: green;");
        } else {
            messageLabel.setText("Registration failed");
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