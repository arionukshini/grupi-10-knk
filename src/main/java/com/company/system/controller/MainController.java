package com.company.system.controller;

import com.company.system.i18n.LanguageManager;
import com.company.system.model.User;
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
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
        dashboardButton.setUserData(new NavItem("▦", LanguageManager.get("menu.dashboard")));
        employeesButton.setUserData(new NavItem("◉", LanguageManager.get("menu.employees")));
        contractsButton.setUserData(new NavItem("□", LanguageManager.get("menu.contracts")));
        salariesButton.setUserData(new NavItem("$", LanguageManager.get("menu.salaries")));
        departmentsButton.setUserData(new NavItem("◇", LanguageManager.get("menu.departments")));
        profileButton.setUserData(new NavItem("◎", LanguageManager.get("menu.profile")));

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
        Alert exit = new Alert(Alert.AlertType.CONFIRMATION);

        exit.setTitle("Exit");
        exit.setHeaderText("Are you sure you want to exit?");
        exit.setContentText(null);

        ButtonType mainMenuBtn = new ButtonType("Quit to Main Menu");
        ButtonType desktopBtn = new ButtonType("Quit to Desktop");
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        exit.getButtonTypes().setAll(mainMenuBtn, desktopBtn, cancelBtn);

        Optional<ButtonType> result = exit.showAndWait();

        if (result.isPresent()) {
            if (result.get() == mainMenuBtn) {
                showWelcome();
            } else if (result.get() == desktopBtn) {
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
                        sq ? "Perdorni menune anesore per te hapur modulet kryesore." : "Use the sidebar to open the main modules.",
                        sq ? "Butoni i menus e zgjeron ose minimizon panelin anesor." : "The menu button expands or collapses the sidebar.",
                        sq ? "Status bar poshte tregon pamjen aktuale te hapur." : "The bottom status bar shows the currently opened view."
                ),
                createHelpSection(
                        sq ? "Gjuha dhe tema" : "Language and theme",
                        sq ? "Butoni i gjuhes kalon mes Shqip dhe English." : "The language button toggles between Albanian and English.",
                        sq ? "Ikona diell/hene kalon mes pamjes se erret dhe te ndritur." : "The sun/moon icon toggles between dark and light mode."
                )
        );

        VBox helpView = new VBox(18, title, intro, sections);
        helpView.getStyleClass().add("profile-page");
        helpView.setStyle("-fx-padding: 26;");

        setContent(helpView);
    }

    private VBox createHelpSection(String sectionTitle, String... lines) {
        Label title = new Label(sectionTitle);
        title.getStyleClass().add("section-title");

        VBox content = new VBox(6, title);

        for (String line : lines) {
            Label item = new Label("- " + line);
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

        Label usernameLabel = new Label(user.getUsername());
        usernameLabel.getStyleClass().add("section-title");

        Label roleLabel = new Label(LanguageManager.get("account.role") + roleText);
        roleLabel.getStyleClass().add("profile-detail");

        Label createdAtLabel = new Label(LanguageManager.get("account.createdat") + formatCreatedAt(user.getCreatedAt()));
        createdAtLabel.getStyleClass().add("profile-detail");

        Button logout = new Button(LanguageManager.get("account.logout"));
        logout.getStyleClass().add("footer-button");
        logout.setOnAction(e -> handleLogout());

        VBox card = new VBox(12, usernameLabel, roleLabel, createdAtLabel, logout);
        card.getStyleClass().add("profile-card");
        card.setMaxWidth(460);

        VBox profileView = new VBox(18, title, card);
        profileView.getStyleClass().add("profile-page");
        profileView.setStyle("-fx-padding: 28;");

        setContent(profileView);
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
