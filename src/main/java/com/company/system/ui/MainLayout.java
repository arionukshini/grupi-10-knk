package com.company.system.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

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

        Label menuPlaceholder = new Label("Menu Bar (placeholder)");
        Label toolbarPlaceholder = new Label("Tool Bar (placeholder)");

        topContainer.getChildren().addAll(menuPlaceholder, toolbarPlaceholder);
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