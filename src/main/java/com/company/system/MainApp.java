package com.company.system;

import com.company.system.ui.MainLayout;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        MainLayout root = new MainLayout();

        Scene scene = new Scene(root, 900, 600);

        stage.setTitle("Contract & Payroll System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}