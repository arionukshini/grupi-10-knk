package com.company.system.controller;

import com.company.system.model.Department;
import com.company.system.service.DepartmentService;
import com.company.system.service.UserService;
import com.company.system.service.WizardUserService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class AddUserWizardController {

    //  Step indicators
    @FXML private StackPane step1Indicator, step2Indicator, step3Indicator, step4Indicator;
    @FXML private Label step1Label, step2Label, step3Label, step4Label;
    @FXML private Region stepLine1, stepLine2, stepLine3;

    //  Content panes
    @FXML private VBox step1Pane, step2Pane, step3Pane, step4Pane;

    //  Navigation buttons
    @FXML private Button backBtn, nextBtn, exitBtn;
    @FXML private Label stepCounter;

    //  Personal Info
    @FXML private TextField firstNameField, lastNameField, emailField;
    @FXML private TextField phoneField, positionField;
    @FXML private ComboBox<String> departmentCombo, statusCombo;
    @FXML private Label emailHintLabel;

    // Contract Info
    @FXML private ComboBox<String> contractTypeCombo, contractStatusCombo;
    @FXML private DatePicker startDatePicker, endDatePicker;

    //  Salary Info
    @FXML private TextField grossSalaryField, bonusField, deductionsField;
    @FXML private TextField workHoursField, vacationDaysField, overtimeHoursField;
    @FXML private Label dailyRateLabel, overtimePayLabel, netSalaryLabel;

    //  Account Info
    @FXML private TextField usernameField;
    @FXML private Label tempPasswordLabel, roleLabel;

    //  State
    private int currentStep = 1;
    private static final int TOTAL_STEPS = 4;
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

    // Loaders

    private void loadDepartments() {
        List<Department> departments = DepartmentService.getAllDepartments();
        for (Department d : departments) {
            departmentCombo.getItems().add(d.getId() + " - " + d.getName());
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

    // Auto-fill email & username

    private void setupNameListeners() {
        firstNameField.textProperty().addListener((obs, o, n) -> autoFillEmailAndUsername());
        lastNameField.textProperty().addListener((obs, o, n) -> autoFillEmailAndUsername());
    }

    private void autoFillEmailAndUsername() {
        String first = firstNameField.getText().trim().toLowerCase()
                .replaceAll("[^a-z]", "");
        String last = lastNameField.getText().trim().toLowerCase()
                .replaceAll("[^a-z]", "");

        if (!first.isEmpty() && !last.isEmpty()) {
            String email = first + "." + last + "@company.com";
            emailField.setText(email);

            // Only pre-fill username if we're already on step 4 or user hasn't typed
            String baseUsername = first + "." + last;
            String unique = WizardUserService.generateUniqueUsername(baseUsername);
            usernameField.setText(unique);
        }
    }

    private void setupEmailValidation() {
        emailField.textProperty().addListener((obs, o, n) -> {
            if (n != null && !n.isBlank() && !n.matches("^[A-Za-z0-9._%+\\-]+@company\\.com$")) {
                emailHintLabel.setText("⚠ Email duhet të jetë @company.com");
                emailHintLabel.setStyle("-fx-text-fill: #e74c3c;");
            } else {
                emailHintLabel.setText("✓ Format i saktë");
                emailHintLabel.setStyle("-fx-text-fill: #27ae60;");
            }
        });
    }

    //  Salary auto-calculations

    private void setupSalaryListeners() {
        grossSalaryField.textProperty().addListener((obs, o, n) -> recalcSalary());
        bonusField.textProperty().addListener((obs, o, n) -> recalcSalary());
        deductionsField.textProperty().addListener((obs, o, n) -> recalcSalary());
        workHoursField.textProperty().addListener((obs, o, n) -> recalcSalary());
        overtimeHoursField.textProperty().addListener((obs, o, n) -> recalcSalary());
        vacationDaysField.textProperty().addListener((obs, o, n) -> recalcSalary());
    }

    private void recalcSalary() {
        try {
            double gross = parseDouble(grossSalaryField.getText());
            double bonus = parseDouble(bonusField.getText());
            double deductions = parseDouble(deductionsField.getText());
            double workHours = parseDouble(workHoursField.getText());
            double overtimeHours = parseDouble(overtimeHoursField.getText());


            double dailyRate = gross > 0 ? gross / 22.0 : 0;

            double hourlyRate = (workHours > 0) ? gross / workHours : 0;
            double overtimePay = hourlyRate * 1.5 * overtimeHours;

            double net = gross + bonus + overtimePay - deductions;

            dailyRateLabel.setText(String.format("%.2f €", dailyRate));
            overtimePayLabel.setText(String.format("%.2f €", overtimePay));
            netSalaryLabel.setText(String.format("%.2f €", net));
        } catch (Exception e) {
            dailyRateLabel.setText("0.00 €");
            overtimePayLabel.setText("0.00 €");
            netSalaryLabel.setText("0.00 €");
        }
    }

    private double parseDouble(String text) {
        if (text == null || text.isBlank()) return 0;
        try { return Double.parseDouble(text.trim()); } catch (NumberFormatException e) { return 0; }
    }

    // Step navigation

    @FXML
    private void onNext() {
        if (!validateCurrentStep()) return;

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
        alert.setTitle("Dalje nga Wizard");
        alert.setHeaderText("A jeni të sigurt?");
        alert.setContentText("Të gjitha të dhënat e shkruara do të humbin. Dëshironi të dilni?");
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                closeWindow();
            }
        });
    }

    private void prepareStep4() {
        // Generate username from first+last name
        String first = firstNameField.getText().trim().toLowerCase().replaceAll("[^a-z]", "");
        String last = lastNameField.getText().trim().toLowerCase().replaceAll("[^a-z]", "");
        String base = first + "." + last;
        String unique = WizardUserService.generateUniqueUsername(base);
        usernameField.setText(unique);

        // Generate temporary password
        generatedPassword = WizardUserService.generateTempPassword();
        tempPasswordLabel.setText(generatedPassword);
        roleLabel.setText("USER");
    }

    private void updateStepUI() {
        // Show/hide panes
        step1Pane.setVisible(currentStep == 1);
        step1Pane.setManaged(currentStep == 1);
        step2Pane.setVisible(currentStep == 2);
        step2Pane.setManaged(currentStep == 2);
        step3Pane.setVisible(currentStep == 3);
        step3Pane.setManaged(currentStep == 3);
        step4Pane.setVisible(currentStep == 4);
        step4Pane.setManaged(currentStep == 4);

        // Update step indicators
        updateIndicator(step1Indicator, step1Label, 1);
        updateIndicator(step2Indicator, step2Label, 2);
        updateIndicator(step3Indicator, step3Label, 3);
        updateIndicator(step4Indicator, step4Label, 4);

        // Update lines
        updateLine(stepLine1, 2);
        updateLine(stepLine2, 3);
        updateLine(stepLine3, 4);

        // Update buttons
        backBtn.setText(currentStep == 1 ? "✕ Anulo" : "← Prapa");
        nextBtn.setText(currentStep == TOTAL_STEPS ? "✓ Krijo Përdoruesin" : "Tjetër →");
        stepCounter.setText("Hapi " + currentStep + " nga " + TOTAL_STEPS);
    }

    private void updateIndicator(StackPane indicator, Label label, int step) {
        indicator.getStyleClass().removeAll("step-active", "step-done", "step-pending");
        label.getStyleClass().removeAll("step-label-active", "step-label-done", "step-label-pending");

        if (step < currentStep) {
            indicator.getStyleClass().add("step-done");
            label.getStyleClass().add("step-label-done");
            // Show checkmark
            if (!indicator.getChildren().isEmpty() && indicator.getChildren().get(0) instanceof Label l) {
                l.setText("✓");
            }
        } else if (step == currentStep) {
            indicator.getStyleClass().add("step-active");
            label.getStyleClass().add("step-label-active");
            if (!indicator.getChildren().isEmpty() && indicator.getChildren().get(0) instanceof Label l) {
                l.setText(String.valueOf(step));
            }
        } else {
            indicator.getStyleClass().add("step-pending");
            label.getStyleClass().add("step-label-pending");
            if (!indicator.getChildren().isEmpty() && indicator.getChildren().get(0) instanceof Label l) {
                l.setText(String.valueOf(step));
            }
        }
    }

    private void updateLine(Region line, int nextStep) {
        line.getStyleClass().removeAll("step-line-done", "step-line-pending");
        if (nextStep <= currentStep) {
            line.getStyleClass().add("step-line-done");
        } else {
            line.getStyleClass().add("step-line-pending");
        }
    }

    //  Validation

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
        if (firstNameField.getText().isBlank()) { showError("Emri është i detyrueshëm."); return false; }
        if (lastNameField.getText().isBlank()) { showError("Mbiemri është i detyrueshëm."); return false; }
        String email = emailField.getText().trim();
        if (email.isBlank() || !email.matches("^[A-Za-z0-9._%+\\-]+@company\\.com$")) {
            showError("Email-i duhet të jetë i formatit @company.com");
            return false;
        }
        if (departmentCombo.getValue() == null) { showError("Zgjidhni departamentin."); return false; }
        return true;
    }

    private boolean validateStep2() {
        if (contractTypeCombo.getValue() == null) { showError("Zgjidhni llojin e kontratës."); return false; }
        if (startDatePicker.getValue() == null) { showError("Data e fillimit është e detyrueshme."); return false; }
        return true;
    }

    private boolean validateStep3() {
        if (grossSalaryField.getText().isBlank()) { showError("Paga bruto është e detyrueshme."); return false; }
        try { Double.parseDouble(grossSalaryField.getText().trim()); }
        catch (NumberFormatException e) { showError("Paga bruto duhet të jetë numër."); return false; }
        return true;
    }

    private boolean validateStep4() {
        if (usernameField.getText().isBlank()) { showError("Username-i nuk mund të jetë bosh."); return false; }
        if (UserService.userExists(usernameField.getText().trim())) {
            showError("Ky username ekziston tashmë. Ndryshojeni."); return false;
        }
        return true;
    }

    // Submit

    private void submitWizard() {
        // Gather all data
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String position = positionField.getText().trim();
        int departmentId = parseDepartmentId(departmentCombo.getValue());
        String empStatus = statusCombo.getValue();

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
                firstName, lastName, email, phone, position, departmentId, empStatus,
                contractType, startDate, endDate, contractStatus,
                gross, bonus, deductions, (int) workHours, vacationDays, overtimeHours,
                username, password
        );

        if (success) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sukses");
            alert.setHeaderText("Përdoruesi u krijua me sukses!");
            alert.setContentText("Username: " + username + "\nFjalëkalim i përkohshëm: " + password);
            alert.showAndWait();
            if (onSuccess != null) onSuccess.run();
            closeWindow();
        } else {
            showError("Gabim gjatë krijimit të përdoruesit. Provoni përsëri.");
        }
    }

    private int parseDepartmentId(String value) {
        if (value == null) return 0;
        try { return Integer.parseInt(value.split(" - ")[0].trim()); }
        catch (Exception e) { return 0; }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Gabim validimi");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) exitBtn.getScene().getWindow();
        stage.close();
    }
}
