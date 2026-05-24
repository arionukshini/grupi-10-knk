package com.company.system.controller;

public class SalariesController {
    private final ObservableList<Salary> salaries =
            FXCollections.observableArrayList();

    private final ObservableList<Salary> salaryHistory =
            FXCollections.observableArrayList();

    private Salary calculatedSalary;

}
