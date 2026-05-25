package com.company.system.controller;

import com.company.system.model.Salary;
import com.company.system.service.SalaryService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
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
    private Button calculateButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button deleteButton;

    @FXML
    public void initialize() {

        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("id"));

        employeeColumn.setCellValueFactory(
                new PropertyValueFactory<>("employeeId"));

        grossColumn.setCellValueFactory(
                new PropertyValueFactory<>("grossSalary"));

        bonusColumn.setCellValueFactory(
                new PropertyValueFactory<>("bonus"));

        deductionsColumn.setCellValueFactory(
                new PropertyValueFactory<>("deductions"));

        vacationColumn.setCellValueFactory(
                new PropertyValueFactory<>("vacationDays"));

        workHoursColumn.setCellValueFactory(
                new PropertyValueFactory<>("workHours"));

        overtimeColumn.setCellValueFactory(
                new PropertyValueFactory<>("overtimeHours"));

        netColumn.setCellValueFactory(
                new PropertyValueFactory<>("netSalary"));

        dateColumn.setCellValueFactory(
                new PropertyValueFactory<>("paymentDate"));

        historyDateColumn.setCellValueFactory(
                new PropertyValueFactory<>("paymentDate"));

        historyGrossColumn.setCellValueFactory(
                new PropertyValueFactory<>("grossSalary"));

        historyBonusColumn.setCellValueFactory(
                new PropertyValueFactory<>("bonus"));

        historyNetColumn.setCellValueFactory(
                new PropertyValueFactory<>("netSalary"));

        loadSalaries();

    }
    private void loadSalaries() {

        salaries.setAll(
                SalaryService.getAllSalaries());

        salariesTable.setItems(salaries);
    }
    private void loadSalaryHistory(int employeeId) {

        salaryHistory.setAll(
                SalaryService.getSalaryHistory(employeeId));

        historyTable.setItems(salaryHistory);
    }

}
