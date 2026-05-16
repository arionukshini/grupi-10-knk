package com.company.system.controller;

import com.company.system.db.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import com.company.system.model.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


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
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));

        employeeTable.setPlaceholder(new javafx.scene.control.Label("No employees found"));

        loadEmployeesFromDatabase();


        employeeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                System.out.println("Selected employee: " + newSelection.getFirstName());
            }
        });
    }

    private void loadEmployeesFromDatabase() {
        ObservableList<Employee> employees = FXCollections.observableArrayList();
        String query = "SELECT * FROM employees";

        Connection connection = DBConnection.connect();

        if (connection == null) {
            System.out.println("Connection failed!");
            return;
        }


            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query))  {

            while (resultSet.next()) {
                Employee employee = new Employee(
                        resultSet.getInt("id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("email"),
                        resultSet.getString("phone"),
                        resultSet.getString("position"),
                        resultSet.getInt("department_id"),
                        resultSet.getDate("hire_date"),
                        resultSet.getDouble("base_salary"),
                        resultSet.getString("status")
                );

                employees.add(employee);
            }

            employeeTable.setItems(employees);

        } catch (SQLException e) {
                System.out.println("Error loading employees: " + e.getMessage());
            }
    }
}

