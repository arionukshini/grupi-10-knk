package com.company.system.controller;

import com.company.system.i18n.LanguageManager;
import com.company.system.service.UserService;
import com.company.system.utils.PasswordUtils;
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
    private TextField emailField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private TextField visibleNewPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField visibleConfirmPasswordField;

    @FXML
    private Button togglePasswordButton;

    @FXML
    private Button resetButton;

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
        titleLabel.setText(LanguageManager.get("reset.title"));
        usernameField.setPromptText(LanguageManager.get("reset.username"));
        emailField.setPromptText(LanguageManager.get("reset.email"));
        newPasswordField.setPromptText(LanguageManager.get("reset.newPassword"));
        visibleNewPasswordField.setPromptText(LanguageManager.get("reset.newPassword"));
        confirmPasswordField.setPromptText(LanguageManager.get("reset.confirmPassword"));
        visibleConfirmPasswordField.setPromptText(LanguageManager.get("reset.confirmPassword"));
        resetButton.setText(LanguageManager.get("reset.button"));
        backToLoginButton.setText(LanguageManager.get("reset.backToLogin"));
        togglePasswordButton.setText("👁");
    }

    private void setupKeyboardAccess() {
        usernameField.setOnAction(event -> emailField.requestFocus());
        emailField.setOnAction(event -> newPasswordField.requestFocus());
        newPasswordField.setOnAction(event -> confirmPasswordField.requestFocus());
        visibleNewPasswordField.setOnAction(event -> visibleConfirmPasswordField.requestFocus());
        confirmPasswordField.setOnAction(event -> resetButton.fire());
        visibleConfirmPasswordField.setOnAction(event -> resetButton.fire());

        resetButton.setAccessibleText("Reset password");
        backToLoginButton.setAccessibleText("Back to login");
    }

    private void setupPasswordToggle() {
        visibleNewPasswordField.textProperty().bindBidirectional(newPasswordField.textProperty());
        visibleConfirmPasswordField.textProperty().bindBidirectional(confirmPasswordField.textProperty());
    }

    @FXML
    public void handleReset() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String newPassword = passwordVisible
                ? visibleNewPasswordField.getText()
                : newPasswordField.getText();
        String confirmPassword = passwordVisible
                ? visibleConfirmPasswordField.getText()
                : confirmPasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText(LanguageManager.get("message.fillAllFields"));
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        String oldPasswordHash = UserService.getPasswordHashByUsernameAndEmail(username, email);

        if (oldPasswordHash == null) {
            messageLabel.setText(LanguageManager.get("message.userEmailMismatch"));
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            messageLabel.setText(LanguageManager.get("message.passwordsDoNotMatch"));
            messageLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (PasswordUtils.verifyPassword(newPassword, oldPasswordHash)) {
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
    public void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;

        visibleNewPasswordField.setVisible(passwordVisible);
        visibleNewPasswordField.setManaged(passwordVisible);
        newPasswordField.setVisible(!passwordVisible);
        newPasswordField.setManaged(!passwordVisible);

        visibleConfirmPasswordField.setVisible(passwordVisible);
        visibleConfirmPasswordField.setManaged(passwordVisible);
        confirmPasswordField.setVisible(!passwordVisible);
        confirmPasswordField.setManaged(!passwordVisible);

        togglePasswordButton.setText(passwordVisible ? "🙈" : "👁");

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
