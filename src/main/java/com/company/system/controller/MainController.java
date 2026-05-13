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

    @FXML private StackPane contentArea;
    @FXML private Label statusLabel;

    @FXML private Menu fileMenu;
    @FXML private Menu manageMenu;
    @FXML private Menu viewMenu;
    @FXML private Menu languageMenu;
    @FXML private Menu helpMenu;

    @FXML private MenuItem exitMenuItem;
    @FXML private MenuItem employeesMenuItem;
    @FXML private MenuItem contractsMenuItem;
    @FXML private MenuItem salariesMenuItem;
    @FXML private MenuItem dashboardMenuItem;
    @FXML private MenuItem albanianMenuItem;
    @FXML private MenuItem englishMenuItem;
    @FXML private MenuItem helpMenuItem;

    @FXML
    public void initialize() {
        updateTexts();
        setStatus(LanguageManager.get("status.ready"));
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

    private void updateTexts() {
        fileMenu.setText(LanguageManager.get("menu.file"));
        manageMenu.setText(LanguageManager.get("menu.manage"));
        viewMenu.setText(LanguageManager.get("menu.view"));
        languageMenu.setText(LanguageManager.get("menu.language"));
        helpMenu.setText(LanguageManager.get("menu.help"));

        exitMenuItem.setText(LanguageManager.get("menu.exit"));
        employeesMenuItem.setText(LanguageManager.get("menu.employees"));
        contractsMenuItem.setText(LanguageManager.get("menu.contracts"));
        salariesMenuItem.setText(LanguageManager.get("menu.salaries"));
        dashboardMenuItem.setText(LanguageManager.get("menu.dashboard"));
        albanianMenuItem.setText(LanguageManager.get("language.albanian"));
        englishMenuItem.setText(LanguageManager.get("language.english"));
        helpMenuItem.setText(LanguageManager.get("menu.help"));
    }

    @FXML
    public void showEmployees() {
        setStatus(LanguageManager.get("menu.employees"));

        Label view = new Label(LanguageManager.get("menu.employees"));
        setContent(view);
    }

    @FXML
    public void showContracts() {
        setStatus(LanguageManager.get("menu.contracts"));

        Label view = new Label(LanguageManager.get("menu.contracts"));
        setContent(view);
    }

    @FXML
    public void showSalaries() {
        setStatus(LanguageManager.get("menu.salaries"));

        Label view = new Label(LanguageManager.get("menu.salaries"));
        setContent(view);
    }

    @FXML
    public void showDashboard() {
        setStatus(LanguageManager.get("menu.dashboard"));

        Label view = new Label(LanguageManager.get("menu.dashboard"));
        setContent(view);
    }

    @FXML
    public void showHelp() {
        setStatus(LanguageManager.get("help.title"));

        Label title = new Label(LanguageManager.get("help.title"));
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextArea content = new TextArea(LanguageManager.get("help.content"));
        content.setWrapText(true);
        content.setEditable(false);
        content.setPrefHeight(250);

        VBox helpView = new VBox(10, title, content);
        helpView.setStyle("-fx-padding: 20;");

        setContent(helpView);
    }

    @FXML
    public void setAlbanian() {
        LanguageManager.setLanguage("sq");
        updateTexts();
        setStatus(LanguageManager.get("status.ready"));
    }

    @FXML
    public void setEnglish() {
        LanguageManager.setLanguage("en");
        updateTexts();
        setStatus(LanguageManager.get("status.ready"));
    }
}