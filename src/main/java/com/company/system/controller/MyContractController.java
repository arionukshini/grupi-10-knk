package com.company.system.controller;

import com.company.system.i18n.LanguageManager;
import com.company.system.model.Contract;
import com.company.system.model.User;
import com.company.system.service.ContractService;
import com.company.system.service.UserService;
import com.company.system.utils.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MyContractController {

    @FXML private Label employeeNameLabel;
    @FXML private Label employeeIdLabel;
    @FXML private Label contractIdLabel;
    @FXML private Label contractTypeLabel;
    @FXML private Label contractTypeLabel2;
    @FXML private Label startDateLabel;
    @FXML private Label endDateLabel;
    @FXML private Label salaryLabel;
    @FXML private Label statusLabel;
    @FXML private Label statusLabel2;
    @FXML private Label statusBadgeLabel;

    private Contract contract;

    @FXML
    public void initialize() {
        User user = Session.getUser();

        if (user == null) {
            showEmpty(LanguageManager.get("user.contract.noData"));
            return;
        }

        int employeeId = UserService.getEmployeeIdByUserId(user.getId());

        if (employeeId <= 0) {
            showEmpty(LanguageManager.get("user.contract.noData"));
            return;
        }

        contract = ContractService.getLatestContractByEmployeeId(employeeId);

        if (contract == null) {
            showEmpty(LanguageManager.get("user.contract.noRecord"));
            return;
        }

        showContract(contract);
    }

    private void showContract(Contract c) {
        employeeNameLabel.setText(c.getEmployeeName() != null ? c.getEmployeeName() : "-");
        employeeIdLabel.setText(String.valueOf(c.getEmployeeId()));
        contractIdLabel.setText(String.valueOf(c.getId()));
        contractTypeLabel.setText(c.getContractType() != null ? c.getContractType() : "-");
        contractTypeLabel2.setText(c.getContractType() != null ? c.getContractType() : "-");
        startDateLabel.setText(c.getStartDate() != null ? c.getStartDate().toString() : "-");
        endDateLabel.setText(c.getEndDate() != null ? c.getEndDate().toString() : "Pa afat");
        salaryLabel.setText(String.format("%.2f EUR", c.getSalary()));

        String status = c.getStatus() != null ? c.getStatus() : "-";
        statusLabel.setText(status);
        statusLabel2.setText(status);
        statusBadgeLabel.setText(LanguageManager.get("user.contract.status") + ": " + status);
    }

    private void showEmpty(String message) {
        if (statusBadgeLabel != null) statusBadgeLabel.setText(message);
    }
}
