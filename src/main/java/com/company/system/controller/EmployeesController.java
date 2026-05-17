package com.company.system.controller;

import com.company.system.model.Employee;
import com.company.system.service.EmployeeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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

public class EmployeesController {

    private final ObservableList<Employee> employees = FXCollections.observableArrayList();
    private FilteredList<Employee> filteredEmployees;

    @FXML
    private Label titleLabel;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Employee> employeesTable;

    @FXML
    private TableColumn<Employee, Integer> idColumn;

    @FXML
    private TableColumn<Employee, String> firstNameColumn;

    @FXML
    private TableColumn<Employee, String> lastNameColumn;

    @FXML
    private TableColumn<Employee, String> emailColumn;

    @FXML
    private TableColumn<Employee, String> phoneColumn;

    @FXML
    private TableColumn<Employee, String> positionColumn;

    @FXML
    private TableColumn<Employee, Integer> departmentColumn;

    @FXML
    private TableColumn<Employee, Date> hireDateColumn;

    @FXML
    private TableColumn<Employee, Double> salaryColumn;

    @FXML
    private TableColumn<Employee, String> statusColumn;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField positionField;

    @FXML
    private TextField departmentIdField;

    @FXML
    private DatePicker hireDatePicker;

    @FXML
    private TextField salaryField;

    @FXML
    private TextField statusField;

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button clearButton;

    @FXML
    public void initialize() {
        setupTable();
        setupSearch();
        setupSelection();
        loadEmployees();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        departmentColumn.setCellValueFactory(new PropertyValueFactory<>("departmentId"));
        hireDateColumn.setCellValueFactory(new PropertyValueFactory<>("hireDate"));
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void setupSearch() {
        filteredEmployees = new FilteredList<>(employees, employee -> true);
        employeesTable.setItems(filteredEmployees);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String keyword = newValue == null ? "" : newValue.toLowerCase().trim();

            filteredEmployees.setPredicate(employee -> {
                if (keyword.isEmpty()) {
                    return true;
                }

                return contains(employee.getFirstName(), keyword)
                        || contains(employee.getLastName(), keyword)
                        || contains(employee.getEmail(), keyword)
                        || contains(employee.getPosition(), keyword)
                        || contains(employee.getStatus(), keyword);
            });
        });
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private void setupSelection() {
        employeesTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldEmployee, selectedEmployee) -> {
                    if (selectedEmployee != null) {
                        fillForm(selectedEmployee);
                    }
                }
        );
    }

    private void loadEmployees() {
        employees.setAll(EmployeeService.getAllEmployees());
    }

    private void fillForm(Employee employee) {
        firstNameField.setText(employee.getFirstName());
        lastNameField.setText(employee.getLastName());
        emailField.setText(employee.getEmail());
        phoneField.setText(employee.getPhone());
        positionField.setText(employee.getPosition());
        departmentIdField.setText(String.valueOf(employee.getDepartmentId()));
        hireDatePicker.setValue(employee.getHireDate().toLocalDate());
        salaryField.setText(String.valueOf(employee.getBaseSalary()));
        statusField.setText(employee.getStatus());
    }

    @FXML
    private void addEmployee() {
        Employee employee = readForm(0);

        if (employee == null) {
            return;
        }

        if (EmployeeService.addEmployee(employee)) {
            loadEmployees();
            clearForm();
            showInfo("Punetori u shtua me sukses.");
        } else {
            showError("Punetori nuk u shtua. Kontrolloni te dhenat ose databazen.");
        }
    }

    @FXML
    private void updateEmployee() {
        Employee selectedEmployee = employeesTable.getSelectionModel().getSelectedItem();

        if (selectedEmployee == null) {
            showError("Zgjidhni nje punetor per perditesim.");
            return;
        }

        Employee employee = readForm(selectedEmployee.getId());

        if (employee == null) {
            return;
        }

        if (EmployeeService.updateEmployee(employee)) {
            loadEmployees();
            clearForm();
            showInfo("Punetori u perditesua me sukses.");
        } else {
            showError("Punetori nuk u perditesua.");
        }
    }

    @FXML
    private void deleteEmployee() {
        Employee selectedEmployee = employeesTable.getSelectionModel().getSelectedItem();

        if (selectedEmployee == null) {
            showError("Zgjidhni nje punetor per fshirje.");
            return;
        }

        if (EmployeeService.deleteEmployee(selectedEmployee.getId())) {
            loadEmployees();
            clearForm();
            showInfo("Punetori u fshi me sukses.");
        } else {
            showError("Punetori nuk u fshi. Kontrolloni nese ka kontrata ose paga te lidhura.");
        }
    }

    @FXML
    private void clearForm() {
        employeesTable.getSelectionModel().clearSelection();
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        phoneField.clear();
        positionField.clear();
        departmentIdField.clear();
        hireDatePicker.setValue(null);
        salaryField.clear();
        statusField.setText("Active");
    }

    private Employee readForm(int id) {
        try {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String position = positionField.getText().trim();
            int departmentId = Integer.parseInt(departmentIdField.getText().trim());
            LocalDate hireDate = hireDatePicker.getValue();
            double salary = Double.parseDouble(salaryField.getText().trim());
            String status = statusField.getText().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                    || position.isEmpty() || hireDate == null || status.isEmpty()) {
                showError("Plotesoni fushat kryesore.");
                return null;
            }

            return new Employee(
                    id,
                    firstName,
                    lastName,
                    email,
                    phone,
                    position,
                    departmentId,
                    Date.valueOf(hireDate),
                    salary,
                    status
            );

        } catch (NumberFormatException e) {
            showError("Department ID dhe paga duhet te jene numra valid.");
            return null;
        }
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sukses");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Gabim");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}