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

}
