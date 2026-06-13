package com.company.system.controller;


import com.company.system.utils.AppLogger;
import com.company.system.i18n.LanguageManager;
import com.company.system.model.Contract;
import com.company.system.model.Employee;
import com.company.system.model.User;
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

    @FXML
    private void handleExportPdf() {
        if (contract == null || employee == null) {
            showAlert(Alert.AlertType.WARNING, "Paralajmerim", "Nuk ka kontrate per te eksportuar.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ruaj kontraten si PDF");
        fileChooser.setInitialFileName("kontrata_" +
                contract.getEmployeeName().replace(" ", "_") + ".pdf");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF File", "*.pdf")
        );

        File file = fileChooser.showSaveDialog(exportPdfButton.getScene().getWindow());
        if (file == null) return;

        try {
            byte[] pdfBytes = ContractPdfService.generateContractPdf(employee, contract);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(pdfBytes);
            }
            showAlert(Alert.AlertType.INFORMATION, "Sukses",
                    "Kontrata PDF u ruajt me sukses:\n" + file.getAbsolutePath());

            java.awt.Desktop.getDesktop().open(file);
        } catch (Exception e) {
            AppLogger.error("Unexpected error", e);
            showAlert(Alert.AlertType.ERROR, "Gabim",
                    "Gabim gjate gjenerimit te PDF-se. Provoni perseri.");
        }
    }

    private void showEmpty(String message) {
        if (statusBadgeLabel != null) statusBadgeLabel.setText(message);
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
