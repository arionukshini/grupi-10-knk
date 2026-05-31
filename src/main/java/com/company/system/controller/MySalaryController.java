package com.company.system.controller;


import com.company.system.model.Salary;
import com.company.system.model.User;
import com.company.system.service.SalaryService;
import com.company.system.service.UserService;
import com.company.system.utils.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.List;

public class MySalaryController {

    @FXML private Label employeeNameLabel;
    @FXML private Label grossSalaryLabel;
    @FXML private Label netSalaryLabel;
    @FXML private Label bonusLabel;
    @FXML private Label deductionsLabel;
    @FXML private Label workedDaysLabel;
    @FXML private Label vacationDaysLabel;
    @FXML private Label workHoursLabel;
    @FXML private Label overtimeHoursLabel;
    @FXML private Label dailyRateLabel;
    @FXML private Label overtimePayLabel;
    @FXML private Label calculatedSalaryLabel;
    @FXML private TextField workedDaysInput;
    @FXML private VBox historyContainer;

    private Salary latestSalary;

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

        latestSalary = SalaryService.getLatestSalaryByEmployeeId(employeeId);

        if (latestSalary == null) {
            showEmpty("Nuk ka pagë të regjistruar.");
            return;
        }

        showSalary(latestSalary);
        showHistory(SalaryService.getSalaryHistory(employeeId));

        workedDaysInput.setText(String.valueOf(latestSalary.getWorkedDays()));
        workedDaysInput.textProperty().addListener((obs, oldValue, newValue) -> calculateByWorkedDays());
        calculateByWorkedDays();
    }

    private void showSalary(Salary salary) {
        employeeNameLabel.setText(salary.getEmployeeName());
        grossSalaryLabel.setText(formatMoney(salary.getGrossSalary()));
        netSalaryLabel.setText(formatMoney(salary.getNetSalary()));
        bonusLabel.setText(formatMoney(salary.getBonus()));
        deductionsLabel.setText(formatMoney(salary.getDeductions()));
        workedDaysLabel.setText(String.valueOf(salary.getWorkedDays()));
        vacationDaysLabel.setText(String.valueOf(salary.getVacationDays()));
        workHoursLabel.setText(String.format("%.1f", salary.getWorkHours()));
        overtimeHoursLabel.setText(String.format("%.1f", salary.getOvertimeHours()));
        dailyRateLabel.setText(formatMoney(salary.getDailyRate()));
        overtimePayLabel.setText(formatMoney(salary.getOvertimePay()));
    }

    private void calculateByWorkedDays() {
        try {
            int workedDays = Integer.parseInt(workedDaysInput.getText().trim());
            workedDays = Math.max(0, Math.min(workedDays, 22));

            double calculatedSalary =
                    latestSalary.getDailyRate() * workedDays
                            + latestSalary.getOvertimePay()
                            + latestSalary.getBonus()
                            - latestSalary.getDeductions();

            calculatedSalaryLabel.setText(formatMoney(calculatedSalary));
        } catch (Exception e) {
            calculatedSalaryLabel.setText("-");
        }
    }

    private void showHistory(List<Salary> history) {
        historyContainer.getChildren().clear();

        if (history == null || history.isEmpty()) {
            historyContainer.getChildren().add(new Label("Nuk ka histori të pagave."));
            return;
        }

        for (Salary salary : history) {
            Label card = new Label(
                    "Data: " + salary.getPaymentDate()
                            + " | Bruto: " + formatMoney(salary.getGrossSalary())
                            + " | Bonus: " + formatMoney(salary.getBonus())
                            + " | Neto: " + formatMoney(salary.getNetSalary())
            );

            card.getStyleClass().addAll("info-card", "body-text");
            historyContainer.getChildren().add(card);
        }
    }

    private void showEmpty(String message) {
        employeeNameLabel.setText(message);
    }

    private String formatMoney(double value) {
        return String.format("%.2f €", value);
    }
}
