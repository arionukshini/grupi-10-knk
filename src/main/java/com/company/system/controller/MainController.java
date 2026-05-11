package com.company.system.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class MainController {

    @FXML
    private StackPane contentArea;

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        setStatus("Application loaded");
    }

    // EXIT APP
    @FXML
    private void handleExit() {
        System.exit(0);
    }

    // CORE NAVIGATION METHOD
    public void setContent(Node node) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(node);
    }

    // STATUS BAR
    public void setStatus(String message) {
        statusLabel.setText(message);
    }

    // MENU ACTIONS (UI ROUTING)

    @FXML
    public void showEmployees() {
        setStatus("Employees view opened");

        // TODO: later replace with real EmployeeView
        Label view = new Label("Employees Module");
        setContent(view);
    }

    @FXML
    public void showContracts() {
        setStatus("Contracts view opened");

        Label view = new Label("Contracts Module");
        setContent(view);
    }

    @FXML
    public void showSalaries() {
        setStatus("Salaries view opened");

        Label view = new Label("Salaries Module");
        setContent(view);
    }

    @FXML
    public void showDashboard() {
        setStatus("Dashboard opened");

        Label view = new Label("Dashboard Module");
        setContent(view);
    }

    @FXML
    public void showHelp() {
        setStatus("Help opened");

        Label view = new Label("Help Module");
        setContent(view);
    }
}