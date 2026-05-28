package com.company.system.controller;

import com.company.system.i18n.LanguageManager;
import com.company.system.model.Salary;
import com.company.system.service.SalaryService;
import com.company.system.utils.DialogUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    private TableColumn<Salary, Double> grossColumn;

    @FXML
    private TableColumn<Salary, Double> bonusColumn;

    @FXML
    private TableColumn<Salary, Double> deductionsColumn;

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
    private Button calculateButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button clearButton;

    @FXML
    public void initialize() {
        loadTexts();
        setupSortedTables();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        employeeColumn.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        grossColumn.setCellValueFactory(new PropertyValueFactory<>("grossSalary"));
        bonusColumn.setCellValueFactory(new PropertyValueFactory<>("bonus"));
        deductionsColumn.setCellValueFactory(new PropertyValueFactory<>("deductions"));
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
        employeeColumn.setText(LanguageManager.get("salaries.employee"));
        grossColumn.setText(LanguageManager.get("salaries.gross"));
        bonusColumn.setText(LanguageManager.get("salaries.bonus"));
        deductionsColumn.setText(LanguageManager.get("salaries.deductions"));
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

        calculateButton.setText(LanguageManager.get("salaries.calculate"));
        saveButton.setText(LanguageManager.get("salaries.save"));
        updateButton.setText(LanguageManager.get("salaries.update"));
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

    private void fillForm(Salary salary) {

        employeeIdField.setText(String.valueOf(salary.getEmployeeId()));
        baseSalaryField.setText(String.valueOf(salary.getGrossSalary()));

        vacationDaysField.setText(String.valueOf(salary.getVacationDays()));
        workHoursField.setText(String.valueOf(salary.getWorkHours()));
        overtimeHoursField.setText(String.valueOf(salary.getOvertimeHours()));
        bonusField.setText(String.valueOf(salary.getBonus()));
        deductionsField.setText(String.valueOf(salary.getDeductions()));

        if (salary.getPaymentDate() != null) {
            paymentDatePicker.setValue(salary.getPaymentDate().toLocalDate());
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
    private void handleCalculateSalary() {

        try {

            int employeeId = Integer.parseInt(employeeIdField.getText());
            double baseSalary = Double.parseDouble(baseSalaryField.getText());

            int workedDays = Integer.parseInt(workedDaysField.getText());
            int vacationDays = Integer.parseInt(vacationDaysField.getText());

            double workHours = Double.parseDouble(workHoursField.getText());
            double overtimeHours = Double.parseDouble(overtimeHoursField.getText());

            double bonus = Double.parseDouble(bonusField.getText());
            double deductions = Double.parseDouble(deductionsField.getText());

            LocalDate localDate = paymentDatePicker.getValue();

            if (localDate == null) {
                showAlert(Alert.AlertType.ERROR,
                        LanguageManager.get("message.error.title"),
                        LanguageManager.get("salaries.paymentDate.required"));
                return;
            }

            Date paymentDate = Date.valueOf(localDate);

            calculatedSalary = SalaryService.calculateSalary(
                    0,
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

            showAlert(Alert.AlertType.INFORMATION,
                    LanguageManager.get("message.success.title"),
                    LanguageManager.get("salaries.calculate.success"));

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR,
                    LanguageManager.get("message.error.title"),
                    LanguageManager.get("salaries.invalid.input"));
        }
    }

    @FXML
    private void handleSaveSalary() {

        if (calculatedSalary == null) {

            showAlert(Alert.AlertType.WARNING,
                    LanguageManager.get("message.warning.title"),
                    LanguageManager.get("salaries.calculate.first"));

            return;
        }

        boolean saved = SalaryService.addSalary(calculatedSalary);

        if (saved) {

            // 🔥 ADD HISTORY ENTRY ALSO
            SalaryService.addSalaryHistory(calculatedSalary);

            loadSalaries();
            clearFields();
            calculatedSalary = null;

            showAlert(Alert.AlertType.INFORMATION,
                    LanguageManager.get("message.success.title"),
                    LanguageManager.get("salaries.save.success"));
        } else {

            showAlert(Alert.AlertType.ERROR,
                    LanguageManager.get("message.error.title"),
                    LanguageManager.get("salaries.save.error"));
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

            int employeeId = Integer.parseInt(employeeIdField.getText());
            double grossSalary = Double.parseDouble(baseSalaryField.getText());

            int vacationDays = Integer.parseInt(vacationDaysField.getText());
            double workHours = Double.parseDouble(workHoursField.getText());
            double overtimeHours = Double.parseDouble(overtimeHoursField.getText());

            double bonus = Double.parseDouble(bonusField.getText());
            double deductions = Double.parseDouble(deductionsField.getText());

            LocalDate localDate = paymentDatePicker.getValue();

            if (localDate == null) {
                showAlert(Alert.AlertType.ERROR,
                        LanguageManager.get("message.error.title"),
                        LanguageManager.get("salaries.paymentDate.required"));
                return;
            }

            Date paymentDate = Date.valueOf(localDate);

            double dailyRate = grossSalary / 22;
            double overtimePay = overtimeHours * (dailyRate / 8) * 1.5;
            double netSalary = grossSalary + bonus - deductions;

            Salary updatedSalary = new Salary(
                    selected.getId(),
                    employeeId,
                    grossSalary,
                    bonus,
                    deductions,
                    vacationDays,
                    workHours,
                    overtimeHours,
                    dailyRate,
                    overtimePay,
                    netSalary,
                    paymentDate
            );

            if (SalaryService.updateSalary(updatedSalary)) {

                loadSalaries();
                clearFields();

                showAlert(Alert.AlertType.INFORMATION,
                        LanguageManager.get("message.success.title"),
                        LanguageManager.get("salaries.update.success"));
            } else {
                showAlert(Alert.AlertType.ERROR,
                        LanguageManager.get("message.error.title"),
                        LanguageManager.get("salaries.update.error"));
            }

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
