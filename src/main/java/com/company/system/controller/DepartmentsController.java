package com.company.system.controller;

import com.company.system.i18n.LanguageManager;
import com.company.system.exceptions.DatabaseOperationException;
import com.company.system.models.Department;
import com.company.system.service.DepartmentService;
import com.company.system.utils.DialogUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import static com.company.system.utils.Validator.departmentNameValidator;
import static com.company.system.utils.Validator.descriptionValidator;
import static com.company.system.utils.Validator.isNotBlank;

public class DepartmentsController {

    private final ObservableList<Department> departments = FXCollections.observableArrayList();
    private FilteredList<Department> filteredDepartments;

    @FXML
    private Label titleLabel;

    @FXML
    private Label subtitleLabel;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Department> departmentsTable;

    @FXML
    private TableColumn<Department, Integer> idColumn;

    @FXML
    private TableColumn<Department, String> nameColumn;

    @FXML
    private TableColumn<Department, String> descriptionColumn;

    @FXML
    private Label formTitleLabel;

    @FXML
    private TextField nameField;

    @FXML
    private TextArea descriptionField;

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
        loadDepartments();
    }

    private void loadTexts() {
        titleLabel.setText(LanguageManager.get("menu.departments"));
        subtitleLabel.setText(LanguageManager.get("departments.subtitle"));
        searchField.setPromptText(LanguageManager.get("departments.search"));
        formTitleLabel.setText(LanguageManager.get("departments.form"));
        nameField.setPromptText(LanguageManager.get("departments.name"));
        descriptionField.setPromptText(LanguageManager.get("departments.description"));

        idColumn.setText(LanguageManager.get("departments.id"));
        nameColumn.setText(LanguageManager.get("departments.name"));
        descriptionColumn.setText(LanguageManager.get("departments.description"));

        if (addButton != null) {
            addButton.setText(LanguageManager.get("departments.add"));
        }
        updateButton.setText(LanguageManager.get("departments.update"));
        deleteButton.setText(LanguageManager.get("departments.delete"));
        clearButton.setText(LanguageManager.get("departments.clear"));
    }

    private void setupTable() {
        departmentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
    }

    private void setupSearch() {
        filteredDepartments = new FilteredList<>(departments, department -> true);
        SortedList<Department> sortedDepartments = new SortedList<>(filteredDepartments);
        sortedDepartments.comparatorProperty().bind(departmentsTable.comparatorProperty());
        departmentsTable.setItems(sortedDepartments);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String keyword = newValue == null ? "" : newValue.toLowerCase().trim();

            filteredDepartments.setPredicate(department -> {
                if (keyword.isEmpty()) {
                    return true;
                }

                return contains(department.getName(), keyword)
                        || contains(department.getDescription(), keyword);
            });
        });
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private void setupSelection() {
        departmentsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldDepartment, selectedDepartment) -> {
                    if (selectedDepartment != null) {
                        fillForm(selectedDepartment);
                    }
                }
        );
    }

    private void loadDepartments() {
        departments.setAll(DepartmentService.getAllDepartments());
    }

    private void fillForm(Department department) {
        nameField.setText(department.getName());
        descriptionField.setText(department.getDescription());
    }

    @FXML
    private void addDepartment() {
        Department department = readForm(0);

        if (department == null) {
            return;
        }

        try {
            if (DepartmentService.addDepartment(department)) {
                loadDepartments();
                clearForm();
                showInfo(LanguageManager.get("departments.add.success"));
            } else {
                showError(LanguageManager.get("departments.add.error"));
            }
        } catch (DatabaseOperationException e) {
            e.showAlert();
        }
    }

    @FXML
    private void updateDepartment() {
        Department selectedDepartment = departmentsTable.getSelectionModel().getSelectedItem();

        if (selectedDepartment == null) {
            showError(LanguageManager.get("departments.select.update"));
            return;
        }

        Department department = readForm(selectedDepartment.getId());

        if (department == null) {
            return;
        }

        try {
            if (DepartmentService.updateDepartment(department)) {
                loadDepartments();
                clearForm();
                showInfo(LanguageManager.get("departments.update.success"));
            }
        } catch (DatabaseOperationException e) {
            e.showAlert();
        }
    }

    @FXML
    private void deleteDepartment() {
        Department selectedDepartment = departmentsTable.getSelectionModel().getSelectedItem();

        if (selectedDepartment == null) {
            showError(LanguageManager.get("departments.select.delete"));
            return;
        }

        if (DepartmentService.deleteDepartment(selectedDepartment.getId())) {
            loadDepartments();
            clearForm();
            showInfo(LanguageManager.get("departments.delete.success"));
        } else {
            showError(LanguageManager.get("departments.delete.error"));
        }
    }

    @FXML
    private void clearForm() {
        departmentsTable.getSelectionModel().clearSelection();
        nameField.clear();
        descriptionField.clear();
    }

    private Department readForm(int id) {
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        String description = descriptionField.getText() == null ? "" : descriptionField.getText().trim();

        if (!isNotBlank(name)) {
            showError(LanguageManager.get("departments.name.required"));
            return null;
        }

        if (!departmentNameValidator(name)) {
            showError(LanguageManager.get("departments.name.length"));
            return null;
        }

        if (!descriptionValidator(description)) {
            showError(LanguageManager.get("departments.description.length"));
            return null;
        }

        return new Department(id, name, description);
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        DialogUtils.style(alert);
        alert.setTitle(LanguageManager.get("departments.success.title"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        DialogUtils.style(alert);
        alert.setTitle(LanguageManager.get("departments.error.title"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
