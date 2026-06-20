package com.company.system.controller.support;

import com.company.system.i18n.LanguageManager;
import com.company.system.models.Contract;
import com.company.system.models.User;
import com.company.system.service.ContractService;
import com.company.system.utils.DialogUtils;
import com.company.system.utils.Session;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.util.List;

public final class ContractExpiryNotifier {

    private ContractExpiryNotifier() {
    }

    public static void showForCurrentUser() {
        User user = Session.getUser();
        if (user == null || user.getEmployeeId() == null || "ADMIN".equalsIgnoreCase(user.getRole())) {
            return;
        }

        List<Contract> expiringContracts = ContractService.getExpiringContractsForEmployee(user.getEmployeeId());
        if (expiringContracts.isEmpty()) {
            return;
        }

        Platform.runLater(() -> showWarning(buildMessage(expiringContracts)));
    }

    private static String buildMessage(List<Contract> contracts) {
        StringBuilder message = new StringBuilder(LanguageManager.get("contract.expiring.intro"));

        for (Contract contract : contracts) {
            message.append(LanguageManager.get("contract.expiring.itemPrefix"))
                    .append(contract.getContractType())
                    .append(LanguageManager.get("contract.expiring.statusSuffix"))
                    .append(LanguageManager.get("contract.expiring.expires"))
                    .append(": ")
                    .append(contract.getEndDate())
                    .append("\n");
        }

        message.append(LanguageManager.get("contract.expiring.footer"));
        return message.toString();
    }

    private static void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        DialogUtils.style(alert);
        alert.setTitle(LanguageManager.get("contract.expiring.alertTitle"));
        alert.setHeaderText(LanguageManager.get("contract.expiring.alertHeader"));

        Label content = new Label(message);
        content.setWrapText(true);
        content.setMaxWidth(400);
        content.setStyle("-fx-font-size: 13px;");

        alert.getDialogPane().setContent(content);
        alert.getDialogPane().setPrefWidth(480);
        alert.showAndWait();
    }
}
