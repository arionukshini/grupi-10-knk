package com.company.system.controller;

import com.company.system.i18n.LanguageManager;
import com.company.system.model.DashboardStats;
import com.company.system.model.DepartmentStats;
import com.company.system.model.SalaryByDepartmentStats;
import com.company.system.service.DashboardService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.util.Map;

public class DashboardController {

    @FXML
    private Label dashboardTitleLabel;

    @FXML
    private Label dashboardSubtitleLabel;

    @FXML
    private Label employeesTitleLabel;

    @FXML
    private Label contractsTitleLabel;

    @FXML
    private Label salariesTitleLabel;

    @FXML
    private Label departmentsTitleLabel;

    @FXML
    private Label activeContractsTitleLabel;

    @FXML
    private Label pendingContractsTitleLabel;

    @FXML
    private Label expiredContractsTitleLabel;

    @FXML
    private Label expiringContractsTitleLabel;

    @FXML
    private Label averageSalaryTitleLabel;

    @FXML
    private Label totalEmployeesLabel;

    @FXML
    private Label totalContractsLabel;

    @FXML
    private Label totalSalariesLabel;

    @FXML
    private Label totalDepartmentsLabel;

    @FXML
    private Label activeContractsLabel;

    @FXML
    private Label pendingContractsLabel;

    @FXML
    private Label expiredContractsLabel;

    @FXML
    private Label expiringContractsLabel;

    @FXML
    private Label averageSalaryLabel;

    @FXML
    private Label biTitleLabel;

    @FXML
    private Label biSummaryLabel;

    @FXML
    private Label insightTitleLabel;

    @FXML
    private Label aiInsightLabel;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private BarChart<String, Number> employeesChart;

    @FXML
    private PieChart contractStatusChart;

    @FXML
    private CategoryAxis salaryDepartmentAxis;

    @FXML
    private NumberAxis salaryAxis;

    @FXML
    private BarChart<String, Number> salaryChart;

    @FXML
    public void initialize() {
        loadTexts();
        loadStatistics();
        loadDepartmentChart();
        loadContractStatusChart();
        loadSalaryChart();
        generateBiSummary();
        generateInsight();
    }

    private void loadTexts() {
        dashboardTitleLabel.setText(LanguageManager.get("dashboard.title"));
        dashboardSubtitleLabel.setText(LanguageManager.get("dashboard.subtitle"));

        employeesTitleLabel.setText(LanguageManager.get("dashboard.employees"));
        contractsTitleLabel.setText(LanguageManager.get("dashboard.contracts"));
        salariesTitleLabel.setText(LanguageManager.get("dashboard.salaries"));
        departmentsTitleLabel.setText(LanguageManager.get("dashboard.departments"));
        activeContractsTitleLabel.setText(LanguageManager.get("dashboard.activeContracts"));
        pendingContractsTitleLabel.setText(LanguageManager.get("dashboard.pendingContracts"));
        expiredContractsTitleLabel.setText(LanguageManager.get("dashboard.expiredContracts"));
        expiringContractsTitleLabel.setText(LanguageManager.get("dashboard.expiringContracts"));
        averageSalaryTitleLabel.setText(LanguageManager.get("dashboard.averageSalary"));
        biTitleLabel.setText(LanguageManager.get("dashboard.bi.title"));
        insightTitleLabel.setText(LanguageManager.get("dashboard.insight.title"));

        xAxis.setLabel(LanguageManager.get("dashboard.axis.department"));
        yAxis.setLabel(LanguageManager.get("dashboard.axis.employees"));
        employeesChart.setTitle(LanguageManager.get("dashboard.chart.title"));

        contractStatusChart.setTitle(LanguageManager.get("dashboard.contractStatus.title"));
        salaryDepartmentAxis.setLabel(LanguageManager.get("dashboard.axis.department"));
        salaryAxis.setLabel(LanguageManager.get("dashboard.axis.salary"));
        salaryChart.setTitle(LanguageManager.get("dashboard.salaryChart.title"));
    }

    private void loadStatistics() {
        DashboardStats stats = DashboardService.getDashboardStats();

        totalEmployeesLabel.setText(String.valueOf(stats.getTotalEmployees()));
        totalContractsLabel.setText(String.valueOf(stats.getTotalContracts()));
        totalSalariesLabel.setText(String.valueOf(stats.getTotalSalaries()));
        totalDepartmentsLabel.setText(String.valueOf(stats.getTotalDepartments()));
        activeContractsLabel.setText(String.valueOf(stats.getActiveContracts()));
        pendingContractsLabel.setText(String.valueOf(stats.getPendingContracts()));
        expiredContractsLabel.setText(String.valueOf(stats.getExpiredContracts()));
        expiringContractsLabel.setText(String.valueOf(stats.getExpiringContracts()));

        averageSalaryLabel.setText(
                String.format("%.2f €", stats.getAverageSalary())
        );
    }

    private void loadDepartmentChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(LanguageManager.get("dashboard.chart.series"));

        for (DepartmentStats stats : DashboardService.getEmployeesPerDepartment()) {
            series.getData().add(
                    new XYChart.Data<>(
                            stats.getDepartmentName(),
                            stats.getEmployeeCount()
                    )
            );
        }

        employeesChart.setData(FXCollections.observableArrayList(series));
    }

    private void loadContractStatusChart() {
        contractStatusChart.getData().clear();

        for (Map.Entry<String, Integer> entry : DashboardService.getContractsByStatus().entrySet()) {
            if (entry.getValue() > 0) {
                contractStatusChart.getData().add(
                        new PieChart.Data(
                                translateContractStatus(entry.getKey()) + " (" + entry.getValue() + ")",
                                entry.getValue()
                        )
                );
            }
        }
    }

    private void loadSalaryChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(LanguageManager.get("dashboard.salaryChart.series"));

        for (SalaryByDepartmentStats stats : DashboardService.getAverageSalaryPerDepartment()) {
            series.getData().add(
                    new XYChart.Data<>(
                            stats.getDepartmentName(),
                            stats.getAverageSalary()
                    )
            );
        }

        salaryChart.setData(FXCollections.observableArrayList(series));
    }

    private void generateBiSummary() {
        DashboardStats stats = DashboardService.getDashboardStats();
        int totalContracts = Math.max(stats.getTotalContracts(), 1);
        double activeRate = (stats.getActiveContracts() * 100.0) / totalContracts;

        biSummaryLabel.setText(String.format(
                LanguageManager.get("dashboard.bi.summary"),
                stats.getTotalEmployees(),
                stats.getTotalContracts(),
                activeRate,
                stats.getAverageSalary()
        ));
    }

    private void generateInsight() {
        DashboardStats stats = DashboardService.getDashboardStats();

        if (stats.getExpiringContracts() > 0) {
            aiInsightLabel.setText(LanguageManager.get("dashboard.insight.expiring"));
        } else if (stats.getPendingContracts() > 0) {
            aiInsightLabel.setText(LanguageManager.get("dashboard.insight.pending"));
        } else if (stats.getExpiredContracts() > 0) {
            aiInsightLabel.setText(LanguageManager.get("dashboard.insight.expired"));
        } else if (stats.getActiveContracts() >= stats.getTotalContracts()) {
            aiInsightLabel.setText(LanguageManager.get("dashboard.insight.good"));
        } else {
            aiInsightLabel.setText(LanguageManager.get("dashboard.insight.normal"));
        }
    }

    private String translateContractStatus(String status) {
        return switch (status) {
            case "Active" -> LanguageManager.get("dashboard.activeContracts");
            case "Pending" -> LanguageManager.get("dashboard.pendingContracts");
            case "Expired" -> LanguageManager.get("dashboard.expiredContracts");
            default -> status;
        };
    }
}
