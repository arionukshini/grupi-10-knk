package com.company.system.controller;

import com.company.system.i18n.LanguageManager;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainController {

    private String currentView = "welcome";

    @FXML
    private Menu fileMenu;

    @FXML
    private Menu manageMenu;

    @FXML
    private Menu viewMenu;

    @FXML
    private Menu helpMenu;

    @FXML
    private MenuItem exitMenuItem;

    @FXML
    private MenuItem employeesMenuItem;

    @FXML
    private MenuItem contractsMenuItem;

    @FXML
    private MenuItem salariesMenuItem;

    @FXML
    private MenuItem dashboardMenuItem;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private StackPane contentArea;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        updateTexts();
    }

    private void updateTexts() {
        fileMenu.setText(LanguageManager.get("menu.file"));
        manageMenu.setText(LanguageManager.get("menu.manage"));
        viewMenu.setText(LanguageManager.get("menu.view"));
        helpMenu.setText(LanguageManager.get("menu.help"));

        exitMenuItem.setText(LanguageManager.get("menu.exit"));
        employeesMenuItem.setText(LanguageManager.get("menu.employees"));
        contractsMenuItem.setText(LanguageManager.get("menu.contracts"));
        salariesMenuItem.setText(LanguageManager.get("menu.salaries"));
        dashboardMenuItem.setText(LanguageManager.get("menu.dashboard"));
        helpMenuItem.setText(LanguageManager.get("menu.help"));

        welcomeLabel.setText(LanguageManager.get("app.welcome"));

        if ("welcome".equals(currentView)) {
            setStatus(LanguageManager.get("status.ready"));
        }
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    public void setContent(Node node) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(node);
    }

    public void setStatus(String message) {
        statusLabel.setText(message);
    }

    @FXML
    public void showEmployees() {
        currentView = "employees";
        setStatus(LanguageManager.get("status.employees"));

        Label view = new Label(LanguageManager.get("module.employees"));
        setContent(view);
    }

    @FXML
    public void showContracts() {
        currentView = "contracts";
        setStatus(LanguageManager.get("status.contracts"));

        Label view = new Label(LanguageManager.get("module.contracts"));
        setContent(view);
    }

    @FXML
    public void showSalaries() {
        currentView = "salaries";
        setStatus(LanguageManager.get("status.salaries"));

        Label view = new Label(LanguageManager.get("module.salaries"));
        setContent(view);
    }

    @FXML
    public void showDashboard() {
        currentView = "dashboard";
        setStatus(LanguageManager.get("status.dashboard"));

        Label view = new Label(LanguageManager.get("module.dashboard"));
        setContent(view);
    }

    @FXML
    public void showHelp() {
        currentView = "help";
        setStatus(LanguageManager.get("status.help"));

        Label title = new Label(LanguageManager.get("help.title"));
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextArea content = new TextArea(LanguageManager.get("help.content"));
        content.setWrapText(true);
        content.setEditable(false);
        content.setPrefRowCount(8);

        VBox helpView = new VBox(10, title, content);
        helpView.setStyle("-fx-padding: 20;");

        setContent(helpView);
    }
}
