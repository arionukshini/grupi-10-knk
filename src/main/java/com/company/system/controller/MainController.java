package com.company.system.controller;

import com.company.system.i18n.LanguageManager;
import com.company.system.model.User;
import com.company.system.utils.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
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
    private Menu languageMenu;

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
    private MenuItem albanianMenuItem;

    @FXML
    private MenuItem englishMenuItem;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private MenuItem accountMenuItem;

    @FXML
    private Button employeesButton;

    @FXML
    private Button contractsButton;

    @FXML
    private Button salariesButton;

    @FXML
    private Button dashboardButton;

    @FXML
    private StackPane contentArea;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label statusLabel;


    @FXML
    public void initialize() {

        updateTexts();
        setStatus(LanguageManager.get("status.ready"));

        initializeContextMenu();
        setupKeyboardShortcuts();

        employeesButton.requestFocus();
    }

    private void initializeContextMenu() {

        ContextMenu contextMenu = new ContextMenu();

        MenuItem refreshItem = new MenuItem("Refresh");
        MenuItem helpItem = new MenuItem("Help");
        MenuItem exitItem = new MenuItem("Exit");

        refreshItem.setOnAction(e ->
                setStatus("Content refreshed")
        );

        helpItem.setOnAction(e ->
                showHelp()
        );

        exitItem.setOnAction(e ->
                System.exit(0)
        );

        contextMenu.getItems().addAll(
                refreshItem,
                helpItem,
                exitItem
        );

        contentArea.setOnContextMenuRequested(event ->
                contextMenu.show(
                        contentArea,
                        event.getScreenX(),
                        event.getScreenY()
                )
        );
    }

    private void setupKeyboardShortcuts() {
        employeesMenuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+E"));
        contractsMenuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+K"));
        salariesMenuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+S"));
        dashboardMenuItem.setAccelerator(KeyCombination.keyCombination("Shortcut+D"));
        helpMenuItem.setAccelerator(KeyCombination.keyCombination("F1"));
    }

    public void updateTexts() {
        fileMenu.setText("☰");
        manageMenu.setText("");
        viewMenu.setText("");
        languageMenu.setText("");
        helpMenu.setText("");
        accountMenu.setText("");

        exitMenuItem.setText(LanguageManager.get("menu.exit"));
        employeesMenuItem.setText(LanguageManager.get("menu.employees"));
        contractsMenuItem.setText(LanguageManager.get("menu.contracts"));
        salariesMenuItem.setText(LanguageManager.get("menu.salaries"));
        dashboardMenuItem.setText(LanguageManager.get("menu.dashboard"));
        albanianMenuItem.setText(LanguageManager.get("language.albanian"));
        englishMenuItem.setText(LanguageManager.get("language.english"));
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
    private void switchToAlbanian() {
        LanguageManager.setLanguage("sq");
        updateTexts();
        refreshCurrentView();
    }

    @FXML
    private void switchToEnglish() {
        LanguageManager.setLanguage("en");
        updateTexts();
        refreshCurrentView();
    }

    private void refreshCurrentView() {
        switch (currentView) {
            case "employees":
                showEmployees();
                break;
            case "contracts":
                showContracts();
                break;
            case "salaries":
                showSalaries();
                break;
            case "dashboard":
                showDashboard();
                break;
            case "help":
                showHelp();
                break;
            case "account":
                showAccount();
                break;
            default:
                setContent(welcomeLabel);
                setStatus(LanguageManager.get("status.ready"));
                break;
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

        boolean sq = isAlbanian();

        Label title = new Label(LanguageManager.get("help.title"));
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #102a43;");

        Label intro = new Label(sq
                ? "Kjo faqe shpjegon menyren e perdorimit te sistemit, navigimin dhe shkurtesat kryesore."
                : "This page explains how to use the system, navigate through modules and use keyboard shortcuts.");
        intro.setWrapText(true);
        intro.setStyle("-fx-font-size: 14px; -fx-text-fill: #52606d;");

        VBox sections = new VBox(14);
        sections.getChildren().addAll(
                createHelpSection(
                        sq ? "Navigimi kryesor" : "Main navigation",
                        sq ? "Perdorni butonat ne toolbar per te hapur modulet kryesore." : "Use the toolbar buttons to open the main modules.",
                        sq ? "Menuja ☰ permban daljen, modulet, gjuhen, ndihmen dhe llogarine." : "The ☰ menu contains exit, modules, language, help and account options.",
                        sq ? "Status bar poshte tregon pamjen aktuale te hapur." : "The bottom status bar shows the currently opened view."
                ),
                createHelpSection(
                        sq ? "Shkurtesat nga tastiera" : "Keyboard shortcuts",
                        "Ctrl+E - " + LanguageManager.get("menu.employees"),
                        "Ctrl+K - " + LanguageManager.get("menu.contracts"),
                        "Ctrl+S - " + LanguageManager.get("menu.salaries"),
                        "Ctrl+D - " + LanguageManager.get("menu.dashboard"),
                        "F1 - " + LanguageManager.get("menu.help")
                ),
                createHelpSection(
                        sq ? "Gjuha" : "Language",
                        sq ? "Gjuha mund te ndryshohet nga menuja ☰ ose nga faqja e llogarise." : "The language can be changed from the ☰ menu or from the account page.",
                        sq ? "Pas ndryshimit te gjuhes, tekstet kryesore perditesohen automatikisht." : "After changing the language, the main texts are updated automatically."
                ),
                createHelpSection(
                        sq ? "Llogaria" : "Account",
                        sq ? "Nga llogaria mund te shihni perdoruesin aktual dhe te ndryshoni gjuhen." : "From the account page you can view the current user and change the language.",
                        sq ? "Butoni per dalje e mbyll sesionin dhe ju kthen te faqja e kyçjes." : "The logout button clears the session and returns you to the login page."
                )
        );

        VBox helpView = new VBox(18, title, intro, sections);
        helpView.setStyle("-fx-padding: 26; -fx-background-color: #f8fafc;");

        ScrollPane scrollPane = new ScrollPane(helpView);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        setContent(scrollPane);
    }

    private VBox createHelpSection(String sectionTitle, String... lines) {
        Label title = new Label(sectionTitle);
        title.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #19316c;");

        VBox content = new VBox(6, title);

        for (String line : lines) {
            Label item = new Label("• " + line);
            item.setWrapText(true);
            item.setStyle("-fx-font-size: 13px; -fx-text-fill: #334e68;");
            content.getChildren().add(item);
        }

        content.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 8;
                -fx-border-color: #d9e2ec;
                -fx-border-radius: 8;
                -fx-padding: 14;
                """);

        return content;
    }

    private boolean isAlbanian() {
        return "sq".equals(LanguageManager.getCurrentLocale().getLanguage());
    }

    @FXML
    public void showAccount() {
        currentView = "account";
        setStatus(LanguageManager.get("status.account"));

        User user = Session.getUser();

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
