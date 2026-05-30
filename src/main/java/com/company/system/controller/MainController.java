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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ContextMenuEvent;
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

public class MainController {

    private static final double EXPANDED_SIDEBAR_WIDTH = 220;
    private static final double COLLAPSED_SIDEBAR_WIDTH = 72;
    private static final double NAV_ICON_SCALE = 0.88;
    private static final double FOOTER_ICON_SCALE = 0.86;

    private static final String ICON_DASHBOARD = "M3 3 H10 V10 H3 Z M14 3 H21 V7 H14 Z M14 11 H21 V21 H14 Z M3 14 H10 V21 H3 Z";
    private static final String ICON_EMPLOYEES = "M16 11 C18.21 11 20 9.21 20 7 C20 4.79 18.21 3 16 3 C13.79 3 12 4.79 12 7 C12 9.21 13.79 11 16 11 Z M8 11 C10.21 11 12 9.21 12 7 C12 4.79 10.21 3 8 3 C5.79 3 4 4.79 4 7 C4 9.21 5.79 11 8 11 Z M8 13 C5.33 13 0 14.34 0 17 V20 H16 V17 C16 14.34 10.67 13 8 13 Z M16 13 C15.69 13 15.34 13.02 14.97 13.05 C16.29 14 17 15.27 17 17 V20 H24 V17 C24 14.34 18.67 13 16 13 Z";
    private static final String ICON_CONTRACTS = "M6 2 H15 L20 7 V22 H6 Z M14 3.5 V8 H18.5 M8 12 H18 M8 16 H18 M8 20 H14";
    private static final String ICON_SALARIES = "M9 3 H15 L14 7 H10 Z M10 7 H14 C18 9 20 12.5 20 17 C20 20.31 16.42 22 12 22 C7.58 22 4 20.31 4 17 C4 12.5 6 9 10 7 Z M12 11 V18 M9.5 13 H13.2 C14.2 13 15 13.67 15 14.55 C15 15.42 14.2 16 13.2 16 H10.8 C9.8 16 9 16.58 9 17.45 C9 18.33 9.8 19 10.8 19 H14.5";
    private static final String ICON_DEPARTMENTS = "M3 21 V9 H9 V21 Z M10 21 V3 H16 V21 Z M17 21 V12 H21 V21 Z M5 12 H7 M12 6 H14 M12 10 H14 M12 14 H14 M19 15 H19";
    private static final String ICON_SETTINGS = "M19.43 12.98 C19.47 12.66 19.5 12.34 19.5 12 C19.5 11.66 19.47 11.34 19.43 11.02 L21.54 9.37 L19.54 5.91 L17.05 6.91 C16.54 6.52 15.99 6.2 15.38 5.95 L15 3.29 H11 L10.62 5.95 C10.01 6.2 9.46 6.52 8.95 6.91 L6.46 5.91 L4.46 9.37 L6.57 11.02 C6.53 11.34 6.5 11.66 6.5 12 C6.5 12.34 6.53 12.66 6.57 12.98 L4.46 14.63 L6.46 18.09 L8.95 17.09 C9.46 17.48 10.01 17.8 10.62 18.05 L11 20.71 H15 L15.38 18.05 C15.99 17.8 16.54 17.48 17.05 17.09 L19.54 18.09 L21.54 14.63 Z M13 15.5 C11.07 15.5 9.5 13.93 9.5 12 C9.5 10.07 11.07 8.5 13 8.5 C14.93 8.5 16.5 10.07 16.5 12 C16.5 13.93 14.93 15.5 13 15.5 Z";
    private static final String ICON_USER = "M12 12 C14.76 12 17 9.76 17 7 C17 4.24 14.76 2 12 2 C9.24 2 7 4.24 7 7 C7 9.76 9.24 12 12 12 Z M4 22 C4 17.58 7.58 14 12 14 C16.42 14 20 17.58 20 22 Z";
    private static final String ICON_LANGUAGE = "M4 4 H13 V7 H11 C10.7 8.4 10.12 9.69 9.25 10.83 C10 11.45 10.9 12.04 12 12.56 L11 14.3 C9.9 13.76 8.93 13.13 8.08 12.43 C7.08 13.25 5.83 14.08 4.3 14.9 L3.35 13.22 C4.73 12.52 5.85 11.82 6.74 11.12 C6.14 10.45 5.62 9.72 5.17 8.92 L6.88 8.05 C7.2 8.6 7.57 9.1 8 9.57 C8.55 8.82 8.94 7.97 9.18 7 H4 Z M15 10 H17 L21 20 H18.9 L18.1 18 H13.9 L13.1 20 H11 Z M14.58 16.2 H17.42 L16 12.55 Z";
    private static final String ICON_HELP = "M12 2 C6.48 2 2 6.48 2 12 C2 17.52 6.48 22 12 22 C17.52 22 22 17.52 22 12 C22 6.48 17.52 2 12 2 Z M11 18 H13 V16 H11 Z M12 6 C9.79 6 8 7.79 8 10 H10 C10 8.9 10.9 8 12 8 C13.1 8 14 8.9 14 10 C14 12 11 11.75 11 15 H13 C13 12.75 16 12.5 16 10 C16 7.79 14.21 6 12 6 Z";
    private static final String ICON_SUN = "M12 4 V2 M12 22 V20 M4.93 4.93 L3.52 3.52 M20.48 20.48 L19.07 19.07 M4 12 H2 M22 12 H20 M4.93 19.07 L3.52 20.48 M20.48 3.52 L19.07 4.93 M12 7 C9.24 7 7 9.24 7 12 C7 14.76 9.24 17 12 17 C14.76 17 17 14.76 17 12 C17 9.24 14.76 7 12 7 Z";
    private static final String ICON_MOON = "M21 12.79 C20.16 13.05 19.28 13.18 18.36 13.18 C14.2 13.18 10.82 9.8 10.82 5.64 C10.82 4.72 10.95 3.84 11.21 3 C6.56 3.45 3 7.36 3 12.12 C3 17.07 6.93 21 11.88 21 C16.64 21 20.55 17.44 21 12.79 Z";

    private final List<String> themeClasses = List.of("light", "dark");
    private final Deque<String> backHistory = new ArrayDeque<>();
    private final Deque<String> forwardHistory = new ArrayDeque<>();

    private String currentView = "welcome";
    private boolean sidebarExpanded = true;
    private boolean darkMode = false;
    private boolean profileLanguageChanged = false;
    private boolean navigatingHistory = false;
    private Timeline sidebarAnimation;

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
    private Button usersButton;

    @FXML
    private Button profileButton;

    @FXML
    private Button languageButton;

    @FXML
    private Button themeButton;

    @FXML
    private Button helpFooterButton;

    @FXML
    private Button themeButton2;

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
        setupMouseNavigation();
        setupContextMenu();
        applyRolePermissions();
        showDashboard();
        checkExpiringContracts();
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
            } else if (event.isShortcutDown() && event.getCode() == KeyCode.R) {
                showDepartments();
                event.consume();
            } else if (event.isShortcutDown() && event.getCode() == KeyCode.U) {
                showUsers();
                event.consume();
            } else if (event.isShortcutDown() && event.getCode() == KeyCode.P) {
                showProfile();
                event.consume();
            } else if (event.isAltDown() && event.getCode() == KeyCode.LEFT) {
                goBack();
                event.consume();
            } else if (event.isAltDown() && event.getCode() == KeyCode.RIGHT) {
                goForward();
                event.consume();
            } else if (event.getCode() == KeyCode.F5) {
                refreshCurrentView();
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

        MenuItem refreshItem = new MenuItem(isAlbanian() ? "Rifresko" : "Refresh");
        refreshItem.setOnAction(event -> refreshCurrentView());

        MenuItem helpItem = new MenuItem(LanguageManager.get("menu.help"));
        helpItem.setOnAction(event -> showHelp());

        MenuItem exitItem = new MenuItem(isAlbanian() ? "Dil nga programi" : "Exit program");
        exitItem.setOnAction(event -> handleExit());

        contextMenu.getItems().setAll(refreshItem, helpItem, new SeparatorMenuItem(), exitItem);

        Platform.runLater(() -> contentArea.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (contextMenu.isShowing() && event.getButton() != MouseButton.SECONDARY) {
                contextMenu.hide();
            }
        }));

        mainShell.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
            refreshItem.setText(isAlbanian() ? "Rifresko" : "Refresh");
            helpItem.setText(LanguageManager.get("menu.help"));
            exitItem.setText(isAlbanian() ? "Dil nga programi" : "Exit program");

            if (contextMenu.isShowing()) {
                contextMenu.hide();
            }

            contextMenu.show(mainShell, event.getScreenX(), event.getScreenY());
            event.consume();
        });
    }

    private void checkExpiringContracts() {
        User user = Session.getUser();
        if (user == null || user.getEmployeeId() == null) return;
        if ("ADMIN".equalsIgnoreCase(user.getRole())) return;

        List<com.company.system.model.Contract> expiring =
                com.company.system.service.ContractService.getExpiringContractsForEmployee(user.getEmployeeId());

        if (expiring.isEmpty()) return;

        boolean sq = isAlbanian();

        StringBuilder message = new StringBuilder();
        message.append(sq
                ? "Kontratat tuaja te meposhtme do te skadojne brenda 14 diteve:\n\n"
                : "The following contracts will expire within 14 days:\n\n");

        for (com.company.system.model.Contract c : expiring) {
            message.append("• ")
                    .append(c.getContractType())
                    .append(" - ")
                    .append(sq ? "Skadon" : "Expires")
                    .append(": ")
                    .append(c.getEndDate())
                    .append("\n");
        }

        message.append(sq
                ? "\nJu lutem kontaktoni administratorin per rinovim."
                : "\nPlease contact the administrator for renewal.");

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            DialogUtils.style(alert);
            alert.setTitle(sq ? "Paralajmerim - Kontrata" : "Warning — Contract");
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
        usersButton.setVisible(isAdmin);
        usersButton.setManaged(isAdmin);
    }

    private void updateTexts() {
        dashboardButton.setUserData(new NavItem(ICON_DASHBOARD, LanguageManager.get("menu.dashboard")));
        employeesButton.setUserData(new NavItem(ICON_EMPLOYEES, LanguageManager.get("menu.employees")));
        contractsButton.setUserData(new NavItem(ICON_CONTRACTS, LanguageManager.get("menu.contracts")));
        salariesButton.setUserData(new NavItem(ICON_SALARIES, LanguageManager.get("menu.salaries")));
        departmentsButton.setUserData(new NavItem(ICON_DEPARTMENTS, LanguageManager.get("menu.departments")));
        usersButton.setUserData(new NavItem(ICON_USER, LanguageManager.get("menu.users")));
        profileButton.setUserData(new NavItem(ICON_SETTINGS, LanguageManager.get("menu.profile")));

        welcomeLabel.setText(LanguageManager.get("app.welcome"));
        setIconOnlyButton(languageButton, ICON_LANGUAGE);
        setIconOnlyButton(helpFooterButton, ICON_HELP);
        setIconOnlyButton(themeButton2, ICON_SETTINGS);
        updateSidebarLabels();
        updateLoggedInUser();

        if ("welcome".equals(currentView)) {
            setStatus(LanguageManager.get("status.ready"));
        }
    }

    private void updateLoggedInUser() {
        User user = Session.getUser();
        loggedInLabel.setText("Logged in as: " + UserService.getDisplayName(user));
    }

    private void updateThemeButton() {
        setIconOnlyButton(themeButton, darkMode ? ICON_SUN : ICON_MOON);
        setIconOnlyButton(themeButton2, darkMode ? ICON_SUN : ICON_MOON);
    }

    private void updateSidebarState() {
        if (sidebarAnimation != null) {
            sidebarAnimation.stop();
        }

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
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(sidebar.prefWidthProperty(), startWidth),
                        new KeyValue(sidebar.minWidthProperty(), startWidth),
                        new KeyValue(sidebar.maxWidthProperty(), startWidth),
                        new KeyValue(expandedFooter.opacityProperty(), sidebarExpanded ? 0 : 1)
                ),
                new KeyFrame(
                        Duration.millis(280),
                        new KeyValue(sidebar.prefWidthProperty(), targetWidth, Interpolator.EASE_BOTH),
                        new KeyValue(sidebar.minWidthProperty(), targetWidth, Interpolator.EASE_BOTH),
                        new KeyValue(sidebar.maxWidthProperty(), targetWidth, Interpolator.EASE_BOTH),
                        new KeyValue(expandedFooter.opacityProperty(), sidebarExpanded ? 1 : 0, Interpolator.EASE_BOTH)
                )
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
        setNavButtonText(dashboardButton);
        setNavButtonText(employeesButton);
        setNavButtonText(contractsButton);
        setNavButtonText(salariesButton);
        setNavButtonText(departmentsButton);
        setNavButtonText(usersButton);
        setNavButtonText(profileButton);
    }

    private void setNavButtonText(Button button) {
        if (!(button.getUserData() instanceof NavItem item)) {
            return;
        }

        button.setGraphic(createSidebarIconBox(item.iconPath(), NAV_ICON_SCALE));
        button.setText(sidebarExpanded ? item.label() : "");
        button.setContentDisplay(sidebarExpanded ? ContentDisplay.LEFT : ContentDisplay.GRAPHIC_ONLY);
        button.setGraphicTextGap(14);
        button.setAlignment(sidebarExpanded ? Pos.CENTER_LEFT : Pos.CENTER);
    }

    private void setIconOnlyButton(Button button, String iconPath) {
        button.setText("");
        button.setGraphic(createSidebarIconBox(iconPath, FOOTER_ICON_SCALE));
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.setAlignment(Pos.CENTER);
    }

    private StackPane createSidebarIconBox(String iconPath, double scale) {
        StackPane iconBox = new StackPane(createSidebarIcon(iconPath, scale));
        iconBox.getStyleClass().add("sidebar-icon-box");
        return iconBox;
    }

    private SVGPath createSidebarIcon(String iconPath, double scale) {
        SVGPath icon = new SVGPath();
        icon.setContent(iconPath);
        icon.getStyleClass().add("sidebar-svg-icon");
        icon.setScaleX(scale);
        icon.setScaleY(scale);
        return icon;
    }

    private void setActiveButton(Button activeButton) {
        List<Button> buttons = List.of(
                dashboardButton,
                employeesButton,
                contractsButton,
                salariesButton,
                departmentsButton,
                usersButton,
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

    private void clearActiveButton() {
        List<Button> buttons = List.of(
                dashboardButton,
                employeesButton,
                contractsButton,
                salariesButton,
                departmentsButton,
                usersButton,
                profileButton
        );

        for (Button button : buttons) {
            button.getStyleClass().remove("active");
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
            case "employees" -> showEmployees();
            case "contracts" -> showContracts();
            case "salaries" -> showSalaries();
            case "dashboard" -> showDashboard();
            case "departments" -> showDepartments();
            case "users" -> showUsers();
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
        title.setMaxWidth(360);
        title.setAlignment(Pos.CENTER);
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

    private void recordNavigation(String targetView) {
        if (navigatingHistory || targetView.equals(currentView)) {
            return;
        }

        if (!"welcome".equals(currentView)) {
            backHistory.push(currentView);
        }

        forwardHistory.clear();
    }

    private void goBack() {
        if (backHistory.isEmpty()) {
            return;
        }

        forwardHistory.push(currentView);
        navigateHistoryTo(backHistory.pop());
    }

    private void goForward() {
        if (forwardHistory.isEmpty()) {
            return;
        }

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
            case "employees" -> showEmployees();
            case "contracts" -> showContracts();
            case "salaries" -> showSalaries();
            case "dashboard" -> showDashboard();
            case "departments" -> showDepartments();
            case "users" -> showUsers();
            case "help" -> showHelp();
            case "profile" -> showProfile();
            default -> showDashboard();
        }
    }

    @FXML
    public void showDashboard() {
        recordNavigation("dashboard");
        currentView = "dashboard";
        setStatus(LanguageManager.get("status.dashboard"));

        User user = Session.getUser();
        if (user != null && !"ADMIN".equalsIgnoreCase(user.getRole())) {
            loadView("/views/user-dashboard-view.fxml", dashboardButton, "Failed to load user dashboard.");
            return;
        }

        loadView("/views/dashboard-view.fxml", dashboardButton, "Failed to load dashboard.");
    }

    @FXML
    public void showEmployees() {
        recordNavigation("employees");
        currentView = "employees";
        setStatus(LanguageManager.get("status.employees"));
        loadView("/views/employees-view.fxml", employeesButton, "Failed to load employees.");
    }

    @FXML
    public void showContracts() {
        recordNavigation("contracts");
        currentView = "contracts";
        setStatus(LanguageManager.get("status.contracts"));
        loadView("/views/contracts-view.fxml", contractsButton, "Failed to load contracts.");
    }

    @FXML
    public void showSalaries() {
        recordNavigation("salaries");
        currentView = "salaries";
        setStatus(LanguageManager.get("status.salaries"));
        loadView("/views/salaries-view.fxml", salariesButton, "Failed to load salaries.");
    }

    @FXML
    public void showDepartments() {
        recordNavigation("departments");
        currentView = "departments";
        setStatus(LanguageManager.get("status.departments"));
        loadView("/views/departments-view.fxml", departmentsButton, "Failed to load departments.");
    }

    @FXML
    public void showUsers() {
        recordNavigation("users");
        currentView = "users";
        setStatus(LanguageManager.get("status.users"));
        loadView("/views/users-view.fxml", usersButton, "Failed to load users.");
    }

    @FXML
    public void showHelp() {
        recordNavigation("help");
        currentView = "help";
        setStatus(LanguageManager.get("status.help"));
        clearActiveButton();

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
                        "Ctrl+R - " + LanguageManager.get("menu.departments"),
                        "Ctrl+U - " + LanguageManager.get("menu.users"),
                        "Ctrl+P - " + LanguageManager.get("menu.profile"),
                        "Ctrl+L - " + LanguageManager.get("menu.language"),
                        "Ctrl+H / F1 - " + LanguageManager.get("menu.help"),
                        "Alt+Left / Mouse Back - " + (sq ? "Kthehu prapa" : "Go back"),
                        "Alt+Right / Mouse Forward - " + (sq ? "Shko perpara" : "Go forward"),
                        "F5 - " + (sq ? "Rifresko pamjen aktuale" : "Refresh current view"),
                        "Esc - " + LanguageManager.get("menu.exit")
                ),
                createHelpSection(
                        sq ? "Menuja me klikim te djathte" : "Right-click menu",
                        sq ? "Klikoni me te djathten ne nje hapesire te zbrazet per Rifresko, Ndihma dhe Dil nga programi." : "Right-click an empty area for Refresh, Help and Exit program.",
                        sq ? "Rifresko ngarkon perseri pamjen aktuale pa ndryshuar modulin." : "Refresh reloads the current view without changing modules."
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
        recordNavigation("profile");
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

        Label title = new Label(isAlbanian() ? "Cilesimet" : "Settings");
        title.getStyleClass().add("page-title");

        StackPane userIcon = createSidebarIconBox(ICON_USER, 1.08);
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
        userCard.setMaxWidth(900);

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

        VBox languageRow = new VBox(6, languageBox, languageMessage);
        languageMessage.setText(profileLanguageChanged ? LanguageManager.get("account.language.success") : "");
        profileLanguageChanged = false;

        Button logout = new Button(LanguageManager.get("account.logout"));
        logout.getStyleClass().add("secondary-button");
        logout.setOnAction(e -> handleLogout());

        VBox languageCard = new VBox(12, languageTitle, languageRow);
        languageCard.getStyleClass().add("profile-card");
        languageCard.setPrefWidth(430);
        languageCard.setMaxWidth(Double.MAX_VALUE);

        Label passwordTitle = new Label(isAlbanian() ? "Ndrysho fjalekalimin" : "Change password");
        passwordTitle.getStyleClass().add("section-title");

        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText(isAlbanian() ? "Fjalekalimi aktual" : "Current password");
        currentPasswordField.setMaxWidth(Double.MAX_VALUE);

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText(isAlbanian() ? "Fjalekalimi i ri" : "New password");
        newPasswordField.setMaxWidth(Double.MAX_VALUE);

        Button changePasswordButton = new Button(isAlbanian() ? "Ndrysho fjalekalimin" : "Change password");
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

        Button deleteAccount = new Button(isAlbanian() ? "Fshi llogarine" : "Delete account");
        deleteAccount.getStyleClass().add("danger-text-button");
        deleteAccount.setOnAction(e -> confirmDeleteAccount(user));

        VBox actionsCard = new VBox(14, logout, deleteAccount);
        actionsCard.getStyleClass().add("profile-card");
        actionsCard.setMaxWidth(900);

        VBox profileView = new VBox(18, title, userCard, accountOptions, actionsCard);
        profileView.getStyleClass().add("profile-page");
        profileView.setStyle("-fx-padding: 28;");
        VBox.setVgrow(profileView, Priority.NEVER);

        setContent(profileView);
    }

private String formatCurrency(double value) {
    return String.format("%.2f €", value);
}

private String valueOrDash(String value) {
    return value == null || value.isBlank() ? "-" : value;
}

    private void changePassword(User user, PasswordField currentPasswordField, PasswordField newPasswordField) {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String storedHash = UserService.getPasswordHashByUsername(user.getUsername());

        if (currentPassword == null || currentPassword.isBlank() || newPassword == null || newPassword.isBlank()) {
            showStyledAlert(Alert.AlertType.ERROR,
                    isAlbanian() ? "Gabim" : "Error",
                    isAlbanian() ? "Plotesoni te dy fushat e fjalekalimit." : "Fill both password fields.");
            return;
        }

        if (!PasswordUtils.verifyPassword(currentPassword, storedHash)) {
            showStyledAlert(Alert.AlertType.ERROR,
                    isAlbanian() ? "Gabim" : "Error",
                    isAlbanian() ? "Fjalekalimi aktual nuk eshte i sakte." : "Current password is not correct.");
            return;
        }

        if (PasswordUtils.verifyPassword(newPassword, storedHash)) {
            showStyledAlert(Alert.AlertType.ERROR,
                    isAlbanian() ? "Gabim" : "Error",
                    isAlbanian() ? "Fjalekalimi i ri nuk mund te jete i njejte me te vjetrin." : "New password cannot be the same as the old one.");
            return;
        }

        if (UserService.resetPassword(user.getUsername(), newPassword)) {
            currentPasswordField.clear();
            newPasswordField.clear();
            showStyledAlert(Alert.AlertType.INFORMATION,
                    isAlbanian() ? "Sukses" : "Success",
                    isAlbanian() ? "Fjalekalimi u ndryshua me sukses." : "Password changed successfully.");
        } else {
            showStyledAlert(Alert.AlertType.ERROR,
                    isAlbanian() ? "Gabim" : "Error",
                    isAlbanian() ? "Fjalekalimi nuk u ndryshua." : "Password was not changed.");
        }
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

    private void confirmDeleteAccount(User user) {
        if (UserService.isOnlyAdmin(user)) {
            showStyledAlert(
                    Alert.AlertType.WARNING,
                    isAlbanian() ? "Nuk lejohet" : "Not allowed",
                    isAlbanian()
                            ? "Ju jeni administratori i vetem. Nuk mund ta fshini llogarine pa pasur te pakten edhe nje administrator tjeter."
                            : "You are the only administrator. You cannot delete this account until another administrator exists."
            );
            return;
        }

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
        title.setMaxWidth(370);
        title.setAlignment(Pos.CENTER);

        title.setStyle("""
            -fx-font-size: 18px;
            -fx-font-weight: bold;
            -fx-text-alignment: center;
            """);

        Label subtitle = new Label(
                isAlbanian()
                        ? "Ky veprim do te fshije llogarine, punetorin, kontratat, pagat dhe historikun e pagave."
                        : "This will delete your account, employee record, contracts, salaries and salary history."
        );

        subtitle.setWrapText(true);
        subtitle.setMaxWidth(370);
        subtitle.setMinHeight(Label.USE_PREF_SIZE);
        subtitle.setStyle("""
            -fx-font-size: 13px;
            -fx-opacity: 0.8;
            """);

        VBox content = new VBox(15, icon, title, subtitle);
        content.setAlignment(Pos.CENTER);

        alert.getDialogPane().setContent(content);
        alert.getDialogPane().setPrefWidth(460);
        alert.getDialogPane().setPrefHeight(360);

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

            boolean deleted = UserService.deleteAccountAndEmployeeData(user);

            if (deleted) {
                Session.clear();
                showWelcome();
            } else {
                showStyledAlert(
                        Alert.AlertType.ERROR,
                        isAlbanian() ? "Gabim" : "Error",
                        isAlbanian()
                                ? "Llogaria nuk u fshi. Kontrolloni databazen ose provoni perseri."
                                : "The account was not deleted. Check the database or try again."
                );
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

    private record NavItem(String iconPath, String label) {
    }
}
