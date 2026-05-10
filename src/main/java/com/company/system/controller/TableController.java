package com.company.system.controller;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import com.company.system.model.Employee;

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
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
            salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));
        }
    }

