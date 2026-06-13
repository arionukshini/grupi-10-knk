package com.company.system;


import com.company.system.utils.AppLogger;
import com.company.system.db.DBConnection;
import com.company.system.model.User;
import com.company.system.utils.KeyboardNavigation;
import com.company.system.utils.Session;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Stage primaryStage;

    private static final int[] ICON_SIZES = {16, 32, 48, 64, 128, 256};

    @Override
    public void start(Stage stage) {

        primaryStage = stage;

        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(650);

        configureStageBounds();
        setAppIcons();

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
                    currentSceneWidth(),
                    currentSceneHeight()
            );

            KeyboardNavigation.install(scene);
            KeyboardNavigation.focusFirst(scene.getRoot());
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            AppLogger.error("Unexpected error", e);
        }
    }

    // LOGIN
    public static void showLogin() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/views/login-view.fxml")
            );

            Scene scene = new Scene(loader.load(),
                    currentSceneWidth(),
                    currentSceneHeight()
            );

            KeyboardNavigation.install(scene);
            KeyboardNavigation.focusFirst(scene.getRoot());
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            AppLogger.error("Unexpected error", e);
        }
    }

    // MAIN APP
    public static void openMainApp() {
        try {
            User user = Session.getUser();

            String viewPath = (user != null && "ADMIN".equalsIgnoreCase(user.getRole()))
                    ? "/views/main-view.fxml"
                    : "/views/user-main-view.fxml";

            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource(viewPath)
            );

            Scene scene = new Scene(
                    loader.load(),
                    currentSceneWidth(),
                    currentSceneHeight()
            );

            KeyboardNavigation.install(scene);
            KeyboardNavigation.focusFirst(scene.getRoot());
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            AppLogger.error("Unexpected error", e);
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

    private static double currentSceneWidth() {
        Scene scene = primaryStage.getScene();
        return boundedWidth(scene == null || scene.getWidth() <= 0 ? 900 : scene.getWidth());
    }

    private static double currentSceneHeight() {
        Scene scene = primaryStage.getScene();
        return boundedHeight(scene == null || scene.getHeight() <= 0 ? 650 : scene.getHeight());
    }

    private static void setAppIcons() {
        for (int size : ICON_SIZES) {
            try {
                Image icon = new Image(
                        MainApp.class.getResourceAsStream("/icons/icon-" + size + ".png")
                );
                if (!icon.isError()) {
                    primaryStage.getIcons().add(icon);
                }
            } catch (Exception ignored) {}
        }
    }
}
