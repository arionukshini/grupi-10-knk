package com.company.system.controller;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import com.company.system.model.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Date;


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

        ObservableList<Employee> employees = FXCollections.observableArrayList(
                new Employee(1, "Ardit", "Berisha", "ardit@gmail.com", "044111111",
                        "Manager", 1, Date.valueOf("2024-01-10"), 900.0, "Active"),
                new Employee(2, "Sara", "Krasniqi", "sara@gmail.com", "044222222",
                        "Developer", 2, Date.valueOf("2024-02-15"), 700.0, "Active"),
                new Employee(3, "Luan", "Gashi", "luan@gmail.com", "044333333",
                        "HR", 3, Date.valueOf("2024-03-20"), 650.0, "Active")
        );

        employeeTable.setItems(employees);
        employeeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                System.out.println("Selected employee: " + newSelection.getFirstName());
            }
        });
    }
}

