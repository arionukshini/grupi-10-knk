package com.company.system.controller;

import com.company.system.MainApp;
import com.company.system.i18n.LanguageManager;
import com.company.system.model.User;
import com.company.system.service.UserService;
import com.company.system.utils.KeyboardNavigation;
import com.company.system.utils.Session;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginController {

    @FXML private Label titleLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private Button togglePasswordButton;
    @FXML private Hyperlink forgotPasswordLink;
    @FXML private Button loginButton;
    @FXML private HBox loadingBox;
    @FXML private Label loadingLabel;
    @FXML private Label messageLabel;

    private boolean passwordVisible = false;

    @FXML
    public void initialize() {
        updateTexts();
        setupKeyboardAccess();
        setupPasswordToggle();
    }

    private void setupKeyboardAccess() {
        // Tab order: username -> password -> loginButton -> forgotPasswordLink
        usernameField.setOnAction(event -> passwordField.requestFocus());
        passwordField.setOnAction(event -> loginButton.fire());
        visiblePasswordField.setOnAction(event -> loginButton.fire());

        // Toggle button nuk duhet të jetë në Tab order
        togglePasswordButton.setFocusTraversable(false);

        // Enter në forgotPasswordLink e aktivizon
        forgotPasswordLink.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
                forgotPasswordLink.fire();
            }
        });

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
        loadingLabel.setText(LanguageManager.get("login.loading"));
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
            showLoadingState();
        } else {
            messageLabel.setText(LanguageManager.get("message.invalidCredentials"));
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void showLoadingState() {
        loginButton.setDisable(true);
        forgotPasswordLink.setDisable(true);
        usernameField.setDisable(true);
        passwordField.setDisable(true);
        visiblePasswordField.setDisable(true);
        togglePasswordButton.setDisable(true);
        loadingBox.setVisible(true);
        loadingBox.setManaged(true);

        PauseTransition transition = new PauseTransition(Duration.millis(120));
        transition.setOnFinished(event -> MainApp.openMainApp());
        transition.play();
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
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent root = loader.load();
            stage.getScene().setRoot(root);
            KeyboardNavigation.install(stage.getScene());
            KeyboardNavigation.focusFirst(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
