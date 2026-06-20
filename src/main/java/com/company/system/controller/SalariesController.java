package com.company.system.controller;

import com.company.system.i18n.LanguageManager;
import com.company.system.exceptions.DatabaseOperationException;
import com.company.system.exceptions.InvalidPaymentException;
import com.company.system.exceptions.InvalidSalaryException;
import com.company.system.models.Salary;
import com.company.system.models.Employee;
import com.company.system.service.EmployeeService;
import com.company.system.service.SalaryService;
import com.company.system.utils.DialogUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Date;
import java.time.LocalDate;

public class SalariesController {

    private final ObservableList<Salary> salaries =
            FXCollections.observableArrayList();

    private final ObservableList<Salary> salaryHistory =
            FXCollections.observableArrayList();

    private SortedList<Salary> sortedSalaries;
    private SortedList<Salary> sortedSalaryHistory;

    private Salary calculatedSalary;

    @FXML
    private Label titleLabel;

    @FXML
    private Label subtitleLabel;

    @FXML
    private Label formTitleLabel;

    @FXML
    private Label historyTitleLabel;

    @FXML
    private TableView<Salary> salariesTable;

    @FXML
    private TableColumn<Salary, Integer> idColumn;

    @FXML
    private TableColumn<Salary, Integer> employeeColumn;

    @FXML
    private TableColumn<Salary, String> employeeNameColumn;

    @FXML
    private TableColumn<Salary, Double> grossColumn;

    @FXML
    private TableColumn<Salary, Double> bonusColumn;

    @FXML
    private TableColumn<Salary, Double> deductionsColumn;

    @FXML
    private TableColumn<Salary, Integer> workedDaysColumn;

    @FXML
    private TableColumn<Salary, Integer> vacationColumn;

    @FXML
    private TableColumn<Salary, Double> workHoursColumn;

    @FXML
    private TableColumn<Salary, Double> overtimeColumn;

    @FXML
    private TableColumn<Salary, Double> netColumn;

    @FXML
    private TableColumn<Salary, Date> dateColumn;

    @FXML
    private TableView<Salary> historyTable;

    @FXML
    private TableColumn<Salary, Date> historyDateColumn;

    @FXML
    private TableColumn<Salary, Double> historyGrossColumn;

    @FXML
    private TableColumn<Salary, Double> historyBonusColumn;

    @FXML
    private TableColumn<Salary, Double> historyNetColumn;

    @FXML
    private Label previewTitleLabel;

    @FXML
    private Label previewGrossTitleLabel;

    @FXML
    private Label previewNetTitleLabel;

    @FXML
    private Label grossPreviewLabel;

    @FXML
    private Label netPreviewLabel;

    @FXML
    private TextField employeeIdField;

    @FXML
    private TextField baseSalaryField;

    @FXML
    private TextField workedDaysField;

    @FXML
    private TextField vacationDaysField;

    @FXML
    private TextField workHoursField;

    @FXML
    private TextField overtimeHoursField;

    @FXML
    private TextField bonusField;

    @FXML
    private TextField deductionsField;

    @FXML
    private DatePicker paymentDatePicker;

    @FXML
    private Button saveButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button payButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button clearButton;

    @FXML
    public void initialize() {
        loadTexts();
        setupSortedTables();
        setupLivePreview();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        employeeColumn.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        employeeNameColumn.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        grossColumn.setCellValueFactory(new PropertyValueFactory<>("grossSalary"));
        bonusColumn.setCellValueFactory(new PropertyValueFactory<>("bonus"));
        deductionsColumn.setCellValueFactory(new PropertyValueFactory<>("deductions"));
        workedDaysColumn.setCellValueFactory(new PropertyValueFactory<>("workedDays"));
        vacationColumn.setCellValueFactory(new PropertyValueFactory<>("vacationDays"));
        workHoursColumn.setCellValueFactory(new PropertyValueFactory<>("workHours"));
        overtimeColumn.setCellValueFactory(new PropertyValueFactory<>("overtimeHours"));
        netColumn.setCellValueFactory(new PropertyValueFactory<>("netSalary"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));

        historyDateColumn.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        historyGrossColumn.setCellValueFactory(new PropertyValueFactory<>("grossSalary"));
        historyBonusColumn.setCellValueFactory(new PropertyValueFactory<>("bonus"));
        historyNetColumn.setCellValueFactory(new PropertyValueFactory<>("netSalary"));

        loadSalaries();

        salariesTable.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldValue, selectedSalary) -> {

                    if (selectedSalary != null) {

                        loadSalaryHistory(selectedSalary.getEmployeeId());
                        fillForm(selectedSalary);
                    }
                });
    }

    private void loadTexts() {
        titleLabel.setText(LanguageManager.get("menu.salaries"));
        subtitleLabel.setText(LanguageManager.get("salaries.subtitle"));
        formTitleLabel.setText(LanguageManager.get("salaries.form"));
        historyTitleLabel.setText(LanguageManager.get("salaries.history"));

        idColumn.setText(LanguageManager.get("table.id"));
        employeeColumn.setText(LanguageManager.get("salaries.employeeId"));
        employeeNameColumn.setText(LanguageManager.get("salaries.employee"));
        grossColumn.setText(LanguageManager.get("salaries.gross"));
        bonusColumn.setText(LanguageManager.get("salaries.bonus"));
        deductionsColumn.setText(LanguageManager.get("salaries.deductions"));
        workedDaysColumn.setText(LanguageManager.get("salaries.workedDays"));
        vacationColumn.setText(LanguageManager.get("salaries.vacation"));
        workHoursColumn.setText(LanguageManager.get("salaries.workHours"));
        overtimeColumn.setText(LanguageManager.get("salaries.overtime"));
        netColumn.setText(LanguageManager.get("salaries.net"));
        dateColumn.setText(LanguageManager.get("salaries.paymentDate"));

        historyDateColumn.setText(LanguageManager.get("salaries.paymentDate"));
        historyGrossColumn.setText(LanguageManager.get("salaries.gross"));
        historyBonusColumn.setText(LanguageManager.get("salaries.bonus"));
        historyNetColumn.setText(LanguageManager.get("salaries.net"));

        employeeIdField.setPromptText(LanguageManager.get("salaries.employeeId"));
        baseSalaryField.setPromptText(LanguageManager.get("salaries.baseSalary"));
        workedDaysField.setPromptText(LanguageManager.get("salaries.workedDays"));
        vacationDaysField.setPromptText(LanguageManager.get("salaries.vacationDays"));
        workHoursField.setPromptText(LanguageManager.get("salaries.workHours"));
        overtimeHoursField.setPromptText(LanguageManager.get("salaries.overtimeHours"));
        bonusField.setPromptText(LanguageManager.get("salaries.bonus"));
        deductionsField.setPromptText(LanguageManager.get("salaries.deductions"));
        paymentDatePicker.setPromptText(LanguageManager.get("salaries.paymentDate"));

        if (previewTitleLabel != null) {
            previewTitleLabel.setText(LanguageManager.get("salaries.preview"));
        }
        if (previewGrossTitleLabel != null) {
            previewGrossTitleLabel.setText(LanguageManager.get("salaries.gross"));
        }
        if (previewNetTitleLabel != null) {
            previewNetTitleLabel.setText(LanguageManager.get("salaries.net"));
        }

        if (saveButton != null) {
            saveButton.setText(LanguageManager.get("salaries.save"));
        }
        updateButton.setText(LanguageManager.get("salaries.update"));
        if (payButton != null) {
            payButton.setText(LanguageManager.get("salaries.payEmployee"));
        }
        deleteButton.setText(LanguageManager.get("salaries.delete"));
        clearButton.setText(LanguageManager.get("salaries.clear"));
    }

    private void setupSortedTables() {
        salariesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        sortedSalaries = new SortedList<>(salaries);
        sortedSalaries.comparatorProperty().bind(salariesTable.comparatorProperty());
        salariesTable.setItems(sortedSalaries);

        sortedSalaryHistory = new SortedList<>(salaryHistory);
        sortedSalaryHistory.comparatorProperty().bind(historyTable.comparatorProperty());
        historyTable.setItems(sortedSalaryHistory);
    }

    private void setupLivePreview() {
        Runnable refresh = this::refreshSalaryPreview;

        employeeIdField.textProperty().addListener((obs, oldValue, newValue) -> refresh.run());
        baseSalaryField.textProperty().addListener((obs, oldValue, newValue) -> refresh.run());
        workedDaysField.textProperty().addListener((obs, oldValue, newValue) -> refresh.run());
        vacationDaysField.textProperty().addListener((obs, oldValue, newValue) -> refresh.run());
        workHoursField.textProperty().addListener((obs, oldValue, newValue) -> refresh.run());
        overtimeHoursField.textProperty().addListener((obs, oldValue, newValue) -> refresh.run());
        bonusField.textProperty().addListener((obs, oldValue, newValue) -> refresh.run());
        deductionsField.textProperty().addListener((obs, oldValue, newValue) -> refresh.run());
        paymentDatePicker.valueProperty().addListener((obs, oldValue, newValue) -> refresh.run());

        refreshSalaryPreview();
    }
    private void fillForm(Salary salary) {

        employeeIdField.setText(String.valueOf(salary.getEmployeeId()));

        Employee employee = EmployeeService.getEmployeeById(salary.getEmployeeId());
        if (employee != null) {
            baseSalaryField.setText(String.valueOf(employee.getBaseSalary()));
        } else {
            baseSalaryField.setText(String.valueOf(salary.getDailyRate() * 22));
        }

        workedDaysField.setText(String.valueOf(salary.getWorkedDays()));
        vacationDaysField.setText(String.valueOf(salary.getVacationDays()));
        workHoursField.setText(String.valueOf(salary.getWorkHours()));
        overtimeHoursField.setText(String.valueOf(salary.getOvertimeHours()));
        bonusField.setText(String.valueOf(salary.getBonus()));
        deductionsField.setText(String.valueOf(salary.getDeductions()));

        if (salary.getPaymentDate() != null) {
            paymentDatePicker.setValue(salary.getPaymentDate().toLocalDate());
        }
    }


    private void refreshSalaryPreview() {
        try {
            Salary preview = buildSalaryFromForm(0, false);
            calculatedSalary = preview;
            updatePreviewLabels(preview);
        } catch (Exception ignored) {
            calculatedSalary = null;
            clearPreviewLabels();
        }
    }

    private void updatePreviewLabels(Salary salary) {
        if (salary == null) {
            clearPreviewLabels();
            return;
        }

        if (grossPreviewLabel != null) {
            grossPreviewLabel.setText(String.format("%.2f €", salary.getGrossSalary()));
        }
        if (netPreviewLabel != null) {
            netPreviewLabel.setText(String.format("%.2f €", salary.getNetSalary()));
        }
    }

    private void clearPreviewLabels() {
        if (grossPreviewLabel != null) {
            grossPreviewLabel.setText("-");
        }
        if (netPreviewLabel != null) {
            netPreviewLabel.setText("-");
        }
    }
    private void loadSalaries() {
        salaries.setAll(SalaryService.getAllSalaries());
    }

    private void loadSalaryHistory(int employeeId) {

        salaryHistory.setAll(
                SalaryService.getSalaryHistory(employeeId)
        );

    }

    @FXML
    private void handleSaveSalary() {

        Salary salaryToSave;
        try {
            salaryToSave = buildSalaryFromForm(0, true);
            if (salaryToSave == null) return;
        } catch (InvalidSalaryException e) {
            e.showAlert();
            return;
        } catch (InvalidPaymentException e) {
            e.showAlert();
            return;
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR,
                    LanguageManager.get("message.error.title"),
                    LanguageManager.get("salaries.invalid.input"));
            return;
        }

        try {
            boolean saved = SalaryService.addSalary(salaryToSave);

            if (saved) {

                loadSalaries();
                clearFields();
                calculatedSalary = null;


                showAlert(Alert.AlertType.INFORMATION,
                        LanguageManager.get("message.success.title"),
                        LanguageManager.get("salaries.save.success"));
            }
        } catch (DatabaseOperationException e) {
            e.showAlert();
        }

    }

    @FXML
    private void handleDeleteSalary() {

        Salary selected = salariesTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING,
                    LanguageManager.get("message.warning.title"),
                    LanguageManager.get("salaries.select.first"));
            return;
        }

        if (SalaryService.deleteSalary(selected.getId())) {
            loadSalaries();

            showAlert(Alert.AlertType.INFORMATION,
                    LanguageManager.get("message.success.title"),
                    LanguageManager.get("salaries.delete.success"));
        }
    }

    @FXML
    private void handleUpdateSalary() {

        Salary selected = salariesTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING,
                    LanguageManager.get("message.warning.title"),
                    LanguageManager.get("salaries.select.first"));
            return;
        }

        try {

            Salary updatedSalary = buildSalaryFromForm(selected.getId(), true);
            if (updatedSalary == null) return;

            if (SalaryService.updateSalary(updatedSalary)) {

                loadSalaries();
                loadSalaryHistory(updatedSalary.getEmployeeId());
                clearFields();

                showAlert(Alert.AlertType.INFORMATION,
                        LanguageManager.get("message.success.title"),
                        LanguageManager.get("salaries.update.success"));
            } else {
                showAlert(Alert.AlertType.ERROR,
                        LanguageManager.get("message.error.title"),
                        LanguageManager.get("salaries.update.error"));
            }

        } catch (InvalidSalaryException e) {
            e.showAlert();
        } catch (InvalidPaymentException e) {
            e.showAlert();
        } catch (DatabaseOperationException e) {
            e.showAlert();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR,
                    LanguageManager.get("message.error.title"),
                    LanguageManager.get("salaries.invalid.input"));
        }
    }

    @FXML
    private void handlePayEmployee() {

        Salary selected = salariesTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING,
                    LanguageManager.get("message.warning.title"),
                    LanguageManager.get("salaries.select.first"));
            return;
        }

        try {
            Salary salaryToPay = buildSalaryFromForm(selected.getId(), true);
            if (salaryToPay == null) return;

            if (!SalaryService.updateSalary(salaryToPay)) {
                showAlert(Alert.AlertType.ERROR,
                        LanguageManager.get("message.error.title"),
                        LanguageManager.get("salaries.update.error"));
                return;
            }

            if (SalaryService.addSalaryHistory(salaryToPay)) {
                loadSalaries();
                loadSalaryHistory(salaryToPay.getEmployeeId());
                clearFields();

                showAlert(Alert.AlertType.INFORMATION,
                        LanguageManager.get("message.success.title"),
                        LanguageManager.get("salaries.pay.success"));
            } else {
                showAlert(Alert.AlertType.ERROR,
                        LanguageManager.get("message.error.title"),
                        LanguageManager.get("salaries.pay.error"));
            }
        } catch (InvalidSalaryException e) {
            e.showAlert();
        } catch (InvalidPaymentException e) {
            e.showAlert();
        } catch (DatabaseOperationException e) {
            e.showAlert();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR,
                    LanguageManager.get("message.error.title"),
                    LanguageManager.get("salaries.invalid.input"));
        }
    }

    @FXML
    private void clearFields() {

        salariesTable.getSelectionModel().clearSelection();

        employeeIdField.clear();
        baseSalaryField.clear();
        workedDaysField.clear();
        vacationDaysField.clear();
        workHoursField.clear();
        overtimeHoursField.clear();
        bonusField.clear();
        deductionsField.clear();

        paymentDatePicker.setValue(null);

        calculatedSalary = null;

        clearPreviewLabels();
    }
    private Salary buildSalaryFromForm(int id, boolean requirePaymentDate) {
        int employeeId = parseInt(employeeIdField.getText());
        double baseSalary = parseDouble(baseSalaryField.getText());
        int workedDays = parseInt(workedDaysField.getText());
        int vacationDays = parseInt(vacationDaysField.getText());
        double workHours = parseDouble(workHoursField.getText());
        double overtimeHours = parseDouble(overtimeHoursField.getText());
        double bonus = parseDouble(bonusField.getText());
        double deductions = parseDouble(deductionsField.getText());

        LocalDate localDate = paymentDatePicker.getValue();
        if (localDate == null) {
            if (requirePaymentDate) {
                showAlert(Alert.AlertType.ERROR,
                        LanguageManager.get("message.error.title"),
                        LanguageManager.get("salaries.paymentDate.required"));
                return null;
            }

            localDate = LocalDate.now();
        }

        Date paymentDate = Date.valueOf(localDate);

        return SalaryService.calculateSalary(
                id,
                employeeId,
                baseSalary,
                workedDays,
                vacationDays,
                workHours,
                overtimeHours,
                bonus,
                deductions,
                paymentDate
        );
    }

    private int parseInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }

        return Integer.parseInt(value.trim());
    }

    private double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }

        return Double.parseDouble(value.trim());
    }
    private void showAlert(Alert.AlertType type, String title, String message) {

        Alert alert = new Alert(type);
        DialogUtils.style(alert);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
