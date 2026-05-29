package com.company.system.controller;

import com.company.system.MainApp;
import com.company.system.i18n.LanguageManager;
import com.company.system.model.User;
import com.company.system.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.company.system.utils.Session;


public class LoginController {

    @FXML
    private Label titleLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField visiblePasswordField;

    @FXML
    private Button togglePasswordButton;

    @FXML
    private Hyperlink forgotPasswordLink;

    @FXML
    private Button loginButton;

    @FXML
    private Label messageLabel;

    private boolean passwordVisible = false;

    @FXML
    public void initialize() {
        updateTexts();
        setupKeyboardAccess();
        setupPasswordToggle();

    }

    private void setupKeyboardAccess() {
        usernameField.setOnAction(event -> passwordField.requestFocus());
        passwordField.setOnAction(event -> loginButton.fire());
        visiblePasswordField.setOnAction(event -> loginButton.fire());

        loginButton.setAccessibleText("Login");
        forgotPasswordLink.setAccessibleText("Forgot password");
    }

    private void setupPasswordToggle() {
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());
    }

    private void updateTexts() {
        titleLabel.setText(LanguageManager.get("login.title"));
        usernameField.setPromptText(LanguageManager.get("login.username"));
        passwordField.setPromptText(LanguageManager.get("login.password"));
        visiblePasswordField.setPromptText(LanguageManager.get("login.password"));
        forgotPasswordLink.setText(LanguageManager.get("login.forgotPassword"));
        loginButton.setText(LanguageManager.get("login.button"));
        togglePasswordButton.setText("👁");
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordVisible
                ? visiblePasswordField.getText()
                : passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText(LanguageManager.get("message.fillAllFields"));
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        User user = UserService.login(username, password);

        if (user != null) {
            Session.setUser(user);
            messageLabel.setText(LanguageManager.get("message.loginSuccessful"));
            messageLabel.setStyle("-fx-text-fill: green;");
            MainApp.openMainApp();
        } else {
            messageLabel.setText(LanguageManager.get("message.invalidCredentials"));
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

        togglePasswordButton.setText(passwordVisible ? "🙈" : "👁");
    }

    @FXML
    public void goToResetPassword(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/reset-password-view.fxml")
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
