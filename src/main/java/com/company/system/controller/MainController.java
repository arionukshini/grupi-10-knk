package com.company.system.controller;

import com.company.system.i18n.LanguageManager;
import com.company.system.model.User;
import com.company.system.utils.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import static com.company.system.MainApp.showLogin;

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
    private Menu accountMenu;

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
    private Button employeesButton;

    @FXML
    private Button contractsButton;

    @FXML
    private Button salariesButton;

    @FXML
    private Button dashboardButton;

    @FXML
    private MenuItem accountMenuItem;

    @FXML
    private StackPane contentArea;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        updateTexts();
        setupKeyboardShortcuts();
    }

    private void setupKeyboardShortcuts() {
        employeesMenuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+E"));
        contractsMenuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+K"));
        salariesMenuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+S"));
        dashboardMenuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+D"));
        helpMenuItem.setAccelerator(KeyCombination.keyCombination("F1"));
    }

    public void updateTexts() {
        fileMenu.setText(LanguageManager.get("menu.file"));
        manageMenu.setText(LanguageManager.get("menu.manage"));
        viewMenu.setText(LanguageManager.get("menu.view"));
        helpMenu.setText(LanguageManager.get("menu.help"));
        accountMenu.setText(LanguageManager.get("menu.account"));

        exitMenuItem.setText(LanguageManager.get("menu.exit"));
        employeesMenuItem.setText(LanguageManager.get("menu.employees"));
        contractsMenuItem.setText(LanguageManager.get("menu.contracts"));
        salariesMenuItem.setText(LanguageManager.get("menu.salaries"));
        dashboardMenuItem.setText(LanguageManager.get("menu.dashboard"));
        helpMenuItem.setText(LanguageManager.get("menu.help"));
        accountMenuItem.setText(LanguageManager.get("menu.account"));

        employeesButton.setText(LanguageManager.get("menu.employees"));
        contractsButton.setText(LanguageManager.get("menu.contracts"));
        salariesButton.setText(LanguageManager.get("menu.salaries"));
        dashboardButton.setText(LanguageManager.get("menu.dashboard"));

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

        setStatus(
                LanguageManager.get("status.dashboard")
        );

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/dashboard-view.fxml")
            );

            Parent dashboardView = loader.load();

            setContent(dashboardView);

        } catch (Exception e) {

            e.printStackTrace();

            setStatus("Failed to load dashboard.");
        }
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

    @FXML
    public void showAccount() {
        setStatus(LanguageManager.get("status.account"));

        User user = com.company.system.utils.Session.getUser();

        if (user == null) {
            setContent(new Label("No user logged in"));
            return;
        }

        String username = user.getUsername();
        String password = user.getPassword();

        Label icon = new Label("👤");
        icon.setStyle("-fx-font-size: 60px;");

        Label usernameLabel = new Label(username);
        usernameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        HBox header = new HBox(15, icon, usernameLabel);
        header.setStyle("-fx-alignment: center-left;");

        Label passwordTitle = new Label(LanguageManager.get("login.password"));
        passwordTitle.setStyle("-fx-font-weight: bold;");

        Label passwordLabel = new Label("******");

        Button togglePassword = new Button(LanguageManager.get("account.showpassword"));

        final boolean[] visible = {false};

        togglePassword.setOnAction(e -> {
            visible[0] = !visible[0];
            passwordLabel.setText(visible[0] ? password : "******");
        });

        VBox passwordBox = new VBox(5,
                passwordTitle,
                passwordLabel,
                togglePassword
        );

        Label langTitle = new Label(LanguageManager.get("account.language"));
        langTitle.setStyle("-fx-font-weight: bold;");

        ComboBox<String> languageBox = new ComboBox<>();
        languageBox.getItems().addAll("English", "Shqip");

        String currentLang = LanguageManager.getCurrentLocale().getLanguage();
        languageBox.setValue(currentLang.equals("sq") ? "Shqip" : "English");

        Label langMsg = new Label();

        languageBox.setOnAction(e -> {
            if ("Shqip".equals(languageBox.getValue())) {
                LanguageManager.setLanguage("sq");
            } else {
                LanguageManager.setLanguage("en");
            }

            updateTexts();
            langMsg.setText("✔ " + LanguageManager.get("account.language.success"));
        });

        HBox languageMiniContainer = new HBox(15,
                languageBox,
                langMsg
        );

        VBox languageBoxContainer = new VBox(5,
                langTitle,
                languageMiniContainer
        );

        Button logout = new Button(LanguageManager.get("account.logout"));

        logout.setOnAction(e -> {
            Session.clear();
            showLogin();
        });

        VBox rightSide = new VBox(20,
                header,
                passwordBox,
                languageBoxContainer,
                logout
        );

        rightSide.setStyle("""
                    -fx-padding: 25;
                    -fx-alignment: center-left;
                """);

        setContent(rightSide);
    }
}
