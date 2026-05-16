package com.company.system.controller;

import com.company.system.model.DashboardStats;
import com.company.system.model.DepartmentStats;
import com.company.system.service.DashboardService;

import javafx.collections.FXCollections;

import javafx.fxml.FXML;

import javafx.scene.chart.BarChart;
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
    private BarChart<String, Number> employeesChart;

    @FXML
    public void initialize() {

        loadStatistics();

        loadDepartmentChart();

        generateAIInsight();
    }

    private void loadStatistics() {

        DashboardStats stats =
                DashboardService.getDashboardStats();

        totalEmployeesLabel.setText(
                String.valueOf(stats.getTotalEmployees())
        );

        totalDepartmentsLabel.setText(
                String.valueOf(stats.getTotalDepartments())
        );

        activeContractsLabel.setText(
                String.valueOf(stats.getActiveContracts())
        );

        averageSalaryLabel.setText(
                String.format("%.2f €",
                        stats.getAverageSalary())
        );
    }

    private void loadDepartmentChart() {

        XYChart.Series<String, Number> series =
                new XYChart.Series<>();

        series.setName("Employees");

        for (DepartmentStats stats :
                DashboardService.getEmployeesPerDepartment()) {

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
    }

    private void generateAIInsight() {

        DashboardStats stats =
                DashboardService.getDashboardStats();

        String insight;

        if (stats.getAverageSalary() > 1400) {

            insight =
                    "AI Insight: Employee salaries are above average.";

        } else {

            insight =
                    "AI Insight: Salary average could be improved.";
        }

        aiInsightLabel.setText(insight);
    }
}