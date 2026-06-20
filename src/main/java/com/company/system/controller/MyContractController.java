package com.company.system.controller;


import com.company.system.utils.AppLogger;
import com.company.system.i18n.LanguageManager;
import com.company.system.models.Contract;
import com.company.system.models.Employee;
import com.company.system.models.User;
import com.company.system.service.ContractPdfService;
import com.company.system.service.ContractService;
import com.company.system.service.EmployeeService;
import com.company.system.service.UserService;
import com.company.system.utils.DialogUtils;
import com.company.system.utils.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;

public class MyContractController {

    @FXML private Label titleLabel;
    @FXML private Label contractInfoTitleLabel;
    @FXML private Label contractTypeTitleLabel;
    @FXML private Label statusTitleLabel;
    @FXML private Label contractPeriodTitleLabel;
    @FXML private Label startDateTitleLabel;
    @FXML private Label endDateTitleLabel;
    @FXML private Label contractSalaryTitleLabel;
    @FXML private Label monthlySalaryTitleLabel;
    @FXML private Label employeeInfoTitleLabel;
    @FXML private Label employeeNameTitleLabel;
    @FXML private Label employeeIdTitleLabel;
    @FXML private Label detailsTitleLabel;
    @FXML private Label contractIdTitleLabel;
    @FXML private Label contractTypeSideTitleLabel;
    @FXML private Label statusSideTitleLabel;
    @FXML private Label exportHintLabel;
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
    @FXML private Button exportPdfButton;

    private Contract contract;
    private Employee employee;

    @FXML
    public void initialize() {
        applyTranslations();

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
        employee = EmployeeService.getEmployeeById(employeeId);

        if (contract == null) {
            showEmpty(LanguageManager.get("user.contract.noRecord"));
            if (exportPdfButton != null) exportPdfButton.setDisable(true);
            return;
        }

        showContract(contract);
    }

    private void applyTranslations() {
        titleLabel.setText(LanguageManager.get("user.contract.title"));
        contractInfoTitleLabel.setText(LanguageManager.get("profile.contractInfo"));
        contractTypeTitleLabel.setText(LanguageManager.get("user.contract.type"));
        statusTitleLabel.setText(LanguageManager.get("user.contract.status"));
        contractPeriodTitleLabel.setText(LanguageManager.get("user.contract.period"));
        startDateTitleLabel.setText(LanguageManager.get("user.contract.startDate"));
        endDateTitleLabel.setText(LanguageManager.get("user.contract.endDate"));
        contractSalaryTitleLabel.setText(LanguageManager.get("user.contract.salaryTitle"));
        monthlySalaryTitleLabel.setText(LanguageManager.get("user.salary.monthlyTitle"));
        employeeInfoTitleLabel.setText(LanguageManager.get("user.contract.employeeInfo"));
        employeeNameTitleLabel.setText(LanguageManager.get("user.department.name"));
        employeeIdTitleLabel.setText(LanguageManager.get("salaries.employeeId"));
        detailsTitleLabel.setText(LanguageManager.get("user.contract.details"));
        contractIdTitleLabel.setText(LanguageManager.get("user.contract.contractId"));
        contractTypeSideTitleLabel.setText(LanguageManager.get("leave.type"));
        statusSideTitleLabel.setText(LanguageManager.get("user.contract.status"));
        exportHintLabel.setText(LanguageManager.get("user.contract.exportHint"));
        exportPdfButton.setText(LanguageManager.get("user.contract.exportPdf"));
    }

    private void showContract(Contract c) {
        employeeNameLabel.setText(c.getEmployeeName() != null ? c.getEmployeeName() : "-");
        employeeIdLabel.setText(String.valueOf(c.getEmployeeId()));
        contractIdLabel.setText(String.valueOf(c.getId()));
        contractTypeLabel.setText(c.getContractType() != null ? c.getContractType() : "-");
        contractTypeLabel2.setText(c.getContractType() != null ? c.getContractType() : "-");
        startDateLabel.setText(c.getStartDate() != null ? c.getStartDate().toString() : "-");
        endDateLabel.setText(c.getEndDate() != null ? c.getEndDate().toString() : LanguageManager.get("user.contract.noEndDate"));
        salaryLabel.setText(String.format("%.2f EUR", c.getSalary()));

        String status = displayStatus(c.getStatus());
        statusLabel.setText(status);
        statusLabel2.setText(status);
        statusBadgeLabel.setText(LanguageManager.get("user.contract.status") + ": " + status);
    }

    @FXML
    private void handleExportPdf() {
        if (contract == null || employee == null) {
            showAlert(Alert.AlertType.WARNING,
                    LanguageManager.get("user.message.warning"),
                    LanguageManager.get("user.contract.export.noContract"));
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(LanguageManager.get("user.contract.export.saveTitle"));
        fileChooser.setInitialFileName("kontrata_" +
                contract.getEmployeeName().replace(" ", "_") + ".pdf");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(LanguageManager.get("file.pdf"), "*.pdf")
        );

        File file = fileChooser.showSaveDialog(exportPdfButton.getScene().getWindow());
        if (file == null) return;

        try {
            byte[] pdfBytes = ContractPdfService.generateContractPdf(employee, contract);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(pdfBytes);
            }
            showAlert(Alert.AlertType.INFORMATION,
                    LanguageManager.get("user.message.success"),
                    LanguageManager.get("user.contract.export.success") + "\n" + file.getAbsolutePath());

            java.awt.Desktop.getDesktop().open(file);
        } catch (Exception e) {
            AppLogger.error("Unexpected error", e);
            showAlert(Alert.AlertType.ERROR,
                    LanguageManager.get("user.message.error"),
                    LanguageManager.get("user.contract.export.error"));
        }
    }

    private void showEmpty(String message) {
        if (statusBadgeLabel != null) statusBadgeLabel.setText(message);
    }

    private String displayStatus(String status) {
        return switch (status == null ? "" : status) {
            case "Active" -> LanguageManager.get("status.active");
            case "Pending" -> LanguageManager.get("status.pending");
            case "Expired" -> LanguageManager.get("status.expired");
            case "Inactive" -> LanguageManager.get("status.inactive");
            case "Suspended" -> LanguageManager.get("status.suspended");
            default -> status == null || status.isBlank() ? "-" : status;
        };
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        DialogUtils.style(alert);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
