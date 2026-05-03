package com.company.system.ui;

import javafx.scene.layout.BorderPane;

public class MainLayout extends BorderPane {

    public MainLayout() {
        setTop(new javafx.scene.control.Label("Menu + Toolbar"));
        setCenter(new javafx.scene.control.Label("Content Area"));
        setBottom(new javafx.scene.control.Label("Status Bar"));
    }
}