package com.company.system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {

        primaryStage = stage;

        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);

        primaryStage.setTitle("Contract & Payroll System");

        showLogin();
    }

    // LOGIN
    public static void showLogin() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/views/login-view.fxml")
            );

            Scene scene = new Scene(loader.load(),
                    primaryStage.getWidth() > 0 ? primaryStage.getWidth() : 800,
                    primaryStage.getHeight() > 0 ? primaryStage.getHeight() : 500
            );

            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // MAIN APP
    public static void openMainApp() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/views/main-view.fxml")
            );

            Scene scene = new Scene(
                    loader.load(),
                    primaryStage.getWidth(),
                    primaryStage.getHeight()
            );

            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}