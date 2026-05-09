package com.company.system.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;


public class MainLayout extends BorderPane {

    private StackPane contentArea;

    private Label statusLabel;

    public MainLayout() {
        initializeLayout();
    }

    private void initializeLayout() {

        // ===================== TOP =====================
        VBox topContainer = new VBox();
        topContainer.setSpacing(5);
        topContainer.setPadding(new Insets(5));

        MenuBar menuBar = new MenuBar();

    // ===== FILE MENU =====
        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> System.exit(0));

        fileMenu.getItems().add(exitItem);

    // ===== MANAGE MENU =====
        Menu manageMenu = new Menu("Manage");
        MenuItem employeesItem = new MenuItem("Employees");
        MenuItem contractsItem = new MenuItem("Contracts");
        MenuItem salariesItem = new MenuItem("Salaries");
        manageMenu.getItems().addAll(employeesItem, contractsItem, salariesItem);

    // ===== VIEW MENU =====
        Menu viewMenu = new Menu("View");
        MenuItem dashboardItem = new MenuItem("Dashboard");
        viewMenu.getItems().add(dashboardItem);

    // ===== HELP MENU =====
        Menu helpMenu = new Menu("Help");
        MenuItem helpItem = new MenuItem("Help");
        helpMenu.getItems().add(helpItem);

        menuBar.getMenus().addAll(fileMenu, manageMenu, viewMenu, helpMenu);

        Label toolbarPlaceholder = new Label("Tool Bar (placeholder)");

        topContainer.getChildren().addAll(menuBar, toolbarPlaceholder);
        setTop(topContainer);

        // ===================== CENTER =====================
        contentArea = new StackPane();
        contentArea.setPadding(new Insets(10));

        Label contentPlaceholder = new Label("Content Area");
        contentArea.getChildren().add(contentPlaceholder);

        setCenter(contentArea);

        // ===================== BOTTOM =====================
        HBox statusBar = new HBox();
        statusBar.setPadding(new Insets(5));

        statusLabel = new Label("Ready");
        statusBar.getChildren().add(statusLabel);

        setBottom(statusBar);
    }



    public void setContent(javafx.scene.Node node) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(node);
    }

    public void setStatus(String message) {
        statusLabel.setText(message);
    }
}