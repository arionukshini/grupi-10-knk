package com.company.system.controller;

import com.company.system.i18n.LanguageManager;
import com.company.system.model.User;
import com.company.system.service.UserService;
import com.company.system.utils.DialogUtils;
import com.company.system.utils.PasswordUtils;
import com.company.system.utils.Session;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

import static com.company.system.MainApp.showWelcome;

public class UserMainController {

    private static final double EXPANDED_SIDEBAR_WIDTH = 220;
    private static final double COLLAPSED_SIDEBAR_WIDTH = 72;
    private static final double NAV_ICON_SCALE = 0.88;
    private static final double FOOTER_ICON_SCALE = 0.86;

    private static final String ICON_DASHBOARD = "M3 3 H10 V10 H3 Z M14 3 H21 V7 H14 Z M14 11 H21 V21 H14 Z M3 14 H10 V21 H3 Z";
    private static final String ICON_CONTRACTS = "M6 2 H15 L20 7 V22 H6 Z M14 3.5 V8 H18.5 M8 12 H18 M8 16 H18 M8 20 H14";
    private static final String ICON_SALARIES = "M9 3 H15 L14 7 H10 Z M10 7 H14 C18 9 20 12.5 20 17 C20 20.31 16.42 22 12 22 C7.58 22 4 20.31 4 17 C4 12.5 6 9 10 7 Z M12 11 V18 M9.5 13 H13.2 C14.2 13 15 13.67 15 14.55 C15 15.42 14.2 16 13.2 16 H10.8 C9.8 16 9 16.58 9 17.45 C9 18.33 9.8 19 10.8 19 H14.5";
    private static final String ICON_DEPARTMENTS = "M3 21 V9 H9 V21 Z M10 21 V3 H16 V21 Z M17 21 V12 H21 V21 Z M5 12 H7 M12 6 H14 M12 10 H14 M12 14 H14 M19 15 H19";
    private static final String ICON_SETTINGS = "M19.43 12.98 C19.47 12.66 19.5 12.34 19.5 12 C19.5 11.66 19.47 11.34 19.43 11.02 L21.54 9.37 L19.54 5.91 L17.05 6.91 C16.54 6.52 15.99 6.2 15.38 5.95 L15 3.29 H11 L10.62 5.95 C10.01 6.2 9.46 6.52 8.95 6.91 L6.46 5.91 L4.46 9.37 L6.57 11.02 C6.53 11.34 6.5 11.66 6.5 12 C6.5 12.34 6.53 12.66 6.57 12.98 L4.46 14.63 L6.46 18.09 L8.95 17.09 C9.46 17.48 10.01 17.8 10.62 18.05 L11 20.71 H15 L15.38 18.05 C15.99 17.8 16.54 17.48 17.05 17.09 L19.54 18.09 L21.54 14.63 Z M13 15.5 C11.07 15.5 9.5 13.93 9.5 12 C9.5 10.07 11.07 8.5 13 8.5 C14.93 8.5 16.5 10.07 16.5 12 C16.5 13.93 14.93 15.5 13 15.5 Z";
    private static final String ICON_USER = "M12 12 C14.76 12 17 9.76 17 7 C17 4.24 14.76 2 12 2 C9.24 2 7 4.24 7 7 C7 9.76 9.24 12 12 12 Z M4 22 C4 17.58 7.58 14 12 14 C16.42 14 20 17.58 20 22 Z";
    private static final String ICON_LANGUAGE = "M4 4 H13 V7 H11 C10.7 8.4 10.12 9.69 9.25 10.83 C10 11.45 10.9 12.04 12 12.56 L11 14.3 C9.9 13.76 8.93 13.13 8.08 12.43 C7.08 13.25 5.83 14.08 4.3 14.9 L3.35 13.22 C4.73 12.52 5.85 11.82 6.74 11.12 C6.14 10.45 5.62 9.72 5.17 8.92 L6.88 8.05 C7.2 8.6 7.57 9.1 8 9.57 C8.55 8.82 8.94 7.97 9.18 7 H4 Z M15 10 H17 L21 20 H18.9 L18.1 18 H13.9 L13.1 20 H11 Z M14.58 16.2 H17.42 L16 12.55 Z";
    private static final String ICON_HELP = "M12 2 C6.48 2 2 6.48 2 12 C2 17.52 6.48 22 12 22 C17.52 22 22 17.52 22 12 C22 6.48 17.52 2 12 2 Z M11 18 H13 V16 H11 Z M12 6 C9.79 6 8 7.79 8 10 H10 C10 8.9 10.9 8 12 8 C13.1 8 14 8.9 14 10 C14 12 11 11.75 11 15 H13 C13 12.75 16 12.5 16 10 C16 7.79 14.21 6 12 6 Z";
    private static final String ICON_EXIT = "M15 3 H5 C3.9 3 3 3.9 3 5 V19 C3 20.1 3.9 21 5 21 H15 M10 12 H21 M17 8 L21 12 L17 16";
    private static final String ICON_DELETE = "M3 6 H21 M8 6 V4 H16 V6 M6 6 L7 21 H17 L18 6 M10 10 V17 M14 10 V17";
    private static final String ICON_SUN = "M12 4 V2 M12 22 V20 M4.93 4.93 L3.52 3.52 M20.48 20.48 L19.07 19.07 M4 12 H2 M22 12 H20 M4.93 19.07 L3.52 20.48 M20.48 3.52 L19.07 4.93 M12 7 C9.24 7 7 9.24 7 12 C7 14.76 9.24 17 12 17 C14.76 17 17 14.76 17 12 C17 9.24 14.76 7 12 7 Z";
    private static final String ICON_MOON = "M21 12.79 C20.16 13.05 19.28 13.18 18.36 13.18 C14.2 13.18 10.82 9.8 10.82 5.64 C10.82 4.72 10.95 3.84 11.21 3 C6.56 3.45 3 7.36 3 12.12 C3 17.07 6.93 21 11.88 21 C16.64 21 20.55 17.44 21 12.79 Z";

    private final List<String> themeClasses = List.of("light", "dark");
    private final Deque<String> backHistory = new ArrayDeque<>();
    private final Deque<String> forwardHistory = new ArrayDeque<>();

    private String currentView = "welcome";
    private boolean sidebarExpanded = true;
    private boolean darkMode = false;
    private boolean settingsLanguageChanged = false;
    private boolean navigatingHistory = false;
    private Timeline sidebarAnimation;

    @FXML private BorderPane mainShell;
    @FXML private VBox sidebar;
    @FXML private HBox expandedFooter;
    @FXML private Button menuToggleButton;
    @FXML private Button dashboardButton;
    @FXML private Button contractButton;
    @FXML private Button salaryButton;
    @FXML private Button departmentButton;
    @FXML private Button settingsButton;
    @FXML private Button languageButton;
    @FXML private Button helpFooterButton;
    @FXML private Button themeButton;
    @FXML private Button themeButton2;
    @FXML private StackPane contentArea;
    @FXML private Label appTitleLabel;
    @FXML private Label loggedInLabel;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        mainShell.getStyleClass().add("light");
        updateTexts();
        updateLoggedInUser();
        updateThemeButton();
        updateSidebarLabels();
        setStatus(t("Ready", "Gati"));
        setupKeyboardShortcuts();
        setupMouseNavigation();
        setupContextMenu();
        showDashboard();
        checkExpiringContractsPopup();
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
        LanguageManager.setLanguage(isAlbanian() ? "en" : "sq");
        updateTexts();
        refreshCurrentView();
    }

    @FXML
    public void showDashboard() {
        recordNavigation("dashboard");
        currentView = "dashboard";
        setStatus(t("Dashboard opened", "Dashboard u hap"));
        loadView("/views/user-dashboard-view.fxml", dashboardButton, t("Failed to load dashboard.", "Dashboard nuk u hap."));
    }

    @FXML
    public void showContract() {
        recordNavigation("contract");
        currentView = "contract";
        setStatus(t("My Contract opened", "My Contract u hap"));
        loadView("/views/my-contract-view.fxml", contractButton,
                t("Failed to load contract.", "Kontrata nuk u hap."));
    }
    @FXML
    public void showContract() {
        recordNavigation("contract");
        currentView = "contract";
        setStatus(t("My Contract opened", "My Contract u hap"));
        loadView("/views/my-contract-view.fxml", contractButton,
                t("Failed to load contract.", "Kontrata nuk u hap."));
    }

    @FXML
    public void showSalary() {
        recordNavigation("salary");
        currentView = "salary";
        setStatus(t("My Salary opened", "My Salary u hap"));
        loadView("/views/my-salary-view.fxml", salaryButton,
                t("Failed to load salary.", "Paga nuk u hap."));
    }

    @FXML
    public void showDepartment() {
        recordNavigation("department");
        currentView = "department";
        setStatus(t("My Department opened", "My Department u hap"));
        setActiveButton(departmentButton);
        setContent(buildPlaceholderPage(t("My Department", "My Department"),
                t("This page is ready for the user department module.", "Kjo faqe eshte gati per modulin e departamentit te user-it.")));
    }

    @FXML
    public void showSettings() {
        recordNavigation("settings");
        currentView = "settings";
        setStatus(t("Settings opened", "Settings u hapen"));
        setActiveButton(settingsButton);

        User user = Session.getUser();
        if (user == null) {
            setContent(new Label(t("No user logged in", "Nuk ka user te kycur")));
            return;
        }

        Label title = new Label(t("Settings", "Cilesimet"));
        title.getStyleClass().add("page-title");

        StackPane userIcon = createSidebarIconBox(ICON_USER, 1.08);
        userIcon.getStyleClass().add("profile-icon");

        Label usernameLabel = new Label(user.getUsername());
        usernameLabel.getStyleClass().add("section-title");

        String roleText = "ADMIN".equalsIgnoreCase(user.getRole())
                ? LanguageManager.get("account.role.admin")
                : LanguageManager.get("account.role.user");
        Label roleLabel = detailLabel(LanguageManager.get("account.role") + roleText);
        Label createdAtLabel = detailLabel(LanguageManager.get("account.createdat") + formatCreatedAt(user.getCreatedAt()));

        VBox userDetails = new VBox(5, usernameLabel, roleLabel, createdAtLabel);
        HBox userHeader = new HBox(14, userIcon, userDetails);
        userHeader.setAlignment(Pos.CENTER_LEFT);

        VBox userCard = new VBox(userHeader);
        userCard.getStyleClass().add("profile-card");
        userCard.setMaxWidth(900);

        Label languageTitle = new Label(LanguageManager.get("account.language") + ":");
        languageTitle.getStyleClass().add("section-title");

        ComboBox<String> languageBox = new ComboBox<>();
        languageBox.getItems().addAll(LanguageManager.get("language.english"), LanguageManager.get("language.albanian"));
        languageBox.setValue(isAlbanian() ? LanguageManager.get("language.albanian") : LanguageManager.get("language.english"));
        languageBox.setPrefWidth(210);

        Label languageMessage = new Label(settingsLanguageChanged ? LanguageManager.get("account.language.success") : "");
        languageMessage.getStyleClass().add("success-text");
        settingsLanguageChanged = false;

        languageBox.setOnAction(event -> {
            LanguageManager.setLanguage(LanguageManager.get("language.albanian").equals(languageBox.getValue()) ? "sq" : "en");
            settingsLanguageChanged = true;
            updateTexts();
            showSettings();
        });

        VBox languageCard = new VBox(12, languageTitle, languageBox, languageMessage);
        languageCard.getStyleClass().add("profile-card");
        languageCard.setPrefWidth(430);
        languageCard.setMaxWidth(Double.MAX_VALUE);

        Label passwordTitle = new Label(t("Change password", "Ndrysho fjalekalimin"));
        passwordTitle.getStyleClass().add("section-title");

        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText(t("Current password", "Fjalekalimi aktual"));

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText(t("New password", "Fjalekalimi i ri"));

        Button changePasswordButton = new Button(t("Change password", "Ndrysho fjalekalimin"));
        changePasswordButton.getStyleClass().add("primary-button");
        changePasswordButton.setOnAction(e -> changePassword(user, currentPasswordField, newPasswordField));

        VBox passwordCard = new VBox(12, passwordTitle, currentPasswordField, newPasswordField, changePasswordButton);
        passwordCard.getStyleClass().add("profile-card");
        passwordCard.setPrefWidth(430);
        passwordCard.setMaxWidth(Double.MAX_VALUE);

        HBox accountOptions = new HBox(18, languageCard, passwordCard);
        accountOptions.setMaxWidth(900);
        HBox.setHgrow(languageCard, Priority.ALWAYS);
        HBox.setHgrow(passwordCard, Priority.ALWAYS);

        Button logout = new Button(LanguageManager.get("account.logout"));
        logout.getStyleClass().add("secondary-button");
        logout.setOnAction(e -> handleLogout());

        Button deleteAccount = new Button(t("Delete account", "Fshi llogarine"));
        deleteAccount.getStyleClass().add("danger-text-button");
        deleteAccount.setOnAction(e -> confirmDeleteAccount(user));

        VBox actionsCard = new VBox(14, logout, deleteAccount);
        actionsCard.getStyleClass().add("profile-card");
        actionsCard.setMaxWidth(900);

        VBox settingsView = new VBox(18, title, userCard, accountOptions, actionsCard);
        settingsView.getStyleClass().add("profile-page");
        settingsView.setPadding(new Insets(28));
        setContent(wrap(settingsView));
    }

    @FXML
    public void showHelp() {
        recordNavigation("help");
        currentView = "help";
        setStatus(t("Help opened", "Ndihma u hap"));
        clearActiveButton();
        setContent(MainController.createUserHelpView());
    }

    @FXML
    private void handleLogout() {
        Session.clear();
        showWelcome();
    }

    private void setupKeyboardShortcuts() {
        Platform.runLater(() -> contentArea.getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.isShortcutDown() && event.getCode() == KeyCode.K) {
                showContract(); event.consume();
            } else if (event.isShortcutDown() && event.getCode() == KeyCode.S) {
                showSalary(); event.consume();
            } else if (event.isShortcutDown() && event.getCode() == KeyCode.R) {
                showDepartment(); event.consume();
            } else if (event.isShortcutDown() && event.getCode() == KeyCode.D) {
                showDashboard(); event.consume();
            } else if (event.isShortcutDown() && event.getCode() == KeyCode.P) {
                showSettings(); event.consume();
            } else if (event.isAltDown() && event.getCode() == KeyCode.LEFT) {
                goBack(); event.consume();
            } else if (event.isAltDown() && event.getCode() == KeyCode.RIGHT) {
                goForward(); event.consume();
            } else if (event.getCode() == KeyCode.F5) {
                refreshCurrentView(); event.consume();
            } else if (event.getCode() == KeyCode.F1 || (event.isShortcutDown() && event.getCode() == KeyCode.H)) {
                showHelp(); event.consume();
            } else if (KeyCombination.keyCombination("Shortcut+L").match(event)) {
                toggleLanguage(); event.consume();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                handleExit(); event.consume();
            }
        }));
    }

    private void setupMouseNavigation() {
        Platform.runLater(() -> contentArea.getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.BACK) {
                goBack();
                event.consume();
            } else if (event.getButton() == MouseButton.FORWARD) {
                goForward();
                event.consume();
            }
        }));
    }

    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getStyleClass().add("app-context-menu");
        contextMenu.setAutoHide(true);
        contextMenu.setHideOnEscape(true);

        MenuItem refreshItem = new MenuItem();
        refreshItem.setOnAction(event -> refreshCurrentView());

        MenuItem helpItem = new MenuItem();
        helpItem.setOnAction(event -> showHelp());

        MenuItem exitItem = new MenuItem();
        exitItem.setOnAction(event -> handleExit());

        contextMenu.getItems().setAll(refreshItem, helpItem, new SeparatorMenuItem(), exitItem);

        Platform.runLater(() -> contentArea.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (contextMenu.isShowing() && event.getButton() != MouseButton.SECONDARY) {
                contextMenu.hide();
            }
        }));

        mainShell.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
            refreshItem.setText(t("Refresh", "Rifresko"));
            helpItem.setText(t("Help", "Ndihma"));
            exitItem.setText(t("Exit program", "Dil nga programi"));
            if (contextMenu.isShowing()) contextMenu.hide();
            contextMenu.show(mainShell, event.getScreenX(), event.getScreenY());
            event.consume();
        });
    }

    private void updateTexts() {
        if (appTitleLabel != null) {
            appTitleLabel.setText("Contract Manager");
        }
        contractButton.setUserData(new NavItem(ICON_CONTRACTS, t("My Contract", "My Contract")));
        salaryButton.setUserData(new NavItem(ICON_SALARIES, t("My Salary", "My Salary")));
        departmentButton.setUserData(new NavItem(ICON_DEPARTMENTS, t("My Department", "My Department")));
        dashboardButton.setUserData(new NavItem(ICON_DASHBOARD, t("Dashboard", "Dashboard")));
        settingsButton.setUserData(new NavItem(ICON_SETTINGS, t("Settings", "Settings")));

        setIconOnlyButton(languageButton, ICON_LANGUAGE);
        setIconOnlyButton(helpFooterButton, ICON_HELP);
        setIconOnlyButton(themeButton2, ICON_SETTINGS);
        updateLoggedInUser();
        updateSidebarLabels();
        updateThemeButton();
    }

    private void updateLoggedInUser() {
        User user = Session.getUser();
        loggedInLabel.setText(t("Logged in as: ", "I kycur si: ") + UserService.getDisplayName(user));
    }

    private void updateThemeButton() {
        setIconOnlyButton(themeButton, darkMode ? ICON_SUN : ICON_MOON);
        setIconOnlyButton(themeButton2, darkMode ? ICON_SUN : ICON_MOON);
    }

    private void updateSidebarState() {
        if (sidebarAnimation != null) sidebarAnimation.stop();

        double startWidth = sidebar.getWidth() > 0 ? sidebar.getWidth() : sidebar.getPrefWidth();
        double targetWidth = sidebarExpanded ? EXPANDED_SIDEBAR_WIDTH : COLLAPSED_SIDEBAR_WIDTH;

        sidebar.getStyleClass().remove("collapsed");
        menuToggleButton.setDisable(true);

        if (sidebarExpanded) {
            expandedFooter.setVisible(true);
            expandedFooter.setManaged(true);
            themeButton2.setVisible(false);
            themeButton2.setManaged(false);
        } else {
            sidebar.getStyleClass().add("collapsed");
            expandedFooter.setOpacity(1);
        }

        updateSidebarLabels();

        sidebarAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(sidebar.prefWidthProperty(), startWidth),
                        new KeyValue(sidebar.minWidthProperty(), startWidth),
                        new KeyValue(sidebar.maxWidthProperty(), startWidth),
                        new KeyValue(expandedFooter.opacityProperty(), sidebarExpanded ? 0 : 1)),
                new KeyFrame(Duration.millis(280),
                        new KeyValue(sidebar.prefWidthProperty(), targetWidth, Interpolator.EASE_BOTH),
                        new KeyValue(sidebar.minWidthProperty(), targetWidth, Interpolator.EASE_BOTH),
                        new KeyValue(sidebar.maxWidthProperty(), targetWidth, Interpolator.EASE_BOTH),
                        new KeyValue(expandedFooter.opacityProperty(), sidebarExpanded ? 1 : 0, Interpolator.EASE_BOTH))
        );

        sidebarAnimation.setOnFinished(event -> {
            sidebar.setPrefWidth(targetWidth);
            sidebar.setMinWidth(targetWidth);
            sidebar.setMaxWidth(targetWidth);
            expandedFooter.setVisible(sidebarExpanded);
            expandedFooter.setManaged(sidebarExpanded);
            expandedFooter.setOpacity(sidebarExpanded ? 1 : 0);
            themeButton2.setVisible(!sidebarExpanded);
            themeButton2.setManaged(!sidebarExpanded);
            menuToggleButton.setDisable(false);
            updateSidebarLabels();
        });

        sidebarAnimation.play();
    }

    private void updateSidebarLabels() {
        for (Button button : navButtons()) {
            setNavButtonText(button);
        }
    }

    private void setNavButtonText(Button button) {
        if (!(button.getUserData() instanceof NavItem item)) return;
        button.setGraphic(createSidebarIconBox(item.iconPath(), NAV_ICON_SCALE));
        button.setText(sidebarExpanded ? item.label() : "");
        button.setContentDisplay(sidebarExpanded ? ContentDisplay.LEFT : ContentDisplay.GRAPHIC_ONLY);
        button.setGraphicTextGap(14);
        button.setAlignment(sidebarExpanded ? Pos.CENTER_LEFT : Pos.CENTER);
    }

    private void setIconOnlyButton(Button button, String iconPath) {
        if (button == null) return;
        button.setText("");
        button.setGraphic(createSidebarIconBox(iconPath, FOOTER_ICON_SCALE));
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.setAlignment(Pos.CENTER);
    }

    private StackPane createSidebarIconBox(String iconPath, double scale) {
        SVGPath icon = new SVGPath();
        icon.setContent(iconPath);
        icon.getStyleClass().add("sidebar-svg-icon");
        icon.setScaleX(scale);
        icon.setScaleY(scale);

        StackPane box = new StackPane(icon);
        box.getStyleClass().add("sidebar-icon-box");
        return box;
    }

    private StackPane createPopupIcon(String iconPath, String color) {
        SVGPath icon = new SVGPath();
        icon.setContent(iconPath);
        icon.setStyle("-fx-fill: transparent; -fx-stroke: " + color + "; -fx-stroke-width: 2.2; -fx-stroke-line-cap: round; -fx-stroke-line-join: round;");
        icon.setScaleX(2.2);
        icon.setScaleY(2.2);

        StackPane box = new StackPane(icon);
        box.setMinSize(72, 72);
        box.setPrefSize(72, 72);
        box.setMaxSize(72, 72);
        return box;
    }

    private void setActiveButton(Button activeButton) {
        clearActiveButton();
        if (!activeButton.getStyleClass().contains("active")) {
            activeButton.getStyleClass().add("active");
        }
        activeButton.requestFocus();
    }

    private void clearActiveButton() {
        for (Button button : navButtons()) {
            button.getStyleClass().remove("active");
        }
    }

    private List<Button> navButtons() {
        return List.of(contractButton, salaryButton, departmentButton, dashboardButton, settingsButton);
    }

    private void refreshCurrentView() {
        switch (currentView) {
            case "contract" -> showContract();
            case "salary" -> showSalary();
            case "department" -> showDepartment();
            case "dashboard" -> showDashboard();
            case "settings" -> showSettings();
            case "help" -> showHelp();
            default -> showDashboard();
        }
    }

    private void recordNavigation(String targetView) {
        if (navigatingHistory || targetView.equals(currentView)) return;
        if (!"welcome".equals(currentView)) backHistory.push(currentView);
        forwardHistory.clear();
    }

    private void goBack() {
        if (backHistory.isEmpty()) return;
        forwardHistory.push(currentView);
        navigateHistoryTo(backHistory.pop());
    }

    private void goForward() {
        if (forwardHistory.isEmpty()) return;
        backHistory.push(currentView);
        navigateHistoryTo(forwardHistory.pop());
    }

    private void navigateHistoryTo(String view) {
        navigatingHistory = true;
        try {
            showViewByName(view);
        } finally {
            navigatingHistory = false;
        }
    }

    private void showViewByName(String view) {
        switch (view) {
            case "contract" -> showContract();
            case "salary" -> showSalary();
            case "department" -> showDepartment();
            case "dashboard" -> showDashboard();
            case "settings" -> showSettings();
            case "help" -> showHelp();
            default -> showDashboard();
        }
    }

    private void handleExit() {
        Alert alert = new Alert(Alert.AlertType.NONE);
        DialogUtils.style(alert);
        alert.setTitle(isAlbanian() ? "Dalje" : "Exit");
        alert.setHeaderText(null);

        ButtonType mainMenuType = new ButtonType(isAlbanian() ? "Menyja kryesore" : "Main Menu", ButtonBar.ButtonData.OTHER);
        ButtonType desktopType = new ButtonType(isAlbanian() ? "Dil nga programi" : "Quit to Desktop", ButtonBar.ButtonData.OTHER);
        ButtonType cancelType = new ButtonType(isAlbanian() ? "Anulo" : "Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(mainMenuType, desktopType, cancelType);

        Label icon = new Label("ðŸšª");
        icon.setText("");
        icon.setGraphic(createPopupIcon(ICON_EXIT, "#3b82f6"));
        icon.setStyle("-fx-padding: 10;");

        Label title = new Label(isAlbanian() ? "A jeni i sigurt qe doni te dilni?" : "Are you sure you want to exit?");
        title.setWrapText(true); title.setMaxWidth(360); title.setAlignment(Pos.CENTER);
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label subtitle = new Label(isAlbanian() ? "Zgjidhni nje opsion per dalje." : "Choose an exit option.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-opacity: 0.8;");

        VBox content = new VBox(12, icon, title, subtitle);
        content.setAlignment(Pos.CENTER);
        alert.getDialogPane().setPrefWidth(460);
        alert.getDialogPane().setPrefHeight(365);
        alert.getDialogPane().setContent(content);

        Platform.runLater(() -> {
            Button mainMenuBtn = (Button) alert.getDialogPane().lookupButton(mainMenuType);
            Button desktopBtn = (Button) alert.getDialogPane().lookupButton(desktopType);
            Button cancelBtn = (Button) alert.getDialogPane().lookupButton(cancelType);

            mainMenuBtn.setMaxWidth(Double.MAX_VALUE); mainMenuBtn.setPrefHeight(42);
            desktopBtn.setMaxWidth(Double.MAX_VALUE); desktopBtn.setPrefHeight(42);
            cancelBtn.setMaxWidth(Double.MAX_VALUE); cancelBtn.setPrefHeight(42);

            mainMenuBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand;");
            desktopBtn.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand;");
            cancelBtn.setStyle("-fx-background-radius: 10; -fx-cursor: hand;");

            VBox buttonBox = new VBox(10, mainMenuBtn, desktopBtn, cancelBtn);
            buttonBox.setAlignment(Pos.CENTER); buttonBox.setFillWidth(true);
            alert.getDialogPane().setContent(new VBox(18, content, buttonBox));
        });

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == mainMenuType) showWelcome();
            else if (result.get() == desktopType) System.exit(0);
        }
    }

    private void changePassword(User user, PasswordField currentPasswordField, PasswordField newPasswordField) {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String storedHash = UserService.getPasswordHashByUsername(user.getUsername());

        if (currentPassword == null || currentPassword.isBlank() || newPassword == null || newPassword.isBlank()) {
            showStyledAlert(Alert.AlertType.ERROR, t("Error", "Gabim"),
                    t("Fill both password fields.", "Plotesoni te dy fushat e fjalekalimit."));
            return;
        }
        if (!PasswordUtils.verifyPassword(currentPassword, storedHash)) {
            showStyledAlert(Alert.AlertType.ERROR, t("Error", "Gabim"),
                    t("Current password is not correct.", "Fjalekalimi aktual nuk eshte i sakte."));
            return;
        }
        if (PasswordUtils.verifyPassword(newPassword, storedHash)) {
            showStyledAlert(Alert.AlertType.ERROR, t("Error", "Gabim"),
                    t("New password cannot be the same as the old one.", "Fjalekalimi i ri nuk mund te jete i njejte me te vjetrin."));
            return;
        }
        if (UserService.resetPassword(user.getUsername(), newPassword)) {
            currentPasswordField.clear();
            newPasswordField.clear();
            showStyledAlert(Alert.AlertType.INFORMATION, t("Success", "Sukses"),
                    t("Password changed successfully.", "Fjalekalimi u ndryshua me sukses."));
        } else {
            showStyledAlert(Alert.AlertType.ERROR, t("Error", "Gabim"),
                    t("Password was not changed.", "Fjalekalimi nuk u ndryshua."));
        }
    }

    private void confirmDeleteAccount(User user) {
        if (UserService.isOnlyAdmin(user)) {
            showStyledAlert(Alert.AlertType.WARNING, isAlbanian() ? "Nuk lejohet" : "Not allowed",
                    isAlbanian()
                            ? "Ju jeni administratori i vetem. Nuk mund ta fshini llogarine pa pasur te pakten edhe nje administrator tjeter."
                            : "You are the only administrator. You cannot delete this account until another administrator exists.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.NONE);
        DialogUtils.style(alert);
        alert.setTitle(isAlbanian() ? "Fshi llogarine" : "Delete Account");
        alert.setHeaderText(null);

        ButtonType yesType = new ButtonType(isAlbanian() ? "Po, fshije" : "Yes, Delete", ButtonBar.ButtonData.YES);
        ButtonType noType = new ButtonType(isAlbanian() ? "Jo" : "No", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yesType, noType);

        Label icon = new Label("ðŸ—‘");
        icon.setText("");
        icon.setGraphic(createPopupIcon(ICON_DELETE, "#dc2626"));
        icon.setStyle("-fx-padding: 10;");

        Label title = new Label(isAlbanian() ? "A jeni i sigurt qe doni ta fshini llogarine?" : "Are you sure you want to delete your account?");
        title.setWrapText(true); title.setMaxWidth(370); title.setAlignment(Pos.CENTER);
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-alignment: center;");

        Label subtitle = new Label(isAlbanian()
                ? "Ky veprim do te fshije llogarine, punetorin, kontratat, pagat dhe historikun e pagave."
                : "This will delete your account, employee record, contracts, salaries and salary history.");
        subtitle.setWrapText(true); subtitle.setMaxWidth(370); subtitle.setMinHeight(Label.USE_PREF_SIZE);
        subtitle.setStyle("-fx-font-size: 13px; -fx-opacity: 0.8;");

        VBox content = new VBox(15, icon, title, subtitle);
        content.setAlignment(Pos.CENTER);
        alert.getDialogPane().setContent(content);
        alert.getDialogPane().setPrefWidth(460);
        alert.getDialogPane().setPrefHeight(360);

        Platform.runLater(() -> {
            Button yesButton = (Button) alert.getDialogPane().lookupButton(yesType);
            Button noButton = (Button) alert.getDialogPane().lookupButton(noType);
            yesButton.setMaxWidth(Double.MAX_VALUE); yesButton.setPrefHeight(42);
            noButton.setMaxWidth(Double.MAX_VALUE); noButton.setPrefHeight(42);
            yesButton.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand;");
            noButton.setStyle("-fx-background-radius: 10; -fx-cursor: hand;");
            VBox buttonBox = new VBox(10, yesButton, noButton);
            buttonBox.setAlignment(Pos.CENTER); buttonBox.setFillWidth(true);
            alert.getDialogPane().setContent(new VBox(20, content, buttonBox));
        });

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == yesType) {
            boolean deleted = UserService.deleteAccountAndEmployeeData(user);
            if (deleted) { Session.clear(); showWelcome(); }
            else showStyledAlert(Alert.AlertType.ERROR, isAlbanian() ? "Gabim" : "Error",
                    isAlbanian() ? "Llogaria nuk u fshi. Kontrolloni databazen ose provoni perseri."
                            : "The account was not deleted. Check the database or try again.");
        }
    }

    private void checkExpiringContracts() {
        User user = Session.getUser();
        if (user == null || user.getEmployeeId() == null) return;

        List<com.company.system.model.Contract> expiring =
                com.company.system.service.ContractService.getExpiringContractsForEmployee(user.getEmployeeId());
        if (expiring.isEmpty()) return;

        boolean sq = isAlbanian();
        StringBuilder message = new StringBuilder();
        message.append(sq
                ? "Kontratat tuaja te meposhtme do te skadojne brenda 14 diteve:\n\n"
                : "The following contracts will expire within 14 days:\n\n");

        for (com.company.system.model.Contract c : expiring) {
            message.append("â€¢ ").append(c.getContractType())
                    .append(" â€” ").append(sq ? "Skadon" : "Expires")
                    .append(": ").append(c.getEndDate()).append("\n");
        }

        message.append(sq
                ? "\nJu lutem kontaktoni administratorin per rinovim."
                : "\nPlease contact the administrator for renewal.");

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            DialogUtils.style(alert);
            alert.setTitle(sq ? "Paralajmerim â€” Kontrata" : "Warning â€” Contract");
            alert.setHeaderText(sq ? "Kontrata juaj po skadon!" : "Your contract is expiring!");
            Label content = new Label(message.toString());
            content.setWrapText(true);
            content.setMaxWidth(400);
            content.setStyle("-fx-font-size: 13px;");
            alert.getDialogPane().setContent(content);
            alert.getDialogPane().setPrefWidth(480);
            alert.showAndWait();
        });
    }

    private void checkExpiringContractsPopup() {
        User user = Session.getUser();
        if (user == null || user.getEmployeeId() == null) return;

        List<com.company.system.model.Contract> expiring =
                com.company.system.service.ContractService.getExpiringContractsForEmployee(user.getEmployeeId());
        if (expiring.isEmpty()) return;

        boolean sq = isAlbanian();
        StringBuilder message = new StringBuilder();
        message.append(sq
                ? "Kontratat tuaja te meposhtme do te skadojne brenda 14 diteve:\n\n"
                : "The following contracts will expire within 14 days:\n\n");

        for (com.company.system.model.Contract contract : expiring) {
            message.append("- ").append(contract.getContractType())
                    .append(" - ").append(sq ? "Skadon" : "Expires")
                    .append(": ").append(contract.getEndDate()).append("\n");
        }

        message.append(sq
                ? "\nJu lutem kontaktoni administratorin per rinovim."
                : "\nPlease contact the administrator for renewal.");

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            DialogUtils.style(alert);
            alert.setTitle(sq ? "Paralajmerim - Kontrata" : "Warning - Contract");
            alert.setHeaderText(sq ? "Kontrata juaj po skadon!" : "Your contract is expiring!");
            Label content = new Label(message.toString());
            content.setWrapText(true);
            content.setMaxWidth(400);
            content.setStyle("-fx-font-size: 13px;");
            alert.getDialogPane().setContent(content);
            alert.getDialogPane().setPrefWidth(480);
            alert.showAndWait();
        });
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
            setContent(buildPlaceholderPage(t("Error", "Gabim"), errorMessage));
        }
    }

    private Node buildPlaceholderPage(String titleText, String messageText) {
        VBox page = new VBox(18);
        page.getStyleClass().add("profile-page");
        page.setPadding(new Insets(28));

        Label title = new Label(titleText);
        title.getStyleClass().add("page-title");

        Label message = new Label(messageText);
        message.setWrapText(true);
        message.getStyleClass().add("body-text");

        VBox card = new VBox(message);
        card.getStyleClass().add("profile-card");
        card.setMaxWidth(900);

        page.getChildren().addAll(title, card);
        return wrap(page);
    }

    private Label detailLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.getStyleClass().add("profile-detail");
        return label;
    }

    private ScrollPane wrap(Node root) {
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("module-scroll");
        return scrollPane;
    }

    private void setContent(Node node) {
        contentArea.getChildren().setAll(node);
    }

    private void setStatus(String text) {
        statusLabel.setText(text);
    }

    private void showStyledAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        DialogUtils.style(alert);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.getDialogPane().setContent(createWrappedDialogLabel(message, 380));
        alert.getDialogPane().setPrefWidth(460);
        alert.showAndWait();
    }

    private Label createWrappedDialogLabel(String message, double width) {
        Label label = new Label(message);
        label.setWrapText(true);
        label.setMaxWidth(width);
        label.setMinHeight(Label.USE_PREF_SIZE);
        label.getStyleClass().add("body-text");
        return label;
    }

    private String formatCreatedAt(Timestamp createdAt) {
        if (createdAt == null) return "-";
        return createdAt.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private String t(String en, String sq) {
        return isAlbanian() ? sq : en;
    }

    private boolean isAlbanian() {
        return "sq".equals(LanguageManager.getCurrentLocale().getLanguage());
    }

    private record NavItem(String iconPath, String label) {
    }
}
