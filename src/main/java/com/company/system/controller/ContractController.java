package com.company.system.controller;

import com.company.system.i18n.LanguageManager;
import com.company.system.model.Contract;
import com.company.system.service.ContractService;
import com.company.system.utils.DialogUtils;
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

    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label formTitleLabel;

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
    @FXML private ComboBox<String> contractTypeField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField salaryField;
    @FXML private ComboBox<String> statusField;

    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button clearButton;

    @FXML
    public void initialize() {
        loadTexts();
        setupTable();
        setupSearch();
        setupSelection();
        loadContracts();
    }

    private void loadTexts() {
        titleLabel.setText(LanguageManager.get("menu.contracts"));
        subtitleLabel.setText(LanguageManager.get("contracts.subtitle"));
        searchField.setPromptText(LanguageManager.get("contracts.search"));
        formTitleLabel.setText(LanguageManager.get("contracts.form"));

        idColumn.setText(LanguageManager.get("table.id"));
        employeeIdColumn.setText(LanguageManager.get("contracts.employeeId"));
        employeeNameColumn.setText(LanguageManager.get("contracts.employee"));
        contractTypeColumn.setText(LanguageManager.get("contracts.type"));
        startDateColumn.setText(LanguageManager.get("contracts.startDate"));
        endDateColumn.setText(LanguageManager.get("contracts.endDate"));
        salaryColumn.setText(LanguageManager.get("contracts.salary"));
        statusColumn.setText(LanguageManager.get("contracts.status"));

        employeeIdField.setPromptText(LanguageManager.get("contracts.employeeId"));
        contractTypeField.setPromptText(LanguageManager.get("contracts.type"));
        startDatePicker.setPromptText(LanguageManager.get("contracts.startDate"));
        endDatePicker.setPromptText(LanguageManager.get("contracts.endDate"));
        salaryField.setPromptText(LanguageManager.get("contracts.salary"));
        statusField.setPromptText(LanguageManager.get("contracts.status"));

        addButton.setText(LanguageManager.get("contracts.add"));
        updateButton.setText(LanguageManager.get("contracts.update"));
        deleteButton.setText(LanguageManager.get("contracts.delete"));
        clearButton.setText(LanguageManager.get("contracts.clear"));
    }

    private void setupTable() {
        contractsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
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
        contractTypeField.setValue(contract.getContractType());
        startDatePicker.setValue(contract.getStartDate().toLocalDate());

        if (contract.getEndDate() != null) {
            endDatePicker.setValue(contract.getEndDate().toLocalDate());
        } else {
            endDatePicker.setValue(null);
        }

        salaryField.setText(String.valueOf(contract.getSalary()));
        statusField.setValue(contract.getStatus());
    }

    @FXML
    private void addContract() {
        Contract contract = readForm(0);

        if (contract == null) return;

        if (ContractService.addContract(contract)) {
            loadContracts();
            clearForm();
            showInfo(LanguageManager.get("contracts.add.success"));
        } else {
            showError(LanguageManager.get("contracts.add.error"));
        }
    }

    @FXML
    private void updateContract() {
        Contract selectedContract = contractsTable.getSelectionModel().getSelectedItem();

        if (selectedContract == null) {
            showError(LanguageManager.get("contracts.select.update"));
            return;
        }

        Contract contract = readForm(selectedContract.getId());

        if (contract == null) return;

        if (ContractService.updateContract(contract)) {
            loadContracts();
            clearForm();
            showInfo(LanguageManager.get("contracts.update.success"));
        } else {
            showError(LanguageManager.get("contracts.update.error"));
        }
    }

    @FXML
    private void deleteContract() {
        Contract selectedContract = contractsTable.getSelectionModel().getSelectedItem();

        if (selectedContract == null) {
            showError(LanguageManager.get("contracts.select.delete"));
            return;
        }

        if (ContractService.deleteContract(selectedContract.getId())) {
            loadContracts();
            clearForm();
            showInfo(LanguageManager.get("contracts.delete.success"));
        } else {
            showError(LanguageManager.get("contracts.delete.error"));
        }
    }

    @FXML
    private void clearForm() {
        contractsTable.getSelectionModel().clearSelection();
        employeeIdField.clear();
        contractTypeField.setValue(null);
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        salaryField.clear();
        statusField.setValue("Active");
    }

    private Contract readForm(int id) {
        try {
            int employeeId = Integer.parseInt(employeeIdField.getText().trim());
            String contractType = contractTypeField.getValue();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            double salary = Double.parseDouble(salaryField.getText().trim());
            String status = statusField.getValue();

            if (contractType == null || contractType.isEmpty() || startDate == null || status == null || status.isEmpty()) {
                showError(LanguageManager.get("message.fillRequiredFields"));
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
            showError(LanguageManager.get("contracts.number.error"));
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
