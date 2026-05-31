package com.company.system.controller;

import com.company.system.model.Contract;
import com.company.system.model.User;
import com.company.system.service.ContractService;
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
import java.io.PrintWriter;

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
    @FXML private Button exportButton;

    private Contract contract;

    @FXML
    public void initialize() {
        User user = Session.getUser();

        if (user == null) {
            showEmpty("Nuk ka user të kyçur.");
            return;
        }

        int employeeId = UserService.getEmployeeIdByUserId(user.getId());

        if (employeeId <= 0) {
            showEmpty("Useri nuk është i lidhur me punëtor.");
            return;
        }

        contract = ContractService.getLatestContractByEmployeeId(employeeId);

        if (contract == null) {
            showEmpty("Nuk ka kontratë të regjistruar.");
            if (exportButton != null) exportButton.setDisable(true);
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
        salaryLabel.setText(String.format("%.2f €", c.getSalary()));

        String status = c.getStatus() != null ? c.getStatus() : "-";
        statusLabel.setText(status);
        statusLabel2.setText(status);
        statusBadgeLabel.setText("Statusi: " + status);
    }

    @FXML
    private void handleExportTxt() {
        if (contract == null) {
            showAlert(Alert.AlertType.WARNING, "Paralajmërim", "Nuk ka kontratë për të eksportuar.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ruaj kontratën");
        fileChooser.setInitialFileName("kontrata_" + contract.getEmployeeName()
                .replace(" ", "_") + ".txt");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text File", "*.txt")
        );

        File file = fileChooser.showSaveDialog(exportButton.getScene().getWindow());

        if (file == null) return;

        try (PrintWriter writer = new PrintWriter(new FileOutputStream(file))) {
            writer.println("================================================");
            writer.println("           KONTRATA E PUNESIMIT");
            writer.println("================================================");
            writer.println();
            writer.println("Contract ID   : " + contract.getId());
            writer.println("Punetori      : " + contract.getEmployeeName());
            writer.println("Employee ID   : " + contract.getEmployeeId());
            writer.println();
            writer.println("------------------------------------------------");
            writer.println("Lloji         : " + contract.getContractType());
            writer.println("Statusi       : " + contract.getStatus());
            writer.println("Data fillimit : " + contract.getStartDate());
            writer.println("Data mbarimit : " + (contract.getEndDate() != null
                    ? contract.getEndDate().toString() : "Pa afat"));
            writer.println("Paga bazë     : " + String.format("%.2f EUR", contract.getSalary()));
            writer.println();
            writer.println("================================================");
            writer.println("  Ky dokument eshte gjeneruar automatikisht.");
            writer.println("================================================");

            showAlert(Alert.AlertType.INFORMATION, "Sukses",
                    "Kontrata u eksportua me sukses:\n" + file.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Gabim",
                    "Kontrata nuk u eksportua. Provoni perseri.");
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
