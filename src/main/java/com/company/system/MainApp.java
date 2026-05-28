package com.company.system;

import com.company.system.db.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {

        primaryStage = stage;

        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(650);

        configureStageBounds();

        primaryStage.setTitle("Contract & Payroll System");

        DBConnection.initializeDatabase();
        showWelcome();
        primaryStage.setMaximized(true);
    }

    // WELCOME
    public static void showWelcome() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/views/welcome-view.fxml")
            );

            Scene scene = new Scene(loader.load(),
                    boundedWidth(primaryStage.getWidth() > 0 ? primaryStage.getWidth() : 900),
                    boundedHeight(primaryStage.getHeight() > 0 ? primaryStage.getHeight() : 650)
            );

            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // LOGIN
    public static void showLogin() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/views/login-view.fxml")
            );

            Scene scene = new Scene(loader.load(),
                    boundedWidth(primaryStage.getWidth() > 0 ? primaryStage.getWidth() : 900),
                    boundedHeight(primaryStage.getHeight() > 0 ? primaryStage.getHeight() : 650)
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
                    boundedWidth(primaryStage.getWidth()),
                    boundedHeight(primaryStage.getHeight())
            );

            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void configureStageBounds() {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setMaxWidth(bounds.getWidth());
        primaryStage.setMaxHeight(bounds.getHeight());
    }

    private static double boundedWidth(double requestedWidth) {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        return Math.min(Math.max(requestedWidth, primaryStage.getMinWidth()), bounds.getWidth());
    }

    private static double boundedHeight(double requestedHeight) {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        return Math.min(Math.max(requestedHeight, primaryStage.getMinHeight()), bounds.getHeight());
    }
}
