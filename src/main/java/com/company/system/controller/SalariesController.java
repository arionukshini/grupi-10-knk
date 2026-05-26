package com.company.system.controller;

import com.company.system.model.Salary;
import com.company.system.service.SalaryService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    private Salary calculatedSalary;

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
    public void initialize() {

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
        salariesTable.setItems(salaries);
    }

    private void loadSalaryHistory(int employeeId) {

        salaryHistory.setAll(
                SalaryService.getSalaryHistory(employeeId)
        );

        historyTable.setItems(salaryHistory);
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
                showAlert(Alert.AlertType.ERROR, "Error", "Select payment date!");
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
                    "Success",
                    "Salary calculated successfully!");

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR,
                    "Error",
                    "Invalid input values!");
        }
    }

    @FXML
    private void handleSaveSalary() {

        if (calculatedSalary == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Warning",
                    "Calculate salary first!"
            );

            return;
        }

        SalaryService.addSalary(calculatedSalary);

        loadSalaries();

        clearFields();

        calculatedSalary = null;

        showAlert(
                Alert.AlertType.INFORMATION,
                "Success",
                "Salary saved successfully!"
        );
    }
    @FXML
    private void handleDeleteSalary() {

        Salary selected = salariesTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING,
                    "Warning",
                    "Select a salary first!");
            return;
        }

        if (SalaryService.deleteSalary(selected.getId())) {
            loadSalaries();

            showAlert(Alert.AlertType.INFORMATION,
                    "Success",
                    "Salary deleted successfully!");
        }
    }

    @FXML
    private void handleUpdateSalary() {

        Salary selected = salariesTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING,
                    "Warning",
                    "Select a salary first!");
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
                showAlert(Alert.AlertType.ERROR, "Error", "Select payment date!");
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
                        "Success",
                        "Salary updated successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR,
                        "Error",
                        "Update failed!");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR,
                    "Error",
                    "Invalid input values!");
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
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}