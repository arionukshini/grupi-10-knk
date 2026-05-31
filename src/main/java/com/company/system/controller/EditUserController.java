package com.company.system.controller;

import com.company.system.model.User;
import com.company.system.service.UserService;
import com.company.system.service.WizardUserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EditUserController {

    @FXML private Label titleLabel;
    @FXML private TextField employeeIdField;
    @FXML private TextField usernameField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button saveBtn, cancelBtn;
    @FXML private Label errorLabel;

    private User user;
    private Runnable onSuccess;

    public void setUser(User user) {
        this.user = user;
        titleLabel.setText("Edito Përdoruesin: " + user.getUsername());
        usernameField.setText(user.getUsername());
        if (user.getEmployeeId() != null) {
            employeeIdField.setText(String.valueOf(user.getEmployeeId()));
        }
    }

    public void setOnSuccess(Runnable callback) {
        this.onSuccess = callback;
    }

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
    }

    @FXML
    private void onSave() {
        errorLabel.setVisible(false);

        String username = usernameField.getText().trim();
        String empIdText = employeeIdField.getText().trim();
        String newPass = newPasswordField.getText();
        String confirmPass = confirmPasswordField.getText();

        // Validate username
        if (username.isBlank()) {
            showError("Username-i nuk mund të jetë bosh.");
            return;
        }

        // Check username uniqueness (only if changed)
        if (!username.equals(user.getUsername()) && UserService.userExists(username)) {
            showError("Ky username ekziston tashmë.");
            return;
        }

        // Validate employee ID
        Integer employeeId = null;
        if (!empIdText.isBlank()) {
            try {
                employeeId = Integer.parseInt(empIdText);
                if (!WizardUserService.isEmployeeIdUnique(employeeId, user.getId())) {
                    showError("Ky employee_id është i lidhur tashmë me një përdorues tjetër.");
                    return;
                }
            } catch (NumberFormatException e) {
                showError("Employee ID duhet të jetë numër.");
                return;
            }
        }

        // Validate password
        if (!newPass.isBlank()) {
            if (!newPass.equals(confirmPass)) {
                showError("Fjalëkalimet nuk përputhen.");
                return;
            }
            if (newPass.length() < 6) {
                showError("Fjalëkalimi duhet të ketë të paktën 6 karaktere.");
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
            if (onSuccess != null) onSuccess.run();
            closeWindow();
        } else {
            showError("Gabim gjatë ruajtjes. Provoni përsëri.");
        }
    }

    @FXML
    private void onCancel() {
        closeWindow();
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

