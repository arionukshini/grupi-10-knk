package com.company.system.controller;

import com.company.system.i18n.LanguageManager;
import com.company.system.model.User;
import com.company.system.service.UserService;
import com.company.system.utils.DialogUtils;
import com.company.system.utils.Session;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.company.system.MainApp.showWelcome;

public class MainController {

    private static final double EXPANDED_SIDEBAR_WIDTH = 220;
    private static final double COLLAPSED_SIDEBAR_WIDTH = 72;

    private final List<String> themeClasses = List.of("light", "dark");

    private String currentView = "welcome";
    private boolean sidebarExpanded = true;
    private boolean darkMode = false;
    private boolean profileLanguageChanged = false;

    @FXML
    private BorderPane mainShell;

    @FXML
    private VBox sidebar;

    @FXML
    private HBox expandedFooter;

    @FXML
    private Button menuToggleButton;

    @FXML
    private Button dashboardButton;

    @FXML
    private Button employeesButton;

    @FXML
    private Button contractsButton;

    @FXML
    private Button salariesButton;

    @FXML
    private Button departmentsButton;

    @FXML
    private Button profileButton;

    @FXML
    private Button languageButton;

    @FXML
    private Button themeButton;

    @FXML
    private Button settingsButton;

    @FXML
    private StackPane contentArea;

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label loggedInLabel;

    @FXML
    public void initialize() {
        mainShell.getStyleClass().add("light");
        updateTexts();
        updateLoggedInUser();
        updateThemeButton();
        updateSidebarLabels();
        setStatus(LanguageManager.get("status.ready"));
        setupKeyboardShortcuts();
        applyRolePermissions();
        showDashboard();
    }

    @FXML
    private void toggleSidebar() {
        sidebarExpanded = !sidebarExpanded;
        updateSidebarState();
    }

    @FXML
    private void toggleTheme() {
        darkMode = !darkMode;
        mainShell.getStyleClass().removeAll(themeClasses);
        mainShell.getStyleClass().add(darkMode ? "dark" : "light");
        updateThemeButton();
    }

    @FXML
    private void toggleLanguage() {
        if (isAlbanian()) {
            switchToEnglish();
        } else {
            switchToAlbanian();
        }
    }

    private void setupKeyboardShortcuts() {
        Platform.runLater(() -> contentArea.getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.isShortcutDown() && event.getCode() == KeyCode.D) {
                showDashboard();
                event.consume();
            } else if (event.isShortcutDown() && event.getCode() == KeyCode.E) {
                showEmployees();
                event.consume();
            } else if (event.isShortcutDown() && event.getCode() == KeyCode.K) {
                showContracts();
                event.consume();
            } else if (event.isShortcutDown() && event.getCode() == KeyCode.S) {
                showSalaries();
                event.consume();
            } else if (event.isShortcutDown() && event.getCode() == KeyCode.P) {
                showProfile();
                event.consume();
            } else if (event.getCode() == KeyCode.F1 || (event.isShortcutDown() && event.getCode() == KeyCode.H)) {
                showHelp();
                event.consume();
            } else if (KeyCombination.keyCombination("Shortcut+L").match(event)) {
                toggleLanguage();
                event.consume();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                handleExit();
                event.consume();
            }
        }));
    }

    private void applyRolePermissions() {
        User user = Session.getUser();

        if (user == null) {
            return;
        }

        boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole());
        contractsButton.setVisible(isAdmin);
        contractsButton.setManaged(isAdmin);
        salariesButton.setVisible(isAdmin);
        salariesButton.setManaged(isAdmin);
    }

    private void updateTexts() {
        dashboardButton.setUserData(new NavItem("D", LanguageManager.get("menu.dashboard")));
        employeesButton.setUserData(new NavItem("P", LanguageManager.get("menu.employees")));
        contractsButton.setUserData(new NavItem("K", LanguageManager.get("menu.contracts")));
        salariesButton.setUserData(new NavItem("$", LanguageManager.get("menu.salaries")));
        departmentsButton.setUserData(new NavItem("A", LanguageManager.get("menu.departments")));
        profileButton.setUserData(new NavItem("U", LanguageManager.get("menu.profile")));

        welcomeLabel.setText(LanguageManager.get("app.welcome"));
        languageButton.setText(isAlbanian() ? "English" : "Shqip");
        updateSidebarLabels();
        updateLoggedInUser();

        if ("welcome".equals(currentView)) {
            setStatus(LanguageManager.get("status.ready"));
        }
    }

    private void updateLoggedInUser() {
        User user = Session.getUser();
        String username = user == null ? "-" : user.getUsername();
        loggedInLabel.setText("Logged in as: " + username);
    }

    private void updateThemeButton() {
        themeButton.setText(darkMode ? "☀" : "☾");
    }

    private void updateSidebarState() {
        sidebar.setPrefWidth(sidebarExpanded ? EXPANDED_SIDEBAR_WIDTH : COLLAPSED_SIDEBAR_WIDTH);
        sidebar.setMinWidth(sidebarExpanded ? EXPANDED_SIDEBAR_WIDTH : COLLAPSED_SIDEBAR_WIDTH);
        sidebar.getStyleClass().remove("collapsed");

        if (!sidebarExpanded) {
            sidebar.getStyleClass().add("collapsed");
        }

        expandedFooter.setVisible(sidebarExpanded);
        expandedFooter.setManaged(sidebarExpanded);
        settingsButton.setVisible(!sidebarExpanded);
        settingsButton.setManaged(!sidebarExpanded);
        updateSidebarLabels();
    }

    private void updateSidebarLabels() {
        setNavButtonText(dashboardButton);
        setNavButtonText(employeesButton);
        setNavButtonText(contractsButton);
        setNavButtonText(salariesButton);
        setNavButtonText(departmentsButton);
        setNavButtonText(profileButton);
    }

    private void setNavButtonText(Button button) {
        if (!(button.getUserData() instanceof NavItem item)) {
            return;
        }

        button.setText(sidebarExpanded ? item.icon() + "  " + item.label() : item.icon());
    }

    private void setActiveButton(Button activeButton) {
        List<Button> buttons = List.of(
                dashboardButton,
                employeesButton,
                contractsButton,
                salariesButton,
                departmentsButton,
                profileButton
        );

        for (Button button : buttons) {
            button.getStyleClass().remove("active");
        }

        if (!activeButton.getStyleClass().contains("active")) {
            activeButton.getStyleClass().add("active");
        }

        activeButton.requestFocus();
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
            case "employees" -> showEmployees();
            case "contracts" -> showContracts();
            case "salaries" -> showSalaries();
            case "dashboard" -> showDashboard();
            case "departments" -> showDepartments();
            case "help" -> showHelp();
            case "profile" -> showProfile();
            default -> {
                setContent(welcomeLabel);
                setStatus(LanguageManager.get("status.ready"));
            }
        }
    }

    @FXML
    private void handleExit() {

        Alert alert = new Alert(Alert.AlertType.NONE);
        DialogUtils.style(alert);

        alert.setTitle(isAlbanian() ? "Dalje" : "Exit");
        alert.setHeaderText(null);

        ButtonType mainMenuType = new ButtonType(
                isAlbanian() ? "Menyja kryesore" : "Main Menu",
                ButtonBar.ButtonData.OTHER
        );

        ButtonType desktopType = new ButtonType(
                isAlbanian() ? "Dil nga programi" : "Quit to Desktop",
                ButtonBar.ButtonData.OTHER
        );

        ButtonType cancelType = new ButtonType(
                isAlbanian() ? "Anulo" : "Cancel",
                ButtonBar.ButtonData.CANCEL_CLOSE
        );

        alert.getButtonTypes().setAll(mainMenuType, desktopType, cancelType);

        Label icon = new Label("🚪");
        icon.setStyle("""
            -fx-font-size: 64px;
            -fx-padding: 10;
            """);

        Label title = new Label(
                isAlbanian()
                        ? "A jeni i sigurt qe doni te dilni?"
                        : "Are you sure you want to exit?"
        );

        title.setWrapText(true);
        title.setStyle("""
            -fx-font-size: 18px;
            -fx-font-weight: bold;
            """);

        Label subtitle = new Label(
                isAlbanian()
                        ? "Zgjidhni nje opsion per dalje."
                        : "Choose an exit option."
        );

        subtitle.setStyle("""
            -fx-font-size: 13px;
            -fx-opacity: 0.8;
            """);

        VBox content = new VBox(12, icon, title, subtitle);
        content.setAlignment(Pos.CENTER);

        alert.getDialogPane().setPrefWidth(460);
        alert.getDialogPane().setPrefHeight(365);

        alert.getDialogPane().setContent(content);

        Platform.runLater(() -> {

            Button mainMenuBtn = (Button) alert.getDialogPane().lookupButton(mainMenuType);
            Button desktopBtn = (Button) alert.getDialogPane().lookupButton(desktopType);
            Button cancelBtn = (Button) alert.getDialogPane().lookupButton(cancelType);

            mainMenuBtn.setMaxWidth(Double.MAX_VALUE);
            desktopBtn.setMaxWidth(Double.MAX_VALUE);
            cancelBtn.setMaxWidth(Double.MAX_VALUE);

            mainMenuBtn.setPrefHeight(42);
            desktopBtn.setPrefHeight(42);
            cancelBtn.setPrefHeight(42);

            mainMenuBtn.setStyle("""
                -fx-background-color: #3b82f6;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-background-radius: 10;
                -fx-cursor: hand;
                """);

            desktopBtn.setStyle("""
                -fx-background-color: #dc2626;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-background-radius: 10;
                -fx-cursor: hand;
                """);

            cancelBtn.setStyle("""
                -fx-background-radius: 10;
                -fx-cursor: hand;
                """);

            VBox buttonBox = new VBox(10, mainMenuBtn, desktopBtn, cancelBtn);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setFillWidth(true);

            alert.getDialogPane().setContent(new VBox(18, content, buttonBox));
        });

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {
            if (result.get() == mainMenuType) {
                showWelcome();
            } else if (result.get() == desktopType) {
                System.exit(0);
            }
        }
    }

    @FXML
    private void handleLogout() {
        Session.clear();
        showWelcome();
    }

    public void setContent(Node node) {
        contentArea.getChildren().setAll(node);
    }

    public void setStatus(String message) {
        statusLabel.setText(message);
    }

    @FXML
    public void showDashboard() {
        currentView = "dashboard";
        setStatus(LanguageManager.get("status.dashboard"));
        loadView("/views/dashboard-view.fxml", dashboardButton, "Failed to load dashboard.");
    }

    @FXML
    public void showEmployees() {
        currentView = "employees";
        setStatus(LanguageManager.get("status.employees"));
        loadView("/views/employees-view.fxml", employeesButton, "Failed to load employees.");
    }

    @FXML
    public void showContracts() {
        currentView = "contracts";
        setStatus(LanguageManager.get("status.contracts"));
        loadView("/views/contracts-view.fxml", contractsButton, "Failed to load contracts.");
    }

    @FXML
    public void showSalaries() {
        currentView = "salaries";
        setStatus(LanguageManager.get("status.salaries"));
        loadView("/views/salaries-view.fxml", salariesButton, "Failed to load salaries.");
    }

    @FXML
    public void showDepartments() {
        currentView = "departments";
        setStatus(LanguageManager.get("status.departments"));
        loadView("/views/departments-view.fxml", departmentsButton, "Failed to load departments.");
    }

    @FXML
    public void showHelp() {
        currentView = "help";
        setStatus(LanguageManager.get("status.help"));

        boolean sq = isAlbanian();
        Label title = new Label(LanguageManager.get("help.title"));
        title.getStyleClass().add("page-title");

        Label intro = new Label(sq
                ? "Kjo faqe shpjegon menyren e perdorimit te sistemit, navigimin dhe shkurtesat kryesore."
                : "This page explains how to use the system, navigate through modules and use keyboard shortcuts.");
        intro.setWrapText(true);
        intro.getStyleClass().add("body-text");

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
        helpView.getStyleClass().add("profile-page");
        helpView.setStyle("-fx-padding: 26;");

        ScrollPane scrollPane = new ScrollPane(helpView);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("module-scroll");

        setContent(scrollPane);
    }

    private VBox createHelpSection(String sectionTitle, String... lines) {
        Label title = new Label(sectionTitle);
        title.getStyleClass().add("section-title");

        VBox content = new VBox(6, title);

        for (String line : lines) {
            Label item = new Label("• " + line);
            item.setWrapText(true);
            item.getStyleClass().add("body-text");
            content.getChildren().add(item);
        }

        content.getStyleClass().add("content-card");
        return content;
    }

    @FXML
    public void showProfile() {
        currentView = "profile";
        setStatus(LanguageManager.get("status.account"));
        setActiveButton(profileButton);

        User user = Session.getUser();

        if (user == null) {
            setContent(new Label("No user logged in"));
            return;
        }

        String roleText = "ADMIN".equalsIgnoreCase(user.getRole())
                ? LanguageManager.get("account.role.admin")
                : LanguageManager.get("account.role.user");

        Label title = new Label(LanguageManager.get("menu.profile"));
        title.getStyleClass().add("page-title");

        Label userIcon = new Label("U");
        userIcon.getStyleClass().add("profile-icon");

        Label usernameLabel = new Label(user.getUsername());
        usernameLabel.getStyleClass().add("section-title");

        Label roleLabel = new Label(LanguageManager.get("account.role") + roleText);
        roleLabel.getStyleClass().add("profile-detail");

        Label createdAtLabel = new Label(LanguageManager.get("account.createdat") + formatCreatedAt(user.getCreatedAt()));
        createdAtLabel.getStyleClass().add("profile-detail");

        VBox userDetails = new VBox(5, usernameLabel, roleLabel, createdAtLabel);
        HBox userHeader = new HBox(14, userIcon, userDetails);

        VBox userCard = new VBox(userHeader);
        userCard.getStyleClass().add("profile-card");
        userCard.setMaxWidth(520);

        Label languageTitle = new Label(LanguageManager.get("account.language") + ":");
        languageTitle.getStyleClass().add("section-title");

        ComboBox<String> languageBox = new ComboBox<>();
        languageBox.getItems().addAll(LanguageManager.get("language.english"), LanguageManager.get("language.albanian"));
        languageBox.setValue(isAlbanian() ? LanguageManager.get("language.albanian") : LanguageManager.get("language.english"));
        languageBox.setPrefWidth(210);

        Label languageMessage = new Label();
        languageMessage.getStyleClass().add("success-text");

        languageBox.setOnAction(event -> {
            String selected = languageBox.getValue();
            String albanianText = LanguageManager.get("language.albanian");

            if (albanianText.equals(selected)) {
                LanguageManager.setLanguage("sq");
            } else {
                LanguageManager.setLanguage("en");
            }

            profileLanguageChanged = true;
            updateTexts();
            showProfile();
        });

        HBox languageRow = new HBox(12, languageBox, languageMessage);
        languageMessage.setText(profileLanguageChanged ? LanguageManager.get("account.language.success") : "");
        profileLanguageChanged = false;

        Button logout = new Button(LanguageManager.get("account.logout"));
        logout.getStyleClass().add("secondary-button");
        logout.setOnAction(e -> handleLogout());

        VBox languageCard = new VBox(12, languageTitle, languageRow, logout);
        languageCard.getStyleClass().add("profile-card");
        languageCard.setMaxWidth(520);

        Button deleteAccount = new Button(isAlbanian() ? "Fshi llogarine" : "Delete account");
        deleteAccount.getStyleClass().add("danger-text-button");
        deleteAccount.setOnAction(e -> confirmDeleteAccount(user));

        VBox dangerCard = new VBox(deleteAccount);
        dangerCard.getStyleClass().add("profile-card");
        dangerCard.setMaxWidth(520);

        VBox profileView = new VBox(18, title, userCard, languageCard, dangerCard);
        profileView.getStyleClass().add("profile-page");
        profileView.setStyle("-fx-padding: 28;");
        VBox.setVgrow(profileView, Priority.NEVER);

        setContent(profileView);
    }

    private void confirmDeleteAccount(User user) {

        Alert alert = new Alert(Alert.AlertType.NONE);
        DialogUtils.style(alert);

        alert.setTitle(isAlbanian() ? "Fshi llogarine" : "Delete Account");
        alert.setHeaderText(null);

        ButtonType yesType = new ButtonType(
                isAlbanian() ? "Po, fshije" : "Yes, Delete",
                ButtonBar.ButtonData.YES
        );

        ButtonType noType = new ButtonType(
                isAlbanian() ? "Jo" : "No",
                ButtonBar.ButtonData.CANCEL_CLOSE
        );

        alert.getButtonTypes().setAll(yesType, noType);

        Label icon = new Label("🗑");
        icon.setStyle("""
            -fx-font-size: 64px;
            -fx-padding: 10;
            """);

        Label title = new Label(
                isAlbanian()
                        ? "A jeni i sigurt qe doni ta fshini llogarine?"
                        : "Are you sure you want to delete your account?"
        );

        title.setWrapText(true);

        title.setStyle("""
            -fx-font-size: 18px;
            -fx-font-weight: bold;
            -fx-text-alignment: center;
            """);

        Label subtitle = new Label(
                isAlbanian()
                        ? "Ky veprim nuk mund te kthehet."
                        : "This action cannot be undone."
        );

        subtitle.setStyle("""
            -fx-font-size: 13px;
            -fx-opacity: 0.8;
            """);

        VBox content = new VBox(15, icon, title, subtitle);
        content.setAlignment(Pos.CENTER);

        alert.getDialogPane().setContent(content);
        alert.getDialogPane().setPrefWidth(460);
        alert.getDialogPane().setPrefHeight(320);

        Platform.runLater(() -> {

            Button yesButton = (Button) alert.getDialogPane().lookupButton(yesType);
            Button noButton = (Button) alert.getDialogPane().lookupButton(noType);

            yesButton.setMaxWidth(Double.MAX_VALUE);
            noButton.setMaxWidth(Double.MAX_VALUE);

            yesButton.setPrefHeight(42);
            noButton.setPrefHeight(42);

            yesButton.setStyle("""
                -fx-background-color: #dc2626;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-background-radius: 10;
                -fx-cursor: hand;
                """);

            noButton.setStyle("""
                -fx-background-radius: 10;
                -fx-cursor: hand;
                """);

            VBox buttonBox = new VBox(10, yesButton, noButton);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setFillWidth(true);

            alert.getDialogPane().setContent(
                    new VBox(20, content, buttonBox)
            );
        });

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == yesType) {

            boolean deleted = UserService.deleteUser(user.getId());

            if (deleted) {
                Session.clear();
                showWelcome();
            }
        }
    }

    private Label createDialogIcon(String iconText, String fallbackText) {
        Label icon = new Label(iconText);
        icon.getStyleClass().add("dialog-icon");
        icon.setMinSize(58, 58);
        icon.setPrefSize(58, 58);

        if (icon.getText() == null || icon.getText().isBlank()) {
            icon.setText(fallbackText);
        }

        return icon;
    }

    private void loadView(String fxmlPath, Button activeButton, String errorMessage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            setContent(view);
            setActiveButton(activeButton);
        } catch (Exception e) {
            e.printStackTrace();
            setStatus(errorMessage);
        }
    }

    private String formatCreatedAt(Timestamp createdAt) {
        if (createdAt == null) {
            return "-";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return createdAt.toLocalDateTime().format(formatter);
    }

    private boolean isAlbanian() {
        return "sq".equals(LanguageManager.getCurrentLocale().getLanguage());
    }

    private record NavItem(String icon, String label) {
    }
}
