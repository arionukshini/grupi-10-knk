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
    private Label totalEmployeesLabel;

    @FXML
    private Label totalDepartmentsLabel;

    @FXML
    private Label activeContractsLabel;

    @FXML
    private Label averageSalaryLabel;

    @FXML
    private Label aiInsightLabel;

    @FXML
    private Label dashboardTitleLabel;

    @FXML
    private Label employeesLabel;

    @FXML
    private Label departmentsLabel;

    @FXML
    private Label contractsLabel;

    @FXML
    private Label salaryLabel;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private BarChart<String, Number> employeesChart;

    @FXML
    public void initialize() {

        loadStatistics();
        loadDepartmentChart();
        generateAIInsight();
    }

    private void loadStatistics() {

        DashboardStats stats = DashboardService.getDashboardStats();

        totalEmployeesLabel.setText(String.valueOf(stats.getTotalEmployees()));
        totalDepartmentsLabel.setText(String.valueOf(stats.getTotalDepartments()));
        activeContractsLabel.setText(String.valueOf(stats.getActiveContracts()));

        averageSalaryLabel.setText(
                String.format("%.2f €", stats.getAverageSalary())
        );

        dashboardTitleLabel.setText(LanguageManager.get("dashboard.title"));

        employeesLabel.setText(LanguageManager.get("dashboard.employees"));
        departmentsLabel.setText(LanguageManager.get("dashboard.departments"));
        contractsLabel.setText(LanguageManager.get("dashboard.contracts"));
        salaryLabel.setText(LanguageManager.get("dashboard.salary"));

        xAxis.setLabel(LanguageManager.get("dashboard.axis.department"));
        yAxis.setLabel(LanguageManager.get("dashboard.axis.employees"));
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

        employeesChart.setData(
                FXCollections.observableArrayList(series)
        );

        employeesChart.setTitle(LanguageManager.get("dashboard.chart.title"));
    }

    private void generateAIInsight() {

        DashboardStats stats = DashboardService.getDashboardStats();

        String insight;

        if (stats.getAverageSalary() > 1400) {
            insight = LanguageManager.get("dashboard.insight.high");
        } else {
            insight = LanguageManager.get("dashboard.insight.low");
        }

        aiInsightLabel.setText(insight);
    }
}