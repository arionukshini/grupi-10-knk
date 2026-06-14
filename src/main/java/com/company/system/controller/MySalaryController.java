package com.company.system.controller;

import com.company.system.i18n.LanguageManager;
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

    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label employeeNameLabel;
    @FXML private Label monthlySalaryTitleLabel;
    @FXML private Label grossSalaryLabelTitle;
    @FXML private Label grossSalaryLabel;
    @FXML private Label netSalaryLabelTitle;
    @FXML private Label netSalaryLabel;
    @FXML private Label bonusDeductionsTitleLabel;
    @FXML private Label bonusLabelTitle;
    @FXML private Label bonusLabel;
    @FXML private Label deductionsLabelTitle;
    @FXML private Label deductionsLabel;
    @FXML private Label workTimeTitleLabel;
    @FXML private Label workedDaysTitleLabel;
    @FXML private Label workedDaysLabel;
    @FXML private Label vacationDaysTitleLabel;
    @FXML private Label vacationDaysLabel;
    @FXML private Label workHoursTitleLabel;
    @FXML private Label workHoursLabel;
    @FXML private Label overtimeTitleLabel;
    @FXML private Label overtimeHoursTitleLabel;
    @FXML private Label overtimeHoursLabel;
    @FXML private Label dailyRateTitleLabel;
    @FXML private Label dailyRateLabel;
    @FXML private Label overtimePayTitleLabel;
    @FXML private Label overtimePayLabel;
    @FXML private Label historyTitleLabel;
    @FXML private Label calculateTitleLabel;
    @FXML private Label workedDaysInputLabel;
    @FXML private Label calculatedSalaryTitleLabel;
    @FXML private Label calculatedSalaryLabel;
    @FXML private Label calculationHintLabel;
    @FXML private TextField workedDaysInput;
    @FXML private VBox historyContainer;

    private Salary latestSalary;

    @FXML
    public void initialize() {
        applyTranslations();

        User user = Session.getUser();
        if (user == null) {
            showEmpty(LanguageManager.get("user.salary.noUser"));
            return;
        }

        int employeeId = UserService.getEmployeeIdByUserId(user.getId());
        if (employeeId <= 0) {
            showEmpty(LanguageManager.get("user.salary.noEmployee"));
            return;
        }

        latestSalary = SalaryService.getLatestSalaryByEmployeeId(employeeId);
        if (latestSalary == null) {
            showEmpty(LanguageManager.get("user.salary.noRecord"));
            return;
        }

        showSalary(latestSalary);
        showHistory(SalaryService.getSalaryHistory(employeeId));

        workedDaysInput.setText(String.valueOf(latestSalary.getWorkedDays()));
        workedDaysInput.textProperty().addListener((obs, oldValue, newValue) -> calculateByWorkedDays());
        calculateByWorkedDays();
    }

    private void applyTranslations() {
        titleLabel.setText(LanguageManager.get("user.salary.title"));
        subtitleLabel.setText(LanguageManager.get("user.salary.subtitle"));
        monthlySalaryTitleLabel.setText(LanguageManager.get("user.salary.monthlyTitle"));
        grossSalaryLabelTitle.setText(LanguageManager.get("user.salary.gross"));
        netSalaryLabelTitle.setText(LanguageManager.get("user.salary.net"));
        bonusDeductionsTitleLabel.setText(LanguageManager.get("user.salary.bonusDeductionsTitle"));
        bonusLabelTitle.setText(LanguageManager.get("user.salary.bonus"));
        deductionsLabelTitle.setText(LanguageManager.get("user.salary.deductions"));
        workTimeTitleLabel.setText(LanguageManager.get("user.salary.workTimeTitle"));
        workedDaysTitleLabel.setText(LanguageManager.get("user.salary.workedDays"));
        vacationDaysTitleLabel.setText(LanguageManager.get("user.salary.vacationDays"));
        workHoursTitleLabel.setText(LanguageManager.get("user.salary.workHours"));
        overtimeTitleLabel.setText(LanguageManager.get("user.salary.overtimeTitle"));
        overtimeHoursTitleLabel.setText(LanguageManager.get("user.salary.overtimeHours"));
        dailyRateTitleLabel.setText(LanguageManager.get("user.salary.dailyRate"));
        overtimePayTitleLabel.setText(LanguageManager.get("user.salary.overtimePay"));
        historyTitleLabel.setText(LanguageManager.get("user.salary.historyTitle"));
        calculateTitleLabel.setText(LanguageManager.get("user.salary.calculateTitle"));
        workedDaysInputLabel.setText(LanguageManager.get("user.salary.workedDays"));
        calculatedSalaryTitleLabel.setText(LanguageManager.get("user.salary.calculatedSalary"));
        calculationHintLabel.setText(LanguageManager.get("user.salary.calculationHint"));
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
            historyContainer.getChildren().add(new Label(LanguageManager.get("user.salary.noHistory")));
            return;
        }

        for (Salary salary : history) {
            Label card = new Label(String.format(
                    LanguageManager.get("user.salary.historyItem"),
                    salary.getPaymentDate(),
                    formatMoney(salary.getGrossSalary()),
                    formatMoney(salary.getBonus()),
                    formatMoney(salary.getNetSalary())
            ));

            card.getStyleClass().addAll("info-card", "body-text");
            historyContainer.getChildren().add(card);
        }
    }

    private void showEmpty(String message) {
        employeeNameLabel.setText(message);
    }

    private String formatMoney(double value) {
        return String.format("%.2f EUR", value);
    }
}
