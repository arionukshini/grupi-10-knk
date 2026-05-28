package com.company.system.controller;

import com.company.system.i18n.LanguageManager;
import com.company.system.model.Department;
import com.company.system.service.DepartmentService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class DepartmentsController {

    @FXML
    private Label titleLabel;

    @FXML
    private Label subtitleLabel;

    @FXML
    private TableView<Department> departmentsTable;

    @FXML
    private TableColumn<Department, Integer> idColumn;

    @FXML
    private TableColumn<Department, String> nameColumn;

    @FXML
    private TableColumn<Department, String> locationColumn;

    @FXML
    public void initialize() {
        loadTexts();
        setupTable();
        loadDepartments();
    }

    private void loadTexts() {
        titleLabel.setText(LanguageManager.get("menu.departments"));
        subtitleLabel.setText(LanguageManager.get("departments.subtitle"));
        idColumn.setText(LanguageManager.get("departments.id"));
        nameColumn.setText(LanguageManager.get("departments.name"));
        locationColumn.setText(LanguageManager.get("departments.location"));
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
    }

    private void loadDepartments() {
        departmentsTable.setItems(FXCollections.observableArrayList(DepartmentService.getAllDepartments()));
    }
}
