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

public class RegisterController {

    @FXML
    private Label titleLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField visiblePasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField visibleConfirmPasswordField;

    @FXML
    private Button togglePasswordButton;

    @FXML
    private Button registerButton;

    @FXML
    private Button backToLoginButton;

    @FXML
    private Label messageLabel;

    private boolean passwordVisible = false;


    @FXML
    public void initialize() {
        updateTexts();
        setupKeyboardAccess();
        setupPasswordToggle();
    }

    private void updateTexts() {
        titleLabel.setText(LanguageManager.get("register.title"));
        usernameField.setPromptText(LanguageManager.get("register.username"));
        passwordField.setPromptText(LanguageManager.get("register.password"));
        visiblePasswordField.setPromptText(LanguageManager.get("register.password"));
        confirmPasswordField.setPromptText(LanguageManager.get("register.confirmPassword"));
        visibleConfirmPasswordField.setPromptText(LanguageManager.get("register.confirmPassword"));
        registerButton.setText(LanguageManager.get("register.button"));
        backToLoginButton.setText(LanguageManager.get("register.backToLogin"));
        togglePasswordButton.setText("👁");
    }

    private void setupKeyboardAccess() {
        usernameField.setOnAction(event -> passwordField.requestFocus());
        passwordField.setOnAction(event -> confirmPasswordField.requestFocus());
        visiblePasswordField.setOnAction(event -> visibleConfirmPasswordField.requestFocus());
        confirmPasswordField.setOnAction(event -> registerButton.fire());
        visibleConfirmPasswordField.setOnAction(event -> registerButton.fire());

        registerButton.setAccessibleText("Register");
        backToLoginButton.setAccessibleText("Back to login");
    }

    private void setupPasswordToggle() {
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());
        visibleConfirmPasswordField.textProperty().bindBidirectional(confirmPasswordField.textProperty());
    }

    @FXML
    public void handleRegister(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordVisible
                ? visiblePasswordField.getText()
                : passwordField.getText();
        String confirmPassword = passwordVisible
                ? visibleConfirmPasswordField.getText()
                : confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText(LanguageManager.get("message.fillAllFields"));
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText(LanguageManager.get("message.passwordsDoNotMatch"));
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (UserService.userExists(username)) {
            messageLabel.setText(LanguageManager.get("message.userAlreadyExists"));
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        boolean success = UserService.register(username, password);

        if (success) {
            messageLabel.setText(LanguageManager.get("message.registerSuccessful"));
            messageLabel.setStyle("-fx-text-fill: green;");
        } else {
            messageLabel.setText(LanguageManager.get("message.registrationFailed"));
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    public void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;

        visiblePasswordField.setVisible(passwordVisible);
        visiblePasswordField.setManaged(passwordVisible);
        passwordField.setVisible(!passwordVisible);
        passwordField.setManaged(!passwordVisible);

        visibleConfirmPasswordField.setVisible(passwordVisible);
        visibleConfirmPasswordField.setManaged(passwordVisible);
        confirmPasswordField.setVisible(!passwordVisible);
        confirmPasswordField.setManaged(!passwordVisible);

        togglePasswordButton.setText(passwordVisible ? "🙈" : "👁");

    }

    @FXML
    public void goToLogin(ActionEvent event) {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/views/login-view.fxml"));

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.getScene().setRoot(loader.load());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
