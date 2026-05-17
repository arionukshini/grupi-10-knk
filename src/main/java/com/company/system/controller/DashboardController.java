package com.company.system.controller;

import com.company.system.i18n.LanguageManager;
import com.company.system.model.DashboardStats;
import com.company.system.model.DepartmentStats;
import com.company.system.service.DashboardService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

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
    private Label activeContractsTitleLabel;

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
    private Label activeContractsLabel;

    @FXML
    private Label expiringContractsLabel;

    @FXML
    private Label averageSalaryLabel;

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
    public void initialize() {
        loadTexts();
        loadStatistics();
        loadDepartmentChart();
        generateInsight();
    }

    private void loadTexts() {
        dashboardTitleLabel.setText(LanguageManager.get("dashboard.title"));
        dashboardSubtitleLabel.setText(LanguageManager.get("dashboard.subtitle"));

        employeesTitleLabel.setText(LanguageManager.get("dashboard.employees"));
        contractsTitleLabel.setText(LanguageManager.get("dashboard.contracts"));
        salariesTitleLabel.setText(LanguageManager.get("dashboard.salaries"));
        activeContractsTitleLabel.setText(LanguageManager.get("dashboard.activeContracts"));
        expiringContractsTitleLabel.setText(LanguageManager.get("dashboard.expiringContracts"));
        averageSalaryTitleLabel.setText(LanguageManager.get("dashboard.averageSalary"));
        insightTitleLabel.setText(LanguageManager.get("dashboard.insight.title"));

        xAxis.setLabel(LanguageManager.get("dashboard.axis.department"));
        yAxis.setLabel(LanguageManager.get("dashboard.axis.employees"));
        employeesChart.setTitle(LanguageManager.get("dashboard.chart.title"));
    }

    private void loadStatistics() {
        DashboardStats stats = DashboardService.getDashboardStats();

        totalEmployeesLabel.setText(String.valueOf(stats.getTotalEmployees()));
        totalContractsLabel.setText(String.valueOf(stats.getTotalContracts()));
        totalSalariesLabel.setText(String.valueOf(stats.getTotalSalaries()));
        activeContractsLabel.setText(String.valueOf(stats.getActiveContracts()));
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

    private void generateInsight() {
        DashboardStats stats = DashboardService.getDashboardStats();

        if (stats.getExpiringContracts() > 0) {
            aiInsightLabel.setText(LanguageManager.get("dashboard.insight.expiring"));
        } else if (stats.getActiveContracts() >= stats.getTotalContracts()) {
            aiInsightLabel.setText(LanguageManager.get("dashboard.insight.good"));
        } else {
            aiInsightLabel.setText(LanguageManager.get("dashboard.insight.normal"));
        }
    }
}