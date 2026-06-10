package com.company.system.controller;

import com.company.system.i18n.LanguageManager;
import com.company.system.model.Department;
import com.company.system.service.DepartmentService;
import com.company.system.service.UserService;
import com.company.system.service.WizardUserService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class AddUserWizardController {

    @FXML private StackPane step1Indicator, step2Indicator, step3Indicator, step4Indicator;
    @FXML private Label step1Label, step2Label, step3Label, step4Label;
    @FXML private Region stepLine1, stepLine2, stepLine3;

    @FXML private VBox step1Pane, step2Pane, step3Pane, step4Pane;
    @FXML private Button backBtn, nextBtn, exitBtn;
    @FXML private Label stepCounter;

    @FXML private TextField firstNameField, lastNameField, emailField;
    @FXML private TextField phoneField, positionField;
    @FXML private ComboBox<String> departmentCombo, statusCombo;
    @FXML private Label emailHintLabel;

    @FXML private ComboBox<String> contractTypeCombo, contractStatusCombo;
    @FXML private DatePicker startDatePicker, endDatePicker;

    @FXML private TextField grossSalaryField, bonusField, deductionsField;
    @FXML private TextField workHoursField, vacationDaysField, overtimeHoursField;
    @FXML private Label dailyRateLabel, overtimePayLabel, netSalaryLabel;

    @FXML private TextField usernameField;
    @FXML private Label tempPasswordLabel, roleLabel;

    private static final int TOTAL_STEPS = 4;
    private int currentStep = 1;
    private String generatedPassword;
    private Runnable onSuccess;

    @FXML
    public void initialize() {
        loadDepartments();
        setupStatusOptions();
        setupContractOptions();
        setupSalaryListeners();
        setupNameListeners();
        setupEmailValidation();
        updateStepUI();
    }

    public void setOnSuccess(Runnable callback) {
        this.onSuccess = callback;
    }

    private void loadDepartments() {
        List<Department> departments = DepartmentService.getAllDepartments();
        for (Department department : departments) {
            departmentCombo.getItems().add(department.getId() + " - " + department.getName());
        }
    }

    private void setupStatusOptions() {
        statusCombo.getItems().addAll("Active", "Inactive", "Suspended", "Pending");
        statusCombo.setValue("Active");
    }

    private void setupContractOptions() {
        contractTypeCombo.getItems().addAll("Full-Time", "Part-Time", "Temporary", "Internship");
        contractTypeCombo.setValue("Full-Time");
        contractStatusCombo.getItems().addAll("Active", "Expired", "Pending");
        contractStatusCombo.setValue("Active");
        startDatePicker.setValue(LocalDate.now());
    }

    private void setupNameListeners() {
        firstNameField.textProperty().addListener((obs, oldValue, newValue) -> autoFillEmailAndUsername());
        lastNameField.textProperty().addListener((obs, oldValue, newValue) -> autoFillEmailAndUsername());
    }

    private void autoFillEmailAndUsername() {
        String first = firstNameField.getText().trim().toLowerCase().replaceAll("[^a-z]", "");
        String last = lastNameField.getText().trim().toLowerCase().replaceAll("[^a-z]", "");

        if (!first.isEmpty() && !last.isEmpty()) {
            emailField.setText(first + "." + last + "@company.com");
            usernameField.setText(WizardUserService.generateUniqueUsername(first + "." + last));
        }
    }

    private void setupEmailValidation() {
        emailField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && !newValue.isBlank() && !newValue.matches("^[A-Za-z0-9._%+\\-]+@company\\.com$")) {
                emailHintLabel.setText(LanguageManager.get("wizard.email.invalidHint"));
                emailHintLabel.setStyle("-fx-text-fill: #e74c3c;");
            } else {
                emailHintLabel.setText(LanguageManager.get("wizard.email.validHint"));
                emailHintLabel.setStyle("-fx-text-fill: #27ae60;");
            }
        });
    }

    private void setupSalaryListeners() {
        grossSalaryField.textProperty().addListener((obs, oldValue, newValue) -> recalcSalary());
        bonusField.textProperty().addListener((obs, oldValue, newValue) -> recalcSalary());
        deductionsField.textProperty().addListener((obs, oldValue, newValue) -> recalcSalary());
        workHoursField.textProperty().addListener((obs, oldValue, newValue) -> recalcSalary());
        overtimeHoursField.textProperty().addListener((obs, oldValue, newValue) -> recalcSalary());
        vacationDaysField.textProperty().addListener((obs, oldValue, newValue) -> recalcSalary());
    }

    private void recalcSalary() {
        try {
            double gross = parseDouble(grossSalaryField.getText());
            double bonus = parseDouble(bonusField.getText());
            double deductions = parseDouble(deductionsField.getText());
            double workHours = parseDouble(workHoursField.getText());
            double overtimeHours = parseDouble(overtimeHoursField.getText());

            double dailyRate = gross > 0 ? gross / 22.0 : 0;
            double hourlyRate = workHours > 0 ? gross / workHours : 0;
            double overtimePay = hourlyRate * 1.5 * overtimeHours;
            double net = gross + bonus + overtimePay - deductions;

            dailyRateLabel.setText(String.format("%.2f EUR", dailyRate));
            overtimePayLabel.setText(String.format("%.2f EUR", overtimePay));
            netSalaryLabel.setText(String.format("%.2f EUR", net));
        } catch (Exception e) {
            dailyRateLabel.setText("0.00 EUR");
            overtimePayLabel.setText("0.00 EUR");
            netSalaryLabel.setText("0.00 EUR");
        }
    }

    private double parseDouble(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @FXML
    private void onNext() {
        if (!validateCurrentStep()) {
            return;
        }

        if (currentStep == TOTAL_STEPS) {
            submitWizard();
            return;
        }

        if (currentStep == 3) {
            prepareStep4();
        }

        currentStep++;
        updateStepUI();
    }

    @FXML
    private void onBack() {
        if (currentStep == 1) {
            confirmExit();
            return;
        }
        currentStep--;
        updateStepUI();
    }

    @FXML
    private void onExit() {
        confirmExit();
    }

    private void confirmExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(LanguageManager.get("wizard.exit.title"));
        alert.setHeaderText(LanguageManager.get("wizard.exit.header"));
        alert.setContentText(LanguageManager.get("wizard.exit.content"));
        alert.showAndWait().ifPresent(button -> {
            if (button == ButtonType.OK) {
                closeWindow();
            }
        });
    }

    private void prepareStep4() {
        String first = firstNameField.getText().trim().toLowerCase().replaceAll("[^a-z]", "");
        String last = lastNameField.getText().trim().toLowerCase().replaceAll("[^a-z]", "");
        usernameField.setText(WizardUserService.generateUniqueUsername(first + "." + last));

        generatedPassword = WizardUserService.generateTempPassword();
        tempPasswordLabel.setText(generatedPassword);
        roleLabel.setText("USER");
    }

    private void updateStepUI() {
        step1Pane.setVisible(currentStep == 1);
        step1Pane.setManaged(currentStep == 1);
        step2Pane.setVisible(currentStep == 2);
        step2Pane.setManaged(currentStep == 2);
        step3Pane.setVisible(currentStep == 3);
        step3Pane.setManaged(currentStep == 3);
        step4Pane.setVisible(currentStep == 4);
        step4Pane.setManaged(currentStep == 4);

        updateIndicator(step1Indicator, step1Label, 1);
        updateIndicator(step2Indicator, step2Label, 2);
        updateIndicator(step3Indicator, step3Label, 3);
        updateIndicator(step4Indicator, step4Label, 4);

        updateLine(stepLine1, 2);
        updateLine(stepLine2, 3);
        updateLine(stepLine3, 4);

        backBtn.setText(currentStep == 1
                ? LanguageManager.get("wizard.button.cancel")
                : LanguageManager.get("wizard.button.back"));
        nextBtn.setText(currentStep == TOTAL_STEPS
                ? LanguageManager.get("wizard.button.create")
                : LanguageManager.get("wizard.button.next"));
        stepCounter.setText(String.format(LanguageManager.get("wizard.stepCounter"), currentStep, TOTAL_STEPS));
    }

    private void updateIndicator(StackPane indicator, Label label, int step) {
        indicator.getStyleClass().removeAll("step-active", "step-done", "step-pending");
        label.getStyleClass().removeAll("step-label-active", "step-label-done", "step-label-pending");

        if (step < currentStep) {
            indicator.getStyleClass().add("step-done");
            label.getStyleClass().add("step-label-done");
            setIndicatorText(indicator, "OK");
        } else if (step == currentStep) {
            indicator.getStyleClass().add("step-active");
            label.getStyleClass().add("step-label-active");
            setIndicatorText(indicator, String.valueOf(step));
        } else {
            indicator.getStyleClass().add("step-pending");
            label.getStyleClass().add("step-label-pending");
            setIndicatorText(indicator, String.valueOf(step));
        }
    }

    private void setIndicatorText(StackPane indicator, String text) {
        if (!indicator.getChildren().isEmpty() && indicator.getChildren().get(0) instanceof Label label) {
            label.setText(text);
        }
    }

    private void updateLine(Region line, int nextStep) {
        line.getStyleClass().removeAll("step-line-done", "step-line-pending");
        line.getStyleClass().add(nextStep <= currentStep ? "step-line-done" : "step-line-pending");
    }

    private boolean validateCurrentStep() {
        return switch (currentStep) {
            case 1 -> validateStep1();
            case 2 -> validateStep2();
            case 3 -> validateStep3();
            case 4 -> validateStep4();
            default -> true;
        };
    }

    private boolean validateStep1() {
        if (firstNameField.getText().isBlank()) {
            showError(LanguageManager.get("wizard.validation.firstName"));
            return false;
        }
        if (lastNameField.getText().isBlank()) {
            showError(LanguageManager.get("wizard.validation.lastName"));
            return false;
        }

        String email = emailField.getText().trim();
        if (email.isBlank() || !email.matches("^[A-Za-z0-9._%+\\-]+@company\\.com$")) {
            showError(LanguageManager.get("wizard.validation.email"));
            return false;
        }

        if (departmentCombo.getValue() == null) {
            showError(LanguageManager.get("wizard.validation.department"));
            return false;
        }
        return true;
    }

    private boolean validateStep2() {
        if (contractTypeCombo.getValue() == null) {
            showError(LanguageManager.get("wizard.validation.contractType"));
            return false;
        }
        if (startDatePicker.getValue() == null) {
            showError(LanguageManager.get("wizard.validation.startDate"));
            return false;
        }
        return true;
    }

    private boolean validateStep3() {
        if (grossSalaryField.getText().isBlank()) {
            showError(LanguageManager.get("wizard.validation.grossSalary"));
            return false;
        }
        try {
            Double.parseDouble(grossSalaryField.getText().trim());
        } catch (NumberFormatException e) {
            showError(LanguageManager.get("wizard.validation.grossSalaryNumber"));
            return false;
        }
        return true;
    }

    private boolean validateStep4() {
        if (usernameField.getText().isBlank()) {
            showError(LanguageManager.get("wizard.validation.username"));
            return false;
        }
        if (UserService.userExists(usernameField.getText().trim())) {
            showError(LanguageManager.get("wizard.validation.usernameExists"));
            return false;
        }
        return true;
    }

    private void submitWizard() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String position = positionField.getText().trim();
        int departmentId = parseDepartmentId(departmentCombo.getValue());
        String employeeStatus = statusCombo.getValue();

        String contractType = contractTypeCombo.getValue();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String contractStatus = contractStatusCombo.getValue();

        double gross = parseDouble(grossSalaryField.getText());
        double bonus = parseDouble(bonusField.getText());
        double deductions = parseDouble(deductionsField.getText());
        double workHours = parseDouble(workHoursField.getText());
        int vacationDays = (int) parseDouble(vacationDaysField.getText());
        double overtimeHours = parseDouble(overtimeHoursField.getText());

        String username = usernameField.getText().trim();
        String password = generatedPassword;

        boolean success = WizardUserService.createFullUser(
                firstName, lastName, email, phone, position, departmentId, employeeStatus,
                contractType, startDate, endDate, contractStatus,
                gross, bonus, deductions, (int) workHours, vacationDays, overtimeHours,
                username, password
        );

        if (success) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(LanguageManager.get("wizard.success.title"));
            alert.setHeaderText(LanguageManager.get("wizard.success.header"));
            alert.setContentText(String.format(LanguageManager.get("wizard.success.content"), username, password));
            alert.showAndWait();
            if (onSuccess != null) {
                onSuccess.run();
            }
            closeWindow();
        } else {
            showError(LanguageManager.get("wizard.create.error"));
        }
    }

    private int parseDepartmentId(String value) {
        if (value == null) {
            return 0;
        }
        try {
            return Integer.parseInt(value.split(" - ")[0].trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(LanguageManager.get("wizard.validation.title"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) exitBtn.getScene().getWindow();
        stage.close();
    }
}
