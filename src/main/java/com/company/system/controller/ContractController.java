package controllers;

import models.Contract;
import database.ContractDatabase;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ContractController {

    
    @FXML private TableView<Contract> tableContracts;
    @FXML private TableColumn<Contract, Integer> colId;
    @FXML private TableColumn<Contract, String> colEmployeeName;
    @FXML private TableColumn<Contract, String> colStatus;

    
    @FXML private TextField txtEmployeeId; 
    @FXML private TextField txtStatus;     

    
    @FXML
    public void initialize() {
      
        colId.setCellValueFactory(new PropertyValueFactory<>("contractId"));
        colEmployeeName.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        refreshTable();
    }

    
    private void refreshTable() {
        tableContracts.setItems(ContractDatabase.getAllContracts());
    }

    
    @FXML
    private void handleAdd() {
        int empId = Integer.parseInt(txtEmployeeId.getText());
        String status = txtStatus.getText();

        if (ContractDatabase.addContract(empId, status)) {
            refreshTable(); // Rifresko pamjen
            clearFields();
        }
    }

    
    @FXML
    private void handleUpdate() {
        Contract selected = tableContracts.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String newStatus = txtStatus.getText();
            if (ContractDatabase.updateContract(selected.getContractId(), newStatus)) {
                refreshTable();
                clearFields();
            }
        }
    }

    
    @FXML
    private void handleDelete() {
        Contract selected = tableContracts.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (ContractDatabase.deleteContract(selected.getContractId())) {
                refreshTable();
                clearFields();
            }
        }
    }

    private void clearFields() {
        txtEmployeeId.clear();
        txtStatus.clear();
    }
}