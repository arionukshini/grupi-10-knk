package com.company.system.controller;

import com.company.system.MainApp;
import com.company.system.i18n.LanguageManager;
import com.company.system.model.User;
import com.company.system.service.UserService;
import com.company.system.utils.DialogUtils;
import com.company.system.utils.KeyboardNavigation;
import com.company.system.utils.PasswordUtils;
import com.company.system.utils.Session;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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

            if (user.mustChangePassword()) {
                showRequiredPasswordChangeDialog(user);
            } else {
                continueLogin();
            }
        } else {
            messageLabel.setText(LanguageManager.get("message.invalidCredentials"));
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void continueLogin() {
        messageLabel.setText(LanguageManager.get("message.loginSuccessful"));
        messageLabel.setStyle("-fx-text-fill: green;");
        showLoadingState();
    }

    private void showRequiredPasswordChangeDialog(User user) {
        Dialog<Void> dialog = new Dialog<>();
        DialogUtils.style(dialog);
        dialog.setTitle(LanguageManager.get("login.changeRequired.title"));
        dialog.setHeaderText(LanguageManager.get("login.changeRequired.header"));
        dialog.setOnCloseRequest(event -> event.consume());

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText(LanguageManager.get("login.changeRequired.newPassword"));

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText(LanguageManager.get("login.changeRequired.confirmPassword"));

        Label message = new Label(LanguageManager.get("login.changeRequired.message"));
        message.setWrapText(true);
        message.getStyleClass().add("body-text");

        Label error = new Label();
        error.setWrapText(true);
        error.getStyleClass().add("error-label");
        error.setVisible(false);
        error.setManaged(false);

        VBox content = new VBox(12, message, newPasswordField, confirmPasswordField, error);
        content.setPrefWidth(380);
        dialog.getDialogPane().setContent(content);

        ButtonType changeType = new ButtonType(LanguageManager.get("login.changeRequired.button"), ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(changeType);

        Node changeButton = dialog.getDialogPane().lookupButton(changeType);
        changeButton.addEventFilter(ActionEvent.ACTION, event -> {
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (newPassword == null || newPassword.isBlank() || confirmPassword == null || confirmPassword.isBlank()) {
                showPasswordDialogError(error, LanguageManager.get("message.fillAllFields"));
                event.consume();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                showPasswordDialogError(error, LanguageManager.get("message.passwordsDoNotMatch"));
                event.consume();
                return;
            }

            if (PasswordUtils.verifyPassword(newPassword, user.getPasswordHash())) {
                showPasswordDialogError(error, LanguageManager.get("message.passwordSameAsOld"));
                event.consume();
                return;
            }

            if (!UserService.changePasswordAndClearRequiredFlag(user.getId(), newPassword)) {
                showPasswordDialogError(error, LanguageManager.get("user.password.notChanged"));
                event.consume();
                return;
            }

            Session.setUser(new User(
                    user.getId(),
                    user.getUsername(),
                    user.getEmployeeId(),
                    user.getEmployeeName(),
                    UserService.getPasswordHashByUsername(user.getUsername()),
                    user.getRole(),
                    user.getCreatedAt(),
                    false
            ));
            continueLogin();
        });

        dialog.setOnShown(event -> newPasswordField.requestFocus());
        dialog.showAndWait();
    }

    private void showPasswordDialogError(Label error, String message) {
        error.setText(message);
        error.setVisible(true);
        error.setManaged(true);
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
