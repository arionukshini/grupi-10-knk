package com.company.system.controller;

import com.company.system.model.Contract;
import com.company.system.service.ContractService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Date;
import java.time.LocalDate;

public class ContractController {

    private final ObservableList<Contract> contracts = FXCollections.observableArrayList();
    private FilteredList<Contract> filteredContracts;

    @FXML private TextField searchField;
    @FXML private TableView<Contract> contractsTable;

    @FXML private TableColumn<Contract, Integer> idColumn;
    @FXML private TableColumn<Contract, Integer> employeeIdColumn;
    @FXML private TableColumn<Contract, String> employeeNameColumn;
    @FXML private TableColumn<Contract, String> contractTypeColumn;
    @FXML private TableColumn<Contract, Date> startDateColumn;
    @FXML private TableColumn<Contract, Date> endDateColumn;
    @FXML private TableColumn<Contract, Double> salaryColumn;
    @FXML private TableColumn<Contract, String> statusColumn;

    @FXML private TextField employeeIdField;
    @FXML private TextField contractTypeField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField salaryField;
    @FXML private TextField statusField;

    @FXML
    public void initialize() {
        setupTable();
        setupSearch();
        setupSelection();
        loadContracts();
    }

    private void setupTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        employeeIdColumn.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        employeeNameColumn.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        contractTypeColumn.setCellValueFactory(new PropertyValueFactory<>("contractType"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void setupSearch() {
        filteredContracts = new FilteredList<>(contracts, contract -> true);
        SortedList<Contract> sortedContracts = new SortedList<>(filteredContracts);
        sortedContracts.comparatorProperty().bind(contractsTable.comparatorProperty());
        contractsTable.setItems(sortedContracts);

        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            String keyword = newValue == null ? "" : newValue.toLowerCase().trim();

            filteredContracts.setPredicate(contract -> {
                if (keyword.isEmpty()) return true;

                return contains(contract.getEmployeeName(), keyword)
                        || contains(contract.getContractType(), keyword)
                        || contains(contract.getStatus(), keyword);
            });
        });
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private void setupSelection() {
        contractsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldContract, selectedContract) -> {
                    if (selectedContract != null) {
                        fillForm(selectedContract);
                    }
                }
        );
    }

    private void loadContracts() {
        contracts.setAll(ContractService.getAllContracts());
    }

    private void fillForm(Contract contract) {
        employeeIdField.setText(String.valueOf(contract.getEmployeeId()));
        contractTypeField.setText(contract.getContractType());
        startDatePicker.setValue(contract.getStartDate().toLocalDate());

        if (contract.getEndDate() != null) {
            endDatePicker.setValue(contract.getEndDate().toLocalDate());
        } else {
            endDatePicker.setValue(null);
        }

        salaryField.setText(String.valueOf(contract.getSalary()));
        statusField.setText(contract.getStatus());
    }

    @FXML
    private void addContract() {
        Contract contract = readForm(0);

        if (contract == null) return;

        if (ContractService.addContract(contract)) {
            loadContracts();
            clearForm();
            showInfo("Kontrata u shtua me sukses.");
        } else {
            showError("Kontrata nuk u shtua.");
        }
    }

    @FXML
    private void updateContract() {
        Contract selectedContract = contractsTable.getSelectionModel().getSelectedItem();

        if (selectedContract == null) {
            showError("Zgjidhni nje kontrate per perditesim.");
            return;
        }

        Contract contract = readForm(selectedContract.getId());

        if (contract == null) return;

        if (ContractService.updateContract(contract)) {
            loadContracts();
            clearForm();
            showInfo("Kontrata u perditesua me sukses.");
        } else {
            showError("Kontrata nuk u perditesua.");
        }
    }

    @FXML
    private void deleteContract() {
        Contract selectedContract = contractsTable.getSelectionModel().getSelectedItem();

        if (selectedContract == null) {
            showError("Zgjidhni nje kontrate per fshirje.");
            return;
        }

        if (ContractService.deleteContract(selectedContract.getId())) {
            loadContracts();
            clearForm();
            showInfo("Kontrata u fshi me sukses.");
        } else {
            showError("Kontrata nuk u fshi.");
        }
    }

    @FXML
    private void clearForm() {
        contractsTable.getSelectionModel().clearSelection();
        employeeIdField.clear();
        contractTypeField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        salaryField.clear();
        statusField.setText("Active");
    }

    private Contract readForm(int id) {
        try {
            int employeeId = Integer.parseInt(employeeIdField.getText().trim());
            String contractType = contractTypeField.getText().trim();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            double salary = Double.parseDouble(salaryField.getText().trim());
            String status = statusField.getText().trim();

            if (contractType.isEmpty() || startDate == null || status.isEmpty()) {
                showError("Plotesoni fushat kryesore.");
                return null;
            }

            return new Contract(
                    id,
                    employeeId,
                    "",
                    contractType,
                    Date.valueOf(startDate),
                    endDate == null ? null : Date.valueOf(endDate),
                    salary,
                    status
            );

        } catch (NumberFormatException e) {
            showError("Employee ID dhe paga duhet te jene numra valid.");
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
