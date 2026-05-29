package com.company.system.controller;

import com.company.system.i18n.LanguageManager;
import com.company.system.model.Employee;
import com.company.system.service.EmployeeService;
import com.company.system.utils.DialogUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.sql.Date;
import java.time.LocalDate;

public class EmployeesController {

    private final ObservableList<Employee> employees = FXCollections.observableArrayList();
    private FilteredList<Employee> filteredEmployees;

    @FXML
    private Label titleLabel;

    @FXML
    private Label subtitleLabel;

    @FXML
    private Label formTitleLabel;

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
    private ComboBox<String> statusField;

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
        loadTexts();
        setupTable();
        setupSearch();
        setupSelection();
        loadEmployees();
    }

    private void loadTexts() {
        titleLabel.setText(LanguageManager.get("menu.employees"));
        subtitleLabel.setText(LanguageManager.get("employees.subtitle"));
        searchField.setPromptText(LanguageManager.get("employees.search"));
        formTitleLabel.setText(LanguageManager.get("employees.form"));

        idColumn.setText(LanguageManager.get("table.id"));
        firstNameColumn.setText(LanguageManager.get("employees.firstName"));
        lastNameColumn.setText(LanguageManager.get("employees.lastName"));
        emailColumn.setText(LanguageManager.get("employees.email"));
        phoneColumn.setText(LanguageManager.get("employees.phone"));
        positionColumn.setText(LanguageManager.get("employees.position"));
        departmentColumn.setText(LanguageManager.get("employees.departmentId"));
        hireDateColumn.setText(LanguageManager.get("employees.hireDate"));
        salaryColumn.setText(LanguageManager.get("employees.salary"));
        statusColumn.setText(LanguageManager.get("employees.status"));

        firstNameField.setPromptText(LanguageManager.get("employees.firstName"));
        lastNameField.setPromptText(LanguageManager.get("employees.lastName"));
        emailField.setPromptText(LanguageManager.get("employees.email"));
        phoneField.setPromptText(LanguageManager.get("employees.phone"));
        positionField.setPromptText(LanguageManager.get("employees.position"));
        departmentIdField.setPromptText(LanguageManager.get("employees.departmentId"));
        hireDatePicker.setPromptText(LanguageManager.get("employees.hireDate"));
        salaryField.setPromptText(LanguageManager.get("employees.baseSalary"));

        if (addButton != null) {
            addButton.setText(LanguageManager.get("employees.add"));
        }
        updateButton.setText(LanguageManager.get("employees.update"));
        deleteButton.setText(LanguageManager.get("employees.delete"));
        clearButton.setText(LanguageManager.get("employees.clear"));
    }

    private void setupTable() {
        employeesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
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
        SortedList<Employee> sortedEmployees = new SortedList<>(filteredEmployees);
        sortedEmployees.comparatorProperty().bind(employeesTable.comparatorProperty());
        employeesTable.setItems(sortedEmployees);

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
        statusField.setValue(employee.getStatus());
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
            showInfo(LanguageManager.get("employees.add.success"));
        } else {
            showError(LanguageManager.get("employees.add.error"));
        }
    }

    @FXML
    private void updateEmployee() {
        Employee selectedEmployee = employeesTable.getSelectionModel().getSelectedItem();

        if (selectedEmployee == null) {
            showError(LanguageManager.get("employees.select.update"));
            return;
        }

        Employee employee = readForm(selectedEmployee.getId());

        if (employee == null) {
            return;
        }

        if (EmployeeService.updateEmployee(employee)) {
            loadEmployees();
            clearForm();
            showInfo(LanguageManager.get("employees.update.success"));
        } else {
            showError(LanguageManager.get("employees.update.error"));
        }
    }

    @FXML
    private void deleteEmployee() {
        Employee selectedEmployee = employeesTable.getSelectionModel().getSelectedItem();

        if (selectedEmployee == null) {
            showError(LanguageManager.get("employees.select.delete"));
            return;
        }

        if (!confirmDeleteEmployee(selectedEmployee)) {
            return;
        }

        if (EmployeeService.deleteEmployee(selectedEmployee.getId())) {
            loadEmployees();
            clearForm();
            showInfo(LanguageManager.get("employees.delete.success"));
        } else {
            showError(LanguageManager.get("employees.delete.error"));
        }
    }

    private boolean confirmDeleteEmployee(Employee employee) {
        final double dialogWidth = 600;
        final double contentWidth = 500;

        Alert alert = new Alert(Alert.AlertType.NONE);
        DialogUtils.style(alert);

        ButtonType yesType = new ButtonType(LanguageManager.get("employees.delete.confirm.yes"), ButtonBar.ButtonData.YES);
        ButtonType noType = new ButtonType(LanguageManager.get("employees.delete.confirm.no"), ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yesType, noType);
        alert.setTitle(LanguageManager.get("employees.delete.confirm.title"));
        alert.setHeaderText(null);

        Label icon = new Label("!");
        icon.setStyle("""
                -fx-background-color: #fef2f2;
                -fx-background-radius: 999;
                -fx-border-color: #dc2626;
                -fx-border-radius: 999;
                -fx-text-fill: #dc2626;
                -fx-font-size: 42px;
                -fx-font-weight: bold;
                -fx-alignment: center;
                -fx-min-width: 76;
                -fx-min-height: 76;
                """);

        Label title = new Label(employee.getFirstName() + " " + employee.getLastName());
        title.setWrapText(true);
        title.setMinWidth(0);
        title.setPrefWidth(contentWidth);
        title.setMaxWidth(contentWidth);
        title.setAlignment(Pos.CENTER);
        title.setStyle("""
                -fx-font-size: 18px;
                -fx-font-weight: bold;
                -fx-text-alignment: center;
                """);

        Label subtitle = new Label(LanguageManager.get("employees.delete.confirm.subtitle"));
        subtitle.setWrapText(true);
        subtitle.setMinWidth(0);
        subtitle.setPrefWidth(contentWidth);
        subtitle.setMaxWidth(contentWidth);
        subtitle.setAlignment(Pos.CENTER);
        subtitle.setStyle("""
                -fx-font-size: 14px;
                -fx-opacity: 0.8;
                -fx-text-alignment: center;
                """);

        VBox content = new VBox(15, icon, title, subtitle);
        content.setAlignment(Pos.CENTER);
        content.setFillWidth(true);
        content.setMinWidth(0);
        content.setPrefWidth(contentWidth);
        content.setMaxWidth(contentWidth);

        alert.getDialogPane().setContent(content);
        alert.getDialogPane().setPrefWidth(dialogWidth);
        alert.getDialogPane().setMinWidth(dialogWidth);
        alert.getDialogPane().setPrefHeight(320);

        Platform.runLater(() -> {
            Button yesButton = (Button) alert.getDialogPane().lookupButton(yesType);
            Button noButton = (Button) alert.getDialogPane().lookupButton(noType);

            yesButton.setPrefWidth(contentWidth);
            noButton.setPrefWidth(contentWidth);
            yesButton.setMaxWidth(Double.MAX_VALUE);
            noButton.setMaxWidth(Double.MAX_VALUE);

            yesButton.setPrefHeight(42);
            noButton.setPrefHeight(42);

            yesButton.setStyle("""
                    -fx-background-color: #dc2626;
                    -fx-text-fill: white;
                    -fx-font-weight: bold;
                    -fx-background-radius: 10;
                    -fx-cursor: hand;
                    """);

            noButton.setStyle("""
                    -fx-background-radius: 10;
                    -fx-cursor: hand;
                    """);

            VBox buttonBox = new VBox(10, yesButton, noButton);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setFillWidth(true);
            buttonBox.setMinWidth(0);
            buttonBox.setPrefWidth(contentWidth);
            buttonBox.setMaxWidth(contentWidth);

            VBox popupContent = new VBox(20, content, buttonBox);
            popupContent.setAlignment(Pos.CENTER);
            popupContent.setFillWidth(true);
            popupContent.setMinWidth(0);
            popupContent.setPrefWidth(contentWidth);
            popupContent.setMaxWidth(contentWidth);
            VBox.setVgrow(content, Priority.NEVER);

            alert.getDialogPane().setContent(popupContent);
        });

        return alert.showAndWait()
                .filter(buttonType -> buttonType == yesType)
                .isPresent();
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
        statusField.setValue("Active");
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
            String status = statusField.getValue();

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                    || position.isEmpty() || hireDate == null || status.isEmpty()) {
                showError(LanguageManager.get("message.fillRequiredFields"));
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
            showError(LanguageManager.get("employees.number.error"));
            return null;
        }
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        DialogUtils.style(alert);
        alert.setTitle(LanguageManager.get("message.success.title"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        DialogUtils.style(alert);
        alert.setTitle(LanguageManager.get("message.error.title"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
