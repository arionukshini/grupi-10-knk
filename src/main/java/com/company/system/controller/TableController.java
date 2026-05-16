package com.company.system.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import com.company.system.model.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import com.company.system.service.EmployeeService;


public class TableController {

    @FXML
    private TableView<Employee> employeeTable;

    @FXML
    private TableColumn<Employee, Integer> idColumn;

    @FXML
    private TableColumn<Employee, String> nameColumn;

    @FXML
    private TableColumn<Employee, String> positionColumn;

    @FXML
    private TableColumn<Employee, Double> salaryColumn;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getFirstName() + " " + cellData.getValue().getLastName()
                )
        );
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));

        employeeTable.setPlaceholder(new javafx.scene.control.Label("No employees found"));

        loadEmployees();

        employeeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                System.out.println("Selected employee: " + newSelection.getFirstName());
            }
        });
    }
    @FXML
    private void handleRefresh() {
        loadEmployees();
        System.out.println("Table refreshed.");
    }
    private void loadEmployees() {
        ObservableList<Employee> employees = FXCollections.observableArrayList(EmployeeService.getAllEmployees());
        employeeTable.setItems(employees);
    }
}

